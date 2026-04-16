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
/*
        sprintGameLoop = new Timeline(
                new KeyFrame(Duration.seconds(1.0 / FPS),
                        e -> {
                            this.sprintUpdate();
                        }
                )
        );
        sprintGameLoop.setCycleCount(Timeline.INDEFINITE); // repeat forever
*/
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

            gameOverScreen.openGameOverScreenEffects(gameState, gameScreen);
        } else {
            gameplayManager.update();
        }
    }

    public void blitzUpdate() {
        assert blitzGameLoop == currentGameLoop;

        if (timeManager.isTimesUp()) {
            blitzGameLoop.pause();

            // blitz times up screen open up TODO
            timesUpScreen.openTimesUpScreenEffects(gameState, gameScreen);
        }
        if (gameState.isGameOver()) {
            blitzGameLoop.pause();

            gameOverScreen.openGameOverScreenEffects(gameState, gameScreen);
        } else {
            gameScreen.updateTime(timeManager.getCurrentCounter());
            gameplayManager.update();
        }
    }


    public GameState getGameState() {
        return this.gameState;
    }

    public void pauseGame() {
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;

        gameState.pauseTheGame();
        currentGameLoop.pause();

        pauseMenuScreen.openPauseMenuEffects(gameState, gameScreen);
        //gameScreen.getRoot().setEffect(new GaussianBlur(10));
        //pauseMenuScreen.getRoot().setVisible(true);
    }
    public void resumeGame() {
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;

        // gameScreen.getRoot().setEffect(null); // remove blur
        ParallelTransition combined = pauseMenuScreen.closePauseMenuEffects(gameState, gameScreen);

        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();
        combined.getChildren().add(gameScreenRemoveBlur);

        combined.setOnFinished(e -> {
            pauseMenuScreen.getRoot().setVisible(false);
            //gameScreen.setRemoveEffects();

            // only resume the game logic after the animation is finished
            gameState.resumeTheGame();
            currentGameLoop.play();

            gameState.isTransitionEffectsOn = false;
        });

        combined.play();
        //pauseMenuScreen.getRoot().setVisible(false);
    }

    public void restartGameInGameOver() {
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;

        // transition effects
        gameOverScreen.closeGameOverScreenEffects(gameState, gameScreen);

        // restart model
        currentGameLoop.play();
        gameplayManager.restartGame();
    }

    public void restartGameInTimesUp() {
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;

        // transition effects
        timesUpScreen.closeTimesUpScreenEffects(gameState, gameScreen);

        // restart model
        currentGameLoop.play();
        gameplayManager.restartGame();
    }

    public void restartGameInPauseMenu() {
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;

        // transition effects
        ParallelTransition combined = pauseMenuScreen.closePauseMenuEffects(gameState, gameScreen);

        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();

        combined.getChildren().add(gameScreenRemoveBlur);

        combined.setOnFinished(e -> {
            pauseMenuScreen.getRoot().setVisible(false);
            //gameScreen.setRemoveEffects();

            gameState.isTransitionEffectsOn = false;

            // restart model
            // only start the game after animation is finished
            currentGameLoop.play();
            gameplayManager.restartGame();
        });

        combined.play();


    }
    public void exitButtonInGameOver() {
        // synchronization flags
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;
        this.isIgnoreKeyInput = false;

        // transition effects
        mainWindow.addNodesToRoot(selectMenuScreen.getRoot());

        ParallelTransition combined = gameOverScreen.exitGameOverScreenEffects(gameState);

        FadeTransition fadeInSelectMenuScreen = new FadeTransition(Duration.seconds(0.3), selectMenuScreen.getRoot());
        fadeInSelectMenuScreen.setFromValue(0.0);
        fadeInSelectMenuScreen.setToValue(1.0);
        combined.getChildren().add(fadeInSelectMenuScreen);

        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();
        combined.getChildren().add(gameScreenRemoveBlur);

        combined.setOnFinished(e -> {

            // handle nodes
            mainWindow.removeNodesFromRoot(pauseMenuScreen.getRoot(), gameScreen.getRoot(),
                                           gameOverScreen.getRoot(), timesUpScreen.getRoot());

            gameState.isTransitionEffectsOn = false;
        });

        combined.play();

    }
    public void exitButtonInTimesUp() {
        // synchronization flags
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;
        this.isIgnoreKeyInput = false;

        // transition effects
        mainWindow.addNodesToRoot(selectMenuScreen.getRoot());

        ParallelTransition combined = timesUpScreen.exitTimesUpScreenEffects(gameState);

        FadeTransition fadeInSelectMenuScreen = new FadeTransition(Duration.seconds(0.3), selectMenuScreen.getRoot());
        fadeInSelectMenuScreen.setFromValue(0.0);
        fadeInSelectMenuScreen.setToValue(1.0);
        combined.getChildren().add(fadeInSelectMenuScreen);

        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();
        combined.getChildren().add(gameScreenRemoveBlur);

        combined.setOnFinished(e -> {

            // handle nodes
            mainWindow.removeNodesFromRoot(pauseMenuScreen.getRoot(), gameScreen.getRoot(),
                    gameOverScreen.getRoot(), timesUpScreen.getRoot());

            gameState.isTransitionEffectsOn = false;
        });

        combined.play();

    }
    public void exitButtonInPauseMenu() {
        // synchronization flags
        if (gameState.isTransitionEffectsOn) {
            return;
        }
        gameState.isTransitionEffectsOn = true;
        this.isIgnoreKeyInput = true;

        // transition effects
        mainWindow.addNodesToRoot(selectMenuScreen.getRoot());

        ParallelTransition combined = pauseMenuScreen.exitPauseMenuEffects(gameState);

        FadeTransition fadeInSelectMenuScreen = new FadeTransition(Duration.seconds(0.3), selectMenuScreen.getRoot());
        fadeInSelectMenuScreen.setFromValue(0.0);
        fadeInSelectMenuScreen.setToValue(1.0);
        combined.getChildren().add(fadeInSelectMenuScreen);

        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();
        combined.getChildren().add(gameScreenRemoveBlur);

        combined.setOnFinished(e -> {
            // handle nodes
            mainWindow.removeNodesFromRoot(pauseMenuScreen.getRoot(), gameScreen.getRoot(),
                                           gameOverScreen.getRoot(), timesUpScreen.getRoot());

            gameState.isTransitionEffectsOn = false;
        });

        combined.play();
    }

    public void exitButtonInSelectMenu() {
        // NOTE: no check of "is transition effects on" so it feels more responsive
        this.isIgnoreKeyInput = true;

        // transition effects
        mainWindow.addNodesToRoot(startMenuScreen.getRoot());

        FadeTransition fadeInStartMenuScreen = new FadeTransition(Duration.seconds(0.3), startMenuScreen.getRoot());
        fadeInStartMenuScreen.setFromValue(0.0);
        fadeInStartMenuScreen.setToValue(1.0);


        fadeInStartMenuScreen.setOnFinished(e -> {
            // handle nodes
            mainWindow.removeNodesFromRoot(selectMenuScreen.getRoot());
        });

        fadeInStartMenuScreen.play();
    }

    public void startButtonInStartMenu() {
        // NOTE: no check of "is transition effects on" so it feels more responsive
        this.isIgnoreKeyInput = true; // don't allow key input

        // transition effects
        mainWindow.addNodesToRoot(selectMenuScreen.getRoot());

        FadeTransition fadeInSelectMenu = new FadeTransition(Duration.seconds(0.3), selectMenuScreen.getRoot());
        fadeInSelectMenu.setFromValue(0.0);
        fadeInSelectMenu.setToValue(1.0);

        fadeInSelectMenu.setOnFinished(e -> {
            mainWindow.removeNodesFromRoot(startMenuScreen.getRoot());
        });
        fadeInSelectMenu.play();
    }

    public void relaxButton() {
        // set timer in gameScreen to invisible
        gameScreen.setTimerVisibility(false);

        currentGameLoop = relaxGameLoop;
        gameState.setGameMode(GameMode.RELAX);
        startGame();
    }
    public void blitzButton() {
        // set timer in gameScreen to visible
        gameScreen.setTimerVisibility(true);

        currentGameLoop = blitzGameLoop;
        gameState.setGameMode(GameMode.BLITZ);
        startGame();
    }

    public void startGame() {
        // NOTE: no check of "is transition effects on" so it feels more responsive
        //       but do set "is transition effects on" flag to true to prevent other transition effects
        //       from popping in (e.g. can't pause when starting game screen)
        gameState.isTransitionEffectsOn = true;
        this.isIgnoreKeyInput = false; // allow key input when game start

        // set new background for gameScreen
        gameScreen.setRandomBackGroundImage();

        // transition effects
        mainWindow.addNodesToRoot(gameScreen.getRoot(), pauseMenuScreen.getRoot(),
                                  gameOverScreen.getRoot(), timesUpScreen.getRoot());

        gameScreen.getRoot().setEffect(null); // clear the blur effects just in case.
        pauseMenuScreen.getRoot().setVisible(false);
        gameOverScreen.getRoot().setVisible(false);
        timesUpScreen.getRoot().setVisible(false);

        FadeTransition fadeInGameScreen = new FadeTransition(Duration.seconds(1.0), gameScreen.getRoot());
        fadeInGameScreen.setFromValue(0.0);
        fadeInGameScreen.setToValue(1.0);

        pauseMenuScreen.getRoot().setOpacity(1.0);
        gameOverScreen.getRoot().setOpacity(1.0);

        fadeInGameScreen.setOnFinished(e -> {
            // handle nodes
            mainWindow.removeNodesFromRoot(selectMenuScreen.getRoot());

            gameState.isTransitionEffectsOn = false;

            currentGameLoop.play();
        });
        // set up the model & timer ui before animation finish !!!
        gameplayManager.restartGame();
        gameScreen.updateTime(timeManager.getCurrentCounter());

        fadeInGameScreen.play();

    }


















    public GameplayManager getGameplayManager() {
        return gameplayManager;
    }

    public boolean isIgnoreKeyInput() {
        return isIgnoreKeyInput;
    }





}
