package org.traffic.model;

import static org.traffic.model.LightSet.*;

public enum LightPhase {
    NS_STRAIGHT_RIGHT   (STRAIGHT_RIGHT_GREEN, ALL_RED, STRAIGHT_RIGHT_GREEN, ALL_RED),
    EW_STRAIGHT_RIGHT   (ALL_RED, STRAIGHT_RIGHT_GREEN, ALL_RED, STRAIGHT_RIGHT_GREEN),
    NS_LEFT_EW_RIGHT    (LEFT_GREEN, RIGHT_GREEN, LEFT_GREEN, RIGHT_GREEN),
    EW_LEFT_NS_RIGHT    (RIGHT_GREEN, LEFT_GREEN, RIGHT_GREEN, LEFT_GREEN),
    NORTH_ALL_EAST_RIGHT(ALL_GREEN, RIGHT_GREEN, ALL_RED, ALL_RED),
    EAST_ALL_SOUTH_RIGHT(ALL_RED, ALL_GREEN, RIGHT_GREEN, ALL_RED),
    SOUTH_ALL_WEST_RIGHT(ALL_RED, ALL_RED, ALL_GREEN, RIGHT_GREEN),
    WEST_ALL_NORTH_RIGHT(RIGHT_GREEN, ALL_RED, ALL_RED, ALL_GREEN),
    ALL_DIRECTIONS_STOP(ALL_RED, ALL_RED, ALL_RED, ALL_RED);

    private final LightSet north, east, south, west;

    LightPhase(LightSet north, LightSet east, LightSet south, LightSet west) {
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
    }

    public LightSet getLightSetFor(Direction direction) {
        return switch(direction) {
            case NORTH -> this.north;
            case EAST -> this.east;
            case SOUTH -> this.south;
            case WEST -> this.west;
        };
    }

}
