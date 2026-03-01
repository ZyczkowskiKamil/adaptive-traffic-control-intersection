package org.traffic.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.vehicle.Vehicle;

import java.util.Objects;
import java.util.UUID;

public record Command(
        @JsonIgnore UUID commandId,
        @NotNull CommandType type,
        @Nullable String vehicleId,
        @Nullable Direction startRoad,
        @Nullable Direction endRoad
) {
    public Command {
        if (commandId == null)
            commandId = UUID.randomUUID();
        Objects.requireNonNull(type, "type cannot be null");

        if (type == CommandType.ADD_VEHICLE) {
            Objects.requireNonNull(vehicleId, "vehicleId cannot be null for ADD_VEHICLE");
            Objects.requireNonNull(startRoad, "startRoad cannot be null for ADD_VEHICLE");
            Objects.requireNonNull(endRoad, "endRoad cannot be null for ADD_VEHICLE");
        }
    }

    public Command(CommandType commandType,
                   String vehicleId,
                   Direction startRoad,
                   Direction endRoad) {
        this(UUID.randomUUID(), commandType, vehicleId, startRoad, endRoad);
    }

    public Vehicle toVehicle() {
        if (type != CommandType.ADD_VEHICLE) {
            throw new IllegalStateException("Cannot get vehicle from " + type + " command");
        }

        return new Vehicle(vehicleId, startRoad, endRoad);
    }

    public static Command fromVehicle(Vehicle vehicle) {
        return new Command(
                CommandType.ADD_VEHICLE,
                vehicle.vehicleId(),
                vehicle.startRoad(),
                vehicle.endRoad()
        );
    }

    public static Command fromStep() {
        return new Command(
                CommandType.STEP,
                null,
                null,
                null
        );
    }
}
