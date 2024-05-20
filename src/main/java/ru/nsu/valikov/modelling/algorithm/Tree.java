package ru.nsu.valikov.modelling.algorithm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class Tree {
    public Map<Vector, Pair<Vector, Double>> tree;

    public Tree() {
        tree = new HashMap<>();
    }

    public void insert(Vector node, Vector parent, Double content) {
        tree.put(node, new ImmutablePair<>(parent, content));
    }

    public Vector findClosest(Vector node, ClosestCriteria crit) {
        Vector bestNode = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (Vector n : tree.keySet()) {
            double distance = crit.evaluate(node, n);

            if (distance < bestDistance) {
                bestNode = n;
                bestDistance = distance;
            }
        }

        return bestNode;
    }

    public void backtrack(EdgeIterationCallback<Vector, Double> callback, Vector startingNode) {
        Vector currentNode = startingNode;
        Pair<Vector, Double> parent;

        while (currentNode != null) {
            parent = tree.get(currentNode);
            callback.process(currentNode, parent.getLeft(), parent.getRight());
            currentNode = parent.getLeft();
        }
    }
}
