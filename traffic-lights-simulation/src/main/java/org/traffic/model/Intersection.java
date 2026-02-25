package org.traffic.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Intersection {

    private final Map<Direction, Road> roadMap;

    public Intersection() {
        this.roadMap = new HashMap<>();
        for (Direction direction : Direction.values())
            this.roadMap.put(direction, new Road(direction));
    }

    public void addVehicle(@NotNull Vehicle vehicle) {
        Road road = roadMap.get(vehicle.getStartRoad());
        road.addVehicle(vehicle);
    }

    public List<String> makeStepAndGetVehicleIds() {
        List<Vehicle> vehiclesLeavingIntersection = new LinkedList<>();
        for (Road road : roadMap.values()) {
            List<Vehicle> vehiclesLeavingRoad = road.processStep();
            vehiclesLeavingIntersection.addAll(vehiclesLeavingRoad);
        }

        List<String> vehicleIds = new LinkedList<>();
        for (Vehicle vehicle : vehiclesLeavingIntersection)
            vehicleIds.add(vehicle.getVehicleId());

        return vehicleIds;
    }
}
