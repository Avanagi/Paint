package com.example.paint;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

import java.io.IOException;


/**
 * Основной контроллер, управляющий другими подчиненными
 */
public class Controller {
    @FXML
    private Slider sliderSize;
    @FXML
    private ColorPicker colorPick;
    @FXML
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    MenuController menuController = new MenuController();
    ResizeController resizeController = new ResizeController();
    private Canvas tempCanvas;
    FunctionController functionController;
    private Integer count = 0;

    /**
     * Метод, в котором в реальном времени происходит обработка событий
     */
    @FXML
    public void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
        tempCanvas = new Canvas(canvas.getWidth(), canvas.getHeight());
        functionController = new FunctionController(tempCanvas);
        if (count == 0) {
            canvas.setWidth(canvas.getWidth());
            graphicsContext.setFill(Color.WHITE);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            count++;
        }

        colorPick.setOnAction(e -> {
            Color selectedColor = colorPick.getValue();
            graphicsContext.setStroke(selectedColor);
            graphicsContext.setFill(selectedColor);
        });

        sliderSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newSize = newValue.doubleValue();
            graphicsContext.setLineWidth(newSize);
        });
    }
    @FXML
    public void onClickPencil() {
        functionController.onClickPencil(canvas, graphicsContext, colorPick);
    }
    @FXML
    public void onClickBrush() {
        functionController.onClickBrush(canvas, graphicsContext, colorPick);
    }
    @FXML
    public void onClickClear() {
        functionController.onClickClear(canvas, graphicsContext);
    }
    @FXML
    public void onClickEraser() {
        functionController.onClickEraser(canvas, graphicsContext);
    }
    @FXML
    public void onRectangleClick() {
        functionController.onRectangleClick(canvas, graphicsContext, colorPick, sliderSize);
    }
    @FXML
    public void onCircleClick() {
        functionController.onCircleClick(canvas, graphicsContext, colorPick, sliderSize);
    }
    @FXML
    public void onClickResizeImage() {
        resizeController.onClickResizeImage(canvas, graphicsContext);
    }
    @FXML
    public void NewFile() {
        menuController.NewFile(canvas);
    }
    @FXML
    public void FileSave() {
        menuController.FileSave(canvas);
    }
    @FXML
    public void ApplyExit() {
        menuController.ApplyExit();
    }
    @FXML
    public void InsertImageOnImage() {
        menuController.InsertImageOnImage(canvas, graphicsContext);
    }
    @FXML
    public void BilateralFilter() throws IOException {
        menuController.BilateralFilter(canvas);
    }
}