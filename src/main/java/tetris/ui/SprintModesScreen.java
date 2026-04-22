package tetris.ui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import tetris.util.ButtonHandler;

public class SprintModesScreen extends UiPart<VBox> {

    private static final String FXML = "SprintModesMenu.fxml";

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
        //clear20LinesButtonHandler.handle();
    }
    @FXML
    public void handleClear40Button() {
        //clear40LinesButtonHandler.handle();
    }
    @FXML
    public void handleClear60Button() {
        //clear60LinesButtonHandler.handle();
    }
    @FXML
    public void handleBackButton() {
        //backButtonHandler.handle();
    }

    /**
     * Renders the Sprint Modes Screen (different # lines to clear)
     */
    public void showSprintModesScreen(MainWindow mainWindow, SelectMenuScreen selectMenuScreen) {
        if (this.isUiEffectsOn()) {
            return;
        }
        this.setUiEffectsOn();

        mainWindow.addNodesToRoot(this.getRoot());

        FadeTransition fadeInSprintModeScreen = new FadeTransition(Duration.seconds(1.0), this.getRoot());
        fadeInSprintModeScreen.setFromValue(0.0);
        fadeInSprintModeScreen.setToValue(1.0);
    }

}
