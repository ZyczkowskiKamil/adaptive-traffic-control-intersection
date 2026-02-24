package org.traffic.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Command(
        @NotNull CommandType type,
        @Nullable String vehicleId,
        @Nullable Direction startRoad,
        @Nullable Direction endRoad
) {
}
