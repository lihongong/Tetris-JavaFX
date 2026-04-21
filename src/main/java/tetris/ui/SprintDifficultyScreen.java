package tetris.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import tetris.util.ButtonHandler;

public class SprintDifficultyScreen extends UiPart<VBox> {

    private static final String FXML = "SprintDifficultyMenu.fxml";

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

    public SprintDifficultyScreen() {
        super(FXML);
    }

    public void setSprintDifficultyButtonsHandler(ButtonHandler clear20LinesButtonHandler,
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
}
