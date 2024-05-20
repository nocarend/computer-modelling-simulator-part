package ru.nsu.valikov.modelling.algorithm;

import java.util.Arrays;

public record Vector(double... values) {

    public double getValue(int n) {
        return values[n];
    }

    public Vector add(Vector v) {
        if (values.length != v.values().length) {
            throw new IllegalArgumentException();
        }

        double[] newValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i] + v.getValue(i);
        }

        return new Vector(newValues);
    }

    public Vector divide(double constant) {
        double[] newValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i] / constant;
        }

        return new Vector(newValues);
    }

    public Vector multiply(double constant) {
        double[] newValues = new double[values.length];

        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i] * constant;
        }

        return new Vector(newValues);
    }

    @Override
    public String toString() {
        return Arrays.toString(values);
    }
}
