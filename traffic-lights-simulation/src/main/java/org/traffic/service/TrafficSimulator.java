package org.traffic.service;

import org.traffic.model.TickResult;
import org.traffic.model.dto.*;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.Intersection;
import org.traffic.model.infrastructure.Road;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class TrafficSimulator {

    private final TrafficLightsService trafficLightsService;
    private final Intersection intersection;

    private final IntSupplier getSimulationTime;
    private final IntConsumer setSimulationTime;
    private final Consumer<String> addToSimulationLog;

    public TrafficSimulator(Intersection intersection, IntSupplier getSimulationTime, IntConsumer setSimulationTime, Consumer<String> addToSimulationLog) {
        this.intersection = intersection;
        this.getSimulationTime = getSimulationTime;
        this.setSimulationTime = setSimulationTime;
        this.addToSimulationLog = addToSimulationLog;

        this.trafficLightsService = new TrafficLightsService(intersection, getSimulationTime);
    }

    public SimulationOutput runSimulation(SimulationInput input, boolean realTimeSimulation) {
        var stepStatuses = new ArrayList<StepStatus>();

        for (Command command : input.commands()) {
            if (command.type() == CommandType.ADD_VEHICLE) {
                addToSimulationLog.accept("ADD_VEHICLE");
                handleAddVehicle(command);
                updateLatestSimulationLog();
            } else if (command.type() == CommandType.STEP) {
                addToSimulationLog.accept("STEP");
                StepStatus stepStatus = handleStepCommand(realTimeSimulation);
                stepStatuses.add(stepStatus);
                addToSimulationLog.accept("Leaving vehicles: " + stepStatus.toString() + '\n');
            } else {
                System.err.println("Bad command type: " + command.type());
            }
        }

        return new SimulationOutput(stepStatuses);
    }

    private void updateLatestSimulationLog() {
        StringBuilder currentState = new StringBuilder();

        Map<Direction, Road> roadMap = this.intersection.getRoadMap();
        currentState
                .append(this.getSimulationTime.getAsInt())
                .append(" Phase: ")
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

        this.addToSimulationLog.accept(currentState.toString());
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
     */
    private TickResult performTick(boolean realTimeSimulation) {
        trafficLightsService.handleSimulationStep();
        increaseSimulationTime(1);

        boolean isLightStateActive = trafficLightsService.isLightTransitionStateActive();
        List<String> leavingVehicles = new ArrayList<>();

        if (isLightStateActive) {
            leavingVehicles = intersection.makeStepAndGetVehicleIds();
        }

        updateLatestSimulationLog();
        sleepIfRealTime(realTimeSimulation);

        return new TickResult(leavingVehicles, isLightStateActive, intersection.isEmpty());
    }

    public void increaseSimulationTime(int time) {
        int newTime = this.getSimulationTime.getAsInt() + time;
        this.setSimulationTime.accept(newTime);
    }


}
