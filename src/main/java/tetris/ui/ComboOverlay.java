package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tetris.util.UiAnimation;

public class ComboOverlay extends StackPane {
    private Text comboWording;
    private Text comboNumber;

    private Animation comboAnimation;
    private final int comboOverlayPositionY = -80;

    public ComboOverlay(int width, int height) {
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);

        // Add the stylesheet
        this.getStylesheets().add(getClass().getResource("/view/game-screen.css").toExternalForm());

        comboNumber = new Text();
        comboNumber.getStyleClass().addAll("combo", "combo-number");
        comboWording = new Text("COMBO!");
        comboWording.getStyleClass().addAll("combo", "combo-text");
        this.getChildren().addAll(comboWording, comboNumber);

        this.setVisible(false); // invisible at first

        this.preventLaggy();
    }
    // Bake the text shape into GPU memory and cache it
    private void preventLaggy() {
        // Prevent laggy first few combo overlay display
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED); // Prioritize smooth animation over crispness

        // 1. Make it technically "on screen" but invisible to the user
        this.setOpacity(1.0);
        this.setVisible(true);

        // 2. Cycle through common combo numbers to prime the cache
        // This bakes the stroke and shadow for 0-9 into the GPU memory
        String[] warmUpNumbers = {"0", "2", "3", "4", "5", "6", "7", "8", "9"};

        for (String n : warmUpNumbers) {
            comboNumber.setText(n);
            this.applyCss();
            this.layout();
        }

        // 3. Return to initial state
        this.setVisible(false);
        this.setOpacity(1.0);
    }

    public void showComboAnimation(int combo) {
        if (comboAnimation != null) {
            comboAnimation.stop(); // stop and overwrite the previous combo-1 animation
        }
        initComboAnimation(combo);

        PauseTransition showCase = new PauseTransition(Duration.seconds(0.067));

        final int comboOverlayRiseToY = comboOverlayPositionY - 30;
        final int comboOverlayFallToY = comboOverlayPositionY + 60;
        TranslateTransition comboOverlayRise = UiAnimation.translateY(comboOverlayPositionY, comboOverlayRiseToY, 0.067f, this);
        TranslateTransition comboOverlayFall = UiAnimation.translateY(comboOverlayRiseToY, comboOverlayFallToY, 0.067f, this);
        SequentialTransition comboOverlayRiseAndFall = new SequentialTransition(comboOverlayRise, comboOverlayFall);

        FadeTransition comboFadeOut = UiAnimation.fadeOut(1.0f, 0.25f, 0.133f, this);
        ParallelTransition comboRiseFallAndFade = new ParallelTransition(comboOverlayRiseAndFall, comboFadeOut);

        comboAnimation = new SequentialTransition(showCase, comboRiseFallAndFade);
        comboAnimation.setOnFinished(e -> {
            this.setVisible(false);
        });
        comboAnimation.play();
    }
    private void initComboAnimation(int combo) {
        this.setOpacity(1.0);
        this.setTranslateY(comboOverlayPositionY);
        comboNumber.setText("" + combo);
        this.setVisible(true);
    }
}
