package tetris.ui;

import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Stores the animation of buttons when mouse enters/exits it.
 */
public class ButtonTransitions {
    private final int MOUSE_ENTER_SLIDE_DURATION = 150;

    public static TranslateTransition createHoverLeftTransition(Button button) {
        return new TranslateTransition(Duration.millis(150), button);
    }
    public static TranslateTransition createHoverRightTransition(Button button) {
        return new TranslateTransition(Duration.millis(350), button);
    }

    public static TranslateTransition buttonMouseEnterLeftSlide(Button button, TranslateTransition hoverLeft) {
        //hoverLeft = new TranslateTransition(Duration.millis(150), button);
        if (hoverLeft != null) {
            hoverLeft.stop();
        }
        hoverLeft = new TranslateTransition(Duration.millis(150), button);
        hoverLeft.setToX(-80);
        hoverLeft.play();
        return hoverLeft;
    }

    public static TranslateTransition buttonMouseExitRightSlide(Button button, TranslateTransition hoverRight) {
        //hoverRight = new TranslateTransition(Duration.millis(350), button);
        if (hoverRight != null) {
            hoverRight.stop();
        }
        hoverRight = new TranslateTransition(Duration.millis(350), button);
        hoverRight.setToX(0);
        hoverRight.play();
        return hoverRight;
    }
}
