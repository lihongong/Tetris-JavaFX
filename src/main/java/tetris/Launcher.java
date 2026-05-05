package tetris;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    public static void main(String[] args) {
        System.setProperty("glass.accessible.force", "false");
        Application.launch(Tetris.class, args);
    }
}