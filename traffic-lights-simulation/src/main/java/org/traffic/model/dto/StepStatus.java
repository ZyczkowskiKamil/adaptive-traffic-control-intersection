package org.traffic.model.dto;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record StepStatus(List<String> leftVehicles) {
    @Override
    public @NotNull String toString() {
        var stringBuilder = new StringBuilder();
        for (String vehicleId : leftVehicles)
            stringBuilder.append(vehicleId);
        return stringBuilder.toString();
    }
}
