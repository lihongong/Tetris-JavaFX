package tetris.logic;

import javafx.animation.*;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;
import tetris.ui.*;
import tetris.util.GameMode;

import static tetris.util.TetrisConstants.FPS;

public class GameController {

    // model manager
    private GameplayManager gameplayManager;

    // ui
    private MainWindow mainWindow;
    private GameScreen gameScreen;
    private PauseMenuScreen pauseMenuScreen;
    private GameOverScreen gameOverScreen;
    private TimesUpScreen timesUpScreen;
    private StartMenuScreen startMenuScreen;
    private SelectMenuScreen selectMenuScreen;
    // game state
    private TimeManager timeManager;
    private GameState gameState;
    private Timeline currentGameLoop;
    private Timeline relaxGameLoop;
    private Timeline sprintGameLoop;
    private Timeline blitzGameLoop;

    private boolean isIgnoreKeyInput;



    // game metrics


    // =================================================
    // Initialize the controller
    // =================================================

    /**
     * Is called by loader.load()
     */
    //public void initialize() {
    public GameController(GameScreen gameScreen, PauseMenuScreen pauseMenuScreen, GameOverScreen gameOverScreen,
                          TimesUpScreen timesUpScreen, StartMenuScreen startMenuScreen,
                          SelectMenuScreen selectMenuScreen, MainWindow mainWindow) {

        this.gameScreen = gameScreen;
        this.pauseMenuScreen = pauseMenuScreen;
        this.gameOverScreen = gameOverScreen;
        this.timesUpScreen = timesUpScreen;
        this.startMenuScreen = startMenuScreen;
        this.selectMenuScreen = selectMenuScreen;

        this.mainWindow = mainWindow;

        this.timeManager = new TimeManager();
        this.gameState = new GameState(timeManager);

        this.gameplayManager = new GameplayManager(gameState, gameScreen);
        setUpGameLoop();

        this.isIgnoreKeyInput = true;
    }
    private void setUpGameLoop() {
        // Create the game loop
        relaxGameLoop = new Timeline(
                new KeyFrame(Duration.seconds(1.0 / FPS),
                        e -> {
                            this.relaxUpdate();
                        }
                )
        );
        relaxGameLoop.setCycleCount(Timeline.INDEFINITE); // repeat forever

        sprintGameLoop = new Timeline(
                new KeyFrame(Duration.seconds(1.0 / FPS),
                        e -> {
                            this.sprintUpdate();
                        }
                )
        );
        sprintGameLoop.setCycleCount(Timeline.INDEFINITE); // repeat forever

        blitzGameLoop = new Timeline(
                new KeyFrame(Duration.seconds(1.0 / FPS),
                        e -> {
                            this.blitzUpdate();
                        }
                )
        );
        blitzGameLoop.setCycleCount(Timeline.INDEFINITE); // repeat forever

        // dont start yet until play button!!!! gameLoop.play(); // start the loop
    }

    public void relaxUpdate() {
        assert relaxGameLoop == currentGameLoop;

        if (gameState.isGameOver()) {
            relaxGameLoop.pause();

            gameOverScreen.openGameOverScreenEffects(gameScreen);
        } else {
            gameplayManager.update();
        }
    }

    public void sprintUpdate() {
        assert sprintGameLoop == currentGameLoop;

        if (gameState.isSprintOver()) {
            sprintGameLoop.pause();


        }
        if (gameState.isGameOver()) {
            sprintGameLoop.pause();

            gameOverScreen.openGameOverScreenEffects(gameScreen);
        }
    }

    public void blitzUpdate() {
        assert blitzGameLoop == currentGameLoop;

        if (timeManager.isTimesUp()) {
            blitzGameLoop.pause();

            timesUpScreen.openTimesUpScreenEffects(gameScreen);
        }
        if (gameState.isGameOver()) {
            blitzGameLoop.pause();

            gameOverScreen.openGameOverScreenEffects(gameScreen);
        } else {
            gameScreen.updateTime(timeManager.getCurrentCounter());
            gameplayManager.update();
        }
    }


    public GameState getGameState() {
        return this.gameState;
    }

