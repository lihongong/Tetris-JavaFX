package tetris.ui;

import com.sun.tools.javac.Main;
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

public class SelectMenuScreen extends UiPart<VBox> {
    private static final String FXML = "SelectMenu.fxml";

    private ButtonHandler relaxButtonHandler;
    private ButtonHandler sprintButtonHandler;
    private ButtonHandler blitzButtonHandler;
    private ButtonHandler exitButtonHandler;
    @FXML
    private VBox mainLayout;
    @FXML
    private Label titleLabel;
    @FXML
    private Button relaxButton;
    @FXML
    private Button sprintButton;
    @FXML
    private Button blitzButton;
    @FXML
    private Button exitButton;
    private TranslateTransition relaxButtonHoverAnimation;
    private TranslateTransition sprintButtonHoverAnimation;
    private TranslateTransition blitzButtonHoverAnimation;
    private TranslateTransition exitButtonHoverAnimation;

    public SelectMenuScreen() {
        super(FXML);
    }

    public void setSelectMenuButtonsHandler(ButtonHandler relaxButtonHandler, ButtonHandler sprintButtonHandler,
                                            ButtonHandler blitzButtonHandler, ButtonHandler exitButtonHandler) {
        this.relaxButtonHandler = relaxButtonHandler;
        this.sprintButtonHandler = sprintButtonHandler;
        this.blitzButtonHandler = blitzButtonHandler;
        this.exitButtonHandler = exitButtonHandler;
    }
    @FXML
    public void handleRelaxButton() {
        relaxButtonHandler.handle();
    }
    @FXML
    public void handleSprintButton() {
        sprintButtonHandler.handle();
    }
    @FXML
    public void handleBlitzButton() {
        blitzButtonHandler.handle();
    }
    @FXML
    public void handleExitButton() {
        exitButtonHandler.handle();
    }
    @FXML
    public void handleRelaxButtonMouseEnter(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            relaxButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(relaxButton, relaxButtonHoverAnimation);
        }
    }
    @FXML
    public void handleRelaxButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            relaxButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(relaxButton, relaxButtonHoverAnimation);
        }
    }
    @FXML
    public void handleSprintButtonMouseEnter(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            sprintButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(sprintButton, sprintButtonHoverAnimation);
        }
    }
    @FXML
    public void handleSprintButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            sprintButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(sprintButton, sprintButtonHoverAnimation);
        }
    }
    @FXML
    public void handleBlitzButtonMouseEnter(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            blitzButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(blitzButton, blitzButtonHoverAnimation);
        }
    }
    @FXML
    public void handleBlitzButtonMouseExit(MouseEvent me) {
        if (!this.isUiEffectsOn()) {
            blitzButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(blitzButton, blitzButtonHoverAnimation);
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
     * Add startMenuScreen to MainWindow, fade In the Start menu, remove SelectMenu at the end of effect.
     * @param mainWindow
     * @param startMenuScreen
     */
    public void exitSelectMenuScreenEffect(MainWindow mainWindow, StartMenuScreen startMenuScreen) {
        // prevent multiple click on Buttons and cause bugs :(((
        // e.g. multiple startMenuScreen instances added into mainWindow
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // add start menu screen
        this.showNode(startMenuScreen.getRoot());

        // transition
        // select menu buttons slide out effects
        ParallelTransition combined = this.slideOutButtons();

        FadeTransition fadeInStartMenuScreen = new FadeTransition(Duration.seconds(0.4), startMenuScreen.getRoot());
        fadeInStartMenuScreen.setFromValue(0.0);
        fadeInStartMenuScreen.setToValue(1.0);

        combined.getChildren().add(fadeInStartMenuScreen);

        combined.setOnFinished(e -> {
            // hide select menu at the end
            this.hideNode(this.getRoot());

            this.setUiEffectsOff();
        });

        combined.play();
    }

    /**
     * Renders the gameScreen from SelectMenuScreen.
     * Set random background for gameScreen. add gameScreen, gameOverScreen, timesUpScreen, pauseMenuScreen to MainWindow.
     * Fade in gameScreen and then remove Select Menu Screen at the end of effects
     */
    public void renderGameScreenFromSelectMenu(GameScreen gameScreen) {
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // set background image for gameplay
        gameScreen.setRandomBackGroundImage();

        // transition effects
        // make GameScreen appear
        this.showNode(gameScreen.getRoot());
        gameScreen.getRoot().setEffect(null); // clear the blur effects just in case.

        FadeTransition fadeInGameScreen = new FadeTransition(Duration.seconds(1.0), gameScreen.getRoot());
        fadeInGameScreen.setFromValue(0.0);
        fadeInGameScreen.setToValue(1.0);

        // slide out the select menu buttons
        ParallelTransition combined = this.slideOutButtons();

        combined.getChildren().add(fadeInGameScreen);
        combined.setOnFinished(e -> {
            // handle nodes
            this.hideNode(this.getRoot());

            this.setUiEffectsOff(); // prevent multiple animation happening at once
        });
        combined.play();
    }

    public ParallelTransition openSelectMenuEffects(float animateDuration) {
        this.showNode(this.getRoot());

        FadeTransition fadeInSelectMenu = new FadeTransition(Duration.seconds(animateDuration), this.getRoot());
        fadeInSelectMenu.setFromValue(0.0);
        fadeInSelectMenu.setToValue(1.0);

        ParallelTransition combined = this.slideInButtons();
        combined.getChildren().add(fadeInSelectMenu);
        return combined;
    }

    private ParallelTransition slideInButtons() {
        int fromX = 1200;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.27f;

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), relaxButton);
        slide1.setFromX(fromX);
        slide1.setToX(toX);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), sprintButton);
        slide2.setFromX(fromX);
        slide2.setToX(toX);

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), blitzButton);
        slide3.setFromX(fromX);
        slide3.setToX(toX);

        TranslateTransition slide4 = new TranslateTransition(Duration.seconds(animateDuration), exitButton);
        slide4.setFromX(fromX);
        slide4.setToX(toX);

        FadeTransition fadeIn1 = new FadeTransition(Duration.seconds(animateDuration), relaxButton);
        fadeIn1.setFromValue(fadeInFrom);
        fadeIn1.setToValue(fadeInTo);

        FadeTransition fadeIn2 = new FadeTransition(Duration.seconds(animateDuration), sprintButton);
        fadeIn2.setFromValue(fadeInFrom);
        fadeIn2.setToValue(fadeInTo);

        FadeTransition fadeIn3 = new FadeTransition(Duration.seconds(animateDuration), blitzButton);
        fadeIn3.setFromValue(fadeInFrom);
        fadeIn3.setToValue(fadeInTo);

        FadeTransition fadeIn4 = new FadeTransition(Duration.seconds(animateDuration), exitButton);
        fadeIn4.setFromValue(fadeInFrom);
        fadeIn4.setToValue(fadeInTo);

        FadeTransition titleLabelFadeIn = new FadeTransition(Duration.seconds(animateDuration), titleLabel);
        titleLabelFadeIn.setFromValue(fadeInFrom);
        titleLabelFadeIn.setToValue(fadeInTo);

        return new ParallelTransition(slide1, slide2, slide3, slide4, fadeIn1, fadeIn2, fadeIn3, fadeIn4, titleLabelFadeIn);
    }

    private ParallelTransition slideOutButtons() {
        int toX = 1200;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.3f;

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), relaxButton);
        slide1.setToX(toX);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), sprintButton);
        slide2.setToX(toX);

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), blitzButton);
        slide3.setToX(toX);

        TranslateTransition slide4 = new TranslateTransition(Duration.seconds(animateDuration), exitButton);
        slide4.setToX(toX);

        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), relaxButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeOutTo);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), sprintButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeOutTo);

        FadeTransition fadeOut3 = new FadeTransition(Duration.seconds(animateDuration), blitzButton);
        fadeOut3.setFromValue(fadeOutFrom);
        fadeOut3.setToValue(fadeOutTo);

        FadeTransition fadeOut4 = new FadeTransition(Duration.seconds(animateDuration), exitButton);
        fadeOut4.setFromValue(fadeOutFrom);
        fadeOut4.setToValue(fadeOutTo);

        FadeTransition titleLabelFadeOut = new FadeTransition(Duration.seconds(animateDuration), titleLabel);
        titleLabelFadeOut.setFromValue(fadeOutFrom);
        titleLabelFadeOut.setToValue(fadeOutTo);

        return new ParallelTransition(slide1, slide2, slide3, slide4, fadeOut1, fadeOut2, fadeOut3,
                fadeOut4, titleLabelFadeOut);
    }
}
