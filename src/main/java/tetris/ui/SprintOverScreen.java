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
import tetris.logic.GameState;
import tetris.util.ButtonHandler;
import tetris.util.UiAnimation;

import static tetris.util.TetrisConstants.GAME_OVER_LABEL_CENTER_Y;
import static tetris.util.TetrisConstants.SPRINT_OVER_LABEL_CENTER_Y;

public class SprintOverScreen extends UiPart<VBox> {
    private static final String FXML = "SprintOverScreen.fxml";
    private ButtonHandler restartButtonHandler;
    private ButtonHandler exitButtonHandler;

    @FXML
    Label sprintOverLabel;
    @FXML
    Button restartButton;
    @FXML
    Button exitButton;
    private TranslateTransition restartButtonHoverAnimation;
    private TranslateTransition exitButtonHoverAnimation;

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

        init();

        ScaleTransition s1 = new ScaleTransition(Duration.seconds(1), sprintOverLabel);
        s1.setFromX(0.7);
        s1.setToX(1.3);
        s1.setFromY(0.7);
        s1.setToY(1.3);
        ScaleTransition s2 = new ScaleTransition(Duration.seconds(4), sprintOverLabel);
        s2.setToX(0.8);
        s2.setToY(0.8);
        SequentialTransition sq = new SequentialTransition(s1, s2);

        ParallelTransition sg = gameScreen.zoomIn();

        ParallelTransition p = new ParallelTransition(sq, sg);
        p.play();

    }
    private void init() {
        // Times Up Screen Appear!!! :)
        this.showNode(this.getRoot());

        sprintOverLabel.setOpacity(1.0);
        sprintOverLabel.setTranslateY(SPRINT_OVER_LABEL_CENTER_Y); // place the KO! label at the middle of the gameScreen at first
        restartButton.setOpacity(0); // hide buttons at first :)
        exitButton.setOpacity(0);
        // so that when sliding in animation finishes, the mouse will be detected by buttons as "enter" and animate
        // the "pop left of the buttons", even when the mouse is already inside the button. This is because we disable
        // animation when buttons are sliding in, so the buttons will already be "entered" but no "on enter" animation
        // will happen
        restartButton.setMouseTransparent(true);
        exitButton.setMouseTransparent(true);
    }

    public void closeTimesUpScreenEffects(GameScreen gameScreen, Runnable runnable) {
        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.0f;
        float animateDuration = 0.2f;

        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration, restartButton,
                exitButton, sprintOverLabel);
        // unblur the gameScreen
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();
        ParallelTransition buttonsAndLabelOutEffects = new ParallelTransition(slideOutTrans, fadeInTrans, gameScreenUnblur);

        PauseTransition waitHalfSecond = new PauseTransition(Duration.seconds(0.5));

        ParallelTransition gameScreenFallFromTop = gameScreen.gameScreenUiFlyDownFromTop();

        SequentialTransition gameRestartTransition = new SequentialTransition(buttonsAndLabelOutEffects, waitHalfSecond,
                gameScreenFallFromTop);

        gameRestartTransition.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot()); // "disappear GameOverScreen" as we restart the game again
            UiPart.setUiEffectsOff(); // enable ui interaction again (prevent multiple animation at once)
            runnable.run();
        });

        gameRestartTransition.play();
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

        FadeTransition timesUpLabelFadeOut = new FadeTransition(Duration.seconds(0.3), sprintOverLabel);
        timesUpLabelFadeOut.setFromValue(1.0);
        timesUpLabelFadeOut.setToValue(0.2);

        ParallelTransition combined = new ParallelTransition(slide2, fade2, slide3, fade3, timesUpLabelFadeOut);
        // Hide root node AFTER animation completes

        return combined;
    }

    private void setButtonOnMouseEntered(GameState gameState) {
        // restart button
        restartButton.setOnMouseEntered(e -> {
            if (!this.getRoot().isDisable()) {
                if (restartButtonHoverAnimation != null) {
                    restartButtonHoverAnimation.stop();
                }

                restartButtonHoverAnimation = new TranslateTransition(Duration.millis(200), restartButton);
                restartButtonHoverAnimation.setToX(-80);
                restartButtonHoverAnimation.play();
            }
        });
        restartButton.setOnMouseExited(e -> {
            if (!this.getRoot().isDisable()) {
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
            if (!this.getRoot().isDisable()) {
                if (exitButtonHoverAnimation != null) {
                    exitButtonHoverAnimation.stop();
                }

                exitButtonHoverAnimation = new TranslateTransition(Duration.millis(200), exitButton);
                exitButtonHoverAnimation.setToX(-80);
                exitButtonHoverAnimation.play();
            }
        });
        exitButton.setOnMouseExited(e -> {
            if (!this.getRoot().isDisable()) {
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
