package tetris.logic;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.util.Duration;
import tetris.audio.AudioManager;
import tetris.audio.SoundType;
import tetris.ui.*;
import tetris.util.GameMode;
import tetris.util.SprintMode;

import static tetris.util.TetrisConstants.FPS;
import static tetris.util.TetrisConstants.SPRINT_MODE_A_CAP;
import static tetris.util.TetrisConstants.SPRINT_MODE_B_CAP;
import static tetris.util.TetrisConstants.SPRINT_MODE_C_CAP;

public class GameController {

    // model manager
    private GameplayManager gameplayManager;
    private KeyInputController keyInputController;

    // ui
    private final GameScreen gameScreen;
    private final PauseMenuScreen pauseMenuScreen;
    private final GameOverScreen gameOverScreen;
    private final TimesUpScreen timesUpScreen;
    private final SprintOverScreen sprintOverScreen;
    private final StartMenuScreen startMenuScreen;
    private final SelectMenuScreen selectMenuScreen;
    private final SprintModesScreen sprintModesScreen;
    // game state
    private final TimeManager timeManager;
    private final GameState gameState;
    private Timeline currentGameLoop;
    private Timeline relaxGameLoop;
    private Timeline sprintGameLoop;
    private Timeline blitzGameLoop;

    //private boolean isIgnoreKeyInput;



    // game metrics


    // =================================================
    // Initialize the controller
    // =================================================

