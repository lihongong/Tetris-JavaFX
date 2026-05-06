package tetris.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import tetris.util.GameMode;
import tetris.util.SprintMode;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

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
    private int sprintGoal;

    // data
    @Expose private int relaxHighScore;
    @Expose private int blitzHighScore;
    @Expose private int sprintBestTimeA;
    @Expose private int sprintBestTimeB;
    @Expose private int sprintBestTimeC;

    // User data save
    private static final String FOLDER_NAME = ".my_tetris_game";
    private static final String FILE_NAME = "tetris_metrics.json";
    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();

    // gson will run this constructor to load from disk
    private GameMetrics() {
        this.score = 0;
        this.combo = -1;
        this.numLinesClear = 0;

        // Default user data
        this.relaxHighScore = 0;
        this.blitzHighScore = 0;
        this.sprintBestTimeA = Integer.MAX_VALUE;
        this.sprintBestTimeB = Integer.MAX_VALUE;
        this.sprintBestTimeC = Integer.MAX_VALUE;
    }

    /** factory method and will load from disk */
    public static GameMetrics of() {
        GameMetrics gameMetricsInstance = new GameMetrics();
        gameMetricsInstance.loadFromDisk();
        return gameMetricsInstance;
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
        assert gameMode == GameMode.RELAX || gameMode == GameMode.BLITZ:
                "getHighScore should only be invoke when game mode is Relax or Blitz";

        if (gameMode == GameMode.RELAX) {
            return relaxHighScore;
        } else {
            return blitzHighScore;
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
        } // when sprint mode finishes, it will have its score too because we still call updateScore() in Sprint mode,
          // but it won't update the high score

        saveToDisk(); // remember to save to disk
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

        saveToDisk(); // remember to save to disk
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

    // SAVE TO / LOAD FROM DISK
    public void saveToDisk() {
        File dir = new File(FOLDER_NAME);

        if (!dir.exists()) {
            dir.mkdir();
        }

        File saveFile = new File(dir, FILE_NAME);
        try (Writer writer = new FileWriter(saveFile)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromDisk() {
        File dir = new File(FOLDER_NAME);
        if (!dir.exists()) return;

        File saveFile = new File(dir, FILE_NAME);
        try (Reader reader = new FileReader(saveFile)) {
            GameMetrics loadedData = gson.fromJson(reader, GameMetrics.class);

            // Sync loaded data to this instance
            this.relaxHighScore = loadedData.relaxHighScore;
            this.blitzHighScore = loadedData.blitzHighScore;
            this.sprintBestTimeA = loadedData.sprintBestTimeA;
            this.sprintBestTimeB = loadedData.sprintBestTimeB;
            this.sprintBestTimeC = loadedData.sprintBestTimeC;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
