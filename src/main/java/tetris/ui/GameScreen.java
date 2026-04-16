package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tetris.block.Block;
import tetris.block.Mino;
import tetris.block.MinoBlock;

import javax.swing.*;
import java.util.ArrayList;

import static tetris.util.TetrisConstants.*;
import static tetris.util.TetrisConstants.PLAYING_FIELD_HEIGHT;

public class GameScreen extends UiPart<VBox> {

    private static final String FXML = "GameScreen.fxml";
    private static final String backgroundImagePath = "/images/gameplay_backgrounds/";
    private BackgroundImageSelector backgroundImageSelector;

    @FXML
    private VBox mainLayout;
    @FXML
    private Pane playingField;
    @FXML
    private Pane nextMinoBox;
    @FXML
    private Pane holdMinoBox;
    @FXML
    private Label score;
    @FXML
    private Label highScore;
    @FXML
    private Label timer;
    @FXML
    private ProgressBar timerBar;

    // Graphic contexts
    private GraphicsContext playingFieldGC; // Draw the grid background in the playing field
    private GraphicsContext nextBoxGC; // Draw the minos in the next box
    private GraphicsContext holdBoxGC; // Draw the minos in the hold box
    private GraphicsContext blockGC; // Draw the minos and blocks in the playing field
    private GraphicsContext shadowGC; // Draw the minos' shadow in the playing field
    private GraphicsContext specialEffectGC;

    // Keeps data for animation
    private ArrayList<MinoBlock> fadingBlocks;
    private ArrayList<MinoBlock> fallingBlocks;
    private ArrayList<Integer> numLinesFallList;

    public Parent root;

    public GaussianBlur gaussianBlur = new GaussianBlur(10.0);

    // =================================================
    // Set up UI
    // =================================================

    public GameScreen() {
        super(FXML);
        setInitialBackGroundImage();
        fillInnerParts();
    }

    private void setInitialBackGroundImage() {
        backgroundImageSelector = new BackgroundImageSelector(backgroundImagePath);
        backgroundImageSelector.setRandomBackgroundImage(getRoot()); // apply background to the root (mainLayout (HBox))
    }

    public void setRandomBackGroundImage() {
        backgroundImageSelector.setRandomBackgroundImage(getRoot()); // apply background to the root (mainLayout (HBox))
    }

