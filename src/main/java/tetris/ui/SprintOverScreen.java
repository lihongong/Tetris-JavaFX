package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
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
    private ParallelTransition gameScreenZoomInAnimation;

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

        initSprintOverScreenPhaseOne();

        sprintOverAnimationPhaseOne(gameScreen, runnable);
    }
    private void initSprintOverScreenPhaseOne() {
        // Times Up Screen Appear!!! :)
        this.showNode(this.getRoot());
        finishLabel.setOpacity(0);
        finishLabel.setScaleX(1.2);
        finishLabel.setScaleY(1.2);
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
    public void sprintOverAnimationPhaseOne(GameScreen gameScreen, Runnable runnable) {
        // Finish Label Animation
        FadeTransition fadeInLabel = UiAnimation.fadeIn(0.4f, 1.0f, 0.5f, finishLabel);
        ScaleTransition scaleUpLabel = UiAnimation.scale(1.2f, 1.6f, 0.7f, finishLabel);
        scaleUpLabel.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition scaleUpFadeInLabel = new ParallelTransition(scaleUpLabel, fadeInLabel);
        ScaleTransition scaleDownLabel = UiAnimation.scale(1.6f, 1.0f, 3f, finishLabel);
        FadeTransition fadeOutLabel = UiAnimation.fadeOut(1.0f, 0.0f, 3f, finishLabel);
        ParallelTransition scaleDownFadeOutLabel = new ParallelTransition(scaleDownLabel, fadeOutLabel);
        SequentialTransition finishLabelTransition = new SequentialTransition(scaleUpFadeInLabel, scaleDownFadeOutLabel);

        // Delayed Button & Time Panel slide in Animation
        PauseTransition delayedButtonSlideIn = new PauseTransition(Duration.seconds(3));
        delayedButtonSlideIn.setOnFinished(e -> {
            initSprintOverScreenPhaseTwo(gameScreen.getTimerString(), gameScreen.getBestTimeString());
            sprintOverAnimationPhaseTwo(runnable);
        });

        gameScreenZoomInAnimation = gameScreen.zoomIn();

        // play 3 async animation :)
        finishLabelTransition.play();
        gameScreenZoomInAnimation.play();
        delayedButtonSlideIn.play();
    }
    public void initSprintOverScreenPhaseTwo(String timerString, String bestTimeString) {
        finalTimeLabel.setText(timerString);
        bestTimeLabel.setText(bestTimeString);
        timePanel.setOpacity(1.0);
    }
    public void sprintOverAnimationPhaseTwo(Runnable runnable) {
        ParallelTransition slideIn = UiAnimation.slideIn(500, 0, 0.3f, timePanel,
                restartButton, exitButton);
        ParallelTransition fadeIn = UiAnimation.fadeIn(0.0f, 1.0f, 0.3f, timePanel,
                restartButton, exitButton);
        ParallelTransition slideFadeIn = new ParallelTransition(slideIn, fadeIn);
        slideFadeIn.setOnFinished(e -> {
            UiPart.setUiEffectsOff();

            restartButton.setMouseTransparent(false);
            exitButton.setMouseTransparent(false);

            runnable.run();
        });
        slideFadeIn.play();
    }

    public void closeSprintOverScreenEffects(GameScreen gameScreen, Runnable restartGameLoop,
                                             Runnable restartGameplayManagerAndGameScreen) {
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
        Animation gameScreenDarken = gameScreen.darken();
        gameScreenDarken.setOnFinished(e -> {
            if (gameScreenZoomInAnimation != null) gameScreenZoomInAnimation.stop();
            restartGameplayManagerAndGameScreen.run();
            gameScreen.resetUiPositionAfterAnimation();
            gameScreen.brighten().play();
        });
        ParallelTransition gameRestartTransition = new ParallelTransition(slideOutTrans, fadeOutTrans, gameScreenDarken);

        gameRestartTransition.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot()); // "disappear GameOverScreen" as we restart the game again
            UiPart.setUiEffectsOff(); // enable ui interaction again (prevent multiple animation at once)
            restartGameLoop.run();
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

            // stop the game screen zoom in effect and reset game Screen position !!!
            if (gameScreenZoomInAnimation != null) gameScreenZoomInAnimation.stop();
            gameScreen.resetUiPositionAfterAnimation();

            // allow select menu button to shift left on mouse enter once animation is over
            sprintModesScreen.setIsButtonMouseTransparent(false);
        });
        combined.play();
    }
}
