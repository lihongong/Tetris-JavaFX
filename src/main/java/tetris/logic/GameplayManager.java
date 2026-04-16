package tetris.logic;

import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.ui.GameScreen;
import tetris.util.GameMode;

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
                //***updateWhenMinoIsActiveOnly();
                activeStateManager.update();
            } else {
                // current mino is active (next round) but the effect is still going on
                // don't proceed the game (by not calling updateWhenMinoIsActiveOnly()) until the effect is over
                //System.out.println("HANDLING SPECIAL EFFECT");
                handleClearLineSpecialEffect();
            }
        } else {
            // ****updateWhenMinoIsInactiveOnly();
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

    public ActiveStateManager getActiveStateManager() {
        return activeStateManager;
    }

    public void restartGame() {
        gameState.restartGame();
        // clear inactive blocks array
        for (int row = 0; row < NUM_OF_ROW; row++) {
            for (int col = 0; col < NUM_OF_COL; col++) {
                inactiveBlocksArray[row][col] = null;
            }
        }
        gameScreen.restartGame();

        minoManager.restartMinoManager();

        GameMode gameMode = gameState.getGameMode();
        gameMetrics.setHighScore(gameMode); // set high score first
        gameMetrics.resetScore(); // then reset current score
        gameScreen.updateScore(gameMetrics.getScore());
        gameScreen.updateHighScore(gameMetrics.getHighScore(gameMode));
    }
}
