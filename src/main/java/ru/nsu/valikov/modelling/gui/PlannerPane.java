package ru.nsu.valikov.modelling.gui;


import lombok.SneakyThrows;
import ru.nsu.valikov.modelling.algorithm.DynamicModel;
import ru.nsu.valikov.modelling.algorithm.Planner;
import ru.nsu.valikov.modelling.algorithm.PlannerParameters;
import ru.nsu.valikov.modelling.algorithm.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;

public class PlannerPane extends JPanel {
    private final MainWindow mainWindow;
    private final JProgressBar plannerProgress;
    private final JButton btnStart;
    private Planner planner;
    private boolean plannerExecuted;

    public PlannerPane(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new GridBagLayout());
        btnStart = new JButton("Start!");
        btnStart.setDisabledIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/running.gif"))));
        btnStart.addActionListener(this::startPlanner);
        add(btnStart, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 10, 10), 0, 0));

        plannerProgress = new JProgressBar();
        plannerProgress.setEnabled(false);
        add(plannerProgress, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 5, 10), 0, 0));
    }

    private void startPlanner(ActionEvent e) {
        new Thread(this::dos).start();
    }

    @SneakyThrows
    private void dos() {
        PlannerParameters params = mainWindow.getPlannerParameters();
        if (params.getModel().speed <= 0) {
            for (double i = 20; i <= 200; i += 0.5) {
                params.getModel().speed = i;
                if (!plannerExecuted) {
                    btnStart.setEnabled(false);
                    mainWindow.setEnabled(false);

                    planner = new Planner();
                    planner.configure(params);
                    planner.setPlannerListener(this);

                    plannerProgress.setEnabled(true);
                    plannerProgress.setMaximum(params.getIterations());
                    var a = new Thread(this::plannerThread);
                    a.start();
                    a.join();
                } else {
                    btnStart.setText("Start!");
                    mainWindow.setEnabled(true);
                    plannerExecuted = false;
                }
            }
            return;
        }
        if (!plannerExecuted) {
            btnStart.setEnabled(false);
            mainWindow.setEnabled(false);

            planner = new Planner();
            planner.configure(params);
            planner.setPlannerListener(this);

            plannerProgress.setEnabled(true);
            plannerProgress.setMaximum(params.getIterations());
            new Thread(this::plannerThread).start();
        } else {
            btnStart.setText("Start!");
            mainWindow.setEnabled(true);
            plannerExecuted = false;
        }
    }

    private void plannerThread() {
        planner.plan();
    }

    public void nodeAdded(DynamicModel model, int iteration, Vector from, Vector to) {
        SwingUtilities.invokeLater(() -> {
            plannerProgress.setValue(iteration);
            mainWindow.drawTreeLine((int) model.getPosition(from).getX(),
                    (int) model.getPosition(from).getY(),
                    (int) model.getPosition(to).getX(),
                    (int) model.getPosition(to).getY());
        });
    }

    public void pathFound(DynamicModel model, List<Double> inputs, List<Vector> states) {
        SwingUtilities.invokeLater(() -> {
            mainWindow.drawFinalPath(model, states);
            plannerFinished();
        });
    }

    public void pathNotFound() {
        SwingUtilities.invokeLater(this::plannerFinished);
    }

    private void plannerFinished() {
        btnStart.setEnabled(true);
        btnStart.setText("Clear");
        plannerProgress.setValue(0);
        plannerProgress.setEnabled(false);
        plannerExecuted = true;
    }
}