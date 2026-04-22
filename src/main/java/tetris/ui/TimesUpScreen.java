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
        if (!this.isUiEffectsOn()) {
            restartButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(restartButton, restartButtonHoverAnimation);
        }
    }
    @FXML
    public void handleRestartButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            restartButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(restartButton, restartButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseEnter(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            exitButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(exitButton, exitButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            exitButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(exitButton, exitButtonHoverAnimation);
        }
    }
    /**
     * Shows the Times Up Screen by Sliding in Buttons from the right to the center, fading in the Buttons & Label.
     * Blur the Game Screen.
     */
    public void openTimesUpScreenEffects(GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        // don't set effects is on so that when Pause Menu Buttons shows up, they will immediately respond when mouse
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

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide1.setFromX(fromX);
        slide1.setToX(toX);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), exitButton);
        slide2.setFromX(fromX);
        slide2.setToX(toX);

        FadeTransition fadeIn1 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeIn1.setFromValue(fadeInFrom);
        fadeIn1.setToValue(fadeInTo);

        FadeTransition fadeIn2 = new FadeTransition(Duration.seconds(animateDuration), exitButton);
        fadeIn2.setFromValue(fadeInFrom);
        fadeIn2.setToValue(fadeInTo);

        FadeTransition timesUpLabelFadeIn = new FadeTransition(Duration.seconds(animateDuration), timesUpLabel);
        timesUpLabelFadeIn.setFromValue(fadeInFrom);
        timesUpLabelFadeIn.setToValue(fadeInTo);

        ParallelTransition combined = new ParallelTransition(slide1, fadeIn1, slide2, fadeIn2, timesUpLabelFadeIn);

        combined.setOnFinished(e -> {
            this.setUiEffectsOff();
        });
        combined.play();
    }
    /**
     * Close the Times Up Screen when user click on Restart Button.
     * Slide the buttons to the right, Fade out the buttons & Title Label, Unblur Game Screen.
     */
    public void closeTimesUpScreenEffects(GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.2f;

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide1.setToX(toX); // Move 500px to the right

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), exitButton);
        slide2.setToX(toX); // Move 500px to the right

        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeOutTo);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), exitButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeOutTo);

        FadeTransition timesUpLabelFadeOut = new FadeTransition(Duration.seconds(animateDuration), timesUpLabel);
        timesUpLabelFadeOut.setFromValue(fadeOutFrom);
        timesUpLabelFadeOut.setToValue(fadeOutTo);

        // unblur the gameScreen
        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition(slide1, fadeOut1, slide2, fadeOut2, timesUpLabelFadeOut, gameScreenRemoveBlur);
        // Hide root node AFTER animation completes

        combined.setOnFinished(e -> {
            this.hideNode(this.getRoot()); // disappear the times up screen as we restart the game

            this.setUiEffectsOff(); // prevent multiple animation happening at once
        });

        combined.play();
    }

    /**
     * Add Select Menu Screen to MainWindow and fade in Select Menu Screen.
     * Slide out the buttons from the center to the right, fade out the buttons & label.
     * On animation finish, remove {@code GameScreen, PauseMenuScreen, GameOverScreen, TimesUpScreen} from {@code MainWindow}.
     */
    public void exitTimesUpScreenEffects(MainWindow mainWindow, SelectMenuScreen selectMenuScreen,
                                                       GameScreen gameScreen, PauseMenuScreen pauseMenuScreen,
                                                       GameOverScreen gameOverScreen) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // Make appear SelectMenuScreen -> to prepare for transition into it
        this.showNode(selectMenuScreen.getRoot());

        // Buttons and Title label Sliding & Fading animation
        int toX = 500; // move 500 px
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.3f;

        // Slide out restart & Exit Buttons
        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide1.setToX(toX); // Move 500px from center to the right
        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), exitButton);
        slide2.setToX(toX); // Move 500px from center to the right

        // Fade out Restart, Exit, Game Over Label
        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeOutTo);
        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), exitButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeOutTo);
        FadeTransition fadeOutTimesUpLabel = new FadeTransition(Duration.seconds(animateDuration), timesUpLabel);
        fadeOutTimesUpLabel.setFromValue(fadeOutFrom);
        fadeOutTimesUpLabel.setToValue(fadeOutTo);

        // Fade in the Select Menu Screen
        FadeTransition fadeInSelectMenuScreen = new FadeTransition(Duration.seconds(animateDuration), selectMenuScreen.getRoot());
        fadeInSelectMenuScreen.setFromValue(0.0);
        fadeInSelectMenuScreen.setToValue(1.0);

        // unblur the gameScreen - just in case
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();
        // combine all animation :)
        ParallelTransition combined = new ParallelTransition(slide1, fadeOut1, slide2, fadeOut2, fadeOutTimesUpLabel,
                                                             fadeInSelectMenuScreen, gameScreenUnblur);

        combined.setOnFinished(e -> {
            // Hide/Remove Unused nodes
            this.hideNode(gameScreen.getRoot(), pauseMenuScreen.getRoot(), gameOverScreen.getRoot(), this.getRoot());

            this.setUiEffectsOff(); // enable ui interaction again (disable it to prevent multiple animation at once)
        });

        combined.play();
    }
}
