package ru.nsu.valikov.modelling.algorithm;

import java.util.ArrayList;
import java.util.List;

public class World {
    public List<Rectangle> rectangles;

    public World() {
        rectangles = new ArrayList<>();
    }


    public void add(Rectangle rect) {
        rectangles.add(rect);
    }

    public boolean intersects(Rectangle rectangle) {
        for (Rectangle r : rectangles) {
            if (rectangle.intersects(r)) {
                return true;
            }
        }

        return false;
    }

    public boolean intersects(Line line) {
        for (Rectangle r : rectangles) {
            if (r.intersects(line)) {
                return true;
            }
        }

        return false;
    }

}
