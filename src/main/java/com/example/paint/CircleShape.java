package com.example.paint;

import javafx.scene.paint.Color;

public class CircleShape {
    private final double centerX;
    private final double centerY;
    private double radius;
    private final Color color;
    private final String fillType;

    public CircleShape(double centerX, double centerY, double radius, Color color, String fillType) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.color = color;
        this.fillType = fillType;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }

    public String getFillType() {
        return fillType;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Color getColor() {
        return color;
    }
}
