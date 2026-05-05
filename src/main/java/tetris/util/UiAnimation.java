package tetris.util;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;

public class UiAnimation {
    // Translate Transition
    public static ParallelTransition slideIn(int fromX, int toX, float animateDuration, Node... nodes) {
        ParallelTransition outputAnimations = new ParallelTransition();
        for (Node node : nodes) {
            // prevent nodes (button) from clipping in the center for 1 frame issue
            node.setTranslateX(fromX);

            TranslateTransition slideInTrans = new TranslateTransition(Duration.seconds(animateDuration), node);
            slideInTrans.setFromX(fromX);
            slideInTrans.setToX(toX);
            outputAnimations.getChildren().add(slideInTrans);
        }
        return outputAnimations;
    }
    public static ParallelTransition slideOut(int toX, float animateDuration, Node... nodes) {
        ParallelTransition outputAnimations = new ParallelTransition();
        for (Node node : nodes) {
            TranslateTransition slideOutTrans = new TranslateTransition(Duration.seconds(animateDuration), node);
            slideOutTrans.setToX(toX);
            outputAnimations.getChildren().add(slideOutTrans);
        }
        return outputAnimations;
    }

    // fall -- Translate Y direction
    public static ParallelTransition fall(int fromY, int toY, float animateDuration, Node... nodes) {
        ParallelTransition outputAnimations = new ParallelTransition();
        for (Node node : nodes) {
            TranslateTransition fallTrans = fall(fromY, toY, animateDuration, node);
            outputAnimations.getChildren().add(fallTrans);
        }
        return outputAnimations;
    }
    public static TranslateTransition fall(int fromY, int toY, float animateDuration, Node node) {
        TranslateTransition fallTrans = translateY(fromY, toY, animateDuration, node);
        fallTrans.setInterpolator(Interpolator.SPLINE(0.4, 0.0, 1.0, 1.0));
        return fallTrans;
    }
    public static TranslateTransition translateY(int fromY, int toY, float animateDuration, Node node) {
        TranslateTransition transY = new TranslateTransition(Duration.seconds(animateDuration), node);
        transY.setFromY(fromY);
        transY.setToY(toY);
        return transY;
    }


    // Fade
    public static ParallelTransition fadeIn(float fadeInFrom, float fadeInTo, float animateDuration, Node... nodes) {
        return fade(fadeInFrom, fadeInTo, animateDuration, nodes);
    }
    public static ParallelTransition fadeOut(float fadeOutFrom, float fadeOutTo, float animateDuration, Node... nodes) {
        return fade(fadeOutFrom, fadeOutTo, animateDuration, nodes);
    }
    public static ParallelTransition fade(float fadeFrom, float fadeTo, float animateDuration, Node... nodes) {
        ParallelTransition outputAnimations = new ParallelTransition();
        for (Node node : nodes) {
            node.setOpacity(fadeFrom);

            FadeTransition fadeInTrans = new FadeTransition(Duration.seconds(animateDuration), node);
            fadeInTrans.setFromValue(fadeFrom);
            fadeInTrans.setToValue(fadeTo);
            outputAnimations.getChildren().add(fadeInTrans);
        }
        return outputAnimations;
    }
    public static FadeTransition fadeIn(float fadeInFrom, float fadeInTo, float animateDuration, Node node) {
        FadeTransition fadeInTrans = new FadeTransition(Duration.seconds(animateDuration), node);
        fadeInTrans.setFromValue(fadeInFrom);
        fadeInTrans.setToValue(fadeInTo);
        return fadeInTrans;
    }
    public static FadeTransition fadeOut(float fadeOutFrom, float fadeOutTo, float animateDuration, Node node) {
        FadeTransition fadeInTrans = new FadeTransition(Duration.seconds(animateDuration), node);
        fadeInTrans.setFromValue(fadeOutFrom);
        fadeInTrans.setToValue(fadeOutTo);
        return fadeInTrans;
    }

    // Pop!
    public static SequentialTransition pop(float popUpFrom, float popUpTo, float popDownTo,
                                           float animateDuration, Node node) {
        node.setScaleX(popUpFrom);
        node.setScaleY(popUpFrom);

        ScaleTransition popUpTrans = new ScaleTransition(Duration.seconds(animateDuration), node);
        popUpTrans.setFromX(popUpFrom);
        popUpTrans.setFromX(popUpFrom);
        popUpTrans.setToX(popUpTo);
        popUpTrans.setToY(popUpTo);

        ScaleTransition popDownTrans = new ScaleTransition(Duration.seconds(animateDuration), node);
        popDownTrans.setToX(popDownTo); // down to original size
        popDownTrans.setToY(popDownTo);

        return new SequentialTransition(popUpTrans, popDownTrans);
    }

    // Rotate
    public static RotateTransition rotate(float toAngle, float animateDuration, Node node) {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(animateDuration), node);
        rotateTransition.setToAngle(toAngle);
        return rotateTransition;
    }

    // Scale
    public static ScaleTransition scale(float fromSize, float toSize, float animateDuration, Node node) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(animateDuration), node);
        scaleTransition.setFromX(fromSize);
        scaleTransition.setFromY(fromSize);
        scaleTransition.setToX(toSize);
        scaleTransition.setToY(toSize);
        return scaleTransition;
    }
    public static ParallelTransition scale(float fromSize, float toSize, float animateDuration, Node... nodes) {
        ParallelTransition output = new ParallelTransition();
        for (Node node : nodes) {
            ScaleTransition scaleTransition = scale(fromSize, toSize, animateDuration, node);
            output.getChildren().add(scaleTransition);
        }
        return output;
    }
    public static PauseTransition pause(float pauseDuration) {
        return new PauseTransition(Duration.seconds(pauseDuration));
    }
}
