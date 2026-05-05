package tetris.logic;

import tetris.audio.AudioManager;
import tetris.audio.SoundType;
import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.ui.GameScreen;
import tetris.util.GameMode;

import static tetris.util.TetrisConstants.*;
import static tetris.util.TetrisConstants.NUM_OF_COL;

public class InactiveStateManager {
    private final GameState gameState;
    private final GameScreen gameScreen;
    private final MinoManager minoManager;
    private final MinoBlock[][] inactiveBlocksArray;
    private final GameMetrics gameMetrics;

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

        // doing this will make the currentMino = nextMino -> currentMino.isActive() is True
        // Depends on isEffectsOn(), gameplayManager.update() will either run
        // 1) activeStateManager.update(); OR 2) handleClearLineSpecialEffect();
        minoManager.setNewCurrentNextMino();

        gameState.enableSwapMino();

        // TODO: block touch down soundeffect
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

        // -------------------------------------------------------- :)
        // After checking thru the board to check for lines cleared

        // invoked even if no line is cleared
        // passing 0 line clear into updateScore() will reset gameMetrics combo count
        gameMetrics.updateScore(numLinesClear, gameState.isTSpin());

        if (gotLineRemoval) {
            gameState.turnOnEffect();
            // TODO: sound effect based on numLinesClear & combo effects
            gameScreen.popComboEffect(gameMetrics.getCombo());

            // if in SprintMode, make GameScreen increase the LineMeterTube Height !!!
            if (gameState.getGameMode() == GameMode.SPRINT) {
                gameState.checkSprintOver(gameMetrics.getNumLinesClear(), gameMetrics.getSprintGoal());

                gameScreen.setSprintLineMeterTubeProgress(gameMetrics.getNumLinesClear(), gameMetrics.getSprintGoal());
                gameScreen.updateGameMetricsForSprintMode(gameMetrics.getNumLinesClear(), gameMetrics.getSprintGoal());
            } else {
                gameScreen.updateGameMetricsForDefault(gameMetrics.getNumLinesClear(), gameMetrics.getScore());
            }

            if (isPotentialTSpin) {
                gameState.setIsTSpin();
            }

            // Clear line & Combo & Tspin Sound Effect -- if there is line clear
            playClearSoundEffect(numLinesClear, gameMetrics.getCombo(), isPotentialTSpin);
        }
    }
    private void playClearSoundEffect(int numLinesClear, int combo, boolean isTSpin) {
        // clear 1-4 lines sound
        SoundType lineRemoveSoundType = SoundType.fromLinesRemoved(numLinesClear);
        if (lineRemoveSoundType != null) { // if numlines cleared is > 4 or < 0, play nothing (safety net)
            AudioManager.getInstance().playSfx(lineRemoveSoundType);
        }
        // combo 1-7 sound
        SoundType comboSoundType = SoundType.fromCombo(combo);
        if (comboSoundType != null) {
            AudioManager.getInstance().playSfx(comboSoundType);
        }
        // is TSpin sound
        if (isTSpin) {
            AudioManager.getInstance().playSfx(SoundType.T_SPIN);
        }
    }
}
