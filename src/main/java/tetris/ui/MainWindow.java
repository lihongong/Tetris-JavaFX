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

    // other ui parts
    private GameScreen gameScreen;
    //private GameOverUI gameOverScreen;
    private PauseMenuScreen pauseMenuScreen;
    private GameOverScreen gameOverScreen;
    private TimesUpScreen timesUpScreen;
    private StartMenuScreen startMenuScreen;
    private SelectMenuScreen selectMenuScreen;
    @FXML
    private StackPane gameRoot; // stack different layers (gameplay, pause menu, game over screen)

    public MainWindow(Stage primaryStage) {
        super(FXML);

        config(primaryStage);
        setIcon(primaryStage);
    }

    private void config(Stage primaryStage) {
        try {
            /*
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML));
            loader.setController(this);
            Parent root = loader.load();*/
            Parent root = getRoot();

            this.primaryStage = primaryStage;
            this.mainScene = new Scene(root);
            mainScene.setFill(Color.TRANSPARENT);
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
        //this.gameRoot.getChildren().add(gameOverScreen.getRoot());
        //gameOverScreen.getRoot().setVisible(false); // initially, game over screen isn't visible

        this.startMenuScreen = new StartMenuScreen();
        this.gameRoot.getChildren().add(startMenuScreen.getRoot());

        this.selectMenuScreen = new SelectMenuScreen();
        //this.gameRoot.getChildren().add(selectMenuScreen.getRoot());


        //gameScreen.getRoot().prefWidthProperty().bind(getRoot().widthProperty());
        //gameScreen.getRoot().prefHeightProperty().bind(getRoot().heightProperty());
        //pauseMenuScreen.getRoot().prefWidthProperty().bind(getRoot().widthProperty());
        //pauseMenuScreen.getRoot().prefHeightProperty().bind(getRoot().heightProperty());
    }

    public void setUpGame() {
        this.gameController = new GameController(gameScreen, pauseMenuScreen, gameOverScreen, timesUpScreen,
                startMenuScreen, selectMenuScreen, this);
        // set button handler
        pauseMenuScreen.setResumeRestartExitButtonHandler(gameController::resumeGame, gameController::restartGameInPauseMenu, gameController::exitButtonInPauseMenu);

        gameOverScreen.setRestartExitButtonHandler(gameController::restartGameInGameOver, gameController::exitButtonInGameOver);
        timesUpScreen.setRestartExitButtonHandler(gameController::restartGameInTimesUp, gameController::exitButtonInGameOver);

        startMenuScreen.setStartButtonHandler(gameController::startButtonInStartMenu);

        selectMenuScreen.setRelaxBlitzExitHandler(gameController::relaxButton, gameController::blitzButton, gameController::exitButtonInSelectMenu);

        GameState gameState = gameController.getGameState();
        new KeyInputController(mainScene, gameState, gameController);
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
