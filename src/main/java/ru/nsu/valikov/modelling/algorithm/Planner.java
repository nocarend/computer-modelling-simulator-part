package ru.nsu.valikov.modelling.algorithm;

import lombok.val;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import ru.nsu.valikov.modelling.gui.PlannerPane;

import java.util.*;

public class Planner {
    private World world;
    private ClosestCriteria criteria;
    private DynamicModel model;
    private Vector initialState;
    private Vector finalState;
    private double reachGoalThreshold;
    private double deltaT;
    private int iterations;
    private Range<Double> steeringRange;
    private double steeringIncrement;
    private double tryGoalProbability;
    private PlannerPane listener;
    private int iteration;
    private Random randomGen;

    public void configure(PlannerParameters params) {
        this.world = params.getWorld();
        this.criteria = params.getCriteria();
        this.model = params.getModel();
        this.initialState = params.getInitialState();
        this.finalState = params.getFinalState();
        this.reachGoalThreshold = params.getReachGoalThreshold();
        this.deltaT = params.getDeltaT();
        this.iterations = params.getIterations();
        this.steeringRange = params.getSteeringRange();
        this.steeringIncrement = params.getSteeringIncrement();
        this.tryGoalProbability = params.getTryGoalProbability();
        this.randomGen = new Random();
    }

