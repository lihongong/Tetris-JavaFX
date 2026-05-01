package tetris.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import tetris.logic.GameController;
import tetris.logic.GameState;
import tetris.logic.KeyInputController;

public class MainWindow extends UiPart<StackPane> {
    private static final String FXML = "MainWindow.fxml";
    private Stage primaryStage;
    private Scene mainScene;

    private KeyInputController keyInputController;
    private GameController gameController;

    // UI Screens
    private StartMenuScreen startMenuScreen;
    private SelectMenuScreen selectMenuScreen;
    private SprintModesScreen sprintModesScreen;

    private GameScreen gameScreen;
    private PauseMenuScreen pauseMenuScreen;
    private GameOverScreen gameOverScreen;
    private TimesUpScreen timesUpScreen;
    private SprintOverScreen sprintOverScreen;

    @FXML
    private StackPane gameRoot; // stack different layers (gameplay, pause menu, game over screen)

    public MainWindow(Stage primaryStage) {
        super(FXML);

        config(primaryStage);
        setIcon(primaryStage);
    }

    private void config(Stage primaryStage) {
        try {
            Parent root = getRoot();

            this.primaryStage = primaryStage;
            this.mainScene = new Scene(root);
            mainScene.setFill(Color.BLACK);
            primaryStage.setScene(mainScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIcon(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/tetris_icon.png")));
    }

    public void show() {
        primaryStage.show();

        double minWidth = primaryStage.getWidth();
        double minHeight = primaryStage.getHeight();

        primaryStage.setMinWidth((int) minWidth);
        primaryStage.setMinHeight((int) minHeight);

        primaryStage.setMaximized(true);
    }

    public void fillInnerParts() {
        this.gameScreen = new GameScreen();
        //this.gameRoot.getChildren().add(gameScreen.getRoot());

        this.pauseMenuScreen = new PauseMenuScreen();
        //this.gameRoot.getChildren().add(pauseMenuScreen.getRoot());
        //pauseMenuScreen.getRoot().setVisible(false); // initially, pause screen isn't visible

        this.gameOverScreen = new GameOverScreen();
        this.timesUpScreen = new TimesUpScreen();
        this.sprintOverScreen = new SprintOverScreen();

        this.selectMenuScreen = new SelectMenuScreen();
        this.sprintModesScreen = new SprintModesScreen();

        this.startMenuScreen = new StartMenuScreen();

        // on app start up, selectMenuScreen will be added to mainWindow (not visible) or else the first start animation
        // won't be smooth (Java just-in-time rendering problem :'(
        //this.gameRoot.getChildren().addAll(startMenuScreen.getRoot(), selectMenuScreen.getRoot());
        gameRoot.getChildren().addAll(
                startMenuScreen.getRoot(),
                selectMenuScreen.getRoot(),
                sprintModesScreen.getRoot(),
                gameScreen.getRoot(),
                pauseMenuScreen.getRoot(),
                gameOverScreen.getRoot(),
                timesUpScreen.getRoot(),
                sprintOverScreen.getRoot()
        );

        for (Node n : gameRoot.getChildren()) {
            n.setVisible(false);
        }

        // 5. Show only the Start Menu
        startMenuScreen.getRoot().setVisible(true);
    }

    public void setUpGame() {
        this.gameController = new GameController(gameScreen, pauseMenuScreen, gameOverScreen, timesUpScreen,
                sprintOverScreen, startMenuScreen, selectMenuScreen, sprintModesScreen, mainScene);
        // set button handler
        pauseMenuScreen.setResumeRestartExitButtonHandler(gameController::resumeGame, gameController::restartGameInPauseMenu, gameController::exitButtonInPauseMenu);

        gameOverScreen.setRestartExitButtonHandler(gameController::restartGameInGameOver, gameController::exitButtonInGameOver);
        timesUpScreen.setRestartExitButtonHandler(gameController::restartGameInTimesUp, gameController::exitButtonInTimesUp);
        sprintOverScreen.setRestartExitButtonHandler(gameController::restartGameInSprintOver, gameController::exitButtonInSprintOver);

        startMenuScreen.setStartButtonHandler(gameController::startButtonInStartMenu);
        selectMenuScreen.setSelectMenuButtonsHandler(gameController::relaxButton, gameController::sprintButton, gameController::blitzButton, gameController::exitButtonInSelectMenu);
        sprintModesScreen.setSprintModesButtonsHandler(gameController::clearALinesButton, gameController::clearBLinesButton, gameController::clearCLinesButton, gameController::sprintModesBackButton);

        GameState gameState = gameController.getGameState();
        //new KeyInputController(mainScene, gameState, gameController);
    }

    public void removeNodesFromRoot(Node... toBeRemovedNodes) {
        for (Node toBeRemovedNode : toBeRemovedNodes) {
            gameRoot.getChildren().remove(toBeRemovedNode);
        }
    }

    public void addNodesToRoot(Node... toBeAddedNodes) {
        for (Node toBeAddedNode : toBeAddedNodes) {
            gameRoot.getChildren().add(toBeAddedNode);
        }
    }
}