    private void fillInnerParts() {

        // Draw the grid background in the playing field
        Canvas playingFieldCanvas = new Canvas(PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        playingFieldGC = playingFieldCanvas.getGraphicsContext2D();

        // Draw the minos in the next box
        Canvas nextBoxCanvas = new Canvas(NEXT_BOX_HEIGHT_WIDTH, NEXT_BOX_HEIGHT_WIDTH);
        nextBoxGC = nextBoxCanvas.getGraphicsContext2D();

        // Draw the minos in the hold box
        Canvas holdBoxCanvas = new Canvas(HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);
        holdBoxGC = holdBoxCanvas.getGraphicsContext2D();

        // Draw the minos and blocks in the playing field
        Canvas blockCanvas = new Canvas(PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        blockGC = blockCanvas.getGraphicsContext2D();
        blockCanvas.getGraphicsContext2D().setImageSmoothing(false); // disable anti-aliasing

        // Draw the minos' shadow in the playing field
        Canvas shadowCanvas = new Canvas(PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        shadowGC = shadowCanvas.getGraphicsContext2D();
        shadowCanvas.getGraphicsContext2D().setImageSmoothing(false); // disable anti-aliasing

        // Special effect
        Canvas specialEffectCanvas = new Canvas(PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        specialEffectGC = specialEffectCanvas.getGraphicsContext2D();
        specialEffectCanvas.getGraphicsContext2D().setImageSmoothing(false); // disable anti-aliasing

        // follow the hierarchical order of drawing: playing field grid -> shadow -> mino -> special effect
        playingField.getChildren().add(playingFieldCanvas);
        playingField.getChildren().add(shadowCanvas);
        playingField.getChildren().add(blockCanvas);
        playingField.getChildren().add(specialEffectCanvas);

        nextMinoBox.getChildren().add(nextBoxCanvas);
        holdMinoBox.getChildren().add(holdBoxCanvas);

        // keeps the blocks for animation (fading and falling)
        fadingBlocks = new ArrayList<>();
        fallingBlocks = new ArrayList<>();
        numLinesFallList = new ArrayList<>();

        // score label
        updateScore(0);
        updateHighScore(0);

        drawPlayingFieldGrid();
    }


    // =================================================
    // Drawing
    // =================================================

    public void drawPlayingFieldGrid() {
        int margin = 2;
        int trueSize = BLOCK_SIZE - 2 * margin;
        int gridCount = 0;

        for (int pixelY = TOPMOST_Y; pixelY < PLAYING_FIELD_HEIGHT; pixelY += BLOCK_SIZE) {
            gridCount++;  // trick the counter (odd even switch)

            for (int pixelX = LEFTMOST_PIXEL; pixelX < PLAYING_FIELD_WIDTH; pixelX += BLOCK_SIZE) {
                if (gridCount % 2 == 1) {
                    playingFieldGC.setFill(PLAYING_FIELD_GRID_LIGHT_GREY);
                } else {
                    playingFieldGC.setFill(PLAYING_FIELD_GRID_GREY);
                }
                playingFieldGC.fillRect(pixelX + margin, pixelY + margin, trueSize, trueSize);
                gridCount++;
            }
        }
    }

    public void addMinoInPlayingField(Mino mino) {
        for (Block block : mino.blocks) {
            block.drawAdd(blockGC);
        }
    }
    public void removeMinoInPlayingField(Mino mino) {
        for (Block block : mino.blocks) {
            block.drawRemove(blockGC);
        }
    }
    public void addMinoShadowInPlayingField(Mino mino) {
        for (Block block : mino.shadowBlocks) {
            block.drawAdd(shadowGC);
        }
    }
    public void removeMinoShadowInPlayingField(Mino mino) {
        //shadowGC.clearRect(LEFTMOST_PIXEL, RIGHTMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);

        for (Block block : mino.shadowBlocks) {
            block.drawRemove(shadowGC);
        }
    }

    /**
     * Invoked when mino is deactivated to prevent bug (shadow not being removed in UI).
     */
    public void clearAllShadowInPlayingField() {
        shadowGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
    }

    public void addMinoInNextBox(Mino mino) {
        for (Block block : mino.blocks) {
            block.drawAdd(nextBoxGC);
        }
    }
    public void removeMinoInNextBox(Mino mino) {
        for (Block block : mino.blocks) {
            block.drawRemove(nextBoxGC);
        }
    }

    public void addMinoInHoldBox(Mino mino) {
        for (Block block : mino.blocks) {
            block.drawAdd(holdBoxGC);
        }
    }
    public void removeMinoInHoldBox(Mino mino) {
        for (Block block : mino.blocks) {
            block.drawRemove(holdBoxGC);
        }
    }

    // =================================================
    // Effects
    // =================================================

    public void handleClearLineSpecialEffect(int effectCounter) {

        if (effectCounter >= 0 && effectCounter < BLOCK_FADING_DURATION) {
            int workingCounter = effectCounter;
            for (MinoBlock fadingBlock : fadingBlocks) {
                fadingBlock.drawFading(blockGC, workingCounter);
            }
        }


        if (effectCounter >= BLOCK_FADING_DURATION && effectCounter < BLOCK_FALLING_DURATION + BLOCK_FADING_DURATION) {

            int workingCounter = effectCounter - BLOCK_FADING_DURATION;

            for (int i = 0; i < fallingBlocks.size(); i++) {
                MinoBlock fallingBlock = fallingBlocks.get(i);
                int numLinesFall = numLinesFallList.get(i);

                fallingBlock.drawFalling(blockGC, workingCounter, numLinesFall);
            }
        }
    }

    /**
     * Draw a spinning T shape mino onto the screen.
     * <ul>
     *     <li>The shape is a T-shape mino made up of 8 points (x1,y1 to x8,y8), connected with lines.</li>
     *     <li>The T rotates over time by incrementing the base angle using `effectCounter` and `angleStep`.</li>
     *     <li>Each point's position is calculated using polar coordinates: (length, angle), converted to Cartesian (x, y).</li>
     *     <li>Lengths (`l1` to `l8`) and fixed angle offsets (`a1` to `a8`) are geometrically tuned to create the T shape.</li>
     *     <li>Alpha is computed as a linear fade-out: starts at 1.0 and decreases to 0.0 over `howLong` frames.</li>
     * </ul>
     * @param effectCounter
     */
    public void handleTSpinSpecialEffect(int effectCounter) {
        specialEffectGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);

        double x = LEFTMOST_PIXEL + PLAYING_FIELD_WIDTH / 2.0;
        double y = TOPMOST_PIXEL + PLAYING_FIELD_HEIGHT / 2.0;

        double totalAngle = 2 * Math.PI * 270 / 360; // 270 degrees in radians
        double angleStep = totalAngle / T_SPIN_DURATION;
        double initialAngle = 2 * Math.PI * 150 / 360; // 150 degrees in radians
        double angle = initialAngle + effectCounter * angleStep;
        int size = (int) Math.round(50 + effectCounter / 1.2);

        // Calculate points (same math as your original)
        double l1 = Math.sqrt(1.5 * size * 1.5 * size + size * size);
        double a1 = Math.PI - 0.5880026035 + angle;
        double x1 = l1 * Math.cos(a1) + x;
        double y1 = l1 * Math.sin(a1) + y;

        double l2 = l1;
        double a2 = 0.5880026035 + angle;
        double x2 = l2 * Math.cos(a2) + x;
        double y2 = l2 * Math.sin(a2) + y;

        double l3 = 1.5 * size;
        double a3 = 0 + angle;
        double x3 = l3 * Math.cos(a3) + x;
        double y3 = l3 * Math.sin(a3) + y;

        double l4 = 0.5 * size;
        double a4 = 0 + angle;
        double x4 = l4 * Math.cos(a4) + x;
        double y4 = l4 * Math.sin(a4) + y;

        double l5 = 0.5 * size;
        double a5 = Math.PI + angle;
        double x5 = l5 * Math.cos(a5) + x;
        double y5 = l5 * Math.sin(a5) + y;

        double l6 = 1.5 * size;
        double a6 = Math.PI + angle;
        double x6 = l6 * Math.cos(a6) + x;
        double y6 = l6 * Math.sin(a6) + y;

        double l7 = Math.sqrt(0.5 * size * 0.5 * size + size * size);
        double a7 = -1.107148718 + angle;
        double x7 = l7 * Math.cos(a7) + x;
        double y7 = l7 * Math.sin(a7) + y;

        double l8 = Math.sqrt(0.5 * size * 0.5 * size + size * size);
        double a8 = -Math.PI + 1.107148718 + angle;
        double x8 = l8 * Math.cos(a8) + x;
        double y8 = l8 * Math.sin(a8) + y;

        double alpha = 1.0 - (double) effectCounter / T_SPIN_DURATION; // alpha from 1 to 0

        // Set stroke width and color for first layer (thick purple line)
        specialEffectGC.setLineWidth(10 + 20 * effectCounter / (double) T_SPIN_DURATION);
        specialEffectGC.setStroke(Color.rgb(255, 0, 255, alpha)); // purple with alpha

        // Draw lines - JavaFX has no drawLine for multiple lines at once, so call strokeLine
        specialEffectGC.strokeLine(x1, y1, x2, y2);
        specialEffectGC.strokeLine(x2, y2, x3, y3);
        specialEffectGC.strokeLine(x3, y3, x4, y4);
        specialEffectGC.strokeLine(x4, y4, x7, y7);
        specialEffectGC.strokeLine(x7, y7, x8, y8);
        specialEffectGC.strokeLine(x8, y8, x5, y5);
        specialEffectGC.strokeLine(x5, y5, x6, y6);
        specialEffectGC.strokeLine(x6, y6, x1, y1);

        // Set stroke width and color for second layer (thin white line)
        specialEffectGC.setLineWidth(3 + 5 * effectCounter / (double) T_SPIN_DURATION);
        specialEffectGC.setStroke(Color.rgb(255, 255, 255, alpha)); // white with alpha

        specialEffectGC.strokeLine(x1, y1, x2, y2);
        specialEffectGC.strokeLine(x2, y2, x3, y3);
        specialEffectGC.strokeLine(x3, y3, x4, y4);
        specialEffectGC.strokeLine(x4, y4, x7, y7);
        specialEffectGC.strokeLine(x7, y7, x8, y8);
        specialEffectGC.strokeLine(x8, y8, x5, y5);
        specialEffectGC.strokeLine(x5, y5, x6, y6);
        specialEffectGC.strokeLine(x6, y6, x1, y1);
    }

    public void addFadingBlock(MinoBlock minoBlock) {
        fadingBlocks.add(minoBlock);
    }
    public void addFallingBlock(MinoBlock minoBlock, int numLinesFall) {
        fallingBlocks.add(minoBlock);
        numLinesFallList.add(numLinesFall);
    }

    /**
     * Clears every data and special effects on the screen.
     */
    public void clearEffect() {
        fadingBlocks.clear();
        fallingBlocks.clear();
        numLinesFallList.clear();

        specialEffectGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
    }

    // =================================================
    // Metrics UI
    // =================================================
    public void updateScore(int currentScore) {
        this.score.setText("" + currentScore);
    }
    public void updateHighScore(int newHighScore) {
        this.highScore.setText("" + newHighScore);
    }

    // =================================================
    // Restart game & Pause menu
    // =================================================
    public void restartGame() {
        blockGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        shadowGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        holdBoxGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);
        nextBoxGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);
    }
    public void setBlurEffects() {

        Timeline addingBlurAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(gaussianBlur.radiusProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.8), new KeyValue(gaussianBlur.radiusProperty(), 10.0))
        );

