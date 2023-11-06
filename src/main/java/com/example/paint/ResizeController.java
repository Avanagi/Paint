package com.example.paint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.image.WritableImage;

/**
 * Контроллер, отвечающий за увеличение размера выделенной области
 */
public class ResizeController {
    double startX, startY, endX, endY;
    boolean isSelecting, shouldEnlarge;
    WritableImage originalImage;

    /**
     *
     * @param canvas
     * @param graphicsContext
     */
    public void onClickResizeImage(Canvas canvas, GraphicsContext graphicsContext) {
        canvas.setOnMousePressed(event -> {
            originalImage = null;
            if (event.getButton() == MouseButton.PRIMARY) {
                startX = event.getX();
                startY = event.getY();
                isSelecting = true;
                shouldEnlarge = false;
            }
        });

        canvas.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                endX = event.getX();
                endY = event.getY();
                isSelecting = false;
                shouldEnlarge = true;
                originalImage = canvas.snapshot(null, null);
                redrawCanvas(graphicsContext, canvas);
            }
            canvas.setOnMouseReleased(null);
            canvas.setOnMousePressed(null);
            canvas.setOnMouseDragged(null);
        });

        canvas.setOnMouseDragged(event -> {
            if (isSelecting) {
                endX = event.getX();
                endY = event.getY();
                redrawCanvas(graphicsContext, canvas);
            }
        });
    }

    /**
     * @param graphicsContext
     * @param canvas
     */
    private void redrawCanvas(GraphicsContext graphicsContext, Canvas canvas) {
        if (originalImage != null) {
            double x = Math.min(startX, endX);
            double y = Math.min(startY, endY);
            double width = Math.abs(endX - startX);
            double height = Math.abs(endY - startY);

            if (shouldEnlarge) {
                double scale = 1.5;
                double scaledWidth = width / scale;
                double scaledHeight = height / scale;
                double scaledX = x - (scaledWidth - width) / 2;
                double scaledY = y - (scaledHeight - height) / 2;

                graphicsContext.drawImage(originalImage, scaledX, scaledY, scaledWidth, scaledHeight, x, y, width, height);
            } else {
                graphicsContext.drawImage(originalImage, 0, 0, canvas.getWidth(), canvas.getHeight());
            }
        }
    }
}
