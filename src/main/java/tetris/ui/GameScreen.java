package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tetris.block.Block;
import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.util.UiAnimation;

import java.util.ArrayList;

import static tetris.util.TetrisConstants.*;
import static tetris.util.TetrisConstants.PLAYING_FIELD_HEIGHT;

public class GameScreen extends UiPart<VBox> {

    private static final String FXML = "GameScreen.fxml";
    private static final String backgroundImagePath = "/images/gameplay_backgrounds/";
    private BackgroundImageSelector backgroundImageSelector;

    @FXML
    private VBox root;
    @FXML
    private VBox mainLayout;
    @FXML
    private VBox timerSection;
    @FXML
    private VBox holdBoxMetricsSection;
    @FXML
    private HBox gameplaySection;
    @FXML
    private VBox nextBoxSection;
    @FXML
    private Pane playingField;
    @FXML
    private Pane nextMinoBox;
    @FXML
    private Pane holdMinoBox;
    @FXML
    private Label numLinesLabelInDefault;
    @FXML
    private Label score;
    @FXML
    private Label highScore;
    @FXML
    private Label numLinesLabelInSprint;
    @FXML
    private Label bestTimeLabel;
    @FXML
    private VBox relaxBlitzMetrics;
    @FXML
    private VBox sprintModeMetrics;
    @FXML
    private Label timer;
    @FXML
    private ProgressBar timerBar;
    @FXML
    private StackPane tubeContainer;

    private LineMeterTube lineMeterTube;
    private ComboOverlay comboOverlay;

    // Graphic contexts
    private GraphicsContext playingFieldGC; // Draw the grid background in the playing field
    private GraphicsContext nextBoxGC; // Draw the minos in the next box
    private GraphicsContext holdBoxGC; // Draw the minos in the hold box
    private GraphicsContext blockGC; // Draw the minos and blocks in the playing field
    private GraphicsContext shadowGC; // Draw the minos' shadow in the playing field
    private GraphicsContext specialEffectGC;
    private Timeline activeBlurTimeline;

    // Keeps data for animation
    private ArrayList<MinoBlock> fadingBlocks;
    private ArrayList<MinoBlock> fallingBlocks;
    private ArrayList<Integer> numLinesFallList;

    private final GaussianBlur gaussianBlur = new GaussianBlur(10.0);
    private final ColorAdjust dimEffect = new ColorAdjust();

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

        lineMeterTube = new LineMeterTube(20, PLAYING_FIELD_HEIGHT);
        tubeContainer.getChildren().add(lineMeterTube);
        tubeContainer.setVisible(false); // hide it, only appear when Sprint Mode

        comboOverlay = new ComboOverlay(PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT); // already invisible at first
        playingField.getChildren().add(comboOverlay); // add combo overlay on top of all elements in playing field

        nextMinoBox.getChildren().add(nextBoxCanvas);
        holdMinoBox.getChildren().add(holdBoxCanvas);

        // keeps the blocks for animation (fading and falling)
        fadingBlocks = new ArrayList<>();
        fallingBlocks = new ArrayList<>();
        numLinesFallList = new ArrayList<>();

        // score label
        updateScoreAndLines(0, 0);
        updateHighScore(0);
        updateBestTime(Integer.MAX_VALUE);
        drawPlayingFieldGrid();

        // set up blur effects
        mainLayout.setEffect(gaussianBlur);
        gaussianBlur.setRadius(0); // no blur at start
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
    public void popComboEffect(int combo) {
        if (combo < 1) {
            return;
        }
        comboOverlay.showComboAnimation(combo);

    }

