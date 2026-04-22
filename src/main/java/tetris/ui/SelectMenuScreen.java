package tetris.ui;

import com.sun.tools.javac.Main;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;

public class SelectMenuScreen extends UiPart<VBox> {
    private static final String FXML = "SelectMenu.fxml";

    private ButtonHandler relaxButtonHandler;
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

    public SelectMenuScreen() {
        super(FXML);
    }

    public void setSelectMenuButtonsHandler(ButtonHandler relaxButtonHandler, ButtonHandler sprintButtonHandler,
                                            ButtonHandler blitzButtonHandler, ButtonHandler exitButtonHandler) {
        this.relaxButtonHandler = relaxButtonHandler;
        this.blitzButtonHandler = blitzButtonHandler;
        this.exitButtonHandler = exitButtonHandler;
    }
    @FXML
    public void handleRelaxButton() {
        relaxButtonHandler.handle();
    }
    @FXML
    public void handleBlitzButton() {
        blitzButtonHandler.handle();
    }
    @FXML
    public void handleExitButton() {
        exitButtonHandler.handle();
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
        mainWindow.addNodesToRoot(startMenuScreen.getRoot());

        FadeTransition fadeInStartMenuScreen = new FadeTransition(Duration.seconds(0.4), startMenuScreen.getRoot());
        fadeInStartMenuScreen.setFromValue(0.0);
        fadeInStartMenuScreen.setToValue(1.0);

        fadeInStartMenuScreen.setOnFinished(e -> {
            // remove select menu at the end
            mainWindow.removeNodesFromRoot(this.getRoot());

            this.setUiEffectsOff();
        });

        fadeInStartMenuScreen.play();
    }

    /**
     * Renders the gameScreen from SelectMenuScreen.
     * Set random background for gameScreen. add gameScreen, gameOverScreen, timesUpScreen, pauseMenuScreen to MainWindow.
     * Fade in gameScreen and then remove Select Menu Screen at the end of effects
     */
    public void renderGameScreenFromSelectMenu(MainWindow mainWindow, GameScreen gameScreen,
                                               GameOverScreen gameOverScreen, TimesUpScreen timesUpScreen,
                                               PauseMenuScreen pauseMenuScreen) {
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // set background image for gameplay
        gameScreen.setRandomBackGroundImage();

        // transition effects
        mainWindow.addNodesToRoot(gameScreen.getRoot(), pauseMenuScreen.getRoot(),
                gameOverScreen.getRoot(), timesUpScreen.getRoot());

        gameScreen.getRoot().setEffect(null); // clear the blur effects just in case.
        // hides pause, game over, times up screen
        pauseMenuScreen.getRoot().setVisible(false);
        gameOverScreen.getRoot().setVisible(false);
        timesUpScreen.getRoot().setVisible(false);

        FadeTransition fadeInGameScreen = new FadeTransition(Duration.seconds(1.0), gameScreen.getRoot());
        fadeInGameScreen.setFromValue(0.0);
        fadeInGameScreen.setToValue(1.0);

        fadeInGameScreen.setOnFinished(e -> {
            // handle nodes
            mainWindow.removeNodesFromRoot(this.getRoot());

            this.setUiEffectsOff(); // prevent multiple animation happening at once
        });
        fadeInGameScreen.play();
    }

    public ParallelTransition slideInButtons() {
        int fromX = 500;
        int toX = 0;
        float fadeInFrom = 0.0f;
        float fadeInTo = 1.0f;
        float animateDuration = 0.25f;

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
/*
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
*/
        return new ParallelTransition(slide1, slide2, slide3, slide4); //, fadeIn1, slide2, fadeIn2, slide3, fadeIn3, slide4,
               // fadeIn4, titleLabelFadeIn);
    }
}
