package ru.nsu.valikov.modelling.gui;

import com.google.gson.Gson;
import org.apache.commons.lang3.Range;
import ru.nsu.valikov.modelling.algorithm.CarParameters;
import ru.nsu.valikov.modelling.algorithm.DynamicModel;
import ru.nsu.valikov.modelling.algorithm.PlannerParameters;
import ru.nsu.valikov.modelling.algorithm.SettingsBackup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.*;
import java.text.NumberFormat;
import java.util.Map;

public class ModelChooserPane {
    private final MainWindow mainWindow;
    private final DynamicModel[] models;
    private final JComboBox<DynamicModel> cmbModels;
    private final CardLayout modelParamsLayout;
    private final JPanel modelParamsPanel;
    private final ParameterPanel[] modelParameterPanels;
    private final ParameterPanel carParameterPanel;
    private final ParameterPanel plannerParameterPanel;
    private final JButton btnSave;
    private final JButton btnLoad;

    public ModelChooserPane(MainWindow mainWindow, CarParameters carParameters, DynamicModel... models) {
        this.mainWindow = mainWindow;
        this.models = models;

        cmbModels = new JComboBox<>(models);
        cmbModels.addActionListener(this::modelChanged);

        modelParamsPanel = new JPanel();
        modelParamsLayout = new CardLayout();
        modelParamsPanel.setLayout(modelParamsLayout);
        modelParameterPanels = new ParameterPanel[models.length];

        for (int i = 0; i < models.length; i++) {
            DynamicModel m = models[i];
            modelParameterPanels[i] = new ParameterPanel("ModelParameters", m.getParameterNames());
            modelParamsPanel.add(modelParameterPanels[i], m.getModelName());
        }

        String[] carParameterNames = new String[]{"Speed", "Width", "Length"};
        carParameterPanel = new ParameterPanel("Car parameters", carParameterNames);
        carParameterPanel.setParameterChangeListener(this::carParameterChanged);
        String[] plannerParameterNames = new String[]{"Reach goal threshold", "Time increment (s)", "Max. iterations", "Min. steering angle (deg)", "Max. steering angle (deg)", "Steering increments (deg)", "Try goal probability", "Random seed"};
        plannerParameterPanel = new ParameterPanel("Planner parameters", plannerParameterNames);

        btnSave = new JButton("Save");
        btnSave.addActionListener(this::generateBackup);
        btnLoad = new JButton("Load");
        btnLoad.addActionListener(this::restoreBackup);

        setCarParameters(carParameters);
    }

    public void setEnabled(boolean enabled) {
        cmbModels.setEnabled(enabled);
        carParameterPanel.setEnabled(enabled);
        plannerParameterPanel.setEnabled(enabled);

        for (ParameterPanel p : modelParameterPanels) {
            p.setEnabled(enabled);
        }
    }

