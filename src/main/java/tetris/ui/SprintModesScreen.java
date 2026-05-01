package tetris.ui;

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

public class SprintModesScreen extends UiPart<VBox> {

    private static final String FXML = "SprintModesScreen.fxml";

    private ButtonHandler clear20LinesButtonHandler;
    private ButtonHandler clear40LinesButtonHandler;
    private ButtonHandler clear60LinesButtonHandler;
    private ButtonHandler backButtonHandler;
    @FXML
    private VBox mainLayout;
    @FXML
    private Label titleLabel;
    @FXML
    private Button clear20LinesButton;
    @FXML
    private Button clear40LinesButton;
    @FXML
    private Button clear60LinesButton;
    @FXML
    private Button backButton;
    private TranslateTransition clear20ButtonHoverAnimation;
    private TranslateTransition clear40ButtonHoverAnimation;
    private TranslateTransition clear60ButtonHoverAnimation;
    private TranslateTransition backButtonHoverAnimation;

    public SprintModesScreen() {
        super(FXML);
    }

    public void setSprintModesButtonsHandler(ButtonHandler clear20LinesButtonHandler,
                                                  ButtonHandler clear40LinesButtonHandler,
                                                  ButtonHandler clear60LinesButtonHandler,
                                                  ButtonHandler backButtonHandler) {
        this.clear20LinesButtonHandler = clear20LinesButtonHandler;
        this.clear40LinesButtonHandler = clear40LinesButtonHandler;
        this.clear60LinesButtonHandler = clear60LinesButtonHandler;
        this.backButtonHandler = backButtonHandler;
    }

    @FXML
    public void handleClear20Button() {
        clear20LinesButtonHandler.handle();
    }
    @FXML
    public void handleClear40Button() {
        clear40LinesButtonHandler.handle();
    }
    @FXML
    public void handleClear60Button() {
        clear60LinesButtonHandler.handle();
    }
    @FXML
    public void handleBackButton() {
        backButtonHandler.handle();
    }
    @FXML
    public void handleClear20MouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            clear20ButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(
                    clear20LinesButton, clear20ButtonHoverAnimation);
        }
    }
    @FXML
    public void handleClear20MouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            clear20ButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(
                    clear20LinesButton, clear20ButtonHoverAnimation);
        }
    }
    @FXML
    public void handleClear40MouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            clear40ButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(
                    clear40LinesButton, clear40ButtonHoverAnimation);
        }
    }
    @FXML
    public void handleClear40MouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            clear40ButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(
                    clear40LinesButton, clear40ButtonHoverAnimation);
        }
    }
    @FXML
    public void handleClear60MouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            clear60ButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(
                    clear60LinesButton, clear60ButtonHoverAnimation);
        }
    }
    @FXML
    public void handleClear60MouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            clear60ButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(
                    clear60LinesButton, clear60ButtonHoverAnimation);
        }
    }
    @FXML
    public void handleBackButtonMouseEnter(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            backButtonHoverAnimation = ButtonTransitions.buttonMouseEnterLeftSlide(
                    backButton, backButtonHoverAnimation);
        }
    }
    @FXML
    public void handleBackButtonMouseExit(MouseEvent me) {
        if (!UiPart.isUiEffectsOn()) {
            backButtonHoverAnimation = ButtonTransitions.buttonMouseExitRightSlide(
                    backButton, backButtonHoverAnimation);
        }
    }
    /**
     * Renders the Sprint Modes Screen (different # lines to clear)
     */
    public ParallelTransition openSprintModesScreen(float animateDuration) {
        setIsButtonMouseTransparent(true);

        this.showNode(this.getRoot());

        FadeTransition fadeInSprintModeScreen = new FadeTransition(Duration.seconds(animateDuration), this.getRoot());
        fadeInSprintModeScreen.setFromValue(0.0);
        fadeInSprintModeScreen.setToValue(1.0);

        ParallelTransition combined = this.slideInButtons();
        combined.getChildren().add(fadeInSprintModeScreen);
        return combined;
    }
    public void setIsButtonMouseTransparent(boolean isTransparent) {
        clear20LinesButton.setMouseTransparent(isTransparent);
        clear40LinesButton.setMouseTransparent(isTransparent);
        clear60LinesButton.setMouseTransparent(isTransparent);
        backButton.setMouseTransparent(isTransparent);
    }
    public void fromSprintModesToGameScreen (GameScreen gameScreen) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        // set background image for gameplay
        gameScreen.setRandomBackGroundImage();

        // transition effects
        // make GameScreen appear
        this.showNode(gameScreen.getRoot());
        gameScreen.getRoot().setEffect(null); // clear the blur effects just in case.

        float animateDuration = 0.8f; // Fade into Game Screen Duration
        FadeTransition fadeInGameScreen = new FadeTransition(Duration.seconds(animateDuration), gameScreen.getRoot());
        fadeInGameScreen.setFromValue(0.0);
        fadeInGameScreen.setToValue(1.0);

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

    public void fromSprintModesToSelectMenu(SelectMenuScreen selectMenuScreen) {
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        UiPart.setUiEffectsOn();

        ParallelTransition fadeInSelectMenuScreen = selectMenuScreen.openSelectMenuEffects(0.3f);

        ParallelTransition combined = this.slideOutButtons();

        combined.getChildren().add(fadeInSelectMenuScreen);
        combined.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot());

            UiPart.setUiEffectsOff();

            // allow select menu button to shift left on mouse enter once animation is over
            selectMenuScreen.setIsButtonMouseTransparent(false);
        });

        combined.play();
    }

    private ParallelTransition slideInButtons() {
        int fromX = 1200;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.27f;

        ParallelTransition slideInTrans = UiAnimation.slideIn(fromX, toX, animateDuration, clear20LinesButton,
                                                              clear40LinesButton, clear60LinesButton, backButton);
        ParallelTransition fadeInTrans = UiAnimation.fadeIn(fadeInFrom, fadeInTo, animateDuration, clear20LinesButton,
                                                    clear40LinesButton, clear60LinesButton, backButton, titleLabel);
        return new ParallelTransition(slideInTrans, fadeInTrans);
    }

    private ParallelTransition slideOutButtons() {
        int toX = 1200;
        float fadeOutFrom = 1.0f;
        float fadeOutTo = 0.2f;
        float animateDuration = 0.3f;

        ParallelTransition slideOutTrans = UiAnimation.slideOut(toX, animateDuration, clear20LinesButton,
                                                                clear40LinesButton, clear60LinesButton, backButton);
        ParallelTransition fadeOutTrans = UiAnimation.fadeOut(fadeOutFrom, fadeOutTo, animateDuration, clear20LinesButton,
                                                    clear40LinesButton, clear60LinesButton, backButton, titleLabel);

        return new ParallelTransition(slideOutTrans, fadeOutTrans);
    }

}
