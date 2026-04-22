package tetris.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

public class LineMeterTube extends StackPane {

    private final double width;
    private final double maxHeight;
    private final double triangleTipHeight = 20;

    private LineTo baseRight, tip, baseLeft;

    public LineMeterTube(double width, double maxHeight) {
        this.width = width;
        this.maxHeight = maxHeight;


        this.setMaxSize(width, maxHeight);
        this.setMinSize(width, maxHeight);
        this.setPrefSize(width, maxHeight);
        this.setStyle("-fx-border-color: #555; -fx-border-width: 3; " +
                "-fx-background-color: rgba(255,255,255,0.05); -fx-border-radius: 5;"); // black tube

        // The inner filling (progress bar) -- Path element
        // Use 5 points to form the shape of the progress bar: bottom left, bottom right
        MoveTo startPoint = new MoveTo(width, maxHeight);
        baseRight = new LineTo(width, maxHeight);
        tip = new LineTo(width / 2, maxHeight);
        baseLeft = new LineTo(0, maxHeight);
        LineTo bottomLeft = new LineTo(0, maxHeight);

        Path fillPath = new Path(startPoint, baseRight, tip, baseLeft, bottomLeft);
        fillPath.setFill(Color.CYAN);
        fillPath.setStroke(null);

        this.getChildren().add(fillPath);
        StackPane.setAlignment(fillPath, Pos.BOTTOM_CENTER); // fit the Path to the bottom of the StackPane, so it grows upwards
    }

    public void setTubeProgress(int numLines, int goal) {
        double percentage = Math.min((double) numLines / goal, 1.0);  //                 /\
        double pathFillHeight = percentage * maxHeight;               //                |  |
                                                                      //                |__|
        // Calculate Y offset (Higher fill = Smaller Y value)
        double tipY = maxHeight - pathFillHeight;
        double baseY = Math.min(maxHeight, tipY + triangleTipHeight); // the base is lower than the triangle tip

        // Animate 3 points simultaneously to keep the triangle shape
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(tip.yProperty(), tipY),
                        new KeyValue(baseRight.yProperty(), baseY),
                        new KeyValue(baseLeft.yProperty(), baseY)
                )
        );
        timeline.play();
    }

}