    public void pauseGame() {
        gameState.pauseTheGame();
        currentGameLoop.pause();

        pauseMenuScreen.openPauseMenuEffects(gameScreen);
    }
    public void resumeGame() {
        // resuming game is more time sensitive, so make sure the transitions have ended, only then we resume gameplay
        Runnable resumeGameAfterEffects = () -> {
            // only resume the game logic after the animation is finished
            gameState.resumeTheGame();
            currentGameLoop.play();
        };
        pauseMenuScreen.closePauseMenuEffects(gameScreen, resumeGameAfterEffects);
    }

    public void restartGameInGameOver() {
        // transition effects
        gameOverScreen.closeGameOverScreenEffects(gameScreen);

        // restart model
        currentGameLoop.play();
        gameplayManager.restartGame();
    }

    public void restartGameInTimesUp() {
        // transition effects
        timesUpScreen.closeTimesUpScreenEffects(gameScreen);

        // restart model
        currentGameLoop.play();
        gameplayManager.restartGame();
    }

    public void restartGameInPauseMenu() {
        // transition effects
        pauseMenuScreen.closePauseMenuEffects(gameScreen, null);

        // restart model
        currentGameLoop.play();
        gameplayManager.restartGame();
    }

    public void exitButtonInGameOver() {
        this.isIgnoreKeyInput = false;

        // transition effects -- handled by game over screen
        gameOverScreen.exitGameOverScreenEffects(mainWindow, selectMenuScreen, gameScreen,
                                                 pauseMenuScreen, timesUpScreen);

    }
    public void exitButtonInTimesUp() {
        this.isIgnoreKeyInput = false;

        // transition effects -- handled by times up screen
        timesUpScreen.exitTimesUpScreenEffects(mainWindow, selectMenuScreen, gameScreen,
                pauseMenuScreen, gameOverScreen);

    }
    public void exitButtonInPauseMenu() {
        this.isIgnoreKeyInput = true;

        // transition effects -- handled by pause menu screen :)
        pauseMenuScreen.exitPauseMenuEffects(mainWindow, selectMenuScreen, gameScreen, timesUpScreen, gameOverScreen);
    }

    // =============================
    // Start & Select Menu Buttons
    // =============================

    public void startButtonInStartMenu() {
        // NOTE: no check of "is transition effects on" so it feels more responsive
        this.isIgnoreKeyInput = true; // don't allow key input

        // transition effects
        startMenuScreen.startMenuStartEffect(mainWindow, selectMenuScreen);
    }

    public void exitButtonInSelectMenu() {
        this.isIgnoreKeyInput = true;

        // transition effects
        selectMenuScreen.exitSelectMenuScreenEffect(mainWindow, startMenuScreen);
    }

    public void relaxButton() {
        // hide timer in gameScreen
        gameScreen.hideTimer();

        currentGameLoop = relaxGameLoop;
        gameState.setGameMode(GameMode.RELAX);
        startGame();
    }
    public void sprintButton() {
        // show count up timer in gameScreen
        gameScreen.showCountUpTimer();

        currentGameLoop = sprintGameLoop;
        gameState.setGameMode(GameMode.SPRINT);

        //selectMenuScreen.showSprintModesScreen();
    }
    public void clear20LinesButton() {}
    public void clear40LinesButton() {}
    public void clear60LinesButton() {}
    public void sprintModesBackButton() {}
    public void blitzButton() {
        // set timer and timer bar in gameScreen to visible
        gameScreen.showCountDownTimerAndBar();

        currentGameLoop = blitzGameLoop;
        gameState.setGameMode(GameMode.BLITZ);
        startGame();
    }

    /**
     * Control the UI so that it is prepared for the gameplay
     * Also set up the game model and timer UI
     * Then RUN THE GAME LOOP
     */
    private void startGame() {
        // NOTE: no check of "is transition effects on" so it feels more responsive
        //       but do set "is transition effects on" flag to true to prevent other transition effects
        //       from popping in (e.g. can't pause when starting game screen)
        selectMenuScreen.renderGameScreenFromSelectMenu(mainWindow, gameScreen, gameOverScreen, timesUpScreen,
                                                        pauseMenuScreen);

        isIgnoreKeyInput = false; // allow key input when game start
        currentGameLoop.play(); // RUN THE GAME LOOP

        gameplayManager.restartGame();
        gameScreen.updateTime(timeManager.getCurrentCounter());
    }






    public GameplayManager getGameplayManager() {
        return gameplayManager;
    }

    public boolean isIgnoreKeyInput() {
        return isIgnoreKeyInput;
    }

}
