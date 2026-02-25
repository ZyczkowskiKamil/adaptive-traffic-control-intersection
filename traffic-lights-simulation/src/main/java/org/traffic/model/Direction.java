package org.traffic.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent cardinal directions at intersection.
 * <p>
 *     Each direction number(0-3) is used for calculating maneuvers in the simulation(where cars turn).
 * </p>
 */
public enum Direction {
    @JsonProperty("north")
    NORTH(0),
    @JsonProperty("east")
    EAST(1),
    @JsonProperty("south")
    SOUTH(2),
    @JsonProperty("west")
    WEST(3);

    private final int index;

    Direction(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