        addingBlurAnimation.play();

        this.getRoot().setEffect(gaussianBlur);
    }
    public Animation setRemoveEffects() {
        //this.getRoot().setEffect(null); // remove any blur
        Timeline removingBlurAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(gaussianBlur.radiusProperty(), gaussianBlur.getRadius())),
                new KeyFrame(Duration.seconds(0.15), new KeyValue(gaussianBlur.radiusProperty(), 0))
        );

        // Optional: Remove the effect entirely after animation finishes

        removingBlurAnimation.setOnFinished(e -> {
            // reset effects
            this.getRoot().setEffect(null);
        });

        //removingBlurAnimation.play();

        this.getRoot().setOpacity(1.0); // remove any fading effects

        return removingBlurAnimation;
    }

    public void setTimerVisibility(boolean isVisible) {
        timer.setVisible(isVisible);
        timerBar.setVisible(isVisible);
    }
    public void updateTime(int currentCounter) {
        int numOfSecondsLeft = 120 - currentCounter / FPS;
        int minute = numOfSecondsLeft / 60;
        int second = numOfSecondsLeft % 60;
        String minuteString = String.format("%01d", minute);
        String secondString = String.format("%02d", second);
        timer.setText(minuteString + ":" + secondString);

        double progress = 1.0 - 1.0 * currentCounter / (TWO_MINUTE_DURATION); // 2 minutes
        timerBar.setProgress(progress);
    }
}
