package tetris.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.scene.ParallelCamera;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;

import java.beans.Visibility;

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

        // on app start up, selectMenuScreen will be added to mainWindow (not visible) or else the first start animation
        // won't be smooth (Java just-in-time rendering problem :'(
        // add SelectMenuScreen into mainWindow if it isn't in it already
        /*if (mainWindow.getRoot().getChildren().contains(selectMenuScreen.getRoot())) {
            selectMenuScreen.getRoot().setVisible(true);
        } else {
            mainWindow.addNodesToRoot(selectMenuScreen.getRoot());
        }*/
        this.showNode(selectMenuScreen.getRoot());

        ParallelTransition combined = selectMenuScreen.openSelectMenuEffects(0.3f);

        // remove start menu screen at the end of effect
        combined.setOnFinished(e -> {
            this.hideNode(this.getRoot());

            this.setUiEffectsOff();
        });

        combined.play();
    }
}
