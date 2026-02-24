package org.traffic;

import org.traffic.model.*;

import java.util.ArrayList;

public class TrafficSimulator {

    public SimulationOutput runSimulation(SimulationInput input) {
        var stepStatuses = new ArrayList<StepStatus>();

        for (Command command : input.commands()) {
            if (command.type() == CommandType.ADD_VEHICLE) {
                // TODO - add vehicle
            } else if (command.type() == CommandType.STEP) {
                var currentStepVehicleIds = new ArrayList<String>();

                // TODO - step

                stepStatuses.add(new StepStatus(currentStepVehicleIds));
            } else {
                System.err.println("Bad command type: " + command.type());
            }
        }

        return new SimulationOutput(stepStatuses);
    }
}
