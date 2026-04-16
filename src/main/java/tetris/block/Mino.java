package tetris.block;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import tetris.logic.GameState;
import tetris.util.Copyable;

import static tetris.util.TetrisConstants.*;

public abstract class Mino implements Copyable<Mino> {
    public MinoBlock[] blocks;
    public ShadowBlock[] shadowBlocks;
    public GhostBlock[] ghostBlocks;
    public int direction = 1;  // 1, 2, 3, 4 directions
    public boolean leftCollision, rightCollision, bottomCollision;
    private boolean isActive;
    private Color color;
    private Color shadowColor;
    private int autoDropCounter;  // move the mino down when autoDropCounter hits dropInterval

    /**
     * Is invoked by the constructor of Mino classes that inherits this class
     */
    public void create(Color color) {
        this.color = color;
        this.shadowColor = color;
        this.isActive = true;

        blocks = new MinoBlock[NUM_OF_BLOCKS_PER_MINO];
        shadowBlocks = new ShadowBlock[NUM_OF_BLOCKS_PER_MINO];
        ghostBlocks = new GhostBlock[NUM_OF_BLOCKS_PER_MINO];

        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            blocks[i] = new MinoBlock(color);

            shadowBlocks[i] = new ShadowBlock(color);

            ghostBlocks[i] = new GhostBlock();
        }
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * set {@code isActive} to false and remove the shadow blocks from playing field pane
     */
    public void deactivate(Pane playingField) { //  this method is not used anymore liao !!!!!
        isActive = false;
    }
    public void deactivate() {
        isActive = false;
    }

    // =================================================
    // Mino movements
    // =================================================

