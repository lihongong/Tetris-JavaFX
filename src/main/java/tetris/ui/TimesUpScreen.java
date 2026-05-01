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
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;
import tetris.util.UiAnimation;

import java.util.ArrayList;

import static tetris.util.TetrisConstants.BUTTON_OFF_SCREEN_POS;
import static tetris.util.TetrisConstants.BUTTON_ON_SCREEN_POS;
import static tetris.util.TetrisConstants.TIMES_UP_LABEL_CENTER_Y;

public class TimesUpScreen extends UiPart<VBox> {
    private static final String FXML = "TimesUpScreen.fxml";
    private ButtonHandler restartButtonHandler;
    private ButtonHandler exitButtonHandler;
    @FXML
    Label timesUpLabel;
    @FXML
    Button restartButton;
    @FXML
    Button exitButton;
    private TranslateTransition restartButtonHoverAnimation;
    private TranslateTransition exitButtonHoverAnimation;

    public TimesUpScreen() {
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
    /**
     * Shows the Times Up Screen by Sliding in Buttons from the right to the center, fading in the Buttons & Label.
     * Blur the Game Screen.
     */
    public void openTimesUpScreenEffects(GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        this.setInitScreen();

        this.timesUpAnimation(gameScreen);
    }
    private void setInitScreen() {
        // Times Up Screen Appear!!! :)
        this.showNode(this.getRoot());

        timesUpLabel.setOpacity(1.0);
        timesUpLabel.setScaleX(1.0);
        timesUpLabel.setScaleY(1.0);
        timesUpLabel.setTranslateY(TIMES_UP_LABEL_CENTER_Y); // place the times up label at the middle of the gameScreen at first
        restartButton.setOpacity(0); // hide buttons at first :)
        exitButton.setOpacity(0);
        // so that when sliding in animation finishes, the mouse will be detected by buttons as "enter" and animate
        // the "pop left of the buttons", even when the mouse is already inside the button. This is because we disable
        // animation when buttons are sliding in, so the buttons will already be "entered" but no "on enter" animation
        // will happen
        restartButton.setMouseTransparent(true);
        exitButton.setMouseTransparent(true);
    }
    private void timesUpAnimation(GameScreen gameScreen) {
        // pop in Time's Up title
        SequentialTransition poppingTimesUpTrans = UiAnimation.pop(0.5f, 1.3f, 1.0f, 0.13f, timesUpLabel);
        // Pause for 1 sec
        PauseTransition pauseAfterPopTitle = new PauseTransition(Duration.seconds(1));

        // move the "Time's Up" to the top
        TranslateTransition moveTitleUp = new TranslateTransition(Duration.seconds(0.5), timesUpLabel);
        moveTitleUp.setToY(0);

        ScaleTransition scaleupTitle = UiAnimation.scale(1.0f, 1.5f, 0.5f, timesUpLabel);

        Animation gameScreenBlur = gameScreen.getBlurEffects(); // blur game screen
        // Move "Time's Up" to the top & Blur game screen
        ParallelTransition moveTitleUpAndBlurGameScreen = new ParallelTransition(moveTitleUp, gameScreenBlur, scaleupTitle);
        // Pause slightly
        PauseTransition pauseAfterTitleMoveUp = new PauseTransition(Duration.seconds(0.2));

        // slide & fade in buttons
        float animateDuration = 0.3f;
        ParallelTransition slidingInTrans = UiAnimation.slideIn(BUTTON_OFF_SCREEN_POS, BUTTON_ON_SCREEN_POS,
                animateDuration, restartButton, exitButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeIn(0.0f, 1.0f,
                animateDuration, restartButton, exitButton);
        ParallelTransition buttonSlideFadeIn = new ParallelTransition(slidingInTrans, fadeInTrans);

        // set animations in sequence
        SequentialTransition timesUpAnimation = new SequentialTransition(poppingTimesUpTrans, pauseAfterPopTitle,
                moveTitleUpAndBlurGameScreen, pauseAfterTitleMoveUp, buttonSlideFadeIn);

        timesUpAnimation.setOnFinished(e -> {
            UiPart.setUiEffectsOff();
            restartButton.setMouseTransparent(false);
            exitButton.setMouseTransparent(false);
        });
        timesUpAnimation.play();
    }
    /**
     * Close the Times Up Screen when user click on Restart Button.
     * Slide the buttons to the right, Fade out the buttons & Title Label, Unblur Game Screen.
     */
    public void closeTimesUpScreenEffects(GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        float animateDuration = 0.2f;
        ParallelTransition slidingInTrans = UiAnimation.slideOut(BUTTON_OFF_SCREEN_POS, animateDuration,
                restartButton, exitButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeOut(1.0f, 0.0f,
                animateDuration, restartButton, exitButton, timesUpLabel);
        // unblur the gameScreen
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        ParallelTransition gameRestartTransition = new ParallelTransition(slidingInTrans, fadeInTrans, gameScreenUnblur);

        gameRestartTransition.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot()); // "disappear GameOverScreen" as we restart the game again

            UiPart.setUiEffectsOff(); // enable ui interaction again (prevent multiple animation at once)
        });

        gameRestartTransition.play();
    }
    /**
     * Add Select Menu Screen to MainWindow and fade in Select Menu Screen.
     * Slide out the buttons from the center to the right, fade out the buttons & label.
     * On animation finish, remove {@code GameScreen, PauseMenuScreen, GameOverScreen, TimesUpScreen} from {@code MainWindow}.
     */
    public void fromTimesUpScreenToSelectMenu(SelectMenuScreen selectMenuScreen, GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // Make appear SelectMenuScreen -> to prepare for transition into it
        this.showNode(selectMenuScreen.getRoot());

        // Buttons and Title label Sliding & Fading animation
        // Slide out restart & Exit Buttons
        float animateDuration = 0.3f;
        ParallelTransition slideOutTrans = UiAnimation.slideOut(BUTTON_OFF_SCREEN_POS, animateDuration,
                                                                            restartButton, exitButton);
        // Fade out Restart, Exit, Game Over Label
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(1.0f, 0.0f,
                                                        animateDuration, restartButton, exitButton, timesUpLabel);
        // unblur the gameScreen - just in case
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        // Fade in the Select Menu Screen
        ParallelTransition selectMenuButtonSlidingInEffects = selectMenuScreen.openSelectMenuEffects(animateDuration);

        // combine all animation :)
        ParallelTransition combined = new ParallelTransition(slideOutTrans, fadeOutTrans, gameScreenUnblur,
                                                             selectMenuButtonSlidingInEffects);
        combined.setOnFinished(e -> {
            // Hide/Remove Unused nodes
            UiPart.hideNode(gameScreen.getRoot(), this.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again (disable it to prevent multiple animation at once)
        });
        combined.play();
    }
}
