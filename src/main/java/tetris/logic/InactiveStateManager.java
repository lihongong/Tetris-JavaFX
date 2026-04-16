package tetris.logic;

import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.ui.GameScreen;

import static tetris.util.TetrisConstants.*;
import static tetris.util.TetrisConstants.NUM_OF_COL;

public class InactiveStateManager {
    private GameState gameState;
    private GameScreen gameScreen;
    private MinoManager minoManager;
    private MinoBlock[][] inactiveBlocksArray;
    private GameMetrics gameMetrics;

    public InactiveStateManager(GameState gameState, GameScreen gameScreen, MinoManager minoManager, MinoBlock[][] inactiveBlocksArray, GameMetrics gameMetrics) {
        this.gameState = gameState;
        this.gameScreen = gameScreen;
        this.minoManager = minoManager;
        this.inactiveBlocksArray = inactiveBlocksArray;
        this.gameMetrics = gameMetrics;
    }

    public void update() {
        this.handleRemoveLine();

        if (this.checkGameOver()) {
            gameState.setGameOver();
            return;
        }

        minoManager.setNewCurrentNextMino();

        gameState.enableSwapMino();

        // TODO: combo and soundeffect
    }

    public boolean checkGameOver() {
        Mino currentMino = minoManager.getCurrentMino();
        if (currentMino.isAtStartingPosition()) {
            return true;
        }
        return false;
    }

    /**
     * Starts from the lowest row to the top, if there is a full row (10 blocks in a row), remove it from the
     * playing field, else drop the inactive blocks on top to fill in the gap of the deleted row.
     */
    private void handleRemoveLine() {
        gameScreen.clearAllShadowInPlayingField(); // safety measures :)

        int numLinesClear = 0;
        boolean gotLineRemoval = false;

        Mino currentMino = minoManager.getCurrentMino();

        // check if it is potentially a t spin before adding current mino into inactive block array
        boolean isPotentialTSpin = currentMino.checkTSpinConfiguration(inactiveBlocksArray);

        // add the current mino blocks into the inactive block array
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            int row = currentMino.blocks[i].getRow();
            int col = currentMino.blocks[i].getCol();
            inactiveBlocksArray[row][col] = currentMino.blocks[i];
        }

        for (int r = NUM_OF_ROW - 1; r >= 0; r--) {
            int numBlocksInARow = 0;
            for (int c = 0; c < NUM_OF_COL; c++) {
                if (inactiveBlocksArray[r][c] != null) {
                    numBlocksInARow++;
                }
            }
            // remove line
            if (numBlocksInARow == NUM_OF_COL) {
                gotLineRemoval = true;
                numLinesClear++;
                for (int c = 0; c < inactiveBlocksArray[0].length; c++) {
                    MinoBlock toBeRemovedBlock = inactiveBlocksArray[r][c];
                    if (toBeRemovedBlock != null) {
                        gameScreen.addFadingBlock(toBeRemovedBlock);
                        inactiveBlocksArray[r][c] = null;
                    }
                }
            } else {
                for (int c = 0; c < inactiveBlocksArray[0].length; c++) {
                    MinoBlock fallingBlock = inactiveBlocksArray[r][c];
                    if (fallingBlock != null && gotLineRemoval) {
                        fallingBlock.dropImmediately(numLinesClear);
                        gameScreen.addFallingBlock(fallingBlock, numLinesClear);
                        inactiveBlocksArray[r][c] = null;
                        inactiveBlocksArray[r + numLinesClear][c] = fallingBlock;
                    }
                }
            }
        }
        // after scanning through the board
        if (gotLineRemoval) {
            gameState.turnOnEffect();
            // TODO: sound effect based on numLinesClear
        }
        if (gotLineRemoval && isPotentialTSpin) {
            gameState.setIsTSpin();
            // TODO: t spin sound effect
        }

        gameMetrics.updateScore(numLinesClear, gameState.isTSpin());
        gameScreen.updateScore(gameMetrics.getScore());

    }
}
