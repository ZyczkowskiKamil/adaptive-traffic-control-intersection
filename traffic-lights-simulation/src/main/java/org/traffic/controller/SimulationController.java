package org.traffic.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.TurnDirection;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulationController implements Initializable {

    @FXML
    private TextArea simulationCommandsTextArea;

    @FXML
    private ComboBox<String> addLaneDirectionSelector;

    @FXML
    private ComboBox<String> addLaneRoadSideSelector;

    @FXML
    private ComboBox<String> addVehicleRoadSizeSelector;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Direction roadSide : Direction.values()) {
            addLaneRoadSideSelector.getItems().add(roadSide.toString());
            addVehicleRoadSizeSelector.getItems().add(roadSide.toString());
        }

        for (TurnDirection turnDirection : TurnDirection.values())
            addLaneDirectionSelector.getItems().add(turnDirection.toString());
    }

    @FXML
    private void handleAddLane() {
        System.out.println("ADD lane");
    }

    @FXML
    private void handleAddVehicle() {
        System.out.println("ADD vehicle");
    }

    @FXML
    private void handleImportSimulationFile() {
        System.out.println("IMPORT");
    }

    @FXML
    private void handleExportSimulationFile() {
        System.out.println("EXPORT");
    }

    @FXML
    private void handleRunSimulation() {
        System.out.println("RUN");
    }
}
