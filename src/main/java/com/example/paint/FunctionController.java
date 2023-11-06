
package com.example.paint;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.awt.*;
import java.util.Optional;
import java.util.Stack;

/**
 * Контроллер, отвечающий за работу функций рисования
 */
public class FunctionController {

    private double startX, startY, endX, endY;
    private boolean isDrawing = false;
    private final Canvas tempCanvas;
    private CircleShape currentCircle = null;

    FunctionController(Canvas tempCanvas) {
        this.tempCanvas = tempCanvas;
    }

    /**
     * Функция рисовки карандаша
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     * @param colorPick       Атрибут, отвечающий за цвет карандаша
     */
    public void onClickPencil(Canvas canvas, GraphicsContext graphicsContext, ColorPicker colorPick) {
        try {
            graphicsContext.setStroke(colorPick.getValue());
            canvas.setOnMousePressed(e -> {
                graphicsContext.beginPath();
                graphicsContext.moveTo(e.getX(), e.getY());
                graphicsContext.stroke();
            });

            canvas.setOnMouseDragged(e -> {
                graphicsContext.lineTo(e.getX(), e.getY());
                graphicsContext.stroke();
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Функция отвечает за заливку выбранного объекта
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     * @param colorPick       Атрибут, отвечающий за цвет карандаша
     */
    public void onClickBrush(Canvas canvas, GraphicsContext graphicsContext, ColorPicker colorPick) {
        try {
            WritableImage snapshot = canvas.snapshot(null, null);
            PixelReader pixelReader = snapshot.getPixelReader();
            PixelWriter pixelWriter = graphicsContext.getPixelWriter();
            Color targetColor = pixelReader.getColor(0, 0);

            int canvasWidth = (int) canvas.getWidth();
            int canvasHeight = (int) canvas.getHeight();

            boolean[][] visited = new boolean[canvasWidth][canvasHeight];

            canvas.setOnMousePressed(event -> {
                int x = (int) event.getX();
                int y = (int) event.getY();

                if (x >= 0 && x < canvasWidth && y >= 0 && y < canvasHeight) {
                    Stack<Point> stack = new Stack<>();
                    stack.push(new Point(x, y));

                    while (!stack.isEmpty()) {
                        Point point = stack.pop();
                        x = point.x;
                        y = point.y;

                        if (x < 0 || x >= canvasWidth || y < 0 || y >= canvasHeight || visited[x][y]) {
                            continue;
                        }

                        Color currentColor = pixelReader.getColor(x, y);
                        if (currentColor.equals(targetColor) || currentColor.equals(Color.WHITESMOKE) || currentColor.equals(Color.WHITE)) {
                            pixelWriter.setColor(x, y, colorPick.getValue());
                            visited[x][y] = true;

                            stack.push(new Point(x + 1, y));
                            stack.push(new Point(x - 1, y));
                            stack.push(new Point(x, y + 1));
                            stack.push(new Point(x, y - 1));
                        }
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Функция полностью очищает сцену от всех объектов
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     */
    public void onClickClear(Canvas canvas, GraphicsContext graphicsContext) {
        try {
            canvas.setOnMousePressed(null);
            canvas.setOnMouseDragged(null);
            canvas.setWidth(canvas.getWidth());
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Функция, отвечающая за стирания объектов вручную
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     */
    public void onClickEraser(Canvas canvas, GraphicsContext graphicsContext) {
        try {
            graphicsContext.setStroke(Color.WHITE);
            graphicsContext.setLineWidth(graphicsContext.getLineWidth());

            final boolean[] erasing = {false};

            canvas.setOnMousePressed(event -> erasing[0] = true);

            canvas.setOnMouseReleased(event -> erasing[0] = false);

            canvas.setOnMouseDragged(event -> {
                if (erasing[0]) {
                    double x = event.getX();
                    double y = event.getY();
                    graphicsContext.clearRect(x - graphicsContext.getLineWidth() / 2,
                            y - graphicsContext.getLineWidth() / 2, graphicsContext.getLineWidth(),
                            graphicsContext.getLineWidth());
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Функция рисует прямоугольник в соответствии с выбором заливки
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     * @param colorPick       Параметр, отвечающий за цвет прямоугольника
     * @param sliderSize      Параметр, отвечающий за размер прямоугольника
     */
    public void onRectangleClick(Canvas canvas, GraphicsContext graphicsContext, ColorPicker colorPick,
                                 Slider sliderSize) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выбор типа заливки");
            alert.setHeaderText("Выберите тип заливки:");

            ButtonType noFillButton = new ButtonType("Без заливки");
            ButtonType horizontalFillButton = new ButtonType("Горизонтальная заливка");
            ButtonType verticalFillButton = new ButtonType("Вертикальная заливка");

            alert.getButtonTypes().setAll(noFillButton, horizontalFillButton, verticalFillButton);

            Optional<ButtonType> result = alert.showAndWait();

            result.ifPresent(buttonType -> {
                if (buttonType == noFillButton) {
                    doFill(canvas, graphicsContext, colorPick, "Без заливки", sliderSize);
                } else if (buttonType == horizontalFillButton) {
                    doFill(canvas, graphicsContext, colorPick, "Горизонтальная", sliderSize);
                } else if (buttonType == verticalFillButton) {
                    doFill(canvas, graphicsContext, colorPick, "Вертикальная", sliderSize);
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Функция, отвечающая за логику отрисовки прямоугольников
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     * @param colorPick       Параметр, отвечающий за цвет прямоугольника
     * @param sliderSize      Параметр, отвечающий за размер прямоугольника
     * @param fillType        Параметр, передающий значение выбора заливки
     */
    private void doFill(Canvas canvas, GraphicsContext graphicsContext, ColorPicker colorPick, String fillType,
                        Slider sliderSize) {
        graphicsContext.setLineWidth(graphicsContext.getLineWidth());
        switch (fillType) {
            case "Без заливки" -> {
                canvas.setOnMousePressed(e -> {
                    startX = e.getX();
                    startY = e.getY();
                    endX = startX;
                    endY = startY;
                    isDrawing = true;
                });
                canvas.setOnMouseDragged(e -> {
                    if (isDrawing) {
                        endX = e.getX();
                        endY = e.getY();
                        redrawCanvasRectangle(tempCanvas.getGraphicsContext2D(), startX, startY, endX, endY, canvas);
                    }
                });
                canvas.setOnMouseReleased(e -> {
                    if (isDrawing) {
                        isDrawing = false;
                        double x = Math.min(startX, endX);
                        double y = Math.min(startY, endY);
                        double width = Math.abs(endX - startX);
                        double height = Math.abs(endY - startY);
                        graphicsContext.setStroke(colorPick.getValue());
                        graphicsContext.strokeRect(x, y, width, height);
                        tempCanvas.getGraphicsContext2D().clearRect(0, 0,
                                tempCanvas.getWidth(), tempCanvas.getHeight());
                    }
                });
            }
            case "Горизонтальная" -> {
                canvas.setOnMousePressed(e -> {
                    startX = e.getX();
                    startY = e.getY();
                    endX = startX;
                    endY = startY;
                    isDrawing = true;
                });
                canvas.setOnMouseDragged(e -> {
                    if (isDrawing) {
                        endX = e.getX();
                        endY = e.getY();
                        redrawCanvasRectangle(tempCanvas.getGraphicsContext2D(), startX, startY, endX, endY, canvas);
                    }
                });
                canvas.setOnMouseReleased(e -> {
                    if (isDrawing) {
                        isDrawing = false;
                        double x = Math.min(startX, endX);
                        double y = Math.min(startY, endY);
                        double width = Math.abs(endX - startX);
                        double height = Math.abs(endY - startY);
                        graphicsContext.setStroke(colorPick.getValue());
                        graphicsContext.strokeRect(x, y, width, height);
                        drawHorizontalLinesInsideRectangle(graphicsContext, startX, startY,
                                endX, endY, colorPick.getValue(), sliderSize);
                        tempCanvas.getGraphicsContext2D().clearRect(0, 0,
                                tempCanvas.getWidth(), tempCanvas.getHeight());
                    }
                });
            }
            case "Вертикальная" -> {
                canvas.setOnMousePressed(e -> {
                    startX = e.getX();
                    startY = e.getY();
                    endX = startX;
                    endY = startY;
                    isDrawing = true;
                });
                canvas.setOnMouseDragged(e -> {
                    if (isDrawing) {
                        endX = e.getX();
                        endY = e.getY();
                        redrawCanvasRectangle(tempCanvas.getGraphicsContext2D(), startX, startY, endX, endY, canvas);
                    }
                });
                canvas.setOnMouseReleased(e -> {
                    if (isDrawing) {
                        isDrawing = false;
                        double x1 = Math.min(startX, endX);
                        double y1 = Math.min(startY, endY);
                        double x2 = Math.max(startX, endX);
                        double y2 = Math.max(startY, endY);

                        graphicsContext.setStroke(colorPick.getValue());
                        graphicsContext.strokeRect(x1, y1, x2 - x1, y2 - y1);
                        drawRectangleWithVerticalLines(graphicsContext, x1, y1, x2, y2,
                                colorPick.getValue(), sliderSize);
                        tempCanvas.getGraphicsContext2D().clearRect(0, 0,
                                tempCanvas.getWidth(), tempCanvas.getHeight());
                    }
                });
            }
        }
    }

    /**
     * Функция, ответственная за отрисовку горизонтальных линий внутри прямоугольника
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param x1              атрибут, содержащий координату X начала отрисовки прямоугольника
     * @param y1              атрибут, содержащий координату Y начала отрисовки прямоугольника
     * @param x2              атрибут, содержащий координату X конца отрисовки прямоугольника
     * @param y2              атрибут, содержащий координату Y конца отрисовки прямоугольника
     * @param lineColor       Параметр, отвечающий за цвет
     * @param sliderSize      Параметр, отвечающий за размер
     */
    private void drawHorizontalLinesInsideRectangle(GraphicsContext graphicsContext,
                                                    double x1, double y1, double x2, double y2,
                                                    Color lineColor, Slider sliderSize) {
        double y = y1;
        double step = 25.0;

        if (y1 > y2) {
            while (y > y2) {
                graphicsContext.setStroke(lineColor);
                graphicsContext.setLineWidth(sliderSize.getValue() / 2);
                graphicsContext.strokeLine(x1, y, x2, y);
                y -= step;
            }
        } else {
            while (y < y2) {
                graphicsContext.setStroke(lineColor);
                graphicsContext.setLineWidth(sliderSize.getValue() / 2);
                graphicsContext.strokeLine(x1, y, x2, y);
                y += step;
            }
        }
    }

    /**
     * Функция, ответственная за отрисовку горизонтальных линий внутри прямоугольника
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param x1              атрибут, содержащий координату X начала отрисовки прямоугольника
     * @param y1              атрибут, содержащий координату Y начала отрисовки прямоугольника
     * @param x2              атрибут, содержащий координату X конца отрисовки прямоугольника
     * @param y2              атрибут, содержащий координату Y конца отрисовки прямоугольника
     * @param lineColor       Параметр, отвечающий за цвет
     */
    private void drawRectangleWithVerticalLines(GraphicsContext graphicsContext,
                                                double x1, double y1, double x2, double y2,
                                                Color lineColor, Slider sliderSize) {
        double rectWidth = Math.abs(x2 - x1);
        double rectHeight = Math.abs(y2 - y1);
        double lineSpacing = 25.0;

        if (x1 > x2) {
            graphicsContext.strokeRect(x2, y1, rectWidth, rectHeight);
            for (double x = x2 + lineSpacing / 2; x < x1; x += lineSpacing) {
                graphicsContext.setStroke(lineColor);
                graphicsContext.setLineWidth(sliderSize.getValue() / 2);
                graphicsContext.strokeLine(x, y1, x, y2);
            }
        } else {
            graphicsContext.strokeRect(x1, y1, rectWidth, rectHeight);
            for (double x = x1 + lineSpacing / 2; x < x2; x += lineSpacing) {
                graphicsContext.setStroke(lineColor);
                graphicsContext.setLineWidth(sliderSize.getValue() / 2);
                graphicsContext.strokeLine(x, y1, x, y2);
            }
        }
    }

    /**
     * Функция, отвечающая за отрисовку прямоугольника с учетом всего и вся
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param x1              атрибут, содержащий координату X начала отрисовки прямоугольника
     * @param y1              атрибут, содержащий координату Y начала отрисовки прямоугольника
     * @param x2              атрибут, содержащий координату X конца отрисовки прямоугольника
     * @param y2              атрибут, содержащий координату Y конца отрисовки прямоугольника
     * @param canvas          Сцена, где отрисовывается прямоугольник
     */
    private void redrawCanvasRectangle(GraphicsContext graphicsContext,
                                       double x1, double y1, double x2, double y2, Canvas canvas) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.setLineWidth(graphicsContext.getLineWidth());
        graphicsContext.strokeRect(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Функция рисует груг в соответствии с выбором заливки
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     * @param colorPick       Параметр, отвечающий за цвет круга
     * @param sliderSize      Параметр, отвечающий за размер круга
     */
    public void onCircleClick(Canvas canvas, GraphicsContext graphicsContext, ColorPicker colorPick, Slider sliderSize) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Выбор типа заливки");
            alert.setHeaderText("Выберите тип заливки для круга:");

            ButtonType noFillButton = new ButtonType("Без заливки");
            ButtonType horizontalFillButton = new ButtonType("Горизонтальная заливка");
            ButtonType verticalFillButton = new ButtonType("Вертикальная заливка");

            alert.getButtonTypes().setAll(noFillButton, horizontalFillButton, verticalFillButton);

            Optional<ButtonType> result = alert.showAndWait();

            result.ifPresent(buttonType -> {
                if (buttonType == noFillButton) {
                    doDrawCircle("Без заливки", canvas, colorPick, sliderSize, graphicsContext);
                } else if (buttonType == horizontalFillButton) {
                    doDrawCircle("Горизонтальная", canvas, colorPick, sliderSize, graphicsContext);
                } else if (buttonType == verticalFillButton) {
                    doDrawCircle("Вертикальная", canvas, colorPick, sliderSize, graphicsContext);
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Функция, отвечающая за логику отрисовки кругов
     *
     * @param canvas          Сцена, где рисуются объекты
     * @param graphicsContext Параметр, необходимый для рисования
     * @param colorPick       Параметр, отвечающий за цвет круга
     * @param sliderSize      Параметр, отвечающий за размер круга
     * @param fillType        Параметр, передающий значение выбора заливки
     */
    private void doDrawCircle(String fillType, Canvas canvas, ColorPicker colorPick, Slider sliderSize,
                              GraphicsContext graphicsContext) {
        canvas.setOnMousePressed(e -> {
            double centerX = e.getX();
            double centerY = e.getY();
            double radius = 0;
            isDrawing = true;
            currentCircle = new CircleShape(centerX, centerY, radius, colorPick.getValue(), fillType);
        });

        canvas.setOnMouseDragged(e -> {
            if (isDrawing && currentCircle != null) {
                double mouseX = e.getX();
                double mouseY = e.getY();
                double newRadius = Math.max(Math.abs(mouseX - currentCircle.getCenterX()),
                        Math.abs(mouseY - currentCircle.getCenterY()));
                currentCircle.setRadius(newRadius);
                redrawCanvasCircle(tempCanvas.getGraphicsContext2D(), currentCircle, canvas, sliderSize);
            }
        });

        canvas.setOnMouseReleased(e -> {
            if (isDrawing && currentCircle != null) {
                isDrawing = false;
                currentCircle.setRadius(Math.max(0, currentCircle.getRadius()));
                graphicsContext.setStroke(currentCircle.getColor());
                graphicsContext.setLineWidth(sliderSize.getValue());
                drawCircle(graphicsContext, currentCircle, sliderSize);
                tempCanvas.getGraphicsContext2D().clearRect(0, 0, tempCanvas.getWidth(), tempCanvas.getHeight());
            }
        });
    }

    /**
     * Функция, отвечающая за передачу параметра о типе заливки
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param circle          Объект класс Circle, необходимый для хранения параметров круга
     * @param sliderSize      Параметр, отвечающий за размер
     */
    private void drawCircle(GraphicsContext graphicsContext, CircleShape circle, Slider sliderSize) {
        double centerX = circle.getCenterX();
        double centerY = circle.getCenterY();
        double radius = circle.getRadius();
        graphicsContext.strokeOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        if (circle.getFillType().equals("Горизонтальная")) {
            drawHorizontalLinesInsideCircle(graphicsContext, centerX, centerY, radius, circle.getColor(), sliderSize);
        } else if (circle.getFillType().equals("Вертикальная")) {
            drawVerticalLinesInsideCircle(graphicsContext, centerX, centerY, radius, circle.getColor(), sliderSize);
        }
    }

    /**
     * Функция, ответственная за отрисовку горизонтальных линий внутри круга
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param centerX         Параметр, хранящий центр круга по X
     * @param centerY         Параметр, хранящий центр круга по Y
     * @param radius          Параметр, хранящий радиус круга
     * @param lineColor       Параметр, отвечающий за цвет
     * @param sliderSize      Параметр, отвечающий за размер
     */
    private void drawHorizontalLinesInsideCircle(GraphicsContext graphicsContext, double centerX, double centerY,
                                                 double radius, Color lineColor, Slider sliderSize) {
        int canvasWidth = (int) graphicsContext.getCanvas().getWidth();
        int canvasHeight = (int) graphicsContext.getCanvas().getHeight();
        WritableImage writableImage = new WritableImage(canvasWidth, canvasHeight);
        PixelReader pixelReader = graphicsContext.getCanvas()
                .snapshot(null, null).getPixelReader();
        graphicsContext.setLineWidth(sliderSize.getValue());
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (isPointInsideCircle(x, y, centerX, centerY, radius) && y % (int) (double) 25 == 0) {
                    Color pixelColor = pixelReader.getColor(x, y);
                    if (!pixelColor.equals(lineColor)) {
                        graphicsContext.setStroke(lineColor);
                        graphicsContext.strokeLine(x, y, x, y);
                    }
                }
            }
        }
        graphicsContext.drawImage(writableImage, 0, 0);
    }

    /**
     * Функция проверки, входит ли точка в круг
     *
     * @param x       Координата точки по X
     * @param y       Координата точки по Y
     * @param centerX Координата центра круга по X
     * @param centerY Координата центра круга по Y
     * @param radius  Радиус круга
     * @return Да или Нет
     */
    private boolean isPointInsideCircle(int x, int y, double centerX, double centerY, double radius) {
        double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        return distance <= radius;
    }

    /**
     * Функция, ответственная за отрисовку горизонтальных линий внутри круга
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param centerX         Координата центра круга по X
     * @param centerY         Координата центра круга по Y
     * @param radius          Радиус круга
     * @param lineColor       Параметр, отвечающий за цвет
     * @param sliderSize      Параметр, отвечающий за размер
     */
    private void drawVerticalLinesInsideCircle(GraphicsContext graphicsContext, double centerX, double centerY,
                                               double radius, Color lineColor, Slider sliderSize) {
        int canvasWidth = (int) graphicsContext.getCanvas().getWidth();
        int canvasHeight = (int) graphicsContext.getCanvas().getHeight();
        WritableImage writableImage = new WritableImage(canvasWidth, canvasHeight);
        PixelReader pixelReader = graphicsContext.getCanvas()
                .snapshot(null, null).getPixelReader();
        graphicsContext.setLineWidth(sliderSize.getValue());
        for (int y = 0; y < canvasHeight; y++) {
            for (int x = 0; x < canvasWidth; x++) {
                if (isPointInsideCircle(x, y, centerX, centerY, radius) && x % (double) 25 == 0) {
                    Color pixelColor = pixelReader.getColor(x, y);
                    if (!pixelColor.equals(lineColor)) {
                        graphicsContext.setStroke(lineColor);
                        graphicsContext.strokeLine(x, y, x, y);
                    }
                }
            }
        }

        graphicsContext.drawImage(writableImage, 0, 0);
    }

    /**
     * Функция, отвечающая за отрисовку круга с учетом всего и вся
     *
     * @param graphicsContext Параметр, отвечающий за отрисовку
     * @param circle          Объект класс Circle
     * @param canvas          Сцена, где отрисовывается круг
     * @param sliderSize      Параметр, хранящй толщину линий круга
     */
    private void redrawCanvasCircle(GraphicsContext graphicsContext, CircleShape circle, Canvas canvas,
                                    Slider sliderSize) {
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCircle(graphicsContext, circle, sliderSize);
    }
}