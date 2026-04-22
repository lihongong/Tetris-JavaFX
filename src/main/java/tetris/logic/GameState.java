package tetris.logic;

import tetris.util.GameMode;

import static tetris.util.TetrisConstants.*;

public class GameState {
    private boolean allowSwapMino;
    private boolean isDeactivating;
    private boolean isEffectOn; // effect is on when there is line removal
    private boolean isTSpin;
    private boolean isPaused;

    // game is over flags
    private boolean isGameOver;
    private boolean isSprintOver;


    // game counter
    private int deactivateCounter;
    private int autoDropCounter;
    //private int gameCounter;
    private int effectCounter;

    // game modes and goals
    private GameMode gameMode;
    private TimeManager timeManager;
    private int sprintGoal;



    public GameState(TimeManager timeManager) {
        this.timeManager = timeManager;

        isDeactivating = false;
        isEffectOn = false;
        isTSpin = false;
        isGameOver = false;
        isPaused = false;

        this.allowSwapMino = true;

        deactivateCounter = 0;
        autoDropCounter = 0;
        //gameCounter = 0;
        effectCounter = 0;

    }

    // =================================================
    // Flags methods
    // =================================================

    public boolean isAllowSwapMino() {
        return allowSwapMino;
    }
    public void enableSwapMino() {
        allowSwapMino = true;
    }
    public void disableSwapMino() {
        allowSwapMino = false;
    }
    public void resetDeactivation() {
        this.deactivateCounter = 0;
        this.isDeactivating = false;
    }
    public void resetEffectFlags() {
        isEffectOn = false;
        isTSpin = false;
        effectCounter = 0;
    }
    public boolean isDeactivating() {
        return isDeactivating;
    }
    public void setDeactivating() {
        isDeactivating = true;
    }
    public boolean isEffectOn() {
        return isEffectOn;
    }
    public void turnOnEffect() {
        isEffectOn = true;
    }
    public boolean isTSpin() {
        return isTSpin;
    }
    public void setIsTSpin() {
        isTSpin = true;
    }
    public boolean isTimesUp() {
        return gameMode == GameMode.BLITZ && timeManager.isTimesUp();
    }
    public boolean isGameOver() {
        return isGameOver;
    }
    public void setGameOver() {
        isGameOver = true;
    }
    public void checkSprintOver(int numLines) {
        this.isSprintOver = numLines >= sprintGoal;
        System.out.println("numline: " + numLines + ", sprintgoal: " + sprintGoal);
        System.out.println("checksprintOver " + isSprintOver);
    }
    public boolean isSprintOver() {
        return isSprintOver;
    }

    public void restartGame() {
        //gameCounter = 0;
        timeManager.resetCounter();

        isPaused = false;
        isGameOver = false;

        allowSwapMino = true;
    }

    public boolean isGamePaused() {
        return isPaused;
    }
    public void pauseTheGame() {
        isPaused = true;
    }
    public void resumeTheGame() {
        isPaused = false;
    }


    // =================================================
    // Counters methods
    // =================================================

    public void incrementCounter() {
        //gameCounter++;
        timeManager.incrementCounter();
    }
    public void incrementDeactivationCounter() {
        deactivateCounter++;
    }
    public boolean hasReachedDeactivateInterval() {
        return deactivateCounter == DEACTIVATE_INTERVAL;
    }
    public void incrementAutoDropCounter() {
        autoDropCounter++;
    }
    public boolean hasReachAutoDropInterval() {
        return autoDropCounter == AUTO_DROP_INTERVAL;
    }
    public void resetAutoDropCounter() {
        autoDropCounter = 0;
    }
    public void incrementEffectCounter() {
        effectCounter++;
    }
    public int getEffectCounter() {
        return effectCounter;
    }
    public boolean hasReachSpecialEffectDuration() {
        return effectCounter > SPECIAL_EFFECT_DURATION;
    }


    // game modes

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
    public GameMode getGameMode() {
        return gameMode;
    }

    public void setSprintGoal(int newSprintGoal) {
        this.sprintGoal = newSprintGoal;
    }
    public int getSprintGoal() {
        return this.sprintGoal;
    }



}
