package org.traffic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Direction {
    @JsonProperty("north")
    NORTH,
    @JsonProperty("east")
    EAST,
    @JsonProperty("south")
    SOUTH,
    @JsonProperty("west")
    WEST
}
