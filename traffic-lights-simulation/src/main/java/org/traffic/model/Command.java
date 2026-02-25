package org.traffic.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record Command(
        @NotNull CommandType type,
        @Nullable String vehicleId,
        @Nullable Direction startRoad,
        @Nullable Direction endRoad
) {
    public Command {
        Objects.requireNonNull(type, "type cannot be null");

        if (type == CommandType.ADD_VEHICLE) {
            Objects.requireNonNull(vehicleId, "vehicleId cannot be null for ADD_VEHICLE");
            Objects.requireNonNull(startRoad, "startRoad cannot be null for ADD_VEHICLE");
            Objects.requireNonNull(endRoad, "endRoad cannot be null for ADD_VEHICLE");
        }
    }

    public Vehicle toVehicle() {
        if (type != CommandType.ADD_VEHICLE) {
            throw new IllegalStateException("Cannot get vehicle from " + type + " command");
        }

        return new Vehicle(vehicleId, startRoad, endRoad);
    }
}
