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

    public void fromStartMenuToSelectMenu(SelectMenuScreen selectMenuScreen) {
        // prevent multiple click on Start Button and cause bugs :(((
        if (UiPart.isUiEffectsOn()) {
            return;
        }
        // don't set effects is on so that when Select Menu Buttons shows up, they will immediately respond when mouse
        // on enter -> nicer animation style
        // SIDE EFFECT: Without setting isUiEffectOn as True, other animation can happen whilst the current effect is
        // going on. P.S. The "nice" effect can't be achieved for "GameScreen/SprintMode->SelectMenu" with this method only

        // on app start up, selectMenuScreen will be added to mainWindow (not visible) or else the first start animation
        // won't be smooth (Java just-in-time rendering problem :'(
        // add SelectMenuScreen into mainWindow if it isn't in it already
        this.showNode(selectMenuScreen.getRoot());

        ParallelTransition combined = selectMenuScreen.openSelectMenuEffects(0.3f);

        // remove start menu screen at the end of effect
        combined.setOnFinished(e -> {
            UiPart.hideNode(this.getRoot());

            UiPart.setUiEffectsOff();

            // allow select menu button to shift left on mouse enter once animation is over
            selectMenuScreen.setIsButtonMouseTransparent(false);
        });
        combined.play();
    }
}
