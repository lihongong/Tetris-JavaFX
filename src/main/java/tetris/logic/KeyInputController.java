package tetris.logic;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyInputController {

    public GameState gameState;
    public GameController gameController;
    public GameplayManager gameplayManager;
    public ActiveStateManager activeStateManager;

    // For key press logic
    private final Set<KeyCode> pressedKeys;
    private final Map<KeyCode, KeyState> keyStates;

    /**
     * Saves the state of a key.
     */
    private static class KeyState {
        /** The time when the key was last triggered. */
        private long lastKeyPressedTime;
        /**
         * Is the key being triggered for the 3rd and more times.
         * This flag is false when the key is triggered for the second time (key hold triggers) -- uses longer delay.
         * This flag is true when the key is triggered for the third and more times -- uses shorter delay
         */
        private boolean isInFastRepeatMode;
        /**
         * For "unholdable keys" (e.g. SPACE, ESCAPE) and make sure the key's methods won't be invoked even if the key
         * is contained in {@code pressedKeys} set.
         */
        private boolean hasBeenHandled; // use for non-holdable key (e.g. space, esc, r...)

        public void reset() {
            lastKeyPressedTime = 0;
            isInFastRepeatMode = false;
            hasBeenHandled = false;
        }
    }

    /**
     * Initialises the keyboard input checker via {@code AnimationTimer}.
     */
    public KeyInputController(Scene scene, GameState gameState, GameController gameController) {
        this.gameState = gameState;
        this.gameController = gameController;
        this.gameplayManager = gameController.getGameplayManager();
        this.activeStateManager = gameplayManager.getActiveStateManager();


        pressedKeys = new HashSet<>();
        keyStates = new HashMap<>();
        // holdable key
        keyStates.put(KeyCode.LEFT, new KeyState());
        keyStates.put(KeyCode.RIGHT, new KeyState());
        keyStates.put(KeyCode.UP, new KeyState());
        keyStates.put(KeyCode.DOWN, new KeyState());
        // "unholdable" key
        keyStates.put(KeyCode.SPACE, new KeyState());
        keyStates.put(KeyCode.ESCAPE, new KeyState());
        keyStates.put(KeyCode.R, new KeyState());
        keyStates.put(KeyCode.TAB, new KeyState());
        keyStates.put(KeyCode.SHIFT, new KeyState());


        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.D) {
                pressedKeys.add(KeyCode.RIGHT);
            } else if (e.getCode() == KeyCode.A) {
                pressedKeys.add(KeyCode.LEFT);
            } else if (e.getCode() == KeyCode.W) {
                pressedKeys.add(KeyCode.UP);
            } else if (e.getCode() == KeyCode.S) {
                pressedKeys.add(KeyCode.DOWN);
            } else {
                pressedKeys.add(e.getCode());
            }
        });
        scene.setOnKeyReleased(e -> {
            KeyCode keyCode = e.getCode();

            if (e.getCode() == KeyCode.D) {
                keyCode = KeyCode.RIGHT;
            } else if (e.getCode() == KeyCode.A) {
                keyCode = KeyCode.LEFT;
            } else if (e.getCode() == KeyCode.W) {
                keyCode = KeyCode.UP;
            } else if (e.getCode() == KeyCode.S) {
                keyCode = KeyCode.DOWN;
            }

            pressedKeys.remove(keyCode);

            KeyState keyState = keyStates.get(keyCode);
            if (keyState != null) {
                keyState.reset();
            }
        });

        /**
         * Checks the {@code pressedKey} Set to see if a key (e.g. UP, DOWN, SPACE, ESCAPE) is being pressed.
         * If a key is being pressed, handle it via {@code holdableKeyHandler} and {@code onlyPressKeyHandler}.
         * The check is being conducted at a frequency set by {@code AnimationTimer}.
         */
        new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                if (gameController.isIgnoreKeyInput()) {
                    return;
                }

                if (gameState.isGameOver()) {
                    onlyPressKeyHandler(KeyCode.R, gameController::restartGameInGameOver);
                    //gameState.restartGame();
                    //gameState.playGameLoop(););
                } else if (gameState.isTimesUp()) {
                    onlyPressKeyHandler(KeyCode.R, gameController::restartGameInTimesUp);
                } else if (gameState.isGamePaused()) {
                    // only handle once if both esc & R are pressed
                    if (pressedKeys.contains(KeyCode.ESCAPE)) {
                        onlyPressKeyHandler(KeyCode.ESCAPE, gameController::resumeGame);
                    } else if (pressedKeys.contains(KeyCode.R)) {
                        onlyPressKeyHandler(KeyCode.R, gameController::resumeGame);
                    }

                } else {
                    // game is running
                    if (!pressedKeys.contains(KeyCode.RIGHT) || !pressedKeys.contains(KeyCode.LEFT)) {
                        holdableKeyHandler(KeyCode.LEFT, currentTime, activeStateManager::handleLeftPress);
                        holdableKeyHandler(KeyCode.RIGHT, currentTime, activeStateManager::handleRightPress);
                    }

                    holdableKeyHandler(KeyCode.DOWN, currentTime, activeStateManager::handleDownPress);
                    holdableKeyHandler(KeyCode.UP, currentTime, activeStateManager::handleUpPress);

                    // don't allow repeated space press when hold down on space key
                    onlyPressKeyHandler(KeyCode.SPACE, activeStateManager::handleSpacePress);
                    onlyPressKeyHandler(KeyCode.ESCAPE, gameController::pauseGame);
                    // only handle once if both TAB & SHIFT are pressed
                    if (pressedKeys.contains(KeyCode.TAB)) {
                        onlyPressKeyHandler(KeyCode.TAB, activeStateManager::handleHoldPress);
                    } else if (pressedKeys.contains(KeyCode.SHIFT)) {
                        onlyPressKeyHandler(KeyCode.SHIFT, activeStateManager::handleHoldPress);
                    }

                }
            }
        }.start();
    }
    /**
     * Handles the key press once or multiple times if the key is being hold down for a long time.
     * <p>There is a delay between key trigger when the key is being hold down,
     * it helps makes the "hold" key feels natural but not too slow and unresponsive.
     * When the key is being hold down, it is recognized immediately, then there is a longer delay ({@code initialKeyPressedDelay})
     * for the second trigger, then subsequent triggers will have less delay ({@code holdKeyPressedDelay}) between them.
     * @param keyCode is the Keyboard keys (e.g. UP, DOWN, W, A, S, D)
     * @param activeStateMovementHandler is the method to be executed when the key is pressed.
     */
    public void holdableKeyHandler(KeyCode keyCode, long currentTime, Runnable activeStateMovementHandler) {
        KeyState keyState = keyStates.get(keyCode);
        if (pressedKeys.contains(keyCode)) {
            if (keyState.lastKeyPressedTime == 0) {
                activeStateMovementHandler.run();
                keyState.lastKeyPressedTime = currentTime;
            } else {
                long elapsedTime = currentTime - keyState.lastKeyPressedTime;

                long initialKeyPressedDelay = 200_000_000;
                long holdKeyPressedDelay = 50_000_000;
                long delay = keyState.isInFastRepeatMode ? holdKeyPressedDelay : initialKeyPressedDelay;
                if (elapsedTime >= delay) {
                    activeStateMovementHandler.run();
                    keyState.lastKeyPressedTime = currentTime;
                    keyState.isInFastRepeatMode = true;
                }
            }
        }
    }

    /**
     * Only handles the key press once (even if the key is being hold down for a long time).
     * @param keyCode is the Keyboard keys (e.g. SPACE. ESCAPE, R)
     * @param keyPressHandler is the method to be executed when the key is pressed.
     */
    public void onlyPressKeyHandler(KeyCode keyCode, Runnable keyPressHandler) {
        KeyState keyState = keyStates.get(keyCode);
        if (pressedKeys.contains(keyCode)) {
            if (!keyState.hasBeenHandled) {
                // pressedKeys.remove(keyCode); // removing from the pressedKey set won't work as the OS will insert it again in setOnClicked()
                keyState.hasBeenHandled = true;
                keyPressHandler.run();
            }
        }
    }
}