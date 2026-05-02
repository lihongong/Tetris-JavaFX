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
import tetris.util.UiAnimation;

import java.util.ArrayList;

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
        if (!UiPart.isUiEffectsOn()) {
            relaxButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(relaxButton, relaxButtonHoverAnimation);
        }
    }
    @FXML
    public void handleRelaxButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            relaxButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(relaxButton, relaxButtonHoverAnimation);
        }
    }
    @FXML
    public void handleSprintButtonMouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            sprintButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(sprintButton, sprintButtonHoverAnimation);
        }
    }
    @FXML
    public void handleSprintButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            sprintButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(sprintButton, sprintButtonHoverAnimation);
        }
    }
    @FXML
    public void handleBlitzButtonMouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            blitzButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(blitzButton, blitzButtonHoverAnimation);
        }
    }
    @FXML
    public void handleBlitzButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            blitzButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(blitzButton, blitzButtonHoverAnimation);
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
     * Add startMenuScreen to MainWindow, fade In the Start menu, remove SelectMenu at the end of effect.
     * @param startMenuScreen
     */
    public void fromSelectMenuToStartMenu(StartMenuScreen startMenuScreen) {
        // prevent multiple click on Buttons and cause bugs :(((
        // e.g. multiple startMenuScreen instances added into mainWindow
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // add start menu screen
        this.showNode(startMenuScreen.getRoot());

        // transition
        // select menu buttons slide out effects
        ParallelTransition combined = this.slideOutButtons();

        FadeTransition fadeInStartMenuScreen = UiAnimation.fadeIn(0.0f, 1.0f, 0.4f, startMenuScreen.getRoot());

        combined.getChildren().add(fadeInStartMenuScreen);
        combined.setOnFinished(e -> {
            // hide select menu at the end
            UiPart.hideNode(this.getRoot());

            UiPart.setUiEffectsOff();
        });

        combined.play();
    }
    /**
     * Renders the gameScreen from SelectMenuScreen.
     * Set random background for gameScreen.
     * Fade in gameScreen and then remove Select Menu Screen at the end of effects
     */
    public void fromSelectMenuToGameScreen(GameScreen gameScreen) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // set background image for gameplay
        gameScreen.setRandomBackGroundImage();

        // transition effects
        // make GameScreen appear
        this.showNode(gameScreen.getRoot());
        //gameScreen.getRoot().setEffect(null); // clear the blur effects just in case.
        float animateDuration = 0.8f; // Fade into Game Screen Duration
        FadeTransition fadeInGameScreen = UiAnimation.fadeIn(0.0f, 1.0f, animateDuration, gameScreen.getRoot());

        // slide out the select menu buttons
        ParallelTransition combined = this.slideOutButtons();
        combined.getChildren().add(fadeInGameScreen);
        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot());

            UiPart.setUiEffectsOff(); // prevent multiple animation happening at once
        });
        combined.play();
    }
    /**
     * Renders the Sprint Mode from SelectMenuScreen.
     */
    public void fromSelectMenuToSprintModes(SprintModesScreen sprintModesScreen) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        ParallelTransition fadeInSprintModeScreen = sprintModesScreen.openSprintModesScreen(0.3f);

        ParallelTransition combined = this.slideOutButtons();

        combined.getChildren().add(fadeInSprintModeScreen);
        combined.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot());

            UiPart.setUiEffectsOff();

            // allow select menu button to shift left on mouse enter once animation is over
            sprintModesScreen.setIsButtonMouseTransparent(false);
        });

        combined.play();
    }

    /**
     * Util method to help other screen generate the effects of opening the Select Menu Screen from their screen
     * Don't need use isEffectOn() flag as the screens that use this method should coordinate themselves.
     * @return a {@code ParallelTransition} that contains the fade in and sliding out of buttons effects
     */
    public ParallelTransition openSelectMenuEffects(float animateDuration) {
        setIsButtonMouseTransparent(true);

        this.showNode(this.getRoot());

        FadeTransition fadeInSelectMenu = UiAnimation.fadeIn(0.0f, 1.0f, animateDuration, this.getRoot());

        ParallelTransition combined = this.slideInButtons();

        combined.getChildren().add(fadeInSelectMenu);
        return combined;
    }
    public void setIsButtonMouseTransparent(boolean isTransparent) {
        relaxButton.setMouseTransparent(isTransparent);
        sprintButton.setMouseTransparent(isTransparent);
        blitzButton.setMouseTransparent(isTransparent);
        exitButton.setMouseTransparent(isTransparent);
    }
    /**
     * Animation for Buttons & Label ONLY
     */
    private ParallelTransition slideInButtons() {
        int fromX = 1200;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.27f;

        ParallelTransition slideInTrans = UiAnimation.slideIn(fromX, toX, animateDuration, relaxButton,
                                                                          sprintButton, blitzButton, exitButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeIn(fadeInFrom, fadeInTo, animateDuration, relaxButton,
                                                                   sprintButton, blitzButton, exitButton, titleLabel);
        return new ParallelTransition(slideInTrans, fadeInTrans);
    }
    private ParallelTransition slideOutButtons() {
        int toX = 1200;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.3f;

        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, relaxButton, sprintButton,
                                                                blitzButton, exitButton);
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration, relaxButton,
                                                              sprintButton, blitzButton, exitButton, titleLabel);
        return new ParallelTransition(slideOutTrans, fadeOutTrans);
    }
}
