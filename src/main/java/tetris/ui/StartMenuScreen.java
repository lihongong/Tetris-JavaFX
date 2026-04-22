package tetris.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.scene.ParallelCamera;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;

public class StartMenuScreen extends UiPart<VBox> {
    private static final String FXML = "StartMenu.fxml";
    private ButtonHandler startButtonHandler;
    @FXML
    private Button playButton;

    public StartMenuScreen() {
        super(FXML);
    }

    public void setStartButtonHandler(ButtonHandler startButtonHandler) {
        this.startButtonHandler = startButtonHandler;
    }
    @FXML
    public void handleStartButton() {
        startButtonHandler.handle();
    }

    public void startMenuStartEffect(MainWindow mainWindow, SelectMenuScreen selectMenuScreen) {
        // prevent multiple click on Start Button and cause bugs :(((
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        // add SelectMenuScreen into mainWindow
        mainWindow.addNodesToRoot(selectMenuScreen.getRoot());

        FadeTransition fadeInSelectMenu = new FadeTransition(Duration.seconds(0.2), selectMenuScreen.getRoot());
        fadeInSelectMenu.setFromValue(0.0);
        fadeInSelectMenu.setToValue(1.0);

        ParallelTransition combined = selectMenuScreen.slideInButtons();
        combined.getChildren().add(fadeInSelectMenu);
        // remove start menu screen at the end of effect
        combined.setOnFinished(e -> {
            mainWindow.removeNodesFromRoot(this.getRoot());

            this.setUiEffectsOff();
        });

        combined.play();
    }
}
