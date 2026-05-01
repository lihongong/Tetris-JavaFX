package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.logic.GameState;
import tetris.util.ButtonHandler;
import tetris.util.UiAnimation;

import static tetris.util.TetrisConstants.SPRINT_OVER_LABEL_CENTER_Y;

public class SprintOverScreen extends UiPart<VBox> {
    private static final String FXML = "SprintOverScreen.fxml";
    private ButtonHandler restartButtonHandler;
    private ButtonHandler exitButtonHandler;

    @FXML
    Label finishLabel;
    @FXML
    StackPane timePanel;
    @FXML
    Label finalTimeLabel;
    @FXML
    Label bestTimeLabel;
    @FXML
    Button restartButton;
    @FXML
    Button exitButton;
    private TranslateTransition restartButtonHoverAnimation;
    private TranslateTransition exitButtonHoverAnimation;

    public SprintOverScreen() {
        super(FXML);
    }
    public void setRestartExitButtonHandler(ButtonHandler restartButtonHandler, ButtonHandler exitButtonHandler) {
        this.restartButtonHandler = restartButtonHandler;
        this.exitButtonHandler = exitButtonHandler;
    }

    @FXML
    public void handleRestartButton() {
        restartButtonHandler.handle();
    }
    @FXML
    public void handleExitButton() {
        exitButtonHandler.handle();
    }
    @FXML
    public void handleRestartButtonMouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            restartButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(restartButton, restartButtonHoverAnimation);
        }
    }
    @FXML
    public void handleRestartButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            restartButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(restartButton, restartButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            exitButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(exitButton, exitButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            exitButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(exitButton, exitButtonHoverAnimation);
        }
    }

    public void openSprintOverScreenEffects(GameScreen gameScreen, Runnable runnable) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();


        PauseTransition pauseForAwhile = new PauseTransition(Duration.seconds(0.5));
        pauseForAwhile.setOnFinished(aae -> {
            initSprintOverScreenPhaseOne();

            ScaleTransition s1 = new ScaleTransition(Duration.seconds(1), finishLabel);
            s1.setFromX(0.7);
            s1.setToX(1.3);
            s1.setFromY(0.7);
            s1.setToY(1.3);
            ScaleTransition s2 = new ScaleTransition(Duration.seconds(2), finishLabel);
            s2.setToX(0.8);
            s2.setToY(0.8);
            FadeTransition f2 = UiAnimation.fadeOut(1.0f, 0.0f, 2, finishLabel);

            ParallelTransition p2 = new ParallelTransition(s2, f2);
            SequentialTransition sq = new SequentialTransition(s1, p2);


            ParallelTransition zoomInGameScreen = gameScreen.zoomIn();

            sq.play();
            zoomInGameScreen.play();

            PauseTransition pausedThenPlay = new PauseTransition(Duration.seconds(3));

            pausedThenPlay.setOnFinished(e -> {
                initSprintOverScreenPhaseTwo(gameScreen.getTimerString(), gameScreen.getBestTimeString());
                phaseTwo(runnable);
            });
            pausedThenPlay.play();
        });
        pauseForAwhile.play();
    }
    private void initSprintOverScreenPhaseOne() {
        // Times Up Screen Appear!!! :)
        this.showNode(this.getRoot());
        finishLabel.setOpacity(1.0);
        finishLabel.setScaleX(0.7);
        finishLabel.setScaleY(0.7);
        finishLabel.setTranslateY(SPRINT_OVER_LABEL_CENTER_Y); // place the KO! label at the middle of the gameScreen at first
        restartButton.setOpacity(0); // hide buttons at first :)
        exitButton.setOpacity(0);
        timePanel.setOpacity(0);
        // so that when sliding in animation finishes, the mouse will be detected by buttons as "enter" and animate
        // the "pop left of the buttons", even when the mouse is already inside the button. This is because we disable
        // animation when buttons are sliding in, so the buttons will already be "entered" but no "on enter" animation
        // will happen
        restartButton.setMouseTransparent(true);
        exitButton.setMouseTransparent(true);
    }
    public void initSprintOverScreenPhaseTwo(String timerString, String bestTimeString) {
        finalTimeLabel.setText(timerString);
        bestTimeLabel.setText(bestTimeString);
        timePanel.setOpacity(1.0);
    }
    public void phaseTwo(Runnable runnable) {
        ParallelTransition slideIn = UiAnimation.slideIn(500, 0, 0.3f, timePanel,
                restartButton, exitButton);
        ParallelTransition fadeIn = UiAnimation.fadeIn(0.0f, 1.0f, 0.3f, timePanel,
                restartButton, exitButton);
        ParallelTransition p = new ParallelTransition(slideIn, fadeIn);
        p.setOnFinished(e -> {
            UiPart.setUiEffectsOff();

            restartButton.setMouseTransparent(false);
            exitButton.setMouseTransparent(false);

            runnable.run();
        });
        p.play();
    }

    public void closeSprintOverScreenEffects(GameScreen gameScreen, Runnable runnable) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.0f;
        float animateDuration = 0.2f;

        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration, restartButton,
                exitButton, timePanel);
        // unblur the gameScreen
        Animation closeAndReopenGameScreen = gameScreen.closeAndReopen();
        ParallelTransition buttonsAndLabelOutEffects = new ParallelTransition(slideOutTrans, fadeOutTrans, closeAndReopenGameScreen);

        SequentialTransition gameRestartTransition = new SequentialTransition(buttonsAndLabelOutEffects);

        gameRestartTransition.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot()); // "disappear GameOverScreen" as we restart the game again
            UiPart.setUiEffectsOff(); // enable ui interaction again (prevent multiple animation at once)
            runnable.run();
        });

        gameRestartTransition.play();
    }

    public void fromSprintOverToSprintMode(SprintModesScreen sprintModesScreen, GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // make appear SelectMenuScreen -> to prepare for transition into it
        this.showNode(sprintModesScreen.getRoot());

        float animateDuration = 0.3f;
        ParallelTransition sprintModesButtonSlidingInEffects = sprintModesScreen.openSprintModesScreen(animateDuration);

        int toX = 500; // move 500 px
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        // Slide out restart & Exit Buttons
        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        // Fade out Restart, Exit, Game Over Label
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration,
                restartButton, exitButton, timePanel);

        ParallelTransition combined = new ParallelTransition(sprintModesButtonSlidingInEffects, slideOutTrans, fadeOutTrans);
        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot(), gameScreen.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again

            gameScreen.resetUiPositionAfterAnimation();

            // allow select menu button to shift left on mouse enter once animation is over
            sprintModesScreen.setIsButtonMouseTransparent(false);
        });
    }
}
