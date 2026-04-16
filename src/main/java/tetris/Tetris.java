package tetris;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tetris.logic.GameState;
import tetris.logic.KeyInputController;
import tetris.logic.GameController;
import tetris.ui.GameScreen;
import tetris.ui.MainWindow;
import tetris.ui.PauseMenuScreen;

public class Tetris extends Application {
    private MainWindow mainWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            this.mainWindow = new MainWindow(primaryStage);
            mainWindow.show();
            mainWindow.fillInnerParts();
            mainWindow.setUpGame();

            loadFonts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up fonts for css. JavaFX css doesn't reliably load fonts in css using @font-face, so it is better to load
     * the fonts programmatically.
     */
    public void loadFonts() {
        Font font = Font.loadFont(
                getClass().getClassLoader().getResourceAsStream("fonts/Silkscreen_Regular.ttf"), 10
        );
    }
}
