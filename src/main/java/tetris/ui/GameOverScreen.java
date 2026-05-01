package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;
import tetris.util.UiAnimation;

import static tetris.util.TetrisConstants.GAME_OVER_LABEL_CENTER_Y;
import static tetris.util.TetrisConstants.TIMES_UP_LABEL_CENTER_Y;

public class GameOverScreen extends UiPart<VBox> {
    private static final String FXML = "GameOverScreen.fxml";
    private static final String koString = "K O !";
    private static final String gameOverString = "Game Over";
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
     * @param runnable enables the key input again.
     */
    public void openGameOverScreenEffects(GameScreen gameScreen, Runnable runnable) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        initGameOverScreenPhaseOne();
        SequentialTransition phaseOneAnimation = gameOverAnimationPhaseOne(gameScreen);
        phaseOneAnimation.setOnFinished(e -> {
            initGameOverScreenPhaseTwo();
            // Button and "Game Over" label slide into screen
            gameOverAnimationPhaseTwo(gameScreen, runnable);
        });
        phaseOneAnimation.play();;
    }
    private void initGameOverScreenPhaseOne() {
        // Times Up Screen Appear!!! :)
        this.showNode(this.getRoot());

        gameOverLabel.setOpacity(1.0);
        gameOverLabel.setText(koString);
        gameOverLabel.setTranslateX(-10); // due to "!", the KO need to be adjusted so it looks centered
        gameOverLabel.setTranslateY(GAME_OVER_LABEL_CENTER_Y); // place the KO! label at the middle of the gameScreen at first
        restartButton.setOpacity(0); // hide buttons at first :)
        exitButton.setOpacity(0);
        // so that when sliding in animation finishes, the mouse will be detected by buttons as "enter" and animate
        // the "pop left of the buttons", even when the mouse is already inside the button. This is because we disable
        // animation when buttons are sliding in, so the buttons will already be "entered" but no "on enter" animation
        // will happen
        restartButton.setMouseTransparent(true);
        exitButton.setMouseTransparent(true);
    }
    private SequentialTransition gameOverAnimationPhaseOne(GameScreen gameScreen) {
        RotateTransition koRotate1 = UiAnimation.rotate(15, 0.1f, gameOverLabel);
        ScaleTransition koLabelGrow = UiAnimation.scale(1.0f, 3.0f, 0.15f, gameOverLabel);
        ParallelTransition koLabelShowUp = new ParallelTransition(koRotate1, koLabelGrow);

        PauseTransition pauseAfterKOLabel = UiAnimation.pause(1.5f);

        float animateDuration = 1.2f;
        RotateTransition koRotate2 = UiAnimation.rotate(20, animateDuration, gameOverLabel);
        TranslateTransition koFalling = UiAnimation.fall(TIMES_UP_LABEL_CENTER_Y, 900, animateDuration, gameOverLabel);

        ParallelTransition gameScreenCollapsing = gameScreen.collapsingGameScreenEffects();

        ParallelTransition collapsing = new ParallelTransition();
        collapsing.getChildren().addAll(gameScreenCollapsing, koFalling, koRotate2);

        PauseTransition pauseAfterCollapse = new PauseTransition(Duration.seconds(0.4));

        return new SequentialTransition(koLabelShowUp, pauseAfterKOLabel, collapsing, pauseAfterCollapse);
    }
    private void initGameOverScreenPhaseTwo() {
        // set up for "second stage" of game over screen
        gameOverLabel.setText(gameOverString);
        gameOverLabel.setTranslateY(0);
        gameOverLabel.setRotate(0);
        gameOverLabel.setScaleX(1.5);
        gameOverLabel.setScaleY(1.5);
    }
    private void gameOverAnimationPhaseTwo(GameScreen gameScreen, Runnable runnable) {
        int fromX = 500;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.3f;

        Animation gameScreenBlur = gameScreen.getBlurEffects();

        ParallelTransition slideInTrans = UiAnimation.slideIn(fromX, toX, animateDuration,
                restartButton, exitButton, gameOverLabel);
        ParallelTransition fadeInTrans = UiAnimation.fadeIn(fadeInFrom, fadeInTo, animateDuration,
                restartButton, exitButton, gameOverLabel);
        ParallelTransition gameOverMenuShowUp = new ParallelTransition(slideInTrans, fadeInTrans, gameScreenBlur);

        gameOverMenuShowUp.setOnFinished(e -> {
            UiPart.setUiEffectsOff();
            restartButton.setMouseTransparent(false);
            exitButton.setMouseTransparent(false);

            runnable.run();
        });
        gameOverMenuShowUp.play();
    }
    /**
     * Close the Game Over Screen when user click on Restart Button.
     * Slide the buttons to the right, Fade out the buttons & Title Label, Unblur Game Screen.
     * @param runnable execute currentGameLoop.play()
     */
    public void closeGameOverScreenEffects(GameScreen gameScreen, Runnable runnable) {
        // prevent multiple animation happening at once
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        int toX = 500;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.0f;
        float animateDuration = 0.2f;

        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration, restartButton,
                                                             exitButton, gameOverLabel);
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

            gameScreen.resetUiPositionAfterGameOverAnimation();

            // allow select menu button to shift left on mouse enter once animation is over
            sprintModesScreen.setIsButtonMouseTransparent(false);
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

            gameScreen.resetUiPositionAfterGameOverAnimation();

            // allow select menu button to shift left on mouse enter once animation is over
            selectMenuScreen.setIsButtonMouseTransparent(false);
        });

        combined.play();
    }
    public ParallelTransition exitGameOverBaseEffects(GameScreen gameScreen, float animateDuration) {
        int toX = 500; // move 500 px
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        // Slide out restart & Exit Buttons
        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, restartButton, exitButton);
        // Fade out Restart, Exit, Game Over Label
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration,
                                                                     restartButton, exitButton, gameOverLabel);
        // unblur the gameScreen - just in case
        Animation gameScreenUnblur = gameScreen.setRemoveEffects();
        // combine all animation :)
        return new ParallelTransition(slideOutTrans, fadeOutTrans, gameScreenUnblur);
    }
}
