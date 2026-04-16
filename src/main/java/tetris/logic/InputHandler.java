package tetris.logic;

import tetris.block.Mino;
import tetris.block.MinoBlock;
import tetris.ui.GameScreen;

public class InputHandler {
    private GameState gameState;
    private GameScreen gameScreen;
    private MinoManager minoManager;
    private MinoBlock[][] inactiveBlocksArray;
    private CollisionDetector collisionDetector;

    public InputHandler(GameState gameState, GameScreen gameScreen, MinoManager minoManager, MinoBlock[][] inactiveBlocksArray, CollisionDetector collisionDetector) {
        this.gameState = gameState;
        this.gameScreen = gameScreen;
        this.minoManager = minoManager;
        this.inactiveBlocksArray = inactiveBlocksArray;
        this.collisionDetector = collisionDetector;
    }

    public void handleUpPress() {
        Mino currentMino = minoManager.getCurrentMino();
        // Clear UI
        gameScreen.removeMinoInPlayingField(currentMino);
        gameScreen.removeMinoShadowInPlayingField(currentMino);

        // set the ghost block position (rotation)
        switch(currentMino.direction) {
            case 1: currentMino.getD2(); break;
            case 2: currentMino.getD3(); break;
            case 3: currentMino.getD4(); break;
            case 4: currentMino.getD1(); break;
        }

        // try to rotate the mino and snap to valid position
        currentMino.tryRotatingMino(inactiveBlocksArray, gameState);
        // after rotation, set the shadow position again
        currentMino.setShadowPosition(inactiveBlocksArray);

        // Set UI
        // Note: the order of adding mino shadow and mino doesn't matter since different Canvas object is used.
        gameScreen.addMinoShadowInPlayingField(currentMino);
        gameScreen.addMinoInPlayingField(currentMino);

    }
    public void handleHoldPress() {

        if (minoManager.isHoldMinoEmpty()) {
            minoManager.swapCurrentHoldNextMino();
        } else {
            minoManager.swapCurrentHoldMino();
        }
        gameState.disableSwapMino(); // only allow one swap per "round"

        // Sound effect for Hold (Shift)
        //TetrisPanel.soundEffect.play(11, false); // TODO: sound effect for hold
    }
    public void handleSpacePress() {
        Mino currentMino = minoManager.getCurrentMino();
        // Clear UI
        gameScreen.removeMinoInPlayingField(currentMino);
        gameScreen.clearAllShadowInPlayingField(); // clear all shadow and not just the mino position to prevent bugs

        //****** while(!bottomCollision) {
        while(!collisionDetector.hasBottomCollision()) {

            currentMino.moveDown();

            // ******* this.checkCurrentMinoMovementCollision();
            collisionDetector.checkCurrentMinoMovementCollision(currentMino);
        }
        // deactivate immediately
        currentMino.deactivate();

        // Set UI
        gameScreen.addMinoInPlayingField(currentMino);

        gameState.resetAutoDropCounter();

        // Sound effect for Space
        //TetrisPanel.soundEffect.play(12, false); TODO: sound effect for space
    }
    public void handleDownPress() {
        Mino currentMino = minoManager.getCurrentMino();
        //if (!bottomCollision) {
        if (!collisionDetector.hasBottomCollision()) {
            // Clear UI
            gameScreen.removeMinoInPlayingField(currentMino);
            gameScreen.removeMinoShadowInPlayingField(currentMino);

            currentMino.moveDown();
            gameState.resetAutoDropCounter();

            // Set UI
            gameScreen.addMinoInPlayingField(currentMino);
            gameScreen.addMinoShadowInPlayingField(currentMino);
        } else {
            gameScreen.removeMinoShadowInPlayingField(currentMino); // remember to remove the shadow when "touch down"
        }
    }
    public void handleLeftPress() {
        Mino currentMino = minoManager.getCurrentMino();
        // Clear UI
        gameScreen.removeMinoInPlayingField(currentMino);
        gameScreen.removeMinoShadowInPlayingField(currentMino);

        // **** if (!leftCollision) {
        if (!collisionDetector.hasLeftCollision()) {
            currentMino.moveLeft();
            // after moving to the left, set the shadow position again
            currentMino.setShadowPosition(inactiveBlocksArray);
        }

        // Set UI
        gameScreen.addMinoInPlayingField(currentMino);
        gameScreen.addMinoShadowInPlayingField(currentMino);
    }
    public void handleRightPress() {
        Mino currentMino = minoManager.getCurrentMino();
        // Clear UI
        gameScreen.removeMinoInPlayingField(currentMino);
        gameScreen.removeMinoShadowInPlayingField(currentMino);

        // ****** if (!rightCollision) {
        if (!collisionDetector.hasRightCollision()) {
            currentMino.moveRight();
            // after moving to the right, set the shadow position again
            currentMino.setShadowPosition(inactiveBlocksArray);
        }

        // Set UI
        gameScreen.addMinoInPlayingField(currentMino);
        gameScreen.addMinoShadowInPlayingField(currentMino);
    }



}