    public void handleClearLineSpecialEffect(int effectCounter) {
        // 0 - 6: 7 frames
        if (effectCounter >= 0 && effectCounter < BLOCK_FADING_DURATION) {
            int workingCounter = effectCounter;
            for (MinoBlock fadingBlock : fadingBlocks) {
                fadingBlock.drawFading(blockGC, workingCounter);
            }
        }
        // 7 - 14: 8 frames
        if (effectCounter >= BLOCK_FADING_DURATION && effectCounter < BLOCK_FALLING_DURATION + BLOCK_FADING_DURATION) {

            int workingCounter = effectCounter - BLOCK_FADING_DURATION;

            for (int i = 0; i < fallingBlocks.size(); i++) {
                MinoBlock fallingBlock = fallingBlocks.get(i);
                int numLinesFall = numLinesFallList.get(i);

                fallingBlock.drawFalling(blockGC, workingCounter, numLinesFall);
            }
        }
        // during last frame of falling -- remove all falling block and re-add them,
        // prevent left, right adjacent block from having gaps after falling
        if (effectCounter == BLOCK_FALLING_DURATION + BLOCK_FADING_DURATION - 1) {
            for (MinoBlock fallingBlock : fallingBlocks) {
                fallingBlock.drawRemove(blockGC);
            }
            for (MinoBlock fallingBlock : fallingBlocks) {
                fallingBlock.drawAdd(blockGC);
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
    // Metrics UI -- NUMBERS ~~~~~~
    // =================================================

    public void updateScoreAndLines(int currentScore, int numLinesClear) {
        this.score.setText("" + currentScore);
        this.numLinesLabelInDefault.setText("" + numLinesClear);
    }
    public void updateHighScore(int newHighScore) {
        this.highScore.setText("" + newHighScore);
    }
    public void updateBestTime(int newBestTimeInCounterVal) {
        if (newBestTimeInCounterVal == Integer.MAX_VALUE) {
            bestTimeLabel.setText("--:--:--");
            return;
        }
        int centisecond = (100 * newBestTimeInCounterVal / FPS) % 100; // two decimal places of a second
        int second = (newBestTimeInCounterVal / FPS) % 60;             // mod 60 seconds
        int minute = (newBestTimeInCounterVal / (FPS * 60)) % 60;      // mod 60 minutes
        int hour = (newBestTimeInCounterVal / (FPS * 3600)) % 24;      // mod 24 hrs

        String minuteString = String.format("%02d", minute);
        String secondString = String.format("%02d", second);
        String centisecondString = String.format("%02d", centisecond);
        if (hour == 0) {
            bestTimeLabel.setText(minuteString + ":" + secondString + ":" + centisecondString);
        } else {
            String hourString = String.format("%02d", hour);
            bestTimeLabel.setText(hourString + ":" + minuteString + ":" + secondString + ":" + centisecondString);
        }
    }

    // Invoked by InactiveStateManager -- every line clear, invoke once
    public void updateGameMetricsForSprintMode(int numLinesClear, int sprintGoal) {
        assert !relaxBlitzMetrics.isVisible() && sprintModeMetrics.isVisible();

        this.numLinesLabelInSprint.setText(numLinesClear + "/" + sprintGoal);
    }
    // Invoked by InactiveStateManager -- every line clear, invoke once
    public void updateGameMetricsForDefault(int numLinesClear, int currentScore) {
        assert relaxBlitzMetrics.isVisible() && !sprintModeMetrics.isVisible();

        this.updateScoreAndLines(currentScore, numLinesClear);
    }

    //public void updateNumLinesClear

    // =================================================
    // Restart game & Pause menu
    // =================================================
    public void restartGame(int highScore) {
        blockGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        shadowGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        holdBoxGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);
        nextBoxGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);

        this.updateScoreAndLines(0, 0);
        this.updateHighScore(highScore);
    }
    public void restartGameForSprint(int bestTimeInCounterVal, int sprintGoal) {
        blockGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        shadowGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, PLAYING_FIELD_WIDTH, PLAYING_FIELD_HEIGHT);
        holdBoxGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);
        nextBoxGC.clearRect(LEFTMOST_PIXEL, TOPMOST_PIXEL, HOLD_BOX_HEIGHT_WIDTH, HOLD_BOX_HEIGHT_WIDTH);

        lineMeterTube.resetTube(); // set it to zero progress

