package com.example.paint;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Контроллер, отвечающий за отработку событий, связанных с работой файлов
 */
public class MenuController {
    private String currentFileFormat;

    /**
     * Функция, создающая новую сцену
     * @param canvas Сцена, на которой рисуются объекты
     */
    public void NewFile(Canvas canvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.setFill(Color.WHITESMOKE);
        currentFileFormat = null;
    }

    /**
     * Функция, отвечающая за сохранение файла в выбранном формате
     * @param canvas Сцена, на которой рисуются объекты
     */
    public void FileSave(Canvas canvas) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("BMP Files", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF Files", "*.gif"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpeg"),
                new FileChooser.ExtensionFilter("TIFF Files", "*.tiff"));
        File saveFile = fileChooser.showSaveDialog(canvas.getScene().getWindow());

        if (saveFile != null) {
            WritableImage writableImage = canvas.snapshot(new SnapshotParameters(), null);
            try {
                String format = currentFileFormat != null ? currentFileFormat : "png";
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), format, saveFile);
                currentFileFormat = format;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error saving the image.");
            }
        } else {
            System.out.println("Image file save cancelled.");
        }
    }

    /**
     * Функция выхода из приложения
     */
    public void ApplyExit() {
        System.exit(0);
    }

    /**
     * Функция, возвращающая расширение файла
     * @param filename Параметр, хранящий в себе имя и расширение файла
     * @return Расширение
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filename.substring(lastDotIndex + 1).toLowerCase();
        }
        return "png";
    }

    /**
     * Функция, используюшая дополнительный холст для увеличения изображения на основном
     * @param canvas Канвас, на котором происходят изменения
     * @param graphicsContext Обработчик событий канваса
     */
    public void InsertImageOnImage(Canvas canvas, GraphicsContext graphicsContext) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (selectedFile != null) {
            try {
                BufferedImage overlayImage = ImageIO.read(selectedFile);
                double canvasWidth = canvas.getWidth();
                double canvasHeight = canvas.getHeight();
                double imageWidth = overlayImage.getWidth();
                double imageHeight = overlayImage.getHeight();
                double scaledWidth = Math.min(canvasWidth, imageWidth);
                double scaledHeight = Math.min(canvasHeight, imageHeight);
                double x = (canvasWidth - scaledWidth) / 2;
                double y = (canvasHeight - scaledHeight) / 2;
                Canvas transparentCanvas = new Canvas(canvasWidth, canvasHeight);
                GraphicsContext transparentContext = transparentCanvas.getGraphicsContext2D();
                transparentContext.drawImage(canvas.snapshot(null, null),
                        0, 0, canvasWidth, canvasHeight);
                transparentContext.drawImage(SwingFXUtils.toFXImage(overlayImage, null),
                        x, y, scaledWidth, scaledHeight);
                graphicsContext.drawImage(transparentCanvas.snapshot(null, null),
                        0, 0, canvasWidth, canvasHeight);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading the image.");
            }
        } else {
            System.out.println("Image file selection cancelled.");
        }
    }

    /**
     * Функция, отвечающая за выбор фотки, отправки на обработку и показ
     * @param canvas Канвас, на котором изменяется и показывается фото
     * @throws IOException
     */
    public void BilateralFilter(Canvas canvas) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.bmp", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (selectedFile != null) {
            BufferedImage inputImage = ImageIO.read(selectedFile);
            BilateralFilter filter = new BilateralFilter(inputImage);
            BufferedImage filteredImage = filter.applyFilter();
            filter = new BilateralFilter(filteredImage);
            filteredImage = filter.applyFilter();
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            double canvasWidth = canvas.getWidth();
            double canvasHeight = canvas.getHeight();
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
            graphicsContext.drawImage(SwingFXUtils.toFXImage(filteredImage, null),
                    0, 0, canvasWidth, canvasHeight);
        } else {
            System.out.println("Image file selection cancelled.");
        }
    }
}