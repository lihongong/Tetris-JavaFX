package tetris.logic;

import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.ui.GameScreen;
import tetris.util.GameMode;
import tetris.util.SprintMode;

import static tetris.util.TetrisConstants.*;

public class GameplayManager {

    private GameState gameState;
    private GameScreen gameScreen;

    private MinoBlock[][] inactiveBlocksArray;
    private MinoManager minoManager;

    private ActiveStateManager activeStateManager;
    private InactiveStateManager inactiveStateManager;

    private GameMetrics gameMetrics;


    public GameplayManager(GameState gameState, GameScreen gameScreen) {
        this.gameState = gameState;
        this.gameScreen = gameScreen;

        this.inactiveBlocksArray = new MinoBlock[NUM_OF_ROW][NUM_OF_COL];
        this.minoManager = new MinoManager(gameScreen, gameState, inactiveBlocksArray);

        gameMetrics = new GameMetrics();
        activeStateManager = new ActiveStateManager(gameState, gameScreen, minoManager, inactiveBlocksArray);
        inactiveStateManager = new InactiveStateManager(gameState, gameScreen, minoManager, inactiveBlocksArray, gameMetrics);
    }

    public void update() {
        Mino currentMino = minoManager.getCurrentMino();

        if (currentMino.isActive()){
            if (!gameState.isEffectOn()) {
                activeStateManager.update();
            } else {
                // current mino is active (next round) but the effect is still going on -- clear line effects
                // don't proceed the game (by not calling activeStateManager.update()) until the effect is over
                // Handles the clear line special effects
                handleClearLineSpecialEffect();
            }
        } else {
            inactiveStateManager.update();
        }

        gameState.incrementCounter();
        //gameState.gameCounter = gameState.gameCounter + 1;
    }

    public void handleClearLineSpecialEffect() {
        gameScreen.handleClearLineSpecialEffect(gameState.getEffectCounter());

        if (gameState.isTSpin()) {
            gameScreen.handleTSpinSpecialEffect(gameState.getEffectCounter());
        }

        gameState.incrementEffectCounter();

        if (gameState.hasReachSpecialEffectDuration()) {
            // clear cached block in ui
            gameScreen.clearEffect();

            // add the shadow when the animation is finished
            Mino currentMino = minoManager.getCurrentMino();
            gameScreen.addMinoShadowInPlayingField(currentMino);

            // reset all the flags
            gameState.resetEffectFlags();
        }
    }

    public void lastClearLineEffect(Runnable runnable) {
        gameScreen.handleClearLineSpecialEffect(gameState.getEffectCounter());
        if (gameState.isTSpin()) {
            gameScreen.handleTSpinSpecialEffect(gameState.getEffectCounter());
        }
        gameState.incrementEffectCounter();
        if (gameState.hasReachSpecialEffectDuration()) {
            // clear cached block in ui
            gameScreen.clearEffect();
            // reset all the flags
            gameState.resetEffectFlags();

            runnable.run();
        }
    }

    public void restartGame() {
        if (gameState.getGameMode() == GameMode.SPRINT) {
            restartGameForSprint();
        } else {
            restartGameForDefault();
        }
    }
    public void restartGameForDefault() {
        gameState.restartGame();

        // clear inactive blocks array
        for (int row = 0; row < NUM_OF_ROW; row++) {
            for (int col = 0; col < NUM_OF_COL; col++) {
                inactiveBlocksArray[row][col] = null;
            }
        }

        GameMode gameMode = gameState.getGameMode();
        // !!!!!! gameScreen.restartGame(gameMetrics.getHighScore(gameMode)); MUST COME BEFORE minoManager.restartMinoManager();

        gameMetrics.setHighScore(gameMode); // set high score first
        gameMetrics.resetScoreAndLines(); // then reset current score

        gameScreen.restartGame(gameMetrics.getHighScore(gameMode)); // erase everything in game screen

        // reset minos and draw them accordingly to their new position!
        minoManager.restartMinoManager();
    }

    public void restartGameForSprint() {
        int currGameCounterVal = gameState.getGameCounterVal(); // DO THIS BEFORE gameState.restart() !!!
        boolean isSprintOver = gameState.isSprintOver();
        SprintMode currSprintMode = gameState.getSprintMode();

        // only set best time if the game is fully COMPLETED
        if (isSprintOver) {
            gameMetrics.setBestTime(currGameCounterVal, currSprintMode);
        }

        gameMetrics.resetScoreAndLines();
        gameState.restartGame();

        // clear inactive blocks array
        for (int row = 0; row < NUM_OF_ROW; row++) {
            for (int col = 0; col < NUM_OF_COL; col++) {
                inactiveBlocksArray[row][col] = null;
            }
        }

        // !!!!!! gameScreen.restartGameForSprint(gameMetrics.getBestTime()); MUST COME BEFORE minoManager.restartMinoManager();
        gameScreen.restartGameForSprint(gameMetrics.getBestTime(currSprintMode), gameMetrics.getSprintGoal());

        // reset minos and draw them accordingly to their new position!
        minoManager.restartMinoManager();
    }
    public void restartMinoManager() {
        minoManager.restartMinoManager();
    }

    public void exitGame() {
        if (gameState.getGameMode() == GameMode.SPRINT) {
            exitGameForSprint();
        } else {
            exitGameForDefault();
        }
    }
    public void exitGameForDefault() {
        gameState.restartGame();

        // clear inactive blocks array
        for (int row = 0; row < NUM_OF_ROW; row++) {
            for (int col = 0; col < NUM_OF_COL; col++) {
                inactiveBlocksArray[row][col] = null;
            }
        }

        GameMode gameMode = gameState.getGameMode();
        // !!!!!! gameScreen.restartGame(gameMetrics.getHighScore(gameMode)); MUST COME BEFORE minoManager.restartMinoManager();

        gameMetrics.setHighScore(gameMode); // set high score first
        gameMetrics.resetScoreAndLines(); // then reset current score
    }
    public void exitGameForSprint() {
        int currGameCounterVal = gameState.getGameCounterVal(); // DO THIS BEFORE gameState.restart() !!!
        boolean isSprintOver = gameState.isSprintOver();
        SprintMode currSprintMode = gameState.getSprintMode();

        // only set best time if the game is fully COMPLETED
        if (isSprintOver) {
            gameMetrics.setBestTime(currGameCounterVal, currSprintMode);
        }

        gameMetrics.resetScoreAndLines();
        gameState.restartGame();

        // clear inactive blocks array
        for (int row = 0; row < NUM_OF_ROW; row++) {
            for (int col = 0; col < NUM_OF_COL; col++) {
                inactiveBlocksArray[row][col] = null;
            }
        }
    }

    // =====================================
    // Getters and Setters
    // =====================================

    public ActiveStateManager getActiveStateManager() {
        return activeStateManager;
    }
    public int getHighScore() {
        return gameMetrics.getHighScore(gameState.getGameMode());
    }
    public int getBestTime() {
        SprintMode currSprintMode = gameState.getSprintMode();
        return gameMetrics.getBestTime(currSprintMode);
    }
    public int getSprintGoal() {
        return gameMetrics.getSprintGoal();
    }
    public void setSprintGoal(int sprintGoal) {
        this.gameMetrics.setSprintGoal(sprintGoal);
    }
}
