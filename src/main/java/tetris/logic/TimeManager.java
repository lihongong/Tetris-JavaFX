package tetris.logic;

import tetris.ui.GameScreen;

import java.sql.Time;

import static tetris.util.TetrisConstants.FPS;
import static tetris.util.TetrisConstants.TWO_MINUTE_DURATION;
import static tetris.util.TetrisConstants.WARNING_TIME;

public class TimeManager {
    private GameScreen gameScreen;
    private int counter;
    private int blitzDuration = TWO_MINUTE_DURATION;// TWO_MINUTE_DURATION;
    public void incrementCounter() {
        counter++;

    }
    public void resetCounter() {
        counter = 0;
    }
    public int getCurrentCounter() {
        return counter;
    }
    public boolean isTimesUp() {
        return counter >= blitzDuration;
    }
    public boolean isWarningTime() {
        return counter == WARNING_TIME;
    }
}
