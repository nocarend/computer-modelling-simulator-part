package ru.nsu.valikov.modelling.algorithm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SettingsBackup {
    private double[][] modelParameters;
    private double[] plannerParameters;
    private int activeModel;
    private List<Rectangle> obstacles;
    private double startRotation, goalRotation;
    private Point startTranslation, goalTranslation;

}