    /**
     * Is called by loader.load()
     */
    //public void initialize() {
    public GameController(GameScreen gameScreen, PauseMenuScreen pauseMenuScreen, GameOverScreen gameOverScreen,
                          TimesUpScreen timesUpScreen, SprintOverScreen sprintOverScreen,
                          StartMenuScreen startMenuScreen, SelectMenuScreen selectMenuScreen,
                          SprintModesScreen sprintModesScreen, Scene mainScene) {

        this.gameScreen = gameScreen;
        this.pauseMenuScreen = pauseMenuScreen;
        this.gameOverScreen = gameOverScreen;
        this.timesUpScreen = timesUpScreen;
        this.sprintOverScreen = sprintOverScreen;
        this.startMenuScreen = startMenuScreen;
        this.selectMenuScreen = selectMenuScreen;
        this.sprintModesScreen = sprintModesScreen;

        this.timeManager = new TimeManager();
        this.gameState = new GameState(timeManager);

        this.gameplayManager = new GameplayManager(gameState, gameScreen);
        setUpGameLoop();

        this.keyInputController = new KeyInputController(mainScene, gameState, this,
                gameplayManager.getActiveStateManager());

        //this.isIgnoreKeyInput = true;
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
            AudioManager.getInstance().stopBGM();
            AudioManager.getInstance().playRandomGameOverSound();

            // pause game loop
            relaxGameLoop.pause();

            // disable key input
            keyInputController.disableAllKeyInput();
            // only enable key input after animation is fully finished
            Runnable allowSystemKeyInput = () -> {
                keyInputController.enableSystemInputOnly();
            };
            gameOverScreen.openGameOverScreenEffects(gameScreen, allowSystemKeyInput);
        } else {
            gameplayManager.update();
        }
    }

    public void sprintUpdate() {
        assert sprintGameLoop == currentGameLoop;

        // note: line meter tube is updated under inactiveStateManager.handleRemoveLine()
        gameScreen.updateStopWatch(timeManager.getCurrentCounter());

        if (gameState.isSprintOver()) {

            keyInputController.disableAllKeyInput();

            // dont pause game loop yet, run the last clear line effect only then pause game loop
            // after that, open the sprint over screen,
            // after the effect, only enable system input (no gameplay input)
            Runnable pauseGameLoopAndSprintOverEffects = () -> {
                AudioManager.getInstance().stopBGM();

                sprintGameLoop.pause();

                Runnable allowSystemKeyInput = () -> {
                    keyInputController.enableSystemInputOnly();
                };
                sprintOverScreen.openSprintOverScreenEffects(gameScreen, allowSystemKeyInput);
            };

            gameplayManager.lastClearLineEffect(pauseGameLoopAndSprintOverEffects);
        } else if (gameState.isGameOver()) {
            AudioManager.getInstance().stopBGM();
            AudioManager.getInstance().playRandomGameOverSound();

            sprintGameLoop.pause();

            // disable key input
            keyInputController.disableAllKeyInput();
            // only enable key input after animation is fully finished
            Runnable allowSystemKeyInput = () -> {
                keyInputController.enableSystemInputOnly();
            };
            gameOverScreen.openGameOverScreenEffects(gameScreen, allowSystemKeyInput);
        } else {
            gameplayManager.update();
        }
    }

    public void blitzUpdate() {
        assert blitzGameLoop == currentGameLoop;

        if (timeManager.isWarningTime()) {
            AudioManager.getInstance().playSfx(SoundType.TIME_WARNING);
        }
        gameScreen.updateRemainingTime(timeManager.getCurrentCounter());

        if (timeManager.isTimesUp()) {
            AudioManager.getInstance().stopBGM();
            AudioManager.getInstance().playSfx(SoundType.TIMES_UP);

            blitzGameLoop.pause();

            // disable key input
            keyInputController.disableAllKeyInput();
            // only enable key input after animation is fully finished
            Runnable allowSystemKeyInput = () -> {
                keyInputController.enableSystemInputOnly();
            };
            timesUpScreen.openTimesUpScreenEffects(gameScreen, allowSystemKeyInput);
        } else if (gameState.isGameOver()) {
            AudioManager.getInstance().stopBGM();
            AudioManager.getInstance().playRandomGameOverSound();

            blitzGameLoop.pause();

            // disable key input
            keyInputController.disableAllKeyInput();
            // only enable key input after animation is fully finished
            Runnable allowSystemKeyInput = () -> {
                keyInputController.enableSystemInputOnly();
            };
            gameOverScreen.openGameOverScreenEffects(gameScreen, allowSystemKeyInput);
        } else {
            gameplayManager.update();
        }
    }

    // PAUSE AND RESUME
    public void pauseGame() {
        // prevent pausing before entrance animation to gameScreen is finish -> causing Pause Menu not to show up
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        AudioManager.getInstance().pauseBGM();

        gameState.pauseTheGame();
        currentGameLoop.pause();

        pauseMenuScreen.openPauseMenuEffects(gameScreen);
    }
    public void resumeGame() {
        // resuming game is more time sensitive, so make sure the transitions have ended, only then we resume gameplay
        Runnable resumeGameAfterEffects = () -> {
            AudioManager.getInstance().resumeBGM();

            // only resume the game logic after the animation is finished
            gameState.resumeTheGame();
            currentGameLoop.play();
        };
        pauseMenuScreen.closePauseMenuEffects(gameScreen, resumeGameAfterEffects);
    }
    // RESTART
    public void restartGameInGameOver() {
        // restart model & gameScreen
        gameplayManager.restartGameplayManager();

        Runnable restartGameAfterEffects = () -> {
            AudioManager.getInstance().restartBGM();

            currentGameLoop.play();
            keyInputController.enableAllKeyInput();
        };

        // transition effects
        gameOverScreen.closeGameOverScreenEffects(gameScreen, restartGameAfterEffects);
    }
    public void restartGameInSprintOver() {
        // restart model & game screen
        Runnable restartGameplayManagerAndGameScreen = () -> {
            gameplayManager.restartGameplayManager();
        };
        Runnable restartGameAfterEffects = () -> {
            AudioManager.getInstance().restartBGM();

            currentGameLoop.play();
            keyInputController.enableAllKeyInput();
        };
        // transition effects
        sprintOverScreen.closeSprintOverScreenEffects(gameScreen, restartGameplayManagerAndGameScreen,
                                                      restartGameAfterEffects);
    }
    public void restartGameInTimesUp() {
        AudioManager.getInstance().restartBGM();

        // transition effects
        timesUpScreen.closeTimesUpScreenEffects(gameScreen);

        // restart model
        gameplayManager.restartGameplayManager();
        keyInputController.enableAllKeyInput();
        currentGameLoop.play();
    }
    public void restartGameInPauseMenu() {
        AudioManager.getInstance().restartBGM();

        // transition effects
        pauseMenuScreen.closePauseMenuEffects(gameScreen, null);

        // restart model
        currentGameLoop.play();
        gameplayManager.restartGameplayManager();
    }

    // EXIT
    public void exitButtonInGameOver() {
        if (gameState.getGameMode() == GameMode.BLITZ) {
            AudioManager.getInstance().stopBGM();
        }

        keyInputController.disableAllKeyInput();

        gameplayManager.exitGame(); // save metrics and clear inactiveBlockArray
        // transition effects -- handled by game over screen
        if (gameState.getGameMode() == GameMode.SPRINT) {
            gameOverScreen.fromGameOverScreenToSprintMenu(sprintModesScreen, gameScreen);
        } else {
            gameOverScreen.fromGameOverScreenToSelectMenu(selectMenuScreen, gameScreen);
        }
    }
    public void exitButtonInSprintOver() {
        keyInputController.disableAllKeyInput();

        gameplayManager.exitGame(); // save metrics and clear inactiveBlockArray
        sprintOverScreen.fromSprintOverToSprintMode(sprintModesScreen, gameScreen);
    }
    public void exitButtonInTimesUp() {
        if (gameState.getGameMode() == GameMode.BLITZ) {
            AudioManager.getInstance().stopBGM();
        }

        keyInputController.disableAllKeyInput();

        gameplayManager.exitGame(); // save metrics and clear inactiveBlockArray
        // transition effects -- handled by times up screen (DONT NEED CARE ABT SPRINT MODE CASE)
        timesUpScreen.fromTimesUpScreenToSelectMenu(selectMenuScreen, gameScreen);
    }
    public void exitButtonInPauseMenu() {
        if (gameState.getGameMode() == GameMode.BLITZ) {
            AudioManager.getInstance().stopBGM();
        }

        keyInputController.disableAllKeyInput();

        gameplayManager.exitGame(); // save metrics and clear inactiveBlockArray
        // transition effects -- handled by pause menu screen :)
        if (gameState.getGameMode() == GameMode.SPRINT) {
            pauseMenuScreen.fromPauseMenuToSprintModes(sprintModesScreen, gameScreen);
        } else {
            pauseMenuScreen.fromPauseMenuToSelectMenu(selectMenuScreen, gameScreen);
        }
    }

    // =============================
    // Start & Select & Sprint Menu Buttons
    // =============================

    public void startButtonInStartMenu() {
        // NOTE: no check of "is transition effects on" so it feels more responsive
        keyInputController.disableAllKeyInput(); // don't allow key input

        // transition effects
        startMenuScreen.fromStartMenuToSelectMenu(selectMenuScreen);
    }

    public void exitButtonInSelectMenu() {
        keyInputController.disableAllKeyInput();

        // transition effects
        selectMenuScreen.fromSelectMenuToStartMenu(startMenuScreen);
    }

    public void relaxButton() {
        // hide timer in gameScreen
        gameScreen.setGameScreenForRelaxMode();

        currentGameLoop = relaxGameLoop;
        gameState.setGameMode(GameMode.RELAX); // will also set sprintMode to NONE

        selectMenuScreen.fromSelectMenuToGameScreen(gameScreen);

        AudioManager.getInstance().playBGM(SoundType.BLITZ_OST);

        startGame();
    }
    public void blitzButton() {
        // set timer and timer bar in gameScreen to visible
        gameScreen.setGameScreenForBlitzMode();

        currentGameLoop = blitzGameLoop;
        gameState.setGameMode(GameMode.BLITZ); // will also set sprintMode to NONE

        selectMenuScreen.fromSelectMenuToGameScreen(gameScreen);

        AudioManager.getInstance().playBGM(SoundType.BLITZ_OST);

        startGame();
    }

    public void sprintButton() {
        // goes into Sprint Modes Menu
        selectMenuScreen.fromSelectMenuToSprintModes(sprintModesScreen);
    }
    public void sprintModesBackButton() {
        sprintModesScreen.fromSprintModesToSelectMenu(selectMenuScreen);
    }
    public void clearALinesButton() {
        gameState.setSprintMode(SprintMode.SPRINT_A);
        gameplayManager.setSprintGoal(SPRINT_MODE_A_CAP);
        startSprintGame();
    }
    public void clearBLinesButton() {
        gameState.setSprintMode(SprintMode.SPRINT_B);
        gameplayManager.setSprintGoal(SPRINT_MODE_B_CAP);
        startSprintGame();
    }
    public void clearCLinesButton() {
        gameState.setSprintMode(SprintMode.SPRINT_C);
        gameplayManager.setSprintGoal(SPRINT_MODE_C_CAP);
        startSprintGame();
    }
    public void startSprintGame() {
        // show count up timer in gameScreen
        gameState.setGameMode(GameMode.SPRINT);

        gameScreen.setGameScreenForSprintMode();
        //gameScreen.restartGameForSprint(gameplayManager.getBestTime(), gameplayManager.getSprintGoal());

        currentGameLoop = sprintGameLoop;

        sprintModesScreen.fromSprintModesToGameScreen(gameScreen);
        keyInputController.enableAllKeyInput(); // allow key input when game start
        currentGameLoop.play(); // RUN THE GAME LOOP

        gameplayManager.restartGameplayManager();

        AudioManager.getInstance().playBGM(SoundType.BLITZ_OST);
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
        keyInputController.enableAllKeyInput(); // allow key input when game start
        currentGameLoop.play(); // RUN THE GAME LOOP

        gameplayManager.restartGameplayManager();
    }

    // UTILS
    public GameState getGameState() {
        return this.gameState;
    }
}