    public List<Point> bestPath() {
        Deque<Point> tree = new ArrayDeque<>();
        val start = model.getPosition(initialState);
        tree.addLast(start);
        Map<Point, Pair<Point, Integer>> weights = new HashMap<>();
        val goal = model.getPosition(finalState);
        Deque<Point> fin = new ArrayDeque<>();
        weights.put(start, new ImmutablePair<>(null, 0));
        val inc = 30;
        while (!tree.isEmpty()) {
            val cur = tree.pollFirst();
            if (Math.abs(cur.getY() - goal.getY()) + Math.abs(cur.getX() - goal.getX()) < 10) {
                fin.add(cur);
            }
            val w = weights.get(cur).getRight();
            var y = cur.getY();
            var x = cur.getX();
            if (y + inc <= 500) {
                val p = new Point(x, y + inc);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (y - inc >= 0) {
                val p = new Point(x, y - inc);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x + inc <= 500 && y + inc <= 500) {
                val p = new Point(x + inc, y + inc);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }

            if (x - inc >= 0 && y - inc >= 0) {
                val p = new Point(x - inc, y - inc);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x + inc <= 500) {
                val p = new Point(x + inc, y);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x - inc >= 0) {
                val p = new Point(x - inc, y);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x + inc <= 500 && y - inc >= 0) {
                val p = new Point(x + inc, y - inc);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x - inc >= 0 && y + inc <= 500) {
                val p = new Point(x - inc, y + inc);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x + inc * 0.75 <= 500 && y + inc * 0.75 <= 500) {
                val p = new Point(x + inc * 1.25, y + inc * 0.75);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }

            if (x - inc * 0.75 >= 0 && y - inc * 0.75 >= 0) {
                val p = new Point(x - inc * 0.75, y - inc * 0.75);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x + inc * 0.75 <= 500 && y - inc * 0.75 >= 0) {
                val p = new Point(x + inc * 0.75, y - inc * 0.75);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
            if (x - inc * 0.75 >= 0 && y + inc * 0.75 <= 500) {
                val p = new Point(x - inc * 0.75, y + inc * 0.75);
                if (weights.get(p) == null && !world.intersects(new Line(cur, p))) {
                    tree.addLast(p);
                    weights.put(p, new ImmutablePair<>(cur, w + 1));
                }
            }
        }
        List<Point> res = new ArrayList<>();
        while (!fin.isEmpty()) {
            var calc = fin.pollLast();
            do {
                res.add(calc);
                calc = weights.get(calc).getLeft();
            }
            while (calc != null);
        }
        return res;
    }

    public void plan() {
        Tree tree = new Tree();
        tree.insert(initialState, null, 0.0);
        Vector goalState = null;

        double lengthFinal = Double.POSITIVE_INFINITY;
        Pair<List<Double>, List<Vector>> finalPath = null;

        val best = bestPath();

        for (iteration = 0; iteration < iterations; iteration++) {
            Vector stateRand;

            if (randomGen.nextDouble() < tryGoalProbability) {
                stateRand = finalState;
            } else {
                stateRand = randomConfig(best, iteration, iterations, tree);
            }

            Vector newState;

            newState = extend(stateRand, tree);

            if (newState != null && isCloseEnough(newState, finalState)) {

                goalState = newState;

                if (listener != null) {
                    Pair<List<Double>, List<Vector>> path = getPath(tree, goalState);
                    Vector previous = null;
                    double fPath = 0;
                    for (Vector s : path.getRight()) {
                        if (previous != null) {
                            fPath += Math.sqrt(Math.pow(model.getPosition(previous).getX() - model.getPosition(s).getX(), 2) + Math.pow(model.getPosition(previous).getY() - model.getPosition(s).getY(), 2));
                        }
                        previous = s;
                    }
                    if (fPath < lengthFinal) {
                        lengthFinal = fPath;
                        finalPath = path;
                    }
                }
                break;
            }
        }


        if (finalPath != null) listener.pathFound(model, finalPath.getLeft(), finalPath.getRight());

        if (goalState == null && listener != null) {
            listener.pathNotFound();
        }

    }

    private Pair<List<Double>, List<Vector>> getPath(Tree graph, Vector goalState) {
        List<Double> inputSequence = new ArrayList<>();
        List<Vector> stateSequence = new ArrayList<>();

        graph.backtrack((n1, n2, content) -> {
            if (n2 != null) {
                inputSequence.add(content);
                stateSequence.add(n1);
            }
        }, goalState);

        Collections.reverse(inputSequence);
        Collections.reverse(stateSequence);

        return new ImmutablePair<>(inputSequence, stateSequence);
    }

    public void setPlannerListener(PlannerPane listener) {
        this.listener = listener;
    }

    private boolean isCloseEnough(Vector v1, Vector v2) {
        return criteria.evaluate(v1, v2) < reachGoalThreshold;
    }

    private Vector randomConfig(List<Point> best, int cur, int kmax, Tree tree) {
        Vector stateRand;
        Rectangle rect;
        do {
            stateRand = model.randomState(randomGen, cur, kmax, best);
            val st = stateRand.values();

            rect = new Rectangle(model.getPosition(stateRand), model.getLength(), model.getWidth(), model.getTheta(stateRand));
            for (int i = 0; i < 10; i++) {
                val inc = 10 * (i + 1);
                if (world.intersects(rect)) {
                    stateRand.add(new Vector(0, 0, inc, 0, 0));
                    rect = new Rectangle(model.getPosition(stateRand), model.getLength(), model.getWidth(), model.getTheta(stateRand));
                    if (world.intersects(rect)) {
                        stateRand.add(new Vector(0, 0, -2 * inc, 0, 0));
                        rect = new Rectangle(model.getPosition(stateRand), model.getLength(), model.getWidth(), model.getTheta(stateRand));
                        if (world.intersects(rect)) {
                            stateRand.add(new Vector(0, 0, inc, inc, 0));
                            rect = new Rectangle(model.getPosition(stateRand), model.getLength(), model.getWidth(), model.getTheta(stateRand));
                            if (world.intersects(rect)) {
                                stateRand.add(new Vector(0, 0, 0, -2 * inc, 0));
                                rect = new Rectangle(model.getPosition(stateRand), model.getLength(), model.getWidth(), model.getTheta(stateRand));
                                if (!world.intersects(rect)) {
                                    break;
                                }
                            } else break;
                        } else break;
                    } else break;
                } else break;
                stateRand = new Vector(st);
            }
        } while (world.intersects(rect));

        return stateRand;
    }

    private Vector extend(Vector state, Tree tree) {
        Vector nearest = tree.findClosest(state, criteria);
        Pair<Vector, Double> input = selectInput(nearest, state);
        Vector newState = input.getLeft();
        Line path = new Line(model.getPosition(state), model.getPosition(newState));
        Rectangle car = new Rectangle(model.getPosition(newState), model.getLength(), model.getWidth(), model.getTheta(newState));

        if (!world.intersects(path) && !world.intersects(car)) {
            tree.insert(newState, nearest, input.getRight());

            if (listener != null) {
                listener.nodeAdded(model, iteration, nearest, newState);
            }
            return newState;
        }

        return null;
    }

    private Vector nextState(Vector state, double input) {
        //Fourth-order Runge-Kutta:
        Vector k1 = model.derivatives(state, input);
        Vector k2 = model.derivatives(state.add(k1.divide(2)), input);
        Vector k3 = model.derivatives(state.add(k2.divide(2)), input);
        Vector k4 = model.derivatives(state.add(k3), input);

        Vector sumK = k1.add(k2.multiply(2)).add(k3.multiply(3)).add(k4);
        return state.add(sumK.multiply(deltaT / 6));
    }

    private Pair<Vector, Double> selectInput(Vector from, Vector to) {
        Vector bestState = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        double bestAngle = Double.POSITIVE_INFINITY;

        for (double s = steeringRange.getMinimum(); s < steeringRange.getMaximum(); s += steeringIncrement) {
            Vector state = nextState(from, s);
            double distance = criteria.evaluate(state, to);

            if (distance < bestDistance) {
                bestState = state;
                bestDistance = distance;
                bestAngle = s;
            }
        }

        return new ImmutablePair<>(bestState, bestAngle);
    }
}
