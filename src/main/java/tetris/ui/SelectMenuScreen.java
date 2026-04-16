package tetris.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
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

    public void setRelaxBlitzExitHandler(ButtonHandler relaxButtonHandler, ButtonHandler blitzButtonHandler, ButtonHandler exitButtonHandler) {
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
}
