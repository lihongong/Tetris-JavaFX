package tetris.ui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import tetris.util.UiAnimation;

public class ComboOverlay extends StackPane {
    private Label comboWording;
    private Label comboNumber;

    private final int comboWordingRelativeToNumberPositionY = 25;
    private Animation comboAnimation;
    private final int comboOverlayPositionY = -80;

    public ComboOverlay(int width, int height) {
        this.setMaxSize(width, height);
        this.setMinSize(width, height);
        this.setPrefSize(width, height);

        comboNumber = new Label("9");
        comboWording = new Label("COMBO!");
        comboWording.setTranslateY(comboWordingRelativeToNumberPositionY);
        this.getChildren().addAll(comboNumber, comboWording);

        this.setVisible(false); // invisible at first
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
