package com.example.paint;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.Color;

/**
 * Класс фильтра, который старается убрать с фотографии шумы и старается сглаживать
 */
public class BilateralFilter {
    private final BufferedImage inputImage;
    private final int diameter;
    private final double sigmaColor;
    private final double sigmaSpace;

    /**
     * Конструктор с параметрами
     * @param inputImage Изображение, которое передается в конструктор класса
     */
    public BilateralFilter(BufferedImage inputImage) {
        this.inputImage = inputImage;
        this.diameter = 7;
        this.sigmaColor = 70;
        this.sigmaSpace = 70;
    }

    /**
     * Это метод, который выполняет фильтрацию Bilateral Filter на входном изображении.
     * Он проходит через каждый пиксель изображения и вычисляет взвешенное среднее значение цветовых компонентов
     * пикселей в окне около текущего пикселя с использованием формулы Bilateral Filter. Затем он создает
     * новое изображение (outputImage), в котором каждый пиксель заменяется на результат фильтрации.
     * @return Измененное изображение
     */
    public BufferedImage applyFilter() {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage outputImage = deepCopy(inputImage);

        int halfDiameter = diameter / 2;
        double twoSigmaColorSquared = 2 * sigmaColor * sigmaColor;
        double twoSigmaSpaceSquared = 2 * sigmaSpace * sigmaSpace;

        for (int y = halfDiameter; y < height - halfDiameter; y++) {
            for (int x = halfDiameter; x < width - halfDiameter; x++) {
                double weightSum = 0;
                double redSum = 0;
                double greenSum = 0;
                double blueSum = 0;

                Color centerColor = new Color(inputImage.getRGB(x, y));

                for (int i = -halfDiameter; i <= halfDiameter; i++) {
                    for (int j = -halfDiameter; j <= halfDiameter; j++) {
                        int currentX = x + j;
                        int currentY = y + i;
                        Color currentColor = new Color(inputImage.getRGB(currentX, currentY));

                        double colorDistanceSquared = getColorDistanceSquared(centerColor, currentColor);
                        double spaceDistanceSquared = getSpaceDistanceSquared(x, y, currentX, currentY);

                        double weight = Math.exp(-colorDistanceSquared / twoSigmaColorSquared - spaceDistanceSquared / twoSigmaSpaceSquared);
                        weightSum += weight;

                        redSum += weight * currentColor.getRed();
                        greenSum += weight * currentColor.getGreen();
                        blueSum += weight * currentColor.getBlue();
                    }
                }

                int red = (int) (redSum / weightSum);
                int green = (int) (greenSum / weightSum);
                int blue = (int) (blueSum / weightSum);

                Color filteredColor = new Color(red, green, blue);
                outputImage.setRGB(x, y, filteredColor.getRGB());
            }
        }

        return outputImage;
    }

    /**
     * Это вспомогательный метод, который вычисляет квадрат разницы в цветах между двумя объектами Color.
     * Это используется для вычисления весов при фильтрации.
     * @param c1 Цвет текущей точки
     * @param c2 Цвет соседней точки
     * @return Квадрат разницы в цветах между двумя объектами Color
     */
    private double getColorDistanceSquared(Color c1, Color c2) {
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return dr * dr + dg * dg + db * db;
    }

    /**
     * Это еще один вспомогательный метод, который вычисляет квадрат расстояния в пространстве
     * между двумя точками (пикселями) на изображении. Это также используется для вычисления весов при фильтрации.
     * @param x1 Координата первой точки по X
     * @param y1 Координата первой точки по Y
     * @param x2 Координата второй точки по X
     * @param y2 Координата второй точки по Y
     * @return Квадрат расстояния в пространстве
     */
    private double getSpaceDistanceSquared(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    /**
     * Этот метод создает глубокую копию входного изображения.
     * Глубокая копия означает, что создается новый объект изображения с теми же данными пикселей.
     * Это необходимо, чтобы не изменить исходное изображение в процессе фильтрации.
     * @param original Оригинальное фото
     * @return Фото
     */
    private BufferedImage deepCopy(BufferedImage original) {
        ColorModel cm = original.getColorModel();
        boolean isAlphaPremultyplied = cm.isAlphaPremultiplied();
        WritableRaster raster = original.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultyplied, null);
    }
}
