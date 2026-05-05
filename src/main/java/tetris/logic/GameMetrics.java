package tetris.logic;

import tetris.util.GameMode;
import tetris.util.SprintMode;

import static tetris.util.TetrisConstants.BASIC_SCORE;
import static tetris.util.TetrisConstants.T_SPIN_LINE;

public class GameMetrics {
    // scoring and metrics
    private int score;
    private int numLinesClear;
    /**
     * combo == -1 means reset combo count
     * combo == 0 means 1 consecutive line clear
     * combo == 1 means 2 consecutive line clear etc.
     */
    private int combo;

    // data
    private int relaxHighScore;
    private int blitzHighScore;
    private int sprintGoal;
    private int sprintBestTimeA;
    private int sprintBestTimeB;
    private int sprintBestTimeC;

    public GameMetrics() {
        this.score = 0;
        this.relaxHighScore = 0;
        this.blitzHighScore = 0;
        this.combo = -1;

        this.sprintBestTimeA = Integer.MAX_VALUE;
        this.sprintBestTimeB = Integer.MAX_VALUE;
        this.sprintBestTimeC = Integer.MAX_VALUE;
    }

    public void updateScore(int numLinesClear, boolean isTSpin) {
        // no line clear -- reset combo
        if (numLinesClear <= 0) {
            combo = -1; // reset combo
        } else {
            if (isTSpin) {
                numLinesClear = T_SPIN_LINE;
            }

            if (combo == 3 || combo == 4) {
                numLinesClear *= 2;
            } else if (combo == 5 || combo == 6) {
                numLinesClear *= 3;
            } else if (combo >= 7) {
                numLinesClear *= 4;
            }

            this.numLinesClear += numLinesClear;
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
    public void resetScoreAndLines() {
        score = 0;
        numLinesClear = 0;
        combo = -1;
    }
    public int getHighScore(GameMode gameMode) {
        if (gameMode == GameMode.RELAX) {
            return relaxHighScore;
        } else if (gameMode == GameMode.BLITZ) {
            return blitzHighScore;
        }
        return -1;
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
        } // when sprint mode finishes, it will have its score too because we still call updateScore() in Sprint mode,
          // but it won't update the high score
    }
    public int getNumLinesClear() {
        return this.numLinesClear;
    }
    public void setBestTime(int currentTimeToFinish, SprintMode sprintMode) {
        if (sprintMode == SprintMode.SPRINT_A) {
            if (currentTimeToFinish < sprintBestTimeA) {
                sprintBestTimeA = currentTimeToFinish;
            }
        } else if (sprintMode == SprintMode.SPRINT_B) {
            if (currentTimeToFinish < sprintBestTimeB) {
                sprintBestTimeB = currentTimeToFinish;
            }
        } else {
            if (currentTimeToFinish < sprintBestTimeC) {
                sprintBestTimeC = currentTimeToFinish;
            }
        }
    }
    public int getBestTime(SprintMode sprintMode) {
        if (sprintMode == SprintMode.SPRINT_A) {
            return sprintBestTimeA;
        } else if (sprintMode == SprintMode.SPRINT_B) {
            return sprintBestTimeB;
        } else {
            return sprintBestTimeC;
        }
    }
    public void setSprintGoal(int sprintGoal) {
        this.sprintGoal = sprintGoal;
    }
    public int getSprintGoal() {
        return sprintGoal;
    }
}
