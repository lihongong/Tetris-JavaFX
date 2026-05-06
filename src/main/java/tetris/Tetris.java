package tetris;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tetris.ui.ExitDialog;
import tetris.ui.MainWindow;

public class Tetris extends Application {
    private MainWindow mainWindow;
    private ExitDialog exitDialog;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            loadFonts();

            this.mainWindow = MainWindow.of(primaryStage);
            this.exitDialog = ExitDialog.of(primaryStage);

            primaryStage.setOnCloseRequest(event -> {
                event.consume(); // Stop the window from closing immediately
                exitDialog.showExitConfirmation(primaryStage); // show the confirmation pop up
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets up fonts for css. JavaFX css doesn't reliably load fonts in css using @font-face, so it is better to load
     * the fonts programmatically.
     */
    public void loadFonts() {
        Font.loadFont(
                getClass().getClassLoader().getResourceAsStream("fonts/Silkscreen_Regular.ttf"), 10
        );
        Font.loadFont(
                getClass().getClassLoader().getResourceAsStream("fonts/Tetris_Battle_Font.ttf"), 10
        );
        Font.loadFont(
                getClass().getClassLoader().getResourceAsStream("fonts/Quicksand-Bold.ttf"), 10
        );
        Font.loadFont(
                getClass().getClassLoader().getResourceAsStream("fonts/Quicksand-Regular.ttf"), 10
        );
    }
}
