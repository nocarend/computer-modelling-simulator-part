package ru.nsu.valikov.modelling.algorithm;

import lombok.Getter;

import java.util.*;

public class DynamicModel {
    public double speed;
    @Getter
    private double length;
    @Getter
    private double width;
    private double cf, cr;
    private double mass;
    private double inertia;
    private double lf, lr;

    private static final int STATE_VY = 0;
    private static final int STATE_R = 1;
    private static final int STATE_X = 2;
    private static final int STATE_Y = 3;
    private static final int STATE_THETA = 4;

    public String[] getParameterNames() {
        return new String[]{
                "Front cornering stiffness",
                "Rear cornering stiffness",
                "Mass",
                "Inertia"
        };
    }

    public String getModelName() {
        return "Dynamic Model";
    }

    public void configure(CarParameters carParameters, double... parameters) {
        this.speed = carParameters.speed();
        this.width = carParameters.width();
        this.length = carParameters.length();
        this.cf = parameters[0];
        this.cr = parameters[1];
        this.mass = parameters[2];
        this.inertia = parameters[3];
        this.lf = this.lr = length / 2;
        passed.clear();
        res.clear();
    }

    public Vector derivatives(Vector state, double input) {
        double vy = state.getValue(STATE_VY);
        double r = state.getValue(STATE_R);
        double theta = state.getValue(STATE_THETA);

        double cosInput = Math.cos(input);
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        double a = -(cf * cosInput + cr) / (mass * speed);
        double b = (-lf * cf * cosInput + lr * cr) / (mass * speed) - speed;
        double c = (-lf * cf * cosInput + lr * cr) / (inertia * speed);
        double d = -(lf * lf * cf * cosInput + lr * lr * cr) / (inertia * speed);
        double e = cf * cosInput / mass;
        double f = lf * cf * cosInput / inertia;

        double vyDot = a * vy + c * r + e * input;
        double rDot = b * vy + d * r + f * input;
        double xDot = speed * cosTheta - vy * sinTheta;
        double yDot = speed * sinTheta + vy * cosTheta;

        return new Vector(vyDot, rDot, xDot, yDot, r);
    }

    public Point getPosition(Vector state) {
        return new Point(state.getValue(STATE_X), state.getValue(STATE_Y));
    }

    public double getTheta(Vector state) {
        return state.getValue(STATE_THETA);
    }

    public static Set<Point> passed = new HashSet<>();
    public static List<Point> res = new ArrayList<>();

    public Vector randomState(Random randomGen, int cur, int kmaxk, List<Point> best) {
        double sTheta = randomGen.nextDouble() * 2 * Math.PI;
        var i = randomGen.nextInt(cur / kmaxk * best.size(), best.size());
        var p = best.get(i);
        if (randomGen.nextDouble() > 0.7) {
            p = new Point(randomGen.nextInt(0, 500), randomGen.nextInt(0, 500));
        }
        return new Vector(0, 0, p.getX(), p.getY(), sTheta);
    }

    public Vector positionState(Point position, double theta) {
        return new Vector(0, 0, position.getX(), position.getY(), theta);
    }

    @Override
    public String toString() {
        return getModelName();
    }
}
