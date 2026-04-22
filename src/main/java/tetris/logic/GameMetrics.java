package tetris.logic;

import tetris.util.GameMode;

public class GameMetrics {
    private final int BASIC_SCORE = 10;
    private final int T_SPIN_SCORE = 3; // + extra 3 points
    private int score;
    private int numLinesClear;
    private int relaxHighScore;
    private int blitzHighScore;
    private int sprintFastestTime;
    private int combo;

    public GameMetrics() {
        this.score = 0;
        this.relaxHighScore = 0;
        this.blitzHighScore = 0;
        this.sprintFastestTime = 0;
        this.combo = 0;
    }

    public void updateScore(int numLinesClear, boolean isTSpin) {
        this.numLinesClear += numLinesClear;

        if (numLinesClear <= 0) {
            combo = 0; // reset combo
        } else {
            if (isTSpin) {
                numLinesClear = T_SPIN_SCORE + numLinesClear; // extra points for tspin
            }
            // Non-linearity:
            // more lines cleared in one go -> more score awarded
            // higher combo -> more score awarded
            this.score += numLinesClear * (BASIC_SCORE + numLinesClear + combo*combo*combo);
            combo++;
        }
    }
    public int getCombo() {
        return combo;
    }
    public int getScore() {
        return score;
    }
    public void resetScore() {
        score = 0;
    }
    public int getHighScore(GameMode gameMode) {
        if (gameMode == GameMode.RELAX) {
            return relaxHighScore;
        } else if (gameMode == GameMode.BLITZ) {
            return blitzHighScore;
        } else {
            return sprintFastestTime;
        }
    }
    public void setHighScore(GameMode gameMode) {
        if (gameMode == GameMode.RELAX) {
            if (score > relaxHighScore) {
                relaxHighScore = score;
            }
        } else if (gameMode == GameMode.BLITZ) {
            if (score > blitzHighScore) {
                blitzHighScore = score;
            }
        } else {
            if (score > sprintFastestTime) {
                sprintFastestTime = score;
            }
        }
    }
    public int getNumLinesClear() {
        return this.numLinesClear;
    }
}
