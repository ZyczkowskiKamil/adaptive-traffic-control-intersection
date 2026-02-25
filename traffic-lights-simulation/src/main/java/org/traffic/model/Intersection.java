package org.traffic.model;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Intersection {

    private final Map<Direction, Road> roadMap;

    public Intersection() {
        this.roadMap = new HashMap<>();
        for (Direction direction : Direction.values())
            this.roadMap.put(direction, new Road(direction));
    }

    public void addVehicle(@NotNull Vehicle vehicle) {
        roadMap.get(vehicle.startRoad()).addVehicle(vehicle);
    }

    // TODO
//    public List<String> makeStepAndGetVehicles() {
//
//    }
}
