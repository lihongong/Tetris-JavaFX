package tetris.block;

import javafx.scene.canvas.GraphicsContext;

import static tetris.util.TetrisConstants.BLOCK_SIZE;


public abstract class Block {
    protected int row;  // row on the 20x10 matrix playing field
    protected int col;  // column on the 20x10 matrix playing field
    public int pixelX; // X coordinate for the rectangle position in the UI
    public int pixelY; // Y coordinate for the rectangle position in the UI
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }

    // Might be used in the future for auto-fitting
    public void moveUp() {
        this.row--;
        this.pixelY -= BLOCK_SIZE;
    }
    public void moveDown() {
        this.row++;
        this.pixelY += BLOCK_SIZE;
    }
    public void moveLeft() {
        this.col--;
        this.pixelX -= BLOCK_SIZE;
    }
    public void moveRight() {
        this.col++;
        this.pixelX += BLOCK_SIZE;
    }

    public void setPosition(int pixelX, int pixelY, int col, int row) {
        this.pixelX = pixelX;
        this.pixelY = pixelY;

        this.col = col;
        this.row = row;
    }
    public void setRectangleCoor(int pixelX, int pixelY) {
        this.pixelX = pixelX;
        this.pixelY = pixelY;
    }
    /**
     * Copy the {@code row}, {@code col}, {@code pixelX}, {@code pixelY} and rectangle's position.
     */
    public void copyPosition(Block toBeCopyBlock) {
        this.row = toBeCopyBlock.row;
        this.col = toBeCopyBlock.col;
        this.pixelX = toBeCopyBlock.pixelX;
        this.pixelY = toBeCopyBlock.pixelY;
    }
    public void dropImmediately(int numLineDrop) {
        row += numLineDrop;
    }

    public abstract void drawRemove(GraphicsContext gc);
    public abstract void drawAdd(GraphicsContext gc);
}
