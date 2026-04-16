package tetris.block;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

import java.awt.*;

import static tetris.util.TetrisConstants.*;

public class MinoBlock extends Block {
    private Color color;
    private Color darkColor;
    private Color superDarkColor;
    private Color ultraDarkColor;
    private Color outerGlow1;
    private Color outerGlow2;
    private Color outerGlow3;
    private int margin;

    public MinoBlock(Color color) {
        this.color = color;
        this.margin = 2;

        // Extract RGB components (JavaFX uses 0-1 range, so we convert)
        double red = color.getRed();
        double green = color.getGreen();
        double blue = color.getBlue();

        float div = 2f;
        float div2 = 5f; // for superDark
        float div3 = 7f; // ultraDark (glow)

        // Calculate darker colors
        this.darkColor = Color.rgb(
                (int) Math.round(red * 255 / div),
                (int) Math.round(green * 255 / div),
                (int) Math.round(blue * 255 / div)
        );

        this.superDarkColor = Color.rgb(
                (int) Math.round(red * 255 / div2),
                (int) Math.round(green * 255 / div2),
                (int) Math.round(blue * 255 / div2)
        );

        this.ultraDarkColor = Color.rgb(
                (int) Math.round(red * 255 / div3),
                (int) Math.round(green * 255 / div3),
                (int) Math.round(blue * 255 / div3)
        );

        this.outerGlow1 = Color.rgb(
                (int) Math.round(red * 255 / 2f),
                (int) Math.round(green * 255 / 2f),
                (int) Math.round(blue * 255 / 2f)
        );

        this.outerGlow2 = Color.rgb(
                (int) Math.round(red * 255 / 2.5f),
                (int) Math.round(green * 255 / 2.5f),
                (int) Math.round(blue * 255 / 2.5f)
        );

        this.outerGlow3 = Color.rgb(
                (int) Math.round((red * 255) / 3f),
                (int) Math.round(green * 255 / 3f),
                (int) Math.round(blue * 255 / 3f)
        );

        // Special handling for blue
        if (color.equals(Color.BLUE)) {
            this.color = Color.rgb(0, 120, 255);
            div2 = 4f;
            this.superDarkColor = Color.rgb(0, 8, (int) Math.round(blue * 255 / div2));
            div3 = 5f;
            this.ultraDarkColor = Color.rgb(0, 10, (int) Math.round(blue * 255 / div3));
        }
    }
    @Override
    public void drawAdd(GraphicsContext gc) {
            // Create radial gradient (equivalent to RadialGradientPaint)
            RadialGradient gradient = new RadialGradient(
                    0,                      // focusAngle
                    0,                      // focusDistance
                    pixelX + BLOCK_SIZE/2,             // centerX
                    pixelY + BLOCK_SIZE/2,             // centerY
                    30,                     // radius
                    false,                  // proportional
                    CycleMethod.NO_CYCLE,   // cycleMethod
                    new Stop(0, superDarkColor),
                    new Stop(1, darkColor)
            );

            // Set line width (equivalent to BasicStroke)
            gc.setLineWidth(1.0);

            // Edge glow effect
            gc.setFill(outerGlow3);
            gc.fillRect(pixelX, pixelY, BLOCK_SIZE, BLOCK_SIZE);

            gc.setFill(outerGlow2);
            gc.fillRect(pixelX+1, pixelY+1, BLOCK_SIZE-2, BLOCK_SIZE-2);

            gc.setFill(outerGlow1);
            gc.fillRect(pixelX+2, pixelY+2, BLOCK_SIZE-4, BLOCK_SIZE-4);

            // Inner rectangle with gradient
            gc.setFill(gradient);
            gc.fillRect(pixelX+3, pixelY+3, BLOCK_SIZE-6, BLOCK_SIZE-6);

            // Outer rectangle border
            gc.setStroke(color);
            gc.strokeRect(pixelX+3 + 0.5, pixelY+3 + 0.5, BLOCK_SIZE-6 - 0.5, BLOCK_SIZE-6 - 0.5);

            // Reset stroke width
            gc.setLineWidth(4.0);

            //gc.setFill(Color.WHITE);
            //gc.fillRect(pixelX, pixelY, BLOCK_SIZE, BLOCK_SIZE);
    }
    @Override
    public void drawRemove(GraphicsContext gc) {
        gc.clearRect(pixelX, pixelY, BLOCK_SIZE, BLOCK_SIZE);
    }

    /**
     *
     * @param gc
     * @param workingCounter start from 0 to duration - 1
     */
    public void drawFading(GraphicsContext gc, int workingCounter) {
        this.drawRemove(gc);

        double alpha = 1.0 - (workingCounter / (BLOCK_FADING_DURATION - 1));
        alpha = Math.max(0, alpha);  // Clamp to 0 if alpha goes below 0

        this.darkColor = new Color(darkColor.getRed(), darkColor.getGreen(), darkColor.getBlue(), alpha);
        this.superDarkColor = new Color(superDarkColor.getRed(), superDarkColor.getGreen(), superDarkColor.getBlue(), alpha);
        this.ultraDarkColor = new Color(ultraDarkColor.getRed(), ultraDarkColor.getGreen(), ultraDarkColor.getBlue(), alpha);

        this.drawAdd(gc);

        if (workingCounter == BLOCK_FADING_DURATION - 1) {
            this.drawRemove(gc);
        }
    }
    /**
     *
     * @param gc
     * @param workingCounter start from 0 to duration - 1
     */
    public void drawFalling(GraphicsContext gc, int workingCounter, int numLinesFall) {
        assert workingCounter < BLOCK_FALLING_DURATION;
        assert workingCounter >= 0;

        if (workingCounter < 0) {
            System.out.println("falling counter smaller than  0 !!!!!!!!!!!!!!!!");
        }

        int finalPixelY = row * BLOCK_SIZE;


        int fallHeight = numLinesFall * BLOCK_SIZE;

        drawRemove(gc);

        pixelY = finalPixelY - fallHeight + ((workingCounter * fallHeight) / BLOCK_FALLING_DURATION - 1); // finalPixelY + (workingCounter - BLOCK_FALLING_DURATION) * fallHeight / BLOCK_FALLING_DURATION;
        if (workingCounter == BLOCK_FALLING_DURATION - 1) {
            pixelY = finalPixelY; // snap
        }
        drawAdd(gc);
    }





}
