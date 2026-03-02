package org.traffic.model;

import java.util.List;

public record TickResult(
        List<String> leavingVehicleIds,
        boolean isLightStateActive,
        boolean intersectionIsEmpty
) {}
