package org.traffic.model.trafficlight;

import org.traffic.model.infrastructure.TurnDirection;

public record LightSet(TrafficLightColor leftLight,
                       TrafficLightColor straightLight,
                       TrafficLightColor rightLight) {
    private static final TrafficLightColor G = TrafficLightColor.GREEN;
    private static final TrafficLightColor R = TrafficLightColor.RED;

    static final LightSet ALL_RED = new LightSet(R,R,R);
    static final LightSet ALL_GREEN = new LightSet(G,G,G);
    static final LightSet STRAIGHT_RIGHT_GREEN = new LightSet(R,G,G);
    static final LightSet RIGHT_GREEN = new LightSet(R,R,G);
    static final LightSet LEFT_GREEN = new LightSet(G,R,R);

    public TrafficLightColor getColorInDirection(TurnDirection turnDirection) {
        return switch (turnDirection) {
            case LEFT -> this.leftLight;
            case STRAIGHT -> this.straightLight;
            case RIGHT -> this.rightLight;
        };
    }
}
