package tetris.ui;

import com.sun.tools.javac.Main;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.logic.GameState;
import tetris.util.ButtonHandler;

public class PauseMenuScreen extends UiPart<VBox> {
    private static final String FXML = "PauseMenu.fxml";
    //private GameController gameController;
    ButtonHandler resumeButtonHandler;
    ButtonHandler restartButtonHandler;
    ButtonHandler exitButtonHandler;
    @FXML
    private Label pausedLabel;
    @FXML
    private Button resumeButton;
    @FXML
    private Button restartButton;
    @FXML
    private Button exitButton;
    private TranslateTransition resumeButtonHoverAnimation;
    private TranslateTransition restartButtonHoverAnimation;
    private TranslateTransition exitButtonHoverAnimation;

    public PauseMenuScreen() {
        super(FXML);
    }

    public void setResumeRestartExitButtonHandler(ButtonHandler resumeButtonHandler, ButtonHandler restartButtonHandler,
                                                  ButtonHandler exitButtonHandler) {
        this.resumeButtonHandler = resumeButtonHandler;
        this.restartButtonHandler = restartButtonHandler;
        this.exitButtonHandler = exitButtonHandler;
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
    public void handleExitButton() {
        exitButtonHandler.handle();
    }
    @FXML
    public void handleResumeButtonMouseEnter(MouseEvent me) {
        if (!this.getRoot().isDisable()) {
            resumeButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(resumeButton, resumeButtonHoverAnimation);
        }
    }
    @FXML
    public void handleResumeButtonMouseExit(MouseEvent me) {
        if (!this.getRoot().isDisable()) {
            resumeButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(resumeButton, resumeButtonHoverAnimation);
        }
    }
    @FXML
    public void handleRestartButtonMouseEnter(MouseEvent me) {
        if (!this.getRoot().isDisable()) {
            restartButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(restartButton, restartButtonHoverAnimation);
        }
    }
    @FXML
    public void handleRestartButtonMouseExit(MouseEvent me) {
        if (!this.getRoot().isDisable()) {
            restartButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(restartButton, restartButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseEnter(MouseEvent me) {
        if (!this.getRoot().isDisable()) {
            exitButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(exitButton, exitButtonHoverAnimation);
        }
    }
    @FXML
    public void handleExitButtonMouseExit(MouseEvent me) {
        if (!this.getRoot().isDisable()) {
            exitButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(exitButton, exitButtonHoverAnimation);
        }
    }

    public void openPauseMenuEffects(GameState gameState, GameScreen gameScreen) {

        if (this.getRoot().isDisable()) {
            return;
        }
        this.getRoot().setDisable(true); // prevent multiple animation happening at once

        gameScreen.setBlurEffects();

        int fromX = 500;

        this.getRoot().setVisible(true);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.1), resumeButton);
        slide.setFromX(fromX);
        slide.setToX(0);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(0.1), restartButton);
        slide2.setFromX(fromX);
        slide2.setToX(0);

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(0.1), exitButton);
        slide3.setFromX(fromX);
        slide3.setToX(0);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.1), resumeButton);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.1), restartButton);
        fade2.setFromValue(0.0);
        fade2.setToValue(1.0);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(0.1), exitButton);
        fade3.setFromValue(0.0);
        fade3.setToValue(1.0);

        FadeTransition pausedLabelFadeIn = new FadeTransition(Duration.seconds(0.1), pausedLabel);
        pausedLabelFadeIn.setFromValue(0.0);
        pausedLabelFadeIn.setToValue(1.0);

        ParallelTransition combined = new ParallelTransition(slide, fade, slide2, fade2, slide3, fade3, pausedLabelFadeIn);

        combined.setOnFinished(e -> {
            //gameState.isTransitionEffectsOn = false;
            this.getRoot().setDisable(false);
        });

        combined.play();

    }
    public ParallelTransition closePauseMenuEffects(GameState gameState, GameScreen gameScreen) {
        int fromX = 500;

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.2), resumeButton);
        //slide.setFromX(0);
        slide.setToX(fromX); // Move 200px

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(0.2), restartButton);
        //slide2.setFromX(0);
        slide2.setToX(fromX); // Move 200px

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(0.2), exitButton);
        //slide3.setFromX(0);
        slide3.setToX(fromX); // Move 200px

        FadeTransition fade = new FadeTransition(Duration.seconds(0.2), resumeButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.2);

        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.2), restartButton);
        fade2.setFromValue(1.0);
        fade2.setToValue(0.2);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(0.2), exitButton);
        fade3.setFromValue(1.0);
        fade3.setToValue(0.2);

        FadeTransition pausedLabelFadeOut = new FadeTransition(Duration.seconds(0.2), pausedLabel);
        pausedLabelFadeOut.setFromValue(1.0);
        pausedLabelFadeOut.setToValue(0.2);

        ParallelTransition combined = new ParallelTransition(slide, slide2, fade, fade2, slide3, fade3, pausedLabelFadeOut);
        // Hide root node AFTER animation completes
        return combined;

    }

    public ParallelTransition exitPauseMenuEffects(GameState gameState) {

        int fromX = 500;

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.3), resumeButton);
        //slide.setFromX(0);
        slide.setToX(fromX); // Move 200px

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(0.3), restartButton);
        //slide2.setFromX(0);
        slide2.setToX(fromX); // Move 200px

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(0.3), exitButton);
        //slide3.setFromX(0);
        slide3.setToX(fromX); // Move 200px

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), resumeButton);
        fade.setFromValue(1.0);
        fade.setToValue(0.2);

        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.3), restartButton);
        fade2.setFromValue(1.0);
        fade2.setToValue(0.2);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(0.3), exitButton);
        fade3.setFromValue(1.0);
        fade3.setToValue(0.2);

        FadeTransition pausedLabelFadeOut = new FadeTransition(Duration.seconds(0.3), pausedLabel);
        pausedLabelFadeOut.setFromValue(1.0);
        pausedLabelFadeOut.setToValue(0.2);

        ParallelTransition combined = new ParallelTransition(slide, slide2, fade, fade2, slide3, fade3, pausedLabelFadeOut);
        // Hide root node AFTER animation completes

        return combined;
    }
}
