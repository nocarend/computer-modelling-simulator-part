package ru.nsu.valikov.modelling.algorithm;

public record Line(Point p1, Point p2) {

    public boolean intersects(Line l) {
        Point a = p1;
        Point b = p2;
        Point c = l.p1;

        Point cmp = c.subtract(a);
        Point r = b.subtract(a);
        Point s = l.p2.subtract(c);

        double cmpxr = cmp.getX() * r.getY() - cmp.getY() * r.getX();
        double cmpxs = cmp.getX() * s.getY() - cmp.getY() * s.getX();
        double rxs = r.getX() * s.getY() - r.getY() * s.getX();

        if (cmpxr == 0f) {
            return ((c.getX() - a.getX() < 0) != (c.getX() - b.getX() < 0)) || ((c.getY() - a.getY() < 0) != (c.getY() - b.getY() < 0));
        }

        if (rxs == 0f)
            return false;

        double rxsr = 1f / rxs;
        double t = cmpxs * rxsr;
        double u = cmpxr * rxsr;

        return (t >= 0f) && (t <= 1f) && (u >= 0f) && (u <= 1f);
    }
}
