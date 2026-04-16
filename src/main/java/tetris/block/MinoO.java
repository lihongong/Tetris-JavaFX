package tetris.block;

import javafx.scene.paint.Color;
import tetris.logic.GameState;

import static tetris.util.TetrisConstants.*;

public class MinoO extends Mino {
    public static final int MINO_O_NEXT_PIXEL_X = NEXT_BOX_HEIGHT_WIDTH / 2;
    public static final int MINO_O_NEXT_PIXEL_Y = NEXT_BOX_HEIGHT_WIDTH / 2;
    public MinoO() {
        super();
        create(Color.YELLOW);
    }
    @Override
    public void setPlayingFieldStartPosition() {
        // make sure to set the direction to 1 (or else it will appear to be in the direction 1 but the direction is not 1)
        direction = 1;
        blocks[0].setPosition(MINO_START_PIXEL_X, MINO_START_PIXEL_Y,
                MINO_START_X, MINO_START_Y);
        blocks[1].setPosition(MINO_START_PIXEL_X, MINO_START_PIXEL_Y - BLOCK_SIZE,
                MINO_START_X, MINO_START_Y - 1);
        blocks[2].setPosition(MINO_START_PIXEL_X + BLOCK_SIZE, MINO_START_PIXEL_Y,
                MINO_START_X + 1, MINO_START_Y);
        blocks[3].setPosition(MINO_START_PIXEL_X + BLOCK_SIZE, MINO_START_PIXEL_Y - BLOCK_SIZE,
                MINO_START_X + 1, MINO_START_Y - 1);
    }
    @Override
    public void setNextAndHoldBoxPosition() {
        int deltaX = - BLOCK_SIZE;
        int deltaY = 0;
        blocks[0].setRectangleCoor(MINO_O_NEXT_PIXEL_X + deltaX,
                MINO_O_NEXT_PIXEL_Y + deltaY);
        blocks[1].setRectangleCoor(MINO_O_NEXT_PIXEL_X + deltaX,
                MINO_O_NEXT_PIXEL_Y - BLOCK_SIZE + deltaY);
        blocks[2].setRectangleCoor(MINO_O_NEXT_PIXEL_X + BLOCK_SIZE + deltaX,
                MINO_O_NEXT_PIXEL_Y + deltaY);
        blocks[3].setRectangleCoor(MINO_O_NEXT_PIXEL_X + BLOCK_SIZE + deltaX,
                MINO_O_NEXT_PIXEL_Y - BLOCK_SIZE + deltaY);

        // Don't need to set the row & col
    }
    @Override
    public void getD1() {
    }
    @Override // Rotate
    public void getD2() {
    }
    @Override // Rotate
    public void getD3() {
    }
    @Override // Rotate
    public void getD4() {
    }
    @Override
    public MinoO copy() {
        return new MinoO();
    }
    @Override
    public void tryRotatingMino(Block[][] inactiveBlockArray, GameState gameState) {
        // Do nothing, Square mino can't be rotated
        if (direction < 4) {
            direction++;
        } else {
            direction = 1;
        }
    }
}
