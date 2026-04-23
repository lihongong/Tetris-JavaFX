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
import tetris.util.ButtonHandler;

public class PauseSprintScreen extends UiPart<VBox> {
    private static final String FXML = "PauseMenu.fxml";
    //private GameController gameController;
    ButtonHandler resumeButtonHandler;
    ButtonHandler restartButtonHandler;
    ButtonHandler otherModesButtonHandler;
    @FXML
    private Label pausedLabel;
    @FXML
    private Button resumeButton;
    @FXML
    private Button restartButton;
    @FXML
    private Button otherModesButton;
    private TranslateTransition resumeButtonHoverAnimation;
    private TranslateTransition restartButtonHoverAnimation;
    private TranslateTransition otherModesButtonHoverAnimation;

    public PauseSprintScreen() {
        super(FXML);
    }

    public void setResumeRestartOthersButtonHandler(ButtonHandler resumeButtonHandler, ButtonHandler restartButtonHandler,
                                                    ButtonHandler otherModesButtonHandler) {
        this.resumeButtonHandler = resumeButtonHandler;
        this.restartButtonHandler = restartButtonHandler;
        this.otherModesButtonHandler = otherModesButtonHandler;
    }

    @FXML
    public void handleResumeButton() {
        resumeButtonHandler.handle();
    }
    @FXML
    public void handleRestartButton() {
        restartButtonHandler.handle();
    }
    @FXML
    public void handleOtherModesButton() {
        otherModesButtonHandler.handle();
    }
    @FXML
    public void handleResumeButtonMouseEnter(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            resumeButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(resumeButton, resumeButtonHoverAnimation);
        }
    }
    @FXML
    public void handleResumeButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            resumeButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(resumeButton, resumeButtonHoverAnimation);
        }
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
            otherModesButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(otherModesButton, otherModesButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            otherModesButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(otherModesButton, otherModesButtonHoverAnimation);
        }
    }

    public void openPauseMenuEffects(GameScreen gameScreen) {
        // must still check for other still active effects -> prevent bug where the Pause Buttons sliding left while
        // going right bug as mouse reenters it
        if (this.isUiEffectsOn()) {
            return;
        }
        // don't set effects is on so that when Pause Menu Buttons shows up, they will immediately respond when mouse
        // on enter -> nicer animation style
        // SIDE EFFECT: Without setting isUiEffectOn as True, other animation can happen whilst the current effect is
        // going on, e.g. Pause Menu Screen is Fading/Sliding in, immediately press Esc, the pause screen will immediately
        // get faded out/ slide out -> more responsive tho :)

        // blur the game screen in the background
        gameScreen.setBlurEffects();

        int fromX = 500;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.1f;

        this.showNode(this.getRoot());

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), resumeButton);
        slide1.setFromX(fromX);
        slide1.setToX(toX);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide2.setFromX(fromX);
        slide2.setToX(toX);

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), otherModesButton);
        slide3.setFromX(fromX);
        slide3.setToX(toX);

        FadeTransition fadeIn1 = new FadeTransition(Duration.seconds(animateDuration), resumeButton);
        fadeIn1.setFromValue(fadeInFrom);
        fadeIn1.setToValue(fadeInTo);

        FadeTransition fadeIn2 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeIn2.setFromValue(fadeInFrom);
        fadeIn2.setToValue(fadeInTo);

        FadeTransition fadeIn3 = new FadeTransition(Duration.seconds(animateDuration), otherModesButton);
        fadeIn3.setFromValue(fadeInFrom);
        fadeIn3.setToValue(fadeInTo);

        FadeTransition pausedLabelFadeIn = new FadeTransition(Duration.seconds(animateDuration), pausedLabel);
        pausedLabelFadeIn.setFromValue(fadeInFrom);
        pausedLabelFadeIn.setToValue(fadeInTo);

        ParallelTransition combined = new ParallelTransition(slide1, fadeIn1, slide2, fadeIn2, slide3, fadeIn3,
                pausedLabelFadeIn);

        combined.setOnFinished(e -> {
            this.setUiEffectsOff();
        });
        combined.play();
    }
    /**
     * Closes the pause menu screen when clicking the Resume or Restart button.
     * Slides the buttons to the right and fade them out. Unblur the gameScreen.
     * Run the runnable on animation finish.
     * @param runnable is the code to be executed when effects ends, usually it is to resume the model when user click
     *                 Resume button.
     */
    public void closePauseMenuEffects(GameScreen gameScreen, Runnable runnable) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.2f;

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), resumeButton);
        slide1.setToX(toX); // Move 500px to the right

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide2.setToX(toX); // Move 500px to the right

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), otherModesButton);
        slide3.setToX(toX); // Move 500px to the right

        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), resumeButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeOutTo);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeOutTo);

        FadeTransition fadeOut3 = new FadeTransition(Duration.seconds(animateDuration), otherModesButton);
        fadeOut3.setFromValue(fadeOutFrom);
        fadeOut3.setToValue(fadeOutTo);

        FadeTransition pausedLabelFadeOut = new FadeTransition(Duration.seconds(animateDuration), pausedLabel);
        pausedLabelFadeOut.setFromValue(fadeOutFrom);
        pausedLabelFadeOut.setToValue(fadeOutTo);

        // unblur the gameScreen
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition(slide1, slide2, slide3, fadeOut1, fadeOut2, fadeOut3,
                pausedLabelFadeOut, gameScreenUnblur);

        combined.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot());
            UiPart.setUiEffectsOff(); // prevent multiple animation happening at once

            if (runnable != null) {
                runnable.run();
            }
        });

        combined.play();
    }

    public void exitPauseMenuEffects(MainWindow mainWindow, SelectMenuScreen selectMenuScreen,
                                     GameScreen gameScreen, TimesUpScreen timesUpScreen,
                                     GameOverScreen gameOverScreen) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // Make appear SelectMenuScreen
        this.showNode(selectMenuScreen.getRoot());

        // Buttons and Title label Sliding & Fading animation
        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.3f;

        // Slide out Resume, Restart & Exit Buttons
        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), resumeButton);
        slide1.setToX(toX); // Move 500px to the right
        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide2.setToX(toX); // Move 500px to the right
        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), otherModesButton);
        slide3.setToX(toX); // Move 500px to the right

        // Fade out Resume, Restart, Exit, Paused Label
        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), resumeButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeOutTo);
        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeOutTo);
        FadeTransition fadeOut3 = new FadeTransition(Duration.seconds(animateDuration), otherModesButton);
        fadeOut3.setFromValue(fadeOutFrom);
        fadeOut3.setToValue(fadeOutTo);
        FadeTransition pausedLabelFadeOut = new FadeTransition(Duration.seconds(animateDuration), pausedLabel);
        pausedLabelFadeOut.setFromValue(fadeOutFrom);
        pausedLabelFadeOut.setToValue(fadeOutTo);

        // Fade in the Select Menu Screen
        ParallelTransition selectMenuButtonSlidingInEffects = selectMenuScreen.openSelectMenuEffects(animateDuration);

        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition(slide1, slide2, slide3, fadeOut1, fadeOut2, fadeOut3,
                pausedLabelFadeOut, gameScreenUnblur,
                selectMenuButtonSlidingInEffects);

        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot(), gameScreen.getRoot(), gameOverScreen.getRoot(),
                    timesUpScreen.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again
        });

        combined.play();
    }
}

