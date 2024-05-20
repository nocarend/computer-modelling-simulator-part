package ru.nsu.valikov.modelling.gui;

import com.google.gson.Gson;
import ru.nsu.valikov.modelling.algorithm.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class MainWindow extends JFrame {
    private final WorldPane worldPane;
    private final ModelChooserPane modelChooserPane;

    public MainWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        CarParameters initialParams = new CarParameters(20, 30, 50);
        setLayout(new BorderLayout());
        try (FileReader reader = new FileReader("settings.json")) {
            // Convert JSON file to Map
            Map<?, ?> dataMap = gson.fromJson(reader, Map.class);
            initialParams = new CarParameters((double) dataMap.get("Speed"), (double) dataMap.get("Width"), (double) dataMap.get("Length"));
            // Access data from the map using the keys from your JSON file
        } catch (IOException e) {
            e.printStackTrace();
        }
        worldPane = new WorldPane(this, initialParams);
        add(worldPane, BorderLayout.CENTER);

        modelChooserPane = new ModelChooserPane(this, initialParams, new DynamicModel(), new DynamicModel());

        PlannerPane plannerPane = new PlannerPane(this);
        add(plannerPane, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setTitle("Computer Modelling");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    PlannerParameters getPlannerParameters() {
        PlannerParameters params = new PlannerParameters();
        modelChooserPane.fillPlannerParameters(params);
        worldPane.fillPlannerParameters(params);
        params.setCriteria(new ClosestCriteria(params.getModel()));

        return params;
    }

    void drawTreeLine(int xFrom, int yFrom, int xTo, int yTo) {
        worldPane.drawTreeLine(xFrom, yFrom, xTo, yTo);
    }

    void drawFinalPath(DynamicModel model, java.util.List<Vector> states) {
        worldPane.drawFinalPath(model, states);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            worldPane.enableDrawing();
        } else {
            worldPane.disableDrawing();
        }
        modelChooserPane.setEnabled(enabled);
    }

    void carParametersChanged(CarParameters newParams) {
        worldPane.setCarParameters(newParams);
    }

    void fillBackup(SettingsBackup backup) {
        worldPane.fillBackup(backup);
    }

    void restoreBackup(SettingsBackup backup) {
        worldPane.restoreBackup(backup);
    }
}