package tetris.util;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;

public class UiAnimation {
    // Translate Transition
    public static ArrayList<TranslateTransition> slideIn(int fromX, int toX, float animateDuration, Node... nodes) {
        ArrayList<TranslateTransition> outputAnimations = new ArrayList<>();
        for (Node node : nodes) {
            TranslateTransition slideInTrans = new TranslateTransition(Duration.seconds(animateDuration), node);
            slideInTrans.setFromX(fromX);
            slideInTrans.setToX(toX);
            outputAnimations.add(slideInTrans);
        }
        return outputAnimations;
    }
    public static ArrayList<TranslateTransition> slideOut(int toX, float animateDuration, Node... nodes) {
        ArrayList<TranslateTransition> outputAnimations = new ArrayList<>();
        for (Node node : nodes) {
            TranslateTransition slideOutTrans = new TranslateTransition(Duration.seconds(animateDuration), node);
            slideOutTrans.setToX(toX);
            outputAnimations.add(slideOutTrans);
        }
        return outputAnimations;
    }

    // Fade
    public static ArrayList<FadeTransition> fadeIn(float fadeInFrom, float fadeInTo, float animateDuration, Node... nodes) {
        return fade(fadeInFrom, fadeInTo, animateDuration, nodes);
    }
    public static ArrayList<FadeTransition> fadeOut(float fadeOutFrom, float fadeOutTo, float animateDuration, Node... nodes) {
        return fade(fadeOutFrom, fadeOutTo, animateDuration, nodes);
    }
    public static ArrayList<FadeTransition> fade(float fadeFrom, float fadeTo, float animateDuration, Node... nodes) {
        ArrayList<FadeTransition> outputAnimations = new ArrayList<>();
        for (Node node : nodes) {
            FadeTransition fadeInTrans = new FadeTransition(Duration.seconds(animateDuration), node);
            fadeInTrans.setFromValue(fadeFrom);
            fadeInTrans.setToValue(fadeTo);
            outputAnimations.add(fadeInTrans);
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


}