        // set up new game metrics value on game screen
        this.updateBestTime(bestTimeInCounterVal);
        this.updateGameMetricsForSprintMode(0, sprintGoal);
    }
    public Animation getBlurEffects() {
        mainLayout.setEffect(gaussianBlur);
        gaussianBlur.setRadius(0);

        activeBlurTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1),
                        new KeyValue(gaussianBlur.radiusProperty(), GAME_SCREEN_BLUR_AMOUNT))
        );
        return activeBlurTimeline;
    }
    public Animation setRemoveEffects() {
        // remove any blur
        activeBlurTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.15),
                        new KeyValue(gaussianBlur.radiusProperty(), 0))
        );

        mainLayout.setOpacity(1.0); // remove any fading effects

        activeBlurTimeline.setOnFinished(e -> {
            mainLayout.setEffect(null);
        });

        return activeBlurTimeline;
    }
    public void setGameScreenForRelaxMode() {
        timer.setVisible(false);
        timerBar.setVisible(false);
        // hide lineMeterTube
        this.tubeContainer.setVisible(false);
        // hide and show the left sidebar in game screen
        sprintModeMetrics.setVisible(false);
        relaxBlitzMetrics.setVisible(true);
    }
    public void setGameScreenForSprintMode() {
        timer.setVisible(true);
        timerBar.setVisible(false);
        // show lineMeterTube -- FOR SPRINT MODE
        this.tubeContainer.setVisible(true);
        // hide and show the left sidebar in game screen
        sprintModeMetrics.setVisible(true);
        relaxBlitzMetrics.setVisible(false);
    }
    public void setGameScreenForBlitzMode() {
        timer.setVisible(true);
        timerBar.setVisible(true);
        // hide lineMeterTube
        this.tubeContainer.setVisible(false);
        // hide and show the left sidebar in game screen
        sprintModeMetrics.setVisible(false);
        relaxBlitzMetrics.setVisible(true);
    }

    // =======================================
    // Update game TIME UI
    // =======================================

    /**
     * Shows the stop watch timer for Sprint mode. Shows HOURS (if nonzero) : MINUTES : SECONDS : CENTISECONDS
     */
    public void updateStopWatch(int currentCounter) {
        int centisecond = (100 * currentCounter / FPS) % 100; // two decimal places of a second
        int second = (currentCounter / FPS) % 60;             // mod 60 seconds
        int minute = (currentCounter / (FPS * 60)) % 60;      // mod 60 minutes
        int hour = (currentCounter / (FPS * 3600)) % 24;      // mod 24 hrs

        String minuteString = String.format("%02d", minute);
        String secondString = String.format("%02d", second);
        String centisecondString = String.format("%02d", centisecond);
        timer.setStyle("-fx-text-fill: white");
        if (hour == 0) {
            timer.setText(minuteString + ":" + secondString + ":" + centisecondString);
        } else {
            String hourString = String.format("%02d", hour);
            timer.setText(hourString + ":" + minuteString + ":" + secondString + ":" + centisecondString);
        }
    }
    public void setSprintLineMeterTubeProgress(int numLinesClear, int sprintGoal) {
        lineMeterTube.setTubeProgress(numLinesClear, sprintGoal);
    }

    /**
     * Shows the remaining time and time bar on Blitz mode, 2 minutes count down timer
     * @param currentCounter is the game counter, 120 times per second (FPS)
     */
    public void updateRemainingTime(int currentCounter) {
        int numOfSecondsLeft = 120 - currentCounter / FPS;
        int minute = numOfSecondsLeft / 60;
        int second = numOfSecondsLeft % 60;
        String minuteString = String.format("%01d", minute);
        String secondString = String.format("%02d", second);
        timer.setText(minuteString + ":" + secondString);

        double progress = 1.0 - 1.0 * currentCounter / (TWO_MINUTE_DURATION); // 2 minutes, progress goes from 1.0 to 0.0
        assert progress >= 0.0 && progress <= 1.0 : "Progress out of range: " + progress;
        timerBar.setProgress(progress);

        // set color of timer & timer bar
        String cssColor = getTimerBarColor(progress);

        if (progress < (1.0 / 6) && second % 2 == 0) { // 20 seconds left, starts blinking the timer number :)
            timer.setStyle("-fx-text-fill: " + cssColor + ";");
        } else {
            timer.setStyle("-fx-text-fill: white");
        }
    }

    /**
     * Set the timer bar from Blue -> Green -> Yellow -> Red by following the progress value and adjusting the r,g,b values
     * @param progress goes from 1.0 to 0.0, it is the progress of the timer bar
     * @return color of the timer bar in {@code String} to set it for timer bar color in CSS
     */
    private String getTimerBarColor(double progress) {
        double red, green, blue;
        double firstPhase = 0.5;  // cyan -> green
        double secondPhase = 0.3; // green -> yellow
        double thridPhase = 0.1;  // yellow -> dark orange
                                  // last phase: dark orange -> red

        if (progress > firstPhase) {
            // cyan to green
            double t = (progress - firstPhase) / (1.0 - firstPhase); // normalize progress = (1.0 -> 0.5) to t = (1.0 -> 0.0)
            red = 0.0;
            green = 255 - t * 100;
            blue = t * 255;
        } else if (progress > secondPhase) {
            // green to yellow
            double t = (progress - secondPhase) / (firstPhase - secondPhase); // normalize progress = (0.5-> 0.3) to t = (1.0 -> 0.0)
            red = 255 - t * 255;
            green = 255;
            blue = 0;
        } else if (progress > thridPhase) {
            // yellow to dark orange
            double t = (progress - thridPhase) / (secondPhase - thridPhase); // normalize progress = (0.3-> 0.1) to t = (1.0 -> 0.0)
            red = 255;
            green = t * (255 - 116) + 116;
            blue = 0;
        } else {
            double t = progress / thridPhase; // // normalize progress = (0.1 -> 0.0) to t = (1.0 -> 0.0)
            red = 255;
            green = t * 116;
            blue = 0;
        }

        return String.format("rgb(%d,%d,%d)", (int) red, (int) green, (int) blue);
    }

    public ParallelTransition collapsingGameScreenEffects() {
        float animateDuration = 1.4f;
        ParallelTransition timerAndHoldFallingEffects = UiAnimation.fall(0, 800, animateDuration,
                timerSection, holdBoxMetricsSection);

        ParallelTransition gameAndNextFallingEffects = UiAnimation.fall(0, 800, 1.2f,
                gameplaySection, nextBoxSection);

        Duration duration = Duration.seconds(1.4);
        RotateTransition leftSectionRotate = new RotateTransition(duration, holdBoxMetricsSection);
        leftSectionRotate.setToAngle(-15);

        TranslateTransition holdBoxFallToLeft = new TranslateTransition(duration, holdBoxMetricsSection);
        holdBoxFallToLeft.setToX(-100);

        RotateTransition middleSectionTilt = new RotateTransition(duration, gameplaySection);
        middleSectionTilt.setToAngle(8);

        RotateTransition rightSectionTile = new RotateTransition(duration, nextBoxSection);
        rightSectionTile.setToAngle(20);

        TranslateTransition nextBoxFallToRight = new TranslateTransition(duration, nextBoxSection);
        nextBoxFallToRight.setToX(70);

        ParallelTransition combined = new ParallelTransition(leftSectionRotate, holdBoxFallToLeft, middleSectionTilt,
                rightSectionTile, nextBoxFallToRight, timerAndHoldFallingEffects, gameAndNextFallingEffects);
        return combined;
    }
    public ParallelTransition flyDownFromTop() {
        resetUiPositionAfterAnimation();
        timerSection.setTranslateY(-800);
        holdBoxMetricsSection.setTranslateY(-800);
        gameplaySection.setTranslateY(-800);
        nextBoxSection.setTranslateY(-800);
        ParallelTransition fallingGameScreensParts = UiAnimation.fall(-800, 0, 1.4f,
                timerSection, holdBoxMetricsSection, gameplaySection, nextBoxSection);
        return fallingGameScreensParts;
    }

    public ParallelTransition zoomIn() {
        // custom interpolator: make animation progress fast then slow down to near linear speed
        Interpolator fastThenSlowInterpolator = Interpolator.SPLINE(0.1, 0.14, 0.8, 0.5);
        ScaleTransition gameScreenZoomIn = UiAnimation.scale(1.0f, 5.0f, 7f, mainLayout);
        gameScreenZoomIn.setInterpolator(fastThenSlowInterpolator);

        FadeTransition gameScreenFadeOut = UiAnimation.fadeOut(1.0f, 0.0f, 7, mainLayout);
        gameScreenFadeOut.setInterpolator(Interpolator.EASE_OUT);

        return new ParallelTransition(gameScreenZoomIn, gameScreenFadeOut);
    }
    public Animation darken() {
        root.setEffect(dimEffect);
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dimEffect.brightnessProperty(), 0)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(dimEffect.brightnessProperty(), -1))
        );
    }
    public Animation brighten() {
        Timeline brighten = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dimEffect.brightnessProperty(), -1)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(dimEffect.brightnessProperty(), 0))
        );
        brighten.setOnFinished(e -> {
            root.setEffect(null);
        });
        return brighten;
    }

    public void resetUiPositionAfterAnimation() {
        resetNodes(mainLayout, timerSection, holdBoxMetricsSection, gameplaySection, nextBoxSection);
    }
    private void resetNodes(Node... nodes) {
        for (Node node : nodes) {
            node.setTranslateX(0);
            node.setTranslateY(0);
            node.setRotate(0);
            node.setScaleX(1);
            node.setScaleY(1);
            node.setOpacity(1);
        }
    }
    public String getTimerString() {
        return timer.getText();
    }
    public String getBestTimeString() {
        // don't have to ask the model for the new best score
        return bestTimeLabel.getText();
    }

}