    public CarParameters getCarParameters() {
        Gson gson = new Gson();
        double[] values = new double[3];

        try (FileReader reader = new FileReader("settings.json")) {
            // Convert JSON file to Map
            Map<?, ?> dataMap = gson.fromJson(reader, Map.class);

            values[0] = (double) dataMap.get("Speed");
            values[1] = (double) dataMap.get("Width");
            values[2] = (double) dataMap.get("Length");
            // Access data from the map using the keys from your JSON file
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CarParameters(values[0], values[1], values[2]);
    }

    private DynamicModel getModel() {
        DynamicModel model = (DynamicModel) cmbModels.getSelectedItem();
        Gson gson = new Gson();
        double[] values = new double[10];

        try (FileReader reader = new FileReader("settings.json")) {
            // Convert JSON file to Map
            Map<?, ?> dataMap = gson.fromJson(reader, Map.class);

            values[0] = (double) dataMap.get("FrontCornerStiffness");
            values[1] = (double) dataMap.get("RearCornerStiffness");
            values[2] = (double) dataMap.get("Weight");
            values[3] = (double) dataMap.get("Inertia");
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.configure(getCarParameters(), values);
        return model;
    }

    public void fillPlannerParameters(PlannerParameters params) {
        double[] values = plannerParameterPanel.getValues();
        params.setReachGoalThreshold(values[0]);
        params.setDeltaT(values[1]);
        params.setIterations((int) values[2]);
        params.setSteeringRange(Range.between(Math.toRadians(values[3]), Math.toRadians(values[4])));
        params.setSteeringIncrement(Math.toRadians(values[5]));
        params.setTryGoalProbability(values[6]);
        params.setModel(getModel());
        params.setRandomSeed((long) values[7]);
    }

    private void generateBackup(ActionEvent e) {
        SettingsBackup backup = new SettingsBackup();
        double[][] modelParameters = new double[models.length][];

        backup.setModelParameters(modelParameters);
        backup.setActiveModel(cmbModels.getSelectedIndex());
        mainWindow.fillBackup(backup);

        JFileChooser fc = new JFileChooser();
        int returnValue = fc.showSaveDialog(mainWindow);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
                os.writeObject(backup);
                os.close();
                JOptionPane.showMessageDialog(mainWindow, "Settings saved successfully", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainWindow, "IO error when writing the settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void restoreBackup(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        int returnValue = fc.showOpenDialog(mainWindow);
        SettingsBackup backup = null;

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();

                if (!f.exists()) {
                    JOptionPane.showMessageDialog(mainWindow, "File does not exist", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));
                backup = (SettingsBackup) is.readObject();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainWindow, "IO error when reading the settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carParameterChanged(double[] newParams) {
        CarParameters newCarParams = new CarParameters(newParams[0], newParams[1], newParams[2]);
        mainWindow.carParametersChanged(newCarParams);
    }

    private void modelChanged(ActionEvent e) {
        DynamicModel selectedModel = (DynamicModel) cmbModels.getSelectedItem();
        modelParamsLayout.show(modelParamsPanel, selectedModel.getModelName());
    }

    private void setCarParameters(CarParameters carParameters) {
        carParameterPanel.setValues(new double[]{carParameters.speed(), carParameters.width(), carParameters.length()});
    }

    private class ParameterPanel extends JPanel {
        private JFormattedTextField[] txtValues;
        private ParameterChangeListener listener;

        public ParameterPanel(String title, String[] parameterNames) {
            setLayout(new GridBagLayout());
            int gridRow = 0;

            if (parameterNames.length > 0) {
                txtValues = new JFormattedTextField[parameterNames.length];
                int i = 0;

                for (String s : parameterNames) {
                    JLabel lblName = new JLabel(s);
                    JFormattedTextField txtValue = new JFormattedTextField(NumberFormat.getNumberInstance());
                    txtValue.setValue(0);
                    txtValue.setColumns(12);
                    txtValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtValue.getPreferredSize().height));
                    txtValue.addPropertyChangeListener("value", this::parameterChanged);
                    add(lblName, new GridBagConstraints(0, gridRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
                    add(txtValue, new GridBagConstraints(0, gridRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                    txtValues[i++] = txtValue;
                }
            } else {
                JLabel lblNoParams = new JLabel("No parameters");
                add(lblNoParams, new GridBagConstraints(0, gridRow++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
            }

            add(Box.createGlue(), new GridBagConstraints(0, gridRow++, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            setBorder(BorderFactory.createTitledBorder(title));
        }

        public void setParameterChangeListener(ParameterChangeListener listener) {
            this.listener = listener;
        }

        public double[] getValues() {

            Gson gson = new Gson();
            double[] values = new double[txtValues.length];

            try (FileReader reader = new FileReader("settings.json")) {
                // Convert JSON file to Map
                Map<?, ?> dataMap = gson.fromJson(reader, Map.class);

                values[0] = 20;
                values[1] = 0.01;
                values[2] = ((Double) dataMap.get("Iterations")).intValue();
                values[3] = ((Double) dataMap.get("MinBuildingAngle")).intValue();
                values[4] = ((Double) dataMap.get("MaxBuildingAngle")).intValue();
                values[5] = ((Double) dataMap.get("SteeringInc")).intValue();
                values[6] = ((Double) dataMap.get("FinalStateProc")).intValue();
                values[7] = 42;
                // Access data from the map using the keys from your JSON file
            } catch (IOException e) {
                e.printStackTrace();
            }
            return values;
        }

        public void setValues(double[] values) {
            if (values != null) {
                for (int i = 0; i < txtValues.length; i++) {
                    txtValues[i].setValue(values[i]);
                }
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);

            if (txtValues != null) {
                for (JFormattedTextField t : txtValues) {
                    t.setEnabled(enabled);
                }
            }

            btnSave.setEnabled(enabled);
            btnLoad.setEnabled(enabled);
        }

        private void parameterChanged(PropertyChangeEvent e) {
        }
    }

    private interface ParameterChangeListener {
        void parameterChanged(double[] newValues);
    }
}