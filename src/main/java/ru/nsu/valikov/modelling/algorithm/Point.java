package ru.nsu.valikov.modelling.algorithm;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Point implements Serializable {
    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point add(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    public Point subtract(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public double dot(Point p) {
        return x * p.x + y * p.y;
    }

    public Point multiply(double constant) {
        return new Point(x * constant, y * constant);
    }

    public Point project(Point axis) {
        return axis.multiply(this.dot(axis) / axis.dot(axis));
    }

    public Point rotate(double theta) {
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        return new Point(x * cos - y * sin, x * sin + y * cos);
    }

    public Point translate(Point p) {
        return new Point(x + p.x, y + p.y);
    }

    @Override
    public boolean equals(Object obj) {
        var p = (Point) obj;
        if (Math.abs(p.x - x) < 1e-7 && Math.abs(p.y - y) < 1e-7) {
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return (int) (x * 727 + y);
    }
}
