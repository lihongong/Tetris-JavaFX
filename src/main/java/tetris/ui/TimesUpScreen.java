package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.logic.GameController;
import tetris.logic.GameState;
import tetris.util.ButtonHandler;
import tetris.util.UiAnimation;

import java.util.ArrayList;

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
        // don't set effects is on so that when Times up Buttons shows up, they will immediately respond when mouse
        // on enter -> nicer animation style
        // SIDE EFFECT: Without setting isUiEffectOn as True, other animation can happen whilst the current effect is
        // going on, e.g. Times Up Screen is Fading/Sliding in, immediately press R, the times up screen will immediately
        // get faded out/ slide out

        // blur the game screen in the background
        gameScreen.setBlurEffects();

        // Times Up Screen Appear!!! :)
        this.showNode(this.getRoot());

        int fromX = 500;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.1f;
        // may change for times up opening effect!!!
        ArrayList<TranslateTransition> slidingInTrans = UiAnimation.slideIn(fromX, toX, animateDuration,
                restartButton, exitButton);
        ArrayList<FadeTransition> fadeInTrans = UiAnimation.fadeIn(fadeInFrom, fadeInTo, animateDuration,
                restartButton, exitButton, timesUpLabel);
        ParallelTransition combined = new ParallelTransition();
        combined.getChildren().addAll(slidingInTrans);
        combined.getChildren().addAll(fadeInTrans);

        combined.setOnFinished(e -> {
            UiPart.setUiEffectsOff();
        });
        combined.play();
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

        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.2f;

        ArrayList<TranslateTransition> slidingInTrans = UiAnimation.slideOut(toX, animateDuration,
                restartButton, exitButton);
        ArrayList<FadeTransition> fadeInTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration,
                restartButton, exitButton, timesUpLabel);
        // unblur the gameScreen
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition();
        combined.getChildren().addAll(slidingInTrans);
        combined.getChildren().addAll(fadeInTrans);
        combined.getChildren().addAll(gameScreenUnblur);

        combined.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot()); // "disappear GameOverScreen" as we restart the game again

            UiPart.setUiEffectsOff(); // enable ui interaction again (prevent multiple animation at once)
        });

        combined.play();
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
        int toX = 500; // move 500 px
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.3f;

        // Slide out restart & Exit Buttons
        ArrayList<TranslateTransition> slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        // Fade out Restart, Exit, Game Over Label
        ArrayList<FadeTransition> fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration,
                restartButton, exitButton, timesUpLabel);
        // unblur the gameScreen - just in case
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        // Fade in the Select Menu Screen
        ParallelTransition selectMenuButtonSlidingInEffects = selectMenuScreen.openSelectMenuEffects(animateDuration);

        // combine all animation :)
        ParallelTransition combined = new ParallelTransition();
        combined.getChildren().addAll(slideOutTrans);
        combined.getChildren().addAll(fadeOutTrans);
        combined.getChildren().addAll(gameScreenUnblur, selectMenuButtonSlidingInEffects);

        combined.setOnFinished(e -> {
            // Hide/Remove Unused nodes
            UiPart.hideNode(gameScreen.getRoot(), this.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again (disable it to prevent multiple animation at once)
        });

        combined.play();
    }
}
