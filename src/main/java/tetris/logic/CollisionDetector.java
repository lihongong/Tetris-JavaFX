package tetris.logic;

import tetris.block.Block;
import tetris.block.Mino;

import static tetris.util.TetrisConstants.*;
import static tetris.util.TetrisConstants.RIGHTMOST_X;

public class CollisionDetector {
    private boolean leftCollision;
    private boolean rightCollision;
    private boolean bottomCollision;
    private Block[][] inactiveBlocksArray;

    public CollisionDetector(Block[][] inactiveBlocksArray) {
        this.inactiveBlocksArray = inactiveBlocksArray;
    }

    public boolean hasLeftCollision() {
        return leftCollision;
    }
    public boolean hasRightCollision() {
        return rightCollision;
    }
    public boolean hasBottomCollision() {
        return bottomCollision;
    }

    public void checkCurrentMinoMovementCollision(Mino currentMino) {
        leftCollision = rightCollision = bottomCollision = false;
        for (Block block : currentMino.blocks) {
            // check if the mino collides with the border
            isBorderCollision(block);
            // check if the mino collides with inactive blocks
            isLeftBlocked(block);
            isBottomBlocked(block);
            isRightBlocked(block);
        }
    }

    public void isBorderCollision(Block block) {
        assert block != null;

        int row = block.getRow();
        int col = block.getCol();
        if (col <= LEFTMOST_X) {
            leftCollision = true;
        }
        if (col >= RIGHTMOST_X) {
            rightCollision = true;
        }
        if (row >= BOTTOMMOST_Y) {
            bottomCollision = true;
        }
    }
    /**
     * Checks if the left side of the block is an inactive block.
     */
    public void isLeftBlocked(Block block) {
        assert block != null;

        int row = block.getRow();
        int col = block.getCol();
        if (col <= LEFTMOST_X || inactiveBlocksArray[row][col - 1] != null) {
            leftCollision = true;
        }
    }
    /**
     * Checks if the bottom of the block is an inactive block.
     */
    public void isBottomBlocked(Block block) {
        assert block != null;

        int row = block.getRow();
        int col = block.getCol();
        if (row >= BOTTOMMOST_Y || inactiveBlocksArray[row + 1][col] != null) {
            bottomCollision = true;
        }
    }
    /**
     * Checks if the right side of the block is an inactive block.
     */
    public void isRightBlocked(Block block) {
        assert block != null;

        int row = block.getRow();
        int col = block.getCol();
        if (col >= RIGHTMOST_X || inactiveBlocksArray[row][col + 1] != null) {
            rightCollision = true;
        }
    }
}
