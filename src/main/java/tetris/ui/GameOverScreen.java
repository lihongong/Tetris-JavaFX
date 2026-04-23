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
import tetris.util.UiAnimation;

import java.util.ArrayList;

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
     * Shows the Game over Screen by Sliding in Buttons from the right to the center, fading in the Buttons & Label.
     * Blur the Game Screen.
     */
    public void openGameOverScreenEffects(GameScreen gameScreen) {
        // prevent multiple effects at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        // don't set effects is on so that when Game Over Buttons shows up, they will immediately respond when mouse
        // on enter -> nicer animation style
        // SIDE EFFECT: Without setting isUiEffectOn as True, other animation can happen whilst the current effect is
        // going on, e.g. Game Over Screen is Fading/Sliding in, immediately press R, the gae over screen will immediately
        // get faded out/ slide out -> more responsive tho :)

        // blur the gameScreen in the background
        gameScreen.setBlurEffects();

        // Game Over Screen Appears!!! :)
        this.showNode(this.getRoot());

        int fromX = 500;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.1f;

        ArrayList<TranslateTransition> slidingInTrans = UiAnimation.slideIn(fromX, toX, animateDuration,
                                                                            restartButton, exitButton);
        ArrayList<FadeTransition> fadeInTrans = UiAnimation.fadeIn(fadeInFrom, fadeInTo, animateDuration,
                                                                   restartButton, exitButton, gameOverLabel);
        ParallelTransition combined = new ParallelTransition();
        combined.getChildren().addAll(slidingInTrans);
        combined.getChildren().addAll(fadeInTrans);

        combined.setOnFinished(e -> {
            UiPart.setUiEffectsOff();
        });
        combined.play();
    }
    /**
     * Close the Game Over Screen when user click on Restart Button.
     * Slide the buttons to the right, Fade out the buttons & Title Label, Unblur Game Screen.
     */
    public void closeGameOverScreenEffects(GameScreen gameScreen) {
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
                                                                   restartButton, exitButton, gameOverLabel);
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
    public void fromGameOverScreenToSprintMenu(SprintModesScreen sprintModesScreen, GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // make appear SelectMenuScreen -> to prepare for transition into it
        this.showNode(sprintModesScreen.getRoot());

        float animateDuration = 0.3f;
        ParallelTransition sprintModesButtonSlidingInEffects = sprintModesScreen.openSprintModesScreen(animateDuration);

        // the base exit transition of game over screen
        ParallelTransition baseEffects = this.exitGameOverBaseEffects(gameScreen, animateDuration);

        ParallelTransition combined = new ParallelTransition(sprintModesButtonSlidingInEffects, baseEffects);
        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot(), gameScreen.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again
        });

        combined.play();
    }

    public void fromGameOverScreenToSelectMenu(SelectMenuScreen selectMenuScreen, GameScreen gameScreen) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // make appear SelectMenuScreen -> to prepare for transition into it
        this.showNode(selectMenuScreen.getRoot());

        float animateDuration = 0.3f;
        ParallelTransition selectMenuButtonSlidingInEffects = selectMenuScreen.openSelectMenuEffects(animateDuration);

        // the base exit transition of game over screen
        ParallelTransition baseEffects = this.exitGameOverBaseEffects(gameScreen, animateDuration);

        ParallelTransition combined = new ParallelTransition(selectMenuButtonSlidingInEffects, baseEffects);
        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot(), gameScreen.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again
        });

        combined.play();
    }
    public ParallelTransition exitGameOverBaseEffects(GameScreen gameScreen, float animateDuration) {
        int toX = 500; // move 500 px
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        // Slide out restart & Exit Buttons
        ArrayList<TranslateTransition> slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        // Fade out Restart, Exit, Game Over Label
        ArrayList<FadeTransition> fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration,
                                                                     restartButton, exitButton, gameOverLabel);
        // unblur the gameScreen - just in case
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();
        // combine all animation :)
        ParallelTransition combined = new ParallelTransition();
        combined.getChildren().addAll(slideOutTrans);
        combined.getChildren().addAll(fadeOutTrans);
        combined.getChildren().addAll(gameScreenUnblur);

        return combined;
    }
}
