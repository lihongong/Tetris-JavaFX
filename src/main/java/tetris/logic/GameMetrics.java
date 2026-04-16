package tetris.logic;

import tetris.util.GameMode;

public class GameMetrics {
    private final int BASIC_SCORE = 10;
    private int score;
    private int relaxHighScore;
    private int blitzHighScore;
    private int sprintHighScore;
    private int level;
    private int combo;

    public GameMetrics() {
        this.score = 0;
        this.level = 1;
        this.relaxHighScore = 0;
        this.blitzHighScore = 0;
        this.sprintHighScore = 0;
        this.combo = 0;
    }

    public void updateScore(int numLinesClear, boolean isTSpin) {
        if (numLinesClear <= 0) {
            combo = 0; // reset combo
        } else {
            if (isTSpin) {
                numLinesClear = 6;
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
    public int getLevel() {
        return level;
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
            return sprintHighScore;
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
            if (score > sprintHighScore) {
                sprintHighScore = score;
            }
        }

    }
}
