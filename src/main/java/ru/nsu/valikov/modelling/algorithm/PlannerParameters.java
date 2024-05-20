package ru.nsu.valikov.modelling.algorithm;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Range;

@Setter
@Getter
public class PlannerParameters {
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
    private long randomSeed;

}