    public void moveUp() {
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            blocks[i].moveUp();
        }
    }
    public void moveDown() {
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            blocks[i].moveDown();
        }
    }
    public void moveLeft() {
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            blocks[i].moveLeft();
        }
    }
    public void moveRight() {
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            blocks[i].moveRight();
        }
    }

    // =================================================
    // Mino rotations
    // =================================================

    /**
     * Tries to rotate the mino.
     *
     * <p>After ghost block has been set up, if the position of the mino overlap with other inactive blocks or exceed
     * boundary, this method will try to find an alternate position to "snap" the mino in place. If no alternate
     * position can be found, the mino won't be rotated.
     * Try snapping it to the left, right, bottom, bottom left, bottom right, top left and top right.</>
     */
    /*public void tryRotatingMino(Block[][] inactiveBlockArray, TetrisManager tetrisManager) {
        int altPosition = 1;
        boolean canRotate = true;

        while (isAlternatePositionValid(inactiveBlockArray)) {
            if (altPosition == 1) { // push left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 2) { // push right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
                    ghostBlocks[i].col += 1;
                }
                altPosition++;
            } else if (altPosition == 3) { // push down
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                    ghostBlocks[i].row += 1;
                }
                altPosition++;
            } else if (altPosition == 4) { // push down left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 5) { // push down right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
                    ghostBlocks[i].col += 1;
                }
                altPosition++;
            } else if (altPosition == 6) { // push up left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].row -= 1;
                    ghostBlocks[i].row -= 1;
                    ghostBlocks[i].col -= 1;
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 7) { // push up right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
                    ghostBlocks[i].col += 1;
                }
                altPosition++;
            } else {
                // Don't push up because it will cause infinite falling
                // Reset to original position
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                    ghostBlocks[i].row += 1;
                    // altPosition++;
                }
                canRotate = false;
                break;
            }
        }
        if (canRotate) {
            int numOfDirections = 4;
            this.direction = this.direction < numOfDirections ? this.direction + 1 : 1;
            this.setRotation();

            // Mino is pushed upwards, so need to reset deactivation
            if (altPosition == 7 || altPosition == 8) {
                gameController.resetDeactivation();
            }
        }
    }*/
    // copy version for new refactoring
    public void tryRotatingMino(Block[][] inactiveBlockArray, GameState gameState) {
        int altPosition = 1;
        boolean canRotate = true;

        while (isAlternatePositionValid(inactiveBlockArray)) {
            if (altPosition == 1) { // push left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 2) { // push right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
                    ghostBlocks[i].col += 1;
                }
                altPosition++;
            } else if (altPosition == 3) { // push down
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                    ghostBlocks[i].row += 1;
                }
                altPosition++;
            } else if (altPosition == 4) { // push down left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 5) { // push down right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
                    ghostBlocks[i].col += 1;
                }
                altPosition++;
            } else if (altPosition == 6) { // push up left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].row -= 1;
                    ghostBlocks[i].row -= 1;
                    ghostBlocks[i].col -= 1;
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 7) { // push up right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
                    ghostBlocks[i].col += 1;
                }
                altPosition++;
            } else {
                // Don't push up because it will cause infinite falling
                // Reset to original position
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                    ghostBlocks[i].row += 1;
                    // altPosition++;
                }
                canRotate = false;
                break;
            }
        }
        if (canRotate) {
            int numOfDirections = 4;
            this.direction = this.direction < numOfDirections ? this.direction + 1 : 1;
            this.setRotation();

            // Mino is pushed upwards, so need to reset deactivation
            if (altPosition == 7 || altPosition == 8) {
                gameState.resetDeactivation();
            }
        }
    }

    /**
     * Checks if the rotated mino will exceed boundary or overlap with other inactive blocks.
     */
    public boolean isAlternatePositionValid(Block[][] inactiveBlockArray) {
        assert inactiveBlockArray.length == NUM_OF_ROW;
        assert inactiveBlockArray[0].length == NUM_OF_COL;

        for (GhostBlock ghostBlock : ghostBlocks) {
            int row = ghostBlock.row;
            int col = ghostBlock.col;
            // exceed playing field left boundary
            if (col < LEFTMOST_X) {
                return true;
            }
            // exceed playing field right boundary
            if (col > RIGHTMOST_X) {
                return true;
            }
            // exceed playing field bottom boundary
            if (row > BOTTOMMOST_Y) {
                return true;
            }
            // overlap with inactive block
            if (inactiveBlockArray[row][col] != null) {
                return true;
            }
        }
        return false;
    }
    /**
     * Copies the position of the ghost blocks to the blocks.
     */
    public void setRotation() {
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            int row = ghostBlocks[i].row;
            int col = ghostBlocks[i].col;

            int pixelY = row * BLOCK_SIZE;
            int pixelX = col * BLOCK_SIZE;
            blocks[i].setPosition(pixelX, pixelY, col, row);
        }
    }
    public void setShadowPosition(Block[][] inactiveBlockArray) {
        for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
            shadowBlocks[i].copyPosition(blocks[i]);
        }
        while(!isBottomCollision(inactiveBlockArray)) {
            for (Block shadowBlock : shadowBlocks) {
                shadowBlock.moveDown();
            }
        }
    }
    /**
     * Is used for shadow positioning.
     */
    private boolean isBottomCollision(Block[][] inactiveBlockArray) {
        for (Block shadowBlock : shadowBlocks) {
            int shadowRow = shadowBlock.getRow();
            int shadowCol = shadowBlock.getCol();
            if (shadowRow >= BOTTOMMOST_Y) {
                return true;
            }
            // Bottom is another inactive block
            if (inactiveBlockArray[shadowRow + 1][shadowCol] != null) {
                return true;
            }
        }
        return false;
    }

    // =================================================
    // mino position checking
    // =================================================

    /**
     * Checks if there is any inactive block on top of the T mino.
     * This method is called when clearing a line.
     * @return false for non-T-shape minos, for T-shape mino, return true if there is any inactive block on top of it,
     * else return false.
     */
    public boolean checkTSpinConfiguration(Block[][] inactiveBlocks) {
        return false; // overriden by MinoT
    }

    /**
     * Checks if the mino is at the starting position.
     * <p>This method is invoked when the mino has touched down to see if it is already game over (touch down but still
     * at starting position).</p>
     */
    public boolean isAtStartingPosition() {
        // every mino's block0 will always be at MINO_START_X, MINO_START_Y position at the beginning
        return blocks[0].getRow() == MINO_START_Y && blocks[0].getCol() == MINO_START_X;
    }

    // =================================================
    // Abstract methods
    // =================================================
    public abstract void setPlayingFieldStartPosition();
    public abstract void setNextAndHoldBoxPosition();
    /**
     * set the ghost blocks to the 1st direction configuration.
     */
    public abstract void getD1();
    /**
     * set the ghost blocks to the 2nd direction configuration.
     */
    public abstract void getD2();
    /**
     * set the ghost blocks to the 3rd direction configuration.
     */
    public abstract void getD3();
    /**
     * set the ghost blocks to the 4th direction configuration.
     */
    public abstract void getD4();

}
