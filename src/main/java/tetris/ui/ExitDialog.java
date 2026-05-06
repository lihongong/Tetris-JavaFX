package tetris.ui;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tetris.Tetris;
import tetris.util.UiAnimation;

import java.util.Objects;

public class ExitDialog extends UiPart<StackPane> {
    private static final String FXML = "ExitDialog.fxml";
    private static final String STYLESHEET = "/view/exit-dialog.css";
    private static final double TRIANGLE_LENGTH = 50.0;

    private Stage exitDialogStage;
    private Scene exitDialogScene;

    // Easter egg :)
    private int clickOnTriangleCount;

    @FXML
    private StackPane warningSignContainer;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;

    @FXML
    private void yesButtonHandler() {
        Platform.exit();
        System.exit(0);
    }
    @FXML
    private void noButtonHandler() {
        exitDialogStage.close();
    }
    @FXML
    private void xButtonHandler() {
        exitDialogStage.close();
    }

    public static ExitDialog of(Stage primaryStage) {
        return new ExitDialog(primaryStage);
    }
    private ExitDialog(Stage primaryStage) {
        super(FXML);

        setupStage(primaryStage);
        setupStyle();
        setupWarningSign();
    }
    private void setupStage(Stage primaryStage) {
        exitDialogStage = new Stage();
        exitDialogStage.initModality(Modality.APPLICATION_MODAL);
        exitDialogStage.initOwner(primaryStage);
        exitDialogStage.initStyle(StageStyle.TRANSPARENT);

        // Prevent closing via OS 'X' if undecorated style was ever swapped
        exitDialogStage.setOnCloseRequest(Event::consume);

        Scene scene = new Scene(this.getRoot());
        scene.setFill(Color.TRANSPARENT);
        exitDialogStage.setScene(scene);
    }
    private void setupStyle() {
        String css = Objects.requireNonNull(getClass().getResource(STYLESHEET)).toExternalForm();
        this.getRoot().getStylesheets().add(css);
        this.getRoot().getStyleClass().add("exit-dialog-root");
    }
    public void setupWarningSign() {
        Polygon warningTriangle = new Polygon(
                0, TRIANGLE_LENGTH * 0.866,
                TRIANGLE_LENGTH / 2, 0,
                TRIANGLE_LENGTH, TRIANGLE_LENGTH * 0.866
        );
        warningTriangle.getStyleClass().add("warning-triangle");

        // Exclamation mark in the center of Triangle
        Label warningExclamation = new Label("!");
        warningExclamation.getStyleClass().add("warning-exclamation-label");

        warningSignContainer.getChildren().addAll(warningTriangle, warningExclamation);

        // Create and attach the Shake Animation Easter egg
        SequentialTransition shakeAnimation = createShakeAnimation(warningSignContainer);
        warningSignContainer.setOnMouseClicked(e -> handleEasterEgg(shakeAnimation));
    }
    private SequentialTransition createShakeAnimation(StackPane target) {
        RotateTransition r1 = UiAnimation.rotate(15, 0.1f, target);
        r1.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition r2 = UiAnimation.rotate(-25, 0.15f, target);
        r2.setInterpolator(Interpolator.EASE_BOTH);

        RotateTransition r3 = UiAnimation.rotate(15, 0.12f, target);
        RotateTransition r4 = UiAnimation.rotate(-8, 0.1f, target);
        RotateTransition r5 = UiAnimation.rotate(0, 0.08f, target);

        return new SequentialTransition(r1, r2, r3, r4, r5);
    }
    private void handleEasterEgg(SequentialTransition anim) {
        clickOnTriangleCount++;
        if (clickOnTriangleCount >= 3) {
            clickOnTriangleCount = 0;
            anim.playFromStart();
        }
    }

    public void showExitConfirmation(Stage primaryStage) {
        if (exitDialogStage != null && exitDialogStage.isShowing()) {
            exitDialogStage.toFront(); // Bring the existing one to focus
        } else {

            // Center the exit dialog
            // run later to prevent wrong calculation of width and height of primary & dialog stage
            exitDialogStage.show();
            Platform.runLater(() -> {
                this.getRoot().applyCss();
                this.getRoot().layout();

                double dialogWidth = this.getRoot().getWidth();
                double dialogHeight = this.getRoot().getHeight();

                // recalculate based on the now-stable primaryStage dimensions
                double newX = primaryStage.getX() + (primaryStage.getWidth() / 2) - (dialogWidth / 2);
                double newY = primaryStage.getY() + (primaryStage.getHeight() / 2) - (dialogHeight / 2);

                exitDialogStage.setX(newX);
                exitDialogStage.setY(newY);
            });
        }
    }
}
