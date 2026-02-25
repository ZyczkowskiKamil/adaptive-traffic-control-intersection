package org.traffic;

import org.traffic.model.*;

import java.util.ArrayList;

public class TrafficSimulator {

    private final Intersection intersection;

    public TrafficSimulator() {
        this.intersection = new Intersection();
    }

    public SimulationOutput runSimulation(SimulationInput input) {
        var stepStatuses = new ArrayList<StepStatus>();

        for (Command command : input.commands()) {
            if (command.type() == CommandType.ADD_VEHICLE) {
                var vehicle = command.toVehicle();
                intersection.addVehicle(vehicle);
            } else if (command.type() == CommandType.STEP) {
                var currentStepVehicleIds = intersection.makeStepAndGetVehicles();
                stepStatuses.add(new StepStatus(currentStepVehicleIds));
            } else {
                System.err.println("Bad command type: " + command.type());
            }
        }

        return new SimulationOutput(stepStatuses);
    }
}
