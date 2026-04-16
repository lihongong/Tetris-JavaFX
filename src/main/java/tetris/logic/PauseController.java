package tetris.logic;

import javafx.scene.effect.GaussianBlur;
import tetris.ui.GameScreen;
import tetris.ui.PauseMenuScreen;

public class PauseController{
    GameScreen gameplayUI;
    PauseMenuScreen pauseMenuScreen;

    public PauseController(GameScreen gameplayUI, PauseMenuScreen pauseMenuScreen) {
        this.gameplayUI = gameplayUI;
        this.pauseMenuScreen = pauseMenuScreen;
    }

    public void handlePausePress() {
        gameplayUI.getRoot().setEffect(new GaussianBlur(10));
        pauseMenuScreen.getRoot().setVisible(true);

        // NOTE: Don't reset the pausePress to false, needed to detect resume press

        // TODO: TetrisPanel.OST.pause();

    }
    public void handleResumePress() {
        gameplayUI.getRoot().setEffect(null); // remove blur
        pauseMenuScreen.getRoot().setVisible(false);

        //KeyInputController.resumePress = false;
        //KeyInputController.pausePress = false;

        // TODO:
        // TetrisPanel.OST.resume();
    }
}
