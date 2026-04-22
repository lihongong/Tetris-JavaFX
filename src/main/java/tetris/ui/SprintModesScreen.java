package tetris.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;

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

    /**
     * Renders the Sprint Modes Screen (different # lines to clear)
     */
    public ParallelTransition openSprintModesScreen(float animateDuration) {
        this.showNode(this.getRoot());

        FadeTransition fadeInSprintModeScreen = new FadeTransition(Duration.seconds(animateDuration), this.getRoot());
        fadeInSprintModeScreen.setFromValue(0.0);
        fadeInSprintModeScreen.setToValue(1.0);

        ParallelTransition combined = this.slideInButtons();
        combined.getChildren().add(fadeInSprintModeScreen);
        return combined;
    }

    public void fromSprintModesToGameScreen (GameScreen gameScreen) {
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

        float animateDuration = 0.8f; // Fade into Game Screen Duration
        FadeTransition fadeInGameScreen = new FadeTransition(Duration.seconds(animateDuration), gameScreen.getRoot());
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

    public void fromSprintModesToSelectMenu(SelectMenuScreen selectMenuScreen) {
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        ParallelTransition fadeInSelectMenuScreen = selectMenuScreen.openSelectMenuEffects(0.3f);

        ParallelTransition combined = this.slideOutButtons();

        combined.getChildren().add(fadeInSelectMenuScreen);
        combined.setOnFinished(e -> {
            this.hideNode(this.getRoot());

            this.setUiEffectsOff();
        });

        combined.play();
    }

    private ParallelTransition slideInButtons() {
        int fromX = 1200;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.27f;

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), clear20LinesButton);
        slide1.setFromX(fromX);
        slide1.setToX(toX);
        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), clear40LinesButton);
        slide2.setFromX(fromX);
        slide2.setToX(toX);
        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), clear60LinesButton);
        slide3.setFromX(fromX);
        slide3.setToX(toX);
        TranslateTransition slide4 = new TranslateTransition(Duration.seconds(animateDuration), backButton);
        slide4.setFromX(fromX);
        slide4.setToX(toX);
        FadeTransition fadeIn1 = new FadeTransition(Duration.seconds(animateDuration), clear20LinesButton);
        fadeIn1.setFromValue(fadeInFrom);
        fadeIn1.setToValue(fadeInTo);
        FadeTransition fadeIn2 = new FadeTransition(Duration.seconds(animateDuration), clear40LinesButton);
        fadeIn2.setFromValue(fadeInFrom);
        fadeIn2.setToValue(fadeInTo);

        FadeTransition fadeIn3 = new FadeTransition(Duration.seconds(animateDuration), clear60LinesButton);
        fadeIn3.setFromValue(fadeInFrom);
        fadeIn3.setToValue(fadeInTo);

        FadeTransition fadeIn4 = new FadeTransition(Duration.seconds(animateDuration), backButton);
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

        TranslateTransition slide1 = new TranslateTransition(Duration.seconds(animateDuration), clear20LinesButton);
        slide1.setToX(toX);

        TranslateTransition slide2 = new TranslateTransition(Duration.seconds(animateDuration), clear40LinesButton);
        slide2.setToX(toX);

        TranslateTransition slide3 = new TranslateTransition(Duration.seconds(animateDuration), clear60LinesButton);
        slide3.setToX(toX);

        TranslateTransition slide4 = new TranslateTransition(Duration.seconds(animateDuration), backButton);
        slide4.setToX(toX);

        FadeTransition fadeOut1 = new FadeTransition(Duration.seconds(animateDuration), clear20LinesButton);
        fadeOut1.setFromValue(fadeOutFrom);
        fadeOut1.setToValue(fadeOutTo);

        FadeTransition fadeOut2 = new FadeTransition(Duration.seconds(animateDuration), clear40LinesButton);
        fadeOut2.setFromValue(fadeOutFrom);
        fadeOut2.setToValue(fadeOutTo);

        FadeTransition fadeOut3 = new FadeTransition(Duration.seconds(animateDuration), clear60LinesButton);
        fadeOut3.setFromValue(fadeOutFrom);
        fadeOut3.setToValue(fadeOutTo);

        FadeTransition fadeOut4 = new FadeTransition(Duration.seconds(animateDuration), backButton);
        fadeOut4.setFromValue(fadeOutFrom);
        fadeOut4.setToValue(fadeOutTo);

        FadeTransition titleLabelFadeOut = new FadeTransition(Duration.seconds(animateDuration), titleLabel);
        titleLabelFadeOut.setFromValue(fadeOutFrom);
        titleLabelFadeOut.setToValue(fadeOutTo);

        return new ParallelTransition(slide1, slide2, slide3, slide4, fadeOut1, fadeOut2, fadeOut3,
                fadeOut4, titleLabelFadeOut);
    }

}
