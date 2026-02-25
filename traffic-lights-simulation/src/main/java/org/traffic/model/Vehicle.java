package org.traffic.model;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Vehicle {
    private final @NotNull String vehicleId;
    private final Direction startRoad;
    private final Direction endRoad;
    private final TurnDirection turnDirection;

    public Vehicle(@NotNull String vehicleId, Direction startRoad, Direction endRoad) {
        this.vehicleId = vehicleId;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
        this.turnDirection = calculateVehicleTurnDirection(startRoad, endRoad);
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

    public @NotNull String getVehicleId() {
        return vehicleId;
    }

    public Direction getStartRoad() {
        return startRoad;
    }

    public Direction getEndRoad() {
        return endRoad;
    }

    public TurnDirection getTurnDirection() {
        return turnDirection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Vehicle) obj;
        return Objects.equals(this.vehicleId, that.vehicleId) &&
                Objects.equals(this.startRoad, that.startRoad) &&
                Objects.equals(this.endRoad, that.endRoad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId, startRoad, endRoad);
    }

    @Override
    public String toString() {
        return "Vehicle[" +
                "vehicleId=" + vehicleId + ", " +
                "startRoad=" + startRoad + ", " +
                "endRoad=" + endRoad + ']';
    }

}
