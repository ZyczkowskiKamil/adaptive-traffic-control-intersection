package org.traffic.model;

import org.jetbrains.annotations.NotNull;

public record Vehicle(@NotNull String vehicleId, Direction startRoad, Direction endRoad) {
}
