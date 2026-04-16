package tetris.logic;

import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.ui.GameScreen;

public class ActiveStateManager {
    GameState gameState;
    GameScreen gameScreen;
    MinoManager minoManager;
    MinoBlock[][] inactiveBlocksArray;


    CollisionDetector collisionDetector;
    InputHandler inputHandler;

    public ActiveStateManager(GameState gameState, GameScreen gameScreen, MinoManager minoManager, MinoBlock[][] inactiveBlocksArray) {
        this.gameState = gameState;
        this.gameScreen = gameScreen;
        this.minoManager = minoManager;
        this.inactiveBlocksArray = inactiveBlocksArray;


        collisionDetector = new CollisionDetector(inactiveBlocksArray);
        inputHandler = new InputHandler(gameState, gameScreen, minoManager, inactiveBlocksArray, collisionDetector);
    }

    /**
     * Updates the game when current mino is active.
     * This method only handles auto drop mechanism and deactivation of current mino.
     * Key press movement on mino is handled by {@code handleUpPress}, {@code handleLeftPress} ...
     */
    public void update() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);

        // auto drop
        this.autoDropMechanism();

        // after actions, check if the mino is deactivating
        // NOTE: don't check isDeactivating before any action (KeyInput handling) to prevent buggy output (e.g. floating inactive blocks)
        if (gameState.isDeactivating()) {
            this.startDeactivatingCurrentMino();
        }
    }

    /**
     * Deactivates the mino when {@code DEACTIVATE_INTERVAL} is reached and {@code bottomCollision} is still true.
     * This method is invoked by {@code update()} when {@code isDeactivating} is true.
     */
    public void startDeactivatingCurrentMino() {
        gameState.incrementDeactivationCounter();

        if (gameState.hasReachedDeactivateInterval()) {
            // check if the mino is still hitting the bottom after 1 second
            // if still hitting the bottom, deactivate it
            // reset the deactivate counter and flag
            Mino currentMino = minoManager.getCurrentMino();
            collisionDetector.checkCurrentMinoMovementCollision(currentMino);

            if (collisionDetector.hasBottomCollision()) {
                // Clear the all shadow when touch down (prevent bugs)
                gameScreen.clearAllShadowInPlayingField();
                minoManager.deactivateCurrentMino();
            }
            gameState.resetDeactivation();
        }
    }

     private void autoDropMechanism() {
        //***** if (bottomCollision) {
        if (collisionDetector.hasBottomCollision()) {
            //gameState.isDeactivating = true;
            gameState.setDeactivating();
        } else {
            gameState.incrementAutoDropCounter();
            if (gameState.hasReachAutoDropInterval()) {
                // Clear UI
                Mino currentMino = minoManager.getCurrentMino();
                gameScreen.removeMinoInPlayingField(currentMino);

                currentMino.moveDown();

                // Set UI
                gameScreen.addMinoInPlayingField(currentMino);

                gameState.resetAutoDropCounter();
            }
        }
    }

    public void handleUpPress() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        inputHandler.handleUpPress();
    }
    public void handleLeftPress() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        inputHandler.handleLeftPress();
    }
    public void handleDownPress() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        inputHandler.handleDownPress();
    }
    public void handleRightPress() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        inputHandler.handleRightPress();
    }
    public void handleSpacePress() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        inputHandler.handleSpacePress();
    }
    public void handleHoldPress() {
        Mino currentMino = minoManager.getCurrentMino();
        collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        if (gameState.isAllowSwapMino()) {
            inputHandler.handleHoldPress();
        }
    }

}
