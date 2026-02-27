package org.traffic.service;

import org.traffic.model.dto.*;
import org.traffic.model.infrastructure.Intersection;

import java.util.ArrayList;
import java.util.List;

public class TrafficSimulator {

    private final TrafficLightsService trafficLightsService;
    private final Intersection intersection;

    private int simulationTime = 0;

    public TrafficSimulator() {
        this.intersection = new Intersection();
        this.trafficLightsService = new TrafficLightsService(intersection);
    }

    public SimulationOutput runSimulation(SimulationInput input) {
        var stepStatuses = new ArrayList<StepStatus>();

        for (Command command : input.commands()) {
            if (command.type() == CommandType.ADD_VEHICLE) {
                System.out.println("ADD");
                handleAddVehicle(command);
            } else if (command.type() == CommandType.STEP) {
                System.out.println("STEP");
                List<String> leavingVehiclesIds = handleStepAndGetLeavingVehiclesIds();
                stepStatuses.add(new StepStatus(leavingVehiclesIds));

                for (String id : leavingVehiclesIds) {
                    System.out.println(id + " ");
                }
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

    private List<String> handleStepAndGetLeavingVehiclesIds() {
        do {
            trafficLightsService.handleSimulationStep();
            increaseSimulationTime(1);
        } while (!trafficLightsService.canCarGo());

        return intersection.makeStepAndGetVehicleIds();
    }

    public void increaseSimulationTime(int time) {
        this.simulationTime += time;
    }


}
