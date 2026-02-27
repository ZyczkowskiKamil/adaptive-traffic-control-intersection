package org.traffic.model.dto;

import java.util.List;

public record SimulationOutput(List<StepStatus> stepStatuses) {
}
