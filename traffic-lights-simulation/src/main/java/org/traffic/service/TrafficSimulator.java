package org.traffic.service;

import javafx.beans.property.IntegerProperty;
import org.traffic.model.dto.*;
import org.traffic.model.infrastructure.Intersection;

import java.util.ArrayList;
import java.util.List;

public class TrafficSimulator {

    private final TrafficLightsService trafficLightsService;
    private final Intersection intersection;

    private final IntegerProperty simulationTime;

    public TrafficSimulator(Intersection intersection, IntegerProperty simulationTime) {
        this.intersection = intersection;
        this.trafficLightsService = new TrafficLightsService(intersection, simulationTime);
        this.simulationTime = simulationTime;
    }

    public SimulationOutput runSimulation(SimulationInput input) {
        var stepStatuses = new ArrayList<StepStatus>();

        for (Command command : input.commands()) {
            if (command.type() == CommandType.ADD_VEHICLE) {
                handleAddVehicle(command);
            } else if (command.type() == CommandType.STEP) {
                List<String> leavingVehiclesIds = handleStepCommandAndGetLeavingVehiclesIds();
                stepStatuses.add(new StepStatus(leavingVehiclesIds));
            } else {
                System.err.println("Bad command type: " + command.type());
            }
        }

        return new SimulationOutput(stepStatuses);
    }

    private void handleAddVehicle(Command command) {
        var vehicle = command.toVehicle();
        intersection.addVehicle(vehicle);
    }

    private List<String> handleStepCommandAndGetLeavingVehiclesIds() {
        do {
            trafficLightsService.handleSimulationStep();
            increaseSimulationTime(1);
        } while (!trafficLightsService.canCarGo());

        return intersection.makeStepAndGetVehicleIds();
    }

    public void increaseSimulationTime(int time) {
        this.simulationTime.set(this.simulationTime.get() + time);
    }


}
