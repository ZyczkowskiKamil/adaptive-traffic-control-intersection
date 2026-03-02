package org.traffic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.traffic.model.dto.Command;
import org.traffic.model.dto.SimulationInput;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.Intersection;
import org.traffic.model.infrastructure.TurnDirection;
import org.traffic.model.vehicle.Vehicle;
import org.traffic.service.TrafficSimulator;
import org.traffic.utils.SimulationParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SimulationController implements Initializable {

    private final SimulationParser simulationParser = new SimulationParser();

    private final ObservableList<Command> commandList = FXCollections.observableArrayList();
    private final Map<Direction, List<TurnDirection>> lanesToAdd = new HashMap<>();
    private final IntegerProperty simulationTime = new SimpleIntegerProperty(0);
    private final StringProperty outputText = new SimpleStringProperty("");

    private Stage stage;

    @FXML
    private ListView<Command> commandListView;

    @FXML
    private Label errorLabel;

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

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Label simulationTimeLabel;

    @FXML
    private CheckBox realTimeSimulationCheckbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Direction roadSide : Direction.values()) {
            addLaneRoadSideSelector.getItems().add(roadSide.toString());
            addVehicleStartRoadSelector.getItems().add(roadSide.toString());
            addVehicleEndRoadSelector.getItems().add(roadSide.toString());

            lanesToAdd.put(roadSide, new LinkedList<>());
        }

        for (TurnDirection turnDirection : TurnDirection.values())
            addLaneDirectionSelector.getItems().add(turnDirection.toString());

        commandListView.setItems(commandList);
        commandListView.setCellFactory(_ -> new CommandListCell(commandList));

        simulationTimeLabel.textProperty().bind(simulationTime.asString());
        outputTextArea.textProperty().bind(outputText);
    }

    @FXML
    private void handleAddLane() {
        String roadSideString = addLaneRoadSideSelector.getValue();
        String laneDirectionString = addLaneDirectionSelector.getValue();

        if (roadSideString == null || laneDirectionString == null) {
            errorLabel.setText("Need to choose road side and lane direction when adding lane");
            return;
        }

        Direction roadPosition = Direction.valueOf(roadSideString);
        TurnDirection turnDirection = TurnDirection.valueOf(laneDirectionString);

        lanesToAdd.get(roadPosition).add(turnDirection);
    }

    @FXML
    private void handleAddVehicle() {
        String startRoadString = addVehicleStartRoadSelector.getValue();
        String endRoadString = addVehicleEndRoadSelector.getValue();
        String vehicleId = addVehicleVehicleId.getText();

        if (startRoadString == null || endRoadString == null || vehicleId.isEmpty()) {
            errorLabel.setText("All fields must be selected when adding vehicle");
            return;
        }

        Direction startRoadPosition = Direction.valueOf(startRoadString);
        Direction endRoadPosition = Direction.valueOf(endRoadString);

        Vehicle vehicle = new Vehicle(vehicleId, startRoadPosition, endRoadPosition);
        Command command = Command.fromVehicle(vehicle);

        this.commandList.add(command);
    }

    @FXML
    private void handleAddStep() {
        Command command = Command.fromStep();
        this.commandList.add(command);
    }

    @FXML
    private void resetErrorMessage() {
        this.errorLabel.setText("");
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
                this.errorLabel.setText("Error while importing configuration file");
                System.err.println("Error while importing configuration file");
                e.printStackTrace();
            }
        }
    }

    private void saveJsonStringToFile(String jsonContent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose save destination");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json")
        );

        fileChooser.setInitialFileName("input.json");

        File fileToSave = fileChooser.showSaveDialog(stage);

        if (fileToSave != null) {
            try {
                Path path = fileToSave.toPath();
                Files.writeString(path, jsonContent);
            } catch (IOException e) {
                this.errorLabel.setText("Error saving json file");
                System.err.println("Error saving json file");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExportSimulationFile() {
        try {
            var simulationInput = new SimulationInput(commandList);
            String jsonContent = simulationParser.toJsonString(simulationInput);

            saveJsonStringToFile(jsonContent);
        } catch (JsonProcessingException e) {
            this.errorLabel.setText("Failed to export configuration");
            System.err.println("Export simulation file failed");
        }
    }

    @FXML
    private void handleSaveOutput() {
        String content = this.outputTextArea.getText();

        saveJsonStringToFile(content);
    }

    @FXML
    private void handleRunSimulation() {
        this.simulationTime.set(0);
        var intersection = new Intersection();
        intersection.addLanesToRoad(lanesToAdd);

        var simulationInput = new SimulationInput(new ArrayList<>(commandList));
        var simulator = new TrafficSimulator(intersection, this.simulationTime, this.outputText);
        var runRealTimeSimulation = this.realTimeSimulationCheckbox.isSelected();

        Thread thread = new Thread(() -> {
            try {
                var simulationOutput = simulator.runSimulation(simulationInput, runRealTimeSimulation);
                var outputString = simulationParser.toJsonString(simulationOutput);

                Platform.runLater(() ->
                        this.outputText.set(outputString)
                );

            } catch (JsonProcessingException e) {
                errorLabel.setText("Simulation failed: " + e.getMessage());
                e.printStackTrace();
            }
        });

        thread.start();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
