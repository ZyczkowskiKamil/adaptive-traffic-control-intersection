package org.traffic.model;

import org.jetbrains.annotations.NotNull;

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

    public boolean canMoveTo(Direction destination) {
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

    public Optional<Vehicle> moveAndGetVehicle() {
        return Optional.ofNullable(vehicles.poll());
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
}
