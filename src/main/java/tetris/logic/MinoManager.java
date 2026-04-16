package tetris.logic;

import tetris.Bag;
import tetris.block.*;
import tetris.ui.GameScreen;

public class MinoManager {
    private GameScreen gameplayUI;
    private GameState gameState;

    private Block[][] inactiveBlocksArray;


    private Mino currentMino;
    private Mino holdMino;
    private Mino nextMino;
    private Bag<Mino> bagOfMinos;

    public MinoManager(GameScreen gameplayUI, GameState gameState, Block[][] inactiveBlocksArray) {
        this.gameplayUI = gameplayUI;
        this.gameState = gameState;
        this.inactiveBlocksArray = inactiveBlocksArray;

        fillInBagOfMinos();
        initializeMinos();

    }
    public void fillInBagOfMinos() {
        this.bagOfMinos = new Bag<Mino>(new MinoI(), new MinoJ(), new MinoL(), new MinoO(),
                new MinoS(), new MinoT(), new MinoZ());
    }
    public void initializeMinos() {
        assert bagOfMinos != null;

        currentMino = bagOfMinos.pickRandomly();
        nextMino = bagOfMinos.pickRandomly();
        holdMino = null;

        addMinoToPlayingField();
        addMinoToNextMinoBox();
        // Don't add mino to hold box
    }

    public void addMinoToPlayingField() {
        currentMino.setPlayingFieldStartPosition();
        currentMino.setShadowPosition(inactiveBlocksArray);

        // add shadow blocks before normal blocks so the normal block will be in front when they overlap
        if (!gameState.isEffectOn()) {
            gameplayUI.addMinoShadowInPlayingField(currentMino);
        }
        // if effect is on, effect handler will add the shadow at the end of special effect (i.e. when the effect is about to end)
        gameplayUI.addMinoInPlayingField(currentMino);
    }
    public void removeMinoFromPlayingField() {
        gameplayUI.removeMinoInPlayingField(currentMino);
        gameplayUI.removeMinoShadowInPlayingField(currentMino);
    }
    public void addMinoToNextMinoBox() {
        nextMino.setNextAndHoldBoxPosition();
        gameplayUI.addMinoInNextBox(nextMino);
    }
    public void removeMinoFromNextMinoBox() {
        gameplayUI.removeMinoInNextBox(nextMino);
    }
    public void addMinoToHoldMinoBox() {
        holdMino.setNextAndHoldBoxPosition();
        gameplayUI.addMinoInHoldBox(holdMino);
    }
    public void removeMinoFromHoldMinoBox() {
        gameplayUI.removeMinoInHoldBox(holdMino);
    }

    public void swapCurrentHoldMino() {
        // clear UI
        this.removeMinoFromPlayingField();
        this.removeMinoFromHoldMinoBox();
        // swap mino
        Mino tempMino = currentMino;
        currentMino = holdMino;
        currentMino.setPlayingFieldStartPosition();
        holdMino = tempMino;
        holdMino.setNextAndHoldBoxPosition();
        // update UI
        this.addMinoToPlayingField();
        this.addMinoToHoldMinoBox();
    }
    public void swapCurrentHoldNextMino() {
        // clear UI
        this.removeMinoFromPlayingField();
        this.removeMinoFromNextMinoBox();
        // swap mino
        holdMino = currentMino;
        holdMino.setNextAndHoldBoxPosition();
        currentMino = nextMino;
        currentMino.setPlayingFieldStartPosition();
        nextMino = bagOfMinos.pickRandomly();
        nextMino.setNextAndHoldBoxPosition();
        // update UI
        this.addMinoToPlayingField();
        this.addMinoToNextMinoBox();
        this.addMinoToHoldMinoBox();
    }

    public void setNewCurrentNextMino() {
        // clear UI
        this.removeMinoFromNextMinoBox();

        currentMino = nextMino;
        nextMino = bagOfMinos.pickRandomly();

        // update UI
        this.addMinoToPlayingField();
        this.addMinoToNextMinoBox();
    }

    public void deactivateCurrentMino() {
        currentMino.deactivate();
    }

    public Mino getCurrentMino() {
        return currentMino;
    }
    public boolean isHoldMinoEmpty() {
        return holdMino == null;
    }

    // =================================================
    // Restart game
    // =================================================
    public void restartMinoManager() {
        fillInBagOfMinos();
        initializeMinos();
    }

}
