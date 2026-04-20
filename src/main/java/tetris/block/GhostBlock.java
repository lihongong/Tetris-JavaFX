package tetris.block;

/**
 * Represents the possible block position after rotating and snapping the mino into place.
 * This block isn't visible in UI and is a light-weight Block class.
 */
public class GhostBlock {
    /**
     * row and col may exceed the playing field boundary (0, 9).
     * It is used to predict the position of the Mino after rotation.
     * Made public for easy access.
     */
    public int row;  // row on the 20x10 matrix playing field
    public int col;  // column on the 20x10 matrix playing field
    public void setColRow(int col, int row) {
        this.col = col;
        this.row = row;
    }
}
