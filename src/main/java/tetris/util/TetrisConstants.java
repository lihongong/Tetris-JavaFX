package tetris.util;

import javafx.scene.paint.Color;

public class TetrisConstants {

    // =================================================
    // Mino and Block
    // =================================================

    public static final int BLOCK_SIZE = 30;
    public static final int NUM_OF_BLOCKS_PER_MINO = 4;

    // =================================================
    // Playing Field
    // =================================================

    public static final int NUM_OF_ROW = 20;
    public static final int PLAYING_FIELD_HEIGHT = NUM_OF_ROW * BLOCK_SIZE;
    public static final int NUM_OF_COL = 10;
    public static final int PLAYING_FIELD_WIDTH = NUM_OF_COL * BLOCK_SIZE;
    public static final int LEFTMOST_PIXEL = 0;
    public static final int LEFTMOST_X = 0;
    public static final int RIGHTMOST_PIXEL = NUM_OF_COL * BLOCK_SIZE - 1;
    public static final int RIGHTMOST_X = NUM_OF_COL - 1;
    public static final int TOPMOST_PIXEL = 0;
    public static final int TOPMOST_Y = 0;
    public static final int BOTTOMMOST_PIXEL = NUM_OF_ROW * BLOCK_SIZE - 1;
    public static final int BOTTOMMOST_Y = NUM_OF_ROW - 1;

    // =================================================
    // Other game layout
    // =================================================

    public static final int NEXT_BOX_HEIGHT_WIDTH = 170;
    public static final int HOLD_BOX_HEIGHT_WIDTH = 170;

    // =================================================
    // Mino positioning
    // =================================================

    public static final int MINO_START_PIXEL_X = PLAYING_FIELD_WIDTH / 2 - BLOCK_SIZE;
    public static final int MINO_START_PIXEL_Y = BLOCK_SIZE;
    public static final int MINO_START_X = NUM_OF_COL / 2 - 1;  // start from the fifth column
    public static final int MINO_START_Y = 1;  // start from the second row

    // =================================================
    // Timing
    // =================================================

    public static final int FPS = 120;
    public static final int DEACTIVATE_INTERVAL = FPS;
    public static final int SPECIAL_EFFECT_DURATION = 30;
    public static final int AUTO_DROP_INTERVAL = 120 * 3 / 4; // adjust the dropping speed of minos
    public static final int BLOCK_FADING_DURATION = 7; // in millisecond, around 8 frames when FPS is 120
    public static final int BLOCK_FALLING_DURATION = 8; // in millisecond, around 8 frames when FPS is 120
    public static final int T_SPIN_DURATION = 30;
    public static final int TWO_MINUTE_DURATION = FPS * 120;

    // =================================================
    // Colors and Designs
    // =================================================
    public static final Color PLAYING_FIELD_GRID_LIGHT_GREY = Color.rgb(30, 30, 30, 0.75);
    public static final Color PLAYING_FIELD_GRID_GREY = Color.rgb(16, 16, 16, 0.75);

}
