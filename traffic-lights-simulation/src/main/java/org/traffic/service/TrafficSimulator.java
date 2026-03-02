package org.traffic.service;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import org.traffic.model.TickResult;
import org.traffic.model.dto.*;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.Intersection;
import org.traffic.model.infrastructure.Road;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrafficSimulator {

    private final TrafficLightsService trafficLightsService;
    private final Intersection intersection;

    private final IntegerProperty simulationTime;
    private final StringProperty outputString;

    public TrafficSimulator(Intersection intersection, IntegerProperty simulationTime, StringProperty outputString) {
        this.intersection = intersection;
        this.trafficLightsService = new TrafficLightsService(intersection, simulationTime);
        this.simulationTime = simulationTime;
        this.outputString = outputString;
    }

    public SimulationOutput runSimulation(SimulationInput input, boolean realTimeSimulation) {
        var stepStatuses = new ArrayList<StepStatus>();

        for (Command command : input.commands()) {
            if (command.type() == CommandType.ADD_VEHICLE) {
                handleAddVehicle(command);
                updateOutputString();
            } else if (command.type() == CommandType.STEP) {
                StepStatus stepStatus = handleStepCommand(realTimeSimulation);
                stepStatuses.add(stepStatus);
            } else {
                System.err.println("Bad command type: " + command.type());
            }
        }

        return new SimulationOutput(stepStatuses);
    }

    private void updateOutputString() {
        StringBuilder currentState = new StringBuilder();

        Map<Direction, Road> roadMap = this.intersection.getRoadMap();
        currentState
                .append("Phase: ")
                .append(this.trafficLightsService.getLightPhase())
                .append(" State: ")
                .append(this.trafficLightsService.getLightTransitionState())
                .append('\n');
        for (Direction direction : roadMap.keySet()) {
            Road road = roadMap.get(direction);

            currentState
                    .append(direction.toString())
                    .append(" ")
                    .append("Vehicles: ")
                    .append(road.getVehicleCount())
                    .append(" lights: ")
                    .append(road.getLightSet())
                    .append('\n');
        }

        this.outputString.setValue(
                outputString + currentState.toString() + "\n\n"
        );

    }

    /**
     * Return step status only when light state is ACTIVE
     * If any cars are on intersection make sure that any of them makes move
     */
    private StepStatus handleStepCommand(boolean realTimeSimulation) {
        TickResult tickResult = performTick(realTimeSimulation);

        while (!(tickResult.isLightStateActive() && (tickResult.intersectionIsEmpty() || !tickResult.leavingVehicleIds().isEmpty()))) {
            tickResult = performTick(realTimeSimulation);
        }

        List<String> leavingVehiclesIds = tickResult.leavingVehicleIds();
        return new StepStatus(leavingVehiclesIds);
    }

    private void handleAddVehicle(Command command) {
        var vehicle = command.toVehicle();
        intersection.addVehicle(vehicle);
    }

    private void sleepIfRealTime(boolean realTimeSimulation) {
        if (realTimeSimulation) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Return leaving vehicles only when intersection state is active
     * If leaving vehicles is empty
     * @return
     */
    private TickResult performTick(boolean realTimeSimulation) {
        trafficLightsService.handleSimulationStep();
        increaseSimulationTime(1);

        boolean isLightStateActive = trafficLightsService.isLightTransitionStateActive();
        List<String> leavingVehicles = new ArrayList<>();

        if (isLightStateActive) {
            leavingVehicles = intersection.makeStepAndGetVehicleIds();
        }

        updateOutputString();
        sleepIfRealTime(realTimeSimulation);

        return new TickResult(leavingVehicles, isLightStateActive, intersection.isEmpty());
    }

    public void increaseSimulationTime(int time) {
        int newTime = this.simulationTime.get() + time;

        try {
            if (Platform.isFxApplicationThread()) { // check if on fx thread
                this.simulationTime.set(newTime);
            } else { // try sending to fx thread
                Platform.runLater(() ->
                        this.simulationTime.set(newTime)
                );
            }
        } catch (IllegalStateException e) { // could not find fx thread - we are in CLI mode
            this.simulationTime.set(newTime);
        }
    }


}
