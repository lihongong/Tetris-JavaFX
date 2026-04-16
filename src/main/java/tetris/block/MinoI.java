package tetris.block;

import javafx.scene.paint.Color;
import tetris.logic.GameState;

import static tetris.util.TetrisConstants.*;

public class MinoI extends Mino {
    public static final int MINO_I_NEXT_PIXEL_X = NEXT_BOX_HEIGHT_WIDTH / 2;
    public static final int MINO_I_NEXT_PIXEL_Y = NEXT_BOX_HEIGHT_WIDTH / 2;
    public MinoI() {
        super();
        create(Color.CYAN);
    }
    @Override
    public void setPlayingFieldStartPosition() {
        // make sure to set the direction to 1 (or else it will appear to be in the direction 1 but the direction is not 1)
        direction = 1;
        blocks[0].setPosition(MINO_START_PIXEL_X, MINO_START_PIXEL_Y,
                MINO_START_X, MINO_START_Y);
        blocks[1].setPosition(MINO_START_PIXEL_X - BLOCK_SIZE, MINO_START_PIXEL_Y,
                MINO_START_X - 1, MINO_START_Y);
        blocks[2].setPosition(MINO_START_PIXEL_X + BLOCK_SIZE, MINO_START_PIXEL_Y,
                MINO_START_X + 1, MINO_START_Y);
        blocks[3].setPosition(MINO_START_PIXEL_X + 2 * BLOCK_SIZE, MINO_START_PIXEL_Y,
                MINO_START_X + 2, MINO_START_Y);
    }
    @Override
    public void setNextAndHoldBoxPosition() {
        int deltaX = - BLOCK_SIZE;
        int deltaY = - BLOCK_SIZE / 2;
        blocks[0].setRectangleCoor(MINO_I_NEXT_PIXEL_X + deltaX,
                MINO_I_NEXT_PIXEL_Y + deltaY);
        blocks[1].setRectangleCoor(MINO_I_NEXT_PIXEL_X - BLOCK_SIZE + deltaX,
                MINO_I_NEXT_PIXEL_Y + deltaY);
        blocks[2].setRectangleCoor(MINO_I_NEXT_PIXEL_X + BLOCK_SIZE + deltaX,
                MINO_I_NEXT_PIXEL_Y + deltaY);
        blocks[3].setRectangleCoor(MINO_I_NEXT_PIXEL_X + 2 * BLOCK_SIZE + deltaX,
                MINO_I_NEXT_PIXEL_Y + deltaY);

        // Don't need to set the row & col
    }
    @Override
    public void getD1() {
        ghostBlocks[0].setColRow(blocks[0].getCol(), blocks[0].getRow());
        ghostBlocks[1].setColRow(blocks[0].getCol() - 1, blocks[0].getRow());
        ghostBlocks[2].setColRow(blocks[0].getCol() + 1, blocks[0].getRow());
        ghostBlocks[3].setColRow(blocks[0].getCol() + 2, blocks[0].getRow());
    }
    @Override // Rotate
    public void getD2() {
        ghostBlocks[0].setColRow(blocks[0].getCol(), blocks[0].getRow());
        ghostBlocks[1].setColRow(blocks[0].getCol(), blocks[0].getRow() - 1);
        ghostBlocks[2].setColRow(blocks[0].getCol(), blocks[0].getRow() + 1);
        ghostBlocks[3].setColRow(blocks[0].getCol(), blocks[0].getRow() + 2);
    }
    @Override // Rotate
    public void getD3() {
        getD1();
    }
    @Override // Rotate
    public void getD4() {
        getD2();
    }
    @Override
    public MinoI copy() {
        return new MinoI();
    }
    @Override
    public void tryRotatingMino(Block[][] inactiveBlockArray, GameState gameState) {
        int altPosition = 0;
        boolean canRotate = true;

        while (isAlternatePositionValid(inactiveBlockArray)) {
            if (altPosition == 0) { // push left
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 1) { // push left twice
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col -= 1;
                }
                altPosition++;
            } else if (altPosition == 2) { // push right
                for (int i = 0; i < NUM_OF_BLOCKS_PER_MINO; i++) {
                    ghostBlocks[i].col += 1;
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
}
