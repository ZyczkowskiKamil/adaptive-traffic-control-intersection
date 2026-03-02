package org.traffic.model.infrastructure;

import org.jetbrains.annotations.NotNull;
import org.traffic.model.trafficlight.LightSet;
import org.traffic.model.trafficlight.RoadTrafficLights;
import org.traffic.model.trafficlight.TrafficLightColor;
import org.traffic.model.vehicle.Vehicle;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class Lane {

    private LaneType type;
    private final Queue<Vehicle> vehicles = new LinkedList<>();
    private final Direction laneLocation;

    public Lane(LaneType type, Direction laneLocation) {
        this.type = type;
        this.laneLocation = laneLocation;
    }

    public boolean canMoveFromLaneTo(Direction destination) {
        int startIndex = this.laneLocation.getIndex();
        int endIndex = destination.getIndex();

        int relativeMove = (endIndex - startIndex + 4) % 4;

        return switch (type) {
            case LEFT -> relativeMove == 1;
            case STRAIGHT -> relativeMove == 2;
            case RIGHT -> relativeMove == 3;
            case LEFT_STRAIGHT -> relativeMove == 1 || relativeMove == 2;
            case STRAIGHT_RIGHT -> relativeMove == 2 || relativeMove == 3;
            case LEFT_STRAIGHT_RIGHT ->  relativeMove == 1 || relativeMove == 2 || relativeMove == 3;
        };
    }

    public void addVehicle(@NotNull Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public Optional<Vehicle> moveIfPossibleAndGetVehicle(RoadTrafficLights trafficLights) {
        Vehicle frontVehicle = vehicles.peek();

        if (frontVehicle != null && frontVehicle.hasGreenLight(trafficLights)) {
            return Optional.ofNullable(vehicles.poll());
        }

        return Optional.empty();
    }

    public int vehiclesNumber() {
        return this.vehicles.size();
    }

    public LaneType getType() {
        return type;
    }

    public void setType(LaneType type) {
        this.type = type;
    }

    private boolean canAllCarsGoOnLightPhase(LightSet lightSet) {
        TrafficLightColor G = TrafficLightColor.GREEN;
        return switch (type) {
            case LEFT -> lightSet.leftLight() == G;
            case STRAIGHT -> lightSet.straightLight() == G;
            case RIGHT -> lightSet.rightLight() == G;
            case LEFT_STRAIGHT -> lightSet.leftLight() == G && lightSet.straightLight() == G;
            case STRAIGHT_RIGHT -> lightSet.straightLight() == G && lightSet.rightLight() == G;
            case LEFT_STRAIGHT_RIGHT -> lightSet.leftLight() == G && lightSet.straightLight() == G && lightSet.rightLight() == G;
        };
    }

    public int numberOfCarsThatCanGoOnLightSet(LightSet lightSet) {
        if (canAllCarsGoOnLightPhase(lightSet))
            return this.vehiclesNumber();

        int vehiclesNumber = 0;
        for (Vehicle vehicle : vehicles) {
            var turnDirection = vehicle.turnDirection();
            if (lightSet.getColorInDirection(turnDirection) != TrafficLightColor.GREEN)
                break; // first car that can't move(blocking way)
            vehiclesNumber++;
        }

        return vehiclesNumber;
    }

    public boolean isEmpty() {
        return this.vehicles.isEmpty();
    }

    public int getVehicleCount() {
        return this.vehicles.size();
    }
}
