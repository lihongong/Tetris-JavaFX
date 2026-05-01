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
import tetris.util.GameMode;
import tetris.util.UiAnimation;

import java.util.ArrayList;

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
        if (!UiPart.isUiEffectsOn()) {
            resumeButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(resumeButton, resumeButtonHoverAnimation);
        }
    }
    @FXML
    public void handleResumeButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            resumeButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(resumeButton, resumeButtonHoverAnimation);
        }
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

    public void setPauseMenuForGameModes(GameMode gameMode) {
        if (gameMode == GameMode.RELAX || gameMode == GameMode.BLITZ) {
            exitButton.setText("EXIT");

        } else {
            exitButton.setText("OTHER MODES");
        }
    }

    public void openPauseMenuEffects(GameScreen gameScreen) {
        // must still check for other still active effects -> prevent bug where the Pause Buttons sliding left while
        // going right bug as mouse reenters it
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // so that when sliding in animation finishes, the mouse will be detected by buttons as "enter" and animate
        // the "pop left of the buttons", even when the mouse is already inside the button. This is because we disable
        // animation when buttons are sliding in, so the buttons will already be "entered" but no "on enter" animation
        // will happen
        resumeButton.setMouseTransparent(true);
        restartButton.setMouseTransparent(true);
        exitButton.setMouseTransparent(true);

        // blur the game screen in the background
        Animation gameScreenBlur = gameScreen.getBlurEffects();

        int fromX = 700;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.2f;

        //ArrayList<TranslateTransition> slideInTrans = UiAnimation.slideInT(fromX, toX, animateDuration, resumeButton, restartButton, exitButton);
        ParallelTransition slideInTrans = UiAnimation.slideIn(fromX, toX, animateDuration, resumeButton, restartButton, exitButton);

        ParallelTransition fadeInTrans = UiAnimation.fadeIn(fadeInFrom, fadeInTo, animateDuration,
                                                                  resumeButton, restartButton, exitButton, pausedLabel);

        ParallelTransition combined = new ParallelTransition(fadeInTrans, gameScreenBlur);
        combined.getChildren().addAll(slideInTrans);

        combined.setOnFinished(e -> {
            UiPart.setUiEffectsOff();
            resumeButton.setMouseTransparent(false);
            restartButton.setMouseTransparent(false);
            exitButton.setMouseTransparent(false);
        });

        // Pause menu screen appear!!!
        this.showNode(this.getRoot());

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
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        int toX = 1000;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.4f;

        ParallelTransition slidingInTrans = UiAnimation.slideOut(toX, animateDuration, resumeButton, restartButton,
                                                                 exitButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration, resumeButton,
                                                             restartButton, exitButton, pausedLabel);
        // unblur the gameScreen
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        ParallelTransition combined = new ParallelTransition(slidingInTrans, fadeInTrans, gameScreenUnblur);

        combined.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot());
            UiPart.setUiEffectsOff(); // prevent multiple animation happening at once

            if (runnable != null) {
                runnable.run();
            }
        });

        combined.play();
    }

    public void fromPauseMenuToSelectMenu(SelectMenuScreen selectMenuScreen, GameScreen gameScreen) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // Make appear SelectMenuScreen
        this.showNode(selectMenuScreen.getRoot());
        // Fade in the Select Menu Screen
        float animateDuration = 0.3f;
        ParallelTransition selectMenuButtonSlidingInEffects = selectMenuScreen.openSelectMenuEffects(animateDuration);

        // the base exit transition of pause menu
        ParallelTransition baseEffects = this.exitPauseMenuBaseEffects(gameScreen, animateDuration);

        ParallelTransition combined = new ParallelTransition(selectMenuButtonSlidingInEffects, baseEffects);
        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot(), gameScreen.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again

            // allow select menu button to shift left on mouse enter once animation is over
            selectMenuScreen.setIsButtonMouseTransparent(false);
        });

        combined.play();
    }

    public void fromPauseMenuToSprintModes(SprintModesScreen sprintModesScreen, GameScreen gameScreen) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // Make appear Sprint modes screen
        this.showNode(sprintModesScreen.getRoot());
        // Fade in the Select Menu Screen
        float animateDuration = 0.3f;
        ParallelTransition sprintModesScreenButtonsSlidingInEffects = sprintModesScreen.openSprintModesScreen(animateDuration);

        // the base exit transition of pause menu
        ParallelTransition baseEffects = this.exitPauseMenuBaseEffects(gameScreen, animateDuration);

        ParallelTransition combined = new ParallelTransition(sprintModesScreenButtonsSlidingInEffects, baseEffects);
        combined.setOnFinished(e -> {
            // handle nodes
            UiPart.hideNode(this.getRoot(), gameScreen.getRoot());

            UiPart.setUiEffectsOff(); // enable ui interaction again
        });

        combined.play();
    }

    public ParallelTransition exitPauseMenuBaseEffects(GameScreen gameScreen, float animateDuration) {
        // Buttons and Title label Sliding & Fading animation
        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;

        // Slide out restart & Exit Buttons
        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration,
                                                                            resumeButton, restartButton, exitButton);
        // Fade out Restart, Exit, Game Over Label
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration,
                                                                resumeButton, restartButton, exitButton, pausedLabel);
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();

        return new ParallelTransition(slideOutTrans, fadeOutTrans, gameScreenUnblur);
    }
}
