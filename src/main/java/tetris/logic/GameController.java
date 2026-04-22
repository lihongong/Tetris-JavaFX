package tetris.logic;

import javafx.animation.*;
import javafx.util.Duration;
import tetris.ui.*;
import tetris.util.GameMode;

import static tetris.util.TetrisConstants.FPS;
import static tetris.util.TetrisConstants.SPRINT_MODE_A_CAP;
import static tetris.util.TetrisConstants.SPRINT_MODE_B_CAP;
import static tetris.util.TetrisConstants.SPRINT_MODE_C_CAP;

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
    private SprintModesScreen sprintModesScreen;
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
                          SelectMenuScreen selectMenuScreen, SprintModesScreen sprintModesScreen,
                          MainWindow mainWindow) {

        this.gameScreen = gameScreen;
        this.pauseMenuScreen = pauseMenuScreen;
        this.gameOverScreen = gameOverScreen;
        this.timesUpScreen = timesUpScreen;
        this.startMenuScreen = startMenuScreen;
        this.selectMenuScreen = selectMenuScreen;
        this.sprintModesScreen = sprintModesScreen;

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

            // TODO: open sprint over screen
        }
        if (gameState.isGameOver()) {
            sprintGameLoop.pause();

            gameOverScreen.openGameOverScreenEffects(gameScreen);
        } else {
            gameScreen.updateStopWatch(timeManager.getCurrentCounter()); // note: line meter tube is updated under inactiveStateManager.handleRemoveLine()
            gameplayManager.update();
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
            gameScreen.updateRemainingTime(timeManager.getCurrentCounter());
            gameplayManager.update();
        }
    }


    public GameState getGameState() {
        return this.gameState;
    }

    public void pauseGame() {
        // prevent pausing before entrance animation to gameScreen is finish -> causing Pause Menu not to show up
        if (UiPart.isUiEffectsOn()) {
            return;
        }
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
        if (gameState.getGameMode() == GameMode.SPRINT) {
            pauseMenuScreen.exitPauseMenuFromSprint(sprintModesScreen, gameScreen, timesUpScreen, gameOverScreen);
        } else {
            pauseMenuScreen.exitPauseMenuFromRelaxBlitz(selectMenuScreen, gameScreen, timesUpScreen, gameOverScreen);
        }
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
        selectMenuScreen.fromSelectMenuToStartMenu(startMenuScreen);
    }

    public void relaxButton() {
        // hide timer in gameScreen
        gameScreen.hideTimer();

        currentGameLoop = relaxGameLoop;
        gameState.setGameMode(GameMode.RELAX);

        selectMenuScreen.fromSelectMenuToGameScreen(gameScreen);
        startGame();
    }
    public void blitzButton() {
        // set timer and timer bar in gameScreen to visible
        gameScreen.showCountDownTimerAndBar();

        currentGameLoop = blitzGameLoop;
        gameState.setGameMode(GameMode.BLITZ);

        selectMenuScreen.fromSelectMenuToGameScreen(gameScreen);
        startGame();
    }

    public void sprintButton() {
        // goes into Sprint Modes Menu
        selectMenuScreen.fromSelectMenuToSprintModes(sprintModesScreen);
    }
    public void clearALinesButton() {
        gameState.setSprintGoal(SPRINT_MODE_A_CAP);
        startSprintGame();
    }
    public void clearBLinesButton() {
        gameState.setSprintGoal(SPRINT_MODE_B_CAP);
        startSprintGame();
    }
    public void clearCLinesButton() {
        gameState.setSprintGoal(SPRINT_MODE_C_CAP);
        startSprintGame();
    }
    public void startSprintGame() {
        // show count up timer in gameScreen
        gameScreen.showCountUpTimerAndTube();

        currentGameLoop = sprintGameLoop;
        gameState.setGameMode(GameMode.SPRINT);

        sprintModesScreen.fromSprintModesToGameScreen(gameScreen);
        startGame();
    }
    public void sprintModesBackButton() {
        sprintModesScreen.fromSprintModesToSelectMenu(selectMenuScreen);
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
        isIgnoreKeyInput = false; // allow key input when game start
        currentGameLoop.play(); // RUN THE GAME LOOP

        gameplayManager.restartGame();
    }






    public GameplayManager getGameplayManager() {
        return gameplayManager;
    }

    public boolean isIgnoreKeyInput() {
        return isIgnoreKeyInput;
    }

}
