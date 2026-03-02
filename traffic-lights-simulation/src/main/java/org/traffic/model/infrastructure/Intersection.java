package org.traffic.model.infrastructure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.traffic.model.trafficlight.LightPhase;
import org.traffic.model.trafficlight.LightTransitionState;
import org.traffic.model.vehicle.Vehicle;

import java.util.*;

public class Intersection {

    private final Map<Direction, Road> roadMap;

    public Intersection() {
        this(createDefaultRoads());
    }

    protected Intersection(Map<Direction, Road> roadMap) {
        this.roadMap = roadMap;
    }

    private static Map<Direction, Road> createDefaultRoads() {
        Map<Direction, Road> roads = new HashMap<>();
        for (Direction direction : Direction.values())
            roads.put(direction, new Road(direction));
        return roads;
    }

    public void addVehicle(@NotNull Vehicle vehicle) {
        Road road = roadMap.get(vehicle.startRoad());
        road.addVehicle(vehicle);
    }

    public boolean isEmpty() {
        for (Road road : roadMap.values()) {
            if (!road.isEmpty())
                return false;
        }
        return true;
    }

    public List<String> makeStepAndGetVehicleIds() {
        List<Vehicle> vehiclesLeavingIntersection = new LinkedList<>();
        for (Road road : roadMap.values()) {
            List<Vehicle> vehiclesLeavingRoad = road.processStep();
            vehiclesLeavingIntersection.addAll(vehiclesLeavingRoad);
        }

        List<String> vehicleIds = new LinkedList<>();
        for (Vehicle vehicle : vehiclesLeavingIntersection)
            vehicleIds.add(vehicle.vehicleId());

        return vehicleIds;
    }

    public int getCarsNumberThatCanLeaveIntersection(LightPhase lightPhase) {
        int carsThatCanLeave = 0;
        for (Road road : roadMap.values()) {
            carsThatCanLeave += road.getCarsNumberThatCanGo(lightPhase);
        }
        return carsThatCanLeave;
    }

    public void updateLights(LightPhase lightPhase, LightTransitionState lightState) {
        for (Road road : roadMap.values()) {
            road.updateTrafficLights(lightPhase, lightState);
        }
    }

    public void addLaneToRoad(Direction roadPosition, TurnDirection turnDirection) {
        Road road = roadMap.get(roadPosition);
        road.addLane(turnDirection);
    }

    public void addLanesToRoad(Map<Direction, List<TurnDirection>> lanesMap) {
        for (Direction roadPosition : lanesMap.keySet()) {
            List<TurnDirection> turnList = lanesMap.get(roadPosition);

            for (TurnDirection turnDirection : turnList)
                this.addLaneToRoad(roadPosition, turnDirection);
        }
    }

    public Map<Direction, Road> getRoadMap() {
        return Collections.unmodifiableMap(this.roadMap);
    }
}
