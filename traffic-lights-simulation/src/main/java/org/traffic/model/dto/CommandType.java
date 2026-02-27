package org.traffic.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CommandType {
    @JsonProperty("addVehicle")
    ADD_VEHICLE,
    @JsonProperty("step")
    STEP
}
