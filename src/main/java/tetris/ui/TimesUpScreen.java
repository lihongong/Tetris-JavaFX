package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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

    public void openTimesUpScreenEffects(GameState gameState, GameScreen gameScreen) {
        gameScreen.setBlurEffects();

        setButtonOnMouseEntered(gameState);

        int fromX = 500;

        this.getRoot().setVisible(true);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(0.1), restartButton);
        slide2.setFromX(fromX);
        slide2.setToX(0);

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(0.1), exitButton);
        slide3.setFromX(fromX);
        slide3.setToX(0);

        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.1), restartButton);
        fade2.setFromValue(0.0);
        fade2.setToValue(1.0);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(0.1), exitButton);
        fade3.setFromValue(0.0);
        fade3.setToValue(1.0);

        FadeTransition timesUpLabelFadeIn = new FadeTransition(Duration.seconds(0.1), timesUpLabel);
        timesUpLabelFadeIn.setFromValue(0.0);
        timesUpLabelFadeIn.setToValue(1.0);

        ParallelTransition combined = new ParallelTransition(slide2, fade2, slide3, fade3, timesUpLabelFadeIn);

        combined.setOnFinished(e -> {
            gameState.isTransitionEffectsOn = false;
        });

        combined.play();
    }

    public void closeTimesUpScreenEffects(GameState gameState, GameScreen gameScreen) {
        int fromX = 500;

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(0.2), restartButton);
        //slide2.setFromX(0);
        slide2.setToX(fromX); // Move 200px

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(0.2), exitButton);
        //slide3.setFromX(0);
        slide3.setToX(fromX); // Move 200px

        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.2), restartButton);
        fade2.setFromValue(1.0);
        fade2.setToValue(0.2);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(0.2), exitButton);
        fade3.setFromValue(1.0);
        fade3.setToValue(0.2);

        FadeTransition timesUpLabelFadeOut = new FadeTransition(Duration.seconds(0.2), timesUpLabel);
        timesUpLabelFadeOut.setFromValue(1.0);
        timesUpLabelFadeOut.setToValue(0.2);

        Animation gameScreenRemoveBlur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition(slide2, fade2, slide3, fade3, timesUpLabelFadeOut, gameScreenRemoveBlur);
        // Hide root node AFTER animation completes

        combined.setOnFinished(e -> {
            this.getRoot().setVisible(false);

            gameState.isTransitionEffectsOn = false;
        });

        combined.play();
    }

    public ParallelTransition exitTimesUpScreenEffects(GameState gameState) {
        int fromX = 500;

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(0.3), restartButton);
        //slide2.setFromX(0);
        slide2.setToX(fromX); // Move 200px

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(0.3), exitButton);
        //slide3.setFromX(0);
        slide3.setToX(fromX); // Move 200px

        FadeTransition fade2 = new FadeTransition(Duration.seconds(0.3), restartButton);
        fade2.setFromValue(1.0);
        fade2.setToValue(0.2);

        FadeTransition fade3 = new FadeTransition(Duration.seconds(0.3), exitButton);
        fade3.setFromValue(1.0);
        fade3.setToValue(0.2);

        FadeTransition timesUpLabelFadeOut = new FadeTransition(Duration.seconds(0.3), timesUpLabel);
        timesUpLabelFadeOut.setFromValue(1.0);
        timesUpLabelFadeOut.setToValue(0.2);

        ParallelTransition combined = new ParallelTransition(slide2, fade2, slide3, fade3, timesUpLabelFadeOut);
        // Hide root node AFTER animation completes

        return combined;
    }

    private void setButtonOnMouseEntered(GameState gameState) {
        // restart button
        restartButton.setOnMouseEntered(e -> {
            if (!gameState.isTransitionEffectsOn) {
                if (restartButtonHoverAnimation != null) {
                    restartButtonHoverAnimation.stop();
                }

                restartButtonHoverAnimation = new TranslateTransition(Duration.millis(200), restartButton);
                restartButtonHoverAnimation.setToX(-80);
                restartButtonHoverAnimation.play();
            }
        });
        restartButton.setOnMouseExited(e -> {
            if (!gameState.isTransitionEffectsOn) {
                if (restartButtonHoverAnimation != null) {
                    restartButtonHoverAnimation.stop();
                }
                restartButtonHoverAnimation = new TranslateTransition(Duration.millis(250), restartButton);
                restartButtonHoverAnimation.setToX(0);
                restartButtonHoverAnimation.play();
            }
        });
        // exit button
        exitButton.setOnMouseEntered(e -> {
            if (!gameState.isTransitionEffectsOn) {
                if (exitButtonHoverAnimation != null) {
                    exitButtonHoverAnimation.stop();
                }

                exitButtonHoverAnimation = new TranslateTransition(Duration.millis(200), exitButton);
                exitButtonHoverAnimation.setToX(-80);
                exitButtonHoverAnimation.play();
            }
        });
        exitButton.setOnMouseExited(e -> {
            if (!gameState.isTransitionEffectsOn) {
                if (exitButtonHoverAnimation != null) {
                    exitButtonHoverAnimation.stop();
                }
                exitButtonHoverAnimation = new TranslateTransition(Duration.millis(250), exitButton);
                exitButtonHoverAnimation.setToX(0);
                exitButtonHoverAnimation.play();
            }
        });
    }
}
