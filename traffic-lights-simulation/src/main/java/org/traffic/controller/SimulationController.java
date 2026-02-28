package org.traffic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.traffic.model.dto.Command;
import org.traffic.model.dto.SimulationInput;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.Intersection;
import org.traffic.model.infrastructure.TurnDirection;
import org.traffic.model.vehicle.Vehicle;
import org.traffic.utils.SimulationParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class SimulationController implements Initializable {

    private final Intersection intersection = new Intersection();
    private final SimulationParser simulationParser = new SimulationParser();

    private Stage stage;

    private ObservableList<Command> commandList = FXCollections.observableArrayList();

    @FXML
    private ListView<Command> commandListView;

    @FXML
    private ComboBox<String> addLaneDirectionSelector;

    @FXML
    private ComboBox<String> addLaneRoadSideSelector;

    @FXML
    private ComboBox<String> addVehicleStartRoadSelector;

    @FXML
    private ComboBox<String> addVehicleEndRoadSelector;

    @FXML
    private TextField addVehicleVehicleId;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Direction roadSide : Direction.values()) {
            addLaneRoadSideSelector.getItems().add(roadSide.toString());
            addVehicleStartRoadSelector.getItems().add(roadSide.toString());
            addVehicleEndRoadSelector.getItems().add(roadSide.toString());
        }

        for (TurnDirection turnDirection : TurnDirection.values())
            addLaneDirectionSelector.getItems().add(turnDirection.toString());

        commandListView.setItems(commandList);
        commandListView.setCellFactory(row -> new CommandListCell(commandList));
    }

    @FXML
    private void handleAddLane() {
        String roadSideString = addLaneRoadSideSelector.getValue();
        String laneDirectionString = addLaneDirectionSelector.getValue();

        if (roadSideString == null || laneDirectionString == null) {
            System.err.println("Need to choose road side and lane direction when adding lane");
            return;
        }

        Direction roadPosition = Direction.valueOf(roadSideString);
        TurnDirection turnDirection = TurnDirection.valueOf(laneDirectionString);

        this.intersection.addLaneToRoad(roadPosition, turnDirection);
    }

    @FXML
    private void handleAddVehicle() {
        System.out.println("ADD vehicle");

        String startRoadString = addVehicleStartRoadSelector.getValue();
        String endRoadString = addVehicleEndRoadSelector.getValue();
        String vehicleId = addVehicleVehicleId.getText();

        if (startRoadString == null || endRoadString == null || vehicleId.isEmpty()) {
            System.err.println("All fields must be selected when adding vehicle");
            return;
        }

        Direction startRoadPosition = Direction.valueOf(startRoadString);
        Direction endRoadPosition = Direction.valueOf(endRoadString);

        Vehicle vehicle = new Vehicle(vehicleId, startRoadPosition, endRoadPosition);
        Command command = Command.fromVehicle(vehicle);

        this.commandList.add(command);
    }

    @FXML
    private void handleImportSimulationFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose simulation file \"*.json\"");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                Path path = selectedFile.toPath();
                SimulationInput simulationInput = simulationParser.parseInput(path.toString());

                this.commandList.setAll(simulationInput.commands());
            } catch (IOException e) {
                System.err.println("Error while importing configuration file");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExportSimulationFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose save destination");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );

        fileChooser.setInitialFileName("input.json");

        File fileToSave = fileChooser.showSaveDialog(stage);

        if (fileToSave != null) {
            try {
                SimulationInput simulationInput = new SimulationInput(commandList);
                String jsonContent = simulationParser.toJsonString(simulationInput);

                Path path = fileToSave.toPath();
                Files.writeString(path, jsonContent);
            } catch (IOException e) {
                System.err.println("Error saving file while exporting configuration");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRunSimulation() {
        System.out.println("RUN");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
