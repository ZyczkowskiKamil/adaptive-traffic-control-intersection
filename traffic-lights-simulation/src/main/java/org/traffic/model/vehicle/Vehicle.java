package org.traffic.model.vehicle;

import org.jetbrains.annotations.NotNull;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.TurnDirection;
import org.traffic.model.trafficlight.RoadTrafficLights;

import java.util.Objects;

public record Vehicle(
        @NotNull String vehicleId,
        Direction startRoad,
        Direction endRoad,
        TurnDirection turnDirection) {

    public Vehicle(@NotNull String vehicleId, Direction startRoad, Direction endRoad) {
        this(vehicleId, startRoad, endRoad, calculateVehicleTurnDirection(startRoad, endRoad));
    }

    private static TurnDirection calculateVehicleTurnDirection(Direction startRoad, Direction endRoad) {
        int startIndex = startRoad.getIndex();
        int endIndex = endRoad.getIndex();

        int relativeMove = (endIndex - startIndex + 4) % 4;

        return switch (relativeMove) {
            case 1 -> TurnDirection.LEFT;
            case 2 -> TurnDirection.STRAIGHT;
            case 3 -> TurnDirection.RIGHT;
            default -> throw new IllegalStateException("Vehicle relative move not correct");
        };
    }

    public boolean hasGreenLight(RoadTrafficLights trafficLights) {
        return trafficLights.isLightGreen(this.turnDirection);
    }
}