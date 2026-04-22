package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.logic.GameState;
import tetris.util.ButtonHandler;

public class GameOverScreen extends UiPart<VBox> {
    private static final String FXML = "GameOverScreen.fxml";
    private ButtonHandler restartButtonHandler;
    private ButtonHandler exitButtonHandler;
    @FXML
    Label gameOverLabel;
    @FXML
    Button restartButton;
    @FXML
    Button exitButton;
    private TranslateTransition restartButtonHoverAnimation;
    private TranslateTransition exitButtonHoverAnimation;

    public GameOverScreen() {
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
     * Shows the Game over Screen by Sliding in Buttons from the right to the center, fading in the Buttons & Label.
     * Blur the Game Screen.
     */
    public void openGameOverScreenEffects(GameScreen gameScreen) {
        // prevent multiple effects at once
        if (this.isUiEffectsOn()) {
            return;
        }
        // don't set effects is on so that when Pause Menu Buttons shows up, they will immediately respond when mouse
        // on enter -> nicer animation style
        // SIDE EFFECT: Without setting isUiEffectOn as True, other animation can happen whilst the current effect is
        // going on, e.g. Game Over Screen is Fading/Sliding in, immediately press R, the gae over screen will immediately
        // get faded out/ slide out -> more responsive tho :)

        // blur the gameScreen in the background
        gameScreen.setBlurEffects();

        // Game Over Screen Appears!!! :)
        this.getRoot().setVisible(true);

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

        FadeTransition gameOverLabelFadeIn = new FadeTransition(Duration.seconds(animateDuration), gameOverLabel);
        gameOverLabelFadeIn.setFromValue(fadeInFrom);
        gameOverLabelFadeIn.setToValue(fadeInTo);

        ParallelTransition combined = new ParallelTransition(slide1, fadeIn1, slide2, fadeIn2, gameOverLabelFadeIn);

        combined.setOnFinished(e -> {
            this.setUiEffectsOff();
        });
        combined.play();
    }
    /**
     * Close the Game Over Screen when user click on Restart Button.
     * Slide the buttons to the right, Fade out the buttons & Title Label, Unblur Game Screen.
     */
    public void closeGameOverScreenEffects(GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeInFrom = 0.2f;
        float animateDuration = 0.2f;

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), restartButton);
        slide1.setToX(toX); // Move 500px to the right

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), exitButton);
        slide2.setToX(toX); // Move 500px to the right

        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), restartButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeInFrom);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), exitButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeInFrom);

        FadeTransition gameOverLabelFadeOut = new FadeTransition(Duration.seconds(animateDuration), gameOverLabel);
        gameOverLabelFadeOut.setFromValue(fadeOutFrom);
        gameOverLabelFadeOut.setToValue(fadeInFrom);

        // unblur the gameScreen
        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition(slide1, fadeOut1, slide2, fadeOut2, gameOverLabelFadeOut,
                                                             gameScreenRemoveBlur);

        combined.setOnFinished(e -> {
            this.getRoot().setVisible(false); // "disappear GameOverScreen" as we restart the game again

            this.setUiEffectsOff(); // enable ui interaction again (prevent multiple animation at once)
        });

        combined.play();
    }
    /**
     * Add Select Menu Screen to MainWindow and fade in Select Menu Screen.
     * Slide out the buttons from the center to the right, fade out the buttons & label.
     * On animation finish, remove {@code GameScreen, PauseMenuScreen, GameOverScreen, TimesUpScreen} from {@code MainWindow}.
     */
    public void exitGameOverScreenEffects(MainWindow mainWindow, SelectMenuScreen selectMenuScreen,
                                                        GameScreen gameScreen, PauseMenuScreen pauseMenuScreen,
                                                        TimesUpScreen timesUpScreen) {
        // prevent multiple animation happening at once
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // add SelectMenuScreen into mainWindow -> to prepare for transition into it
        mainWindow.addNodesToRoot(selectMenuScreen.getRoot());

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
        FadeTransition fadeOutGameOverLabel = new FadeTransition(Duration.seconds(animateDuration), gameOverLabel);
        fadeOutGameOverLabel.setFromValue(fadeOutFrom);
        fadeOutGameOverLabel.setToValue(fadeOutTo);

        // Fade in the Select Menu Screen
        FadeTransition fadeInSelectMenuScreen = new FadeTransition(Duration.seconds(animateDuration), selectMenuScreen.getRoot());
        fadeInSelectMenuScreen.setFromValue(0.0);
        fadeInSelectMenuScreen.setToValue(1.0);

        // unblur the gameScreen - just in case
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();
        // combine all animation :)
        ParallelTransition combined = new ParallelTransition(slide1, fadeOut1, slide2, fadeOut2, fadeOutGameOverLabel,
                                                             fadeInSelectMenuScreen, gameScreenUnblur);

        combined.setOnFinished(e -> {
            // Hide/Remove Unused nodes from MainWindow
            mainWindow.removeNodesFromRoot(pauseMenuScreen.getRoot(), gameScreen.getRoot(),
                    this.getRoot(), timesUpScreen.getRoot());

            this.setUiEffectsOff(); // enable ui interaction again
        });

        combined.play();
    }
}
