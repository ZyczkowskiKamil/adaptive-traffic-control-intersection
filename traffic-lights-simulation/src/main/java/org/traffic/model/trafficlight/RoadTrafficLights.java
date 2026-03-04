package org.traffic.model.trafficlight;

import org.traffic.model.infrastructure.TurnDirection;

public class RoadTrafficLights {
    private TrafficLightColor leftColor = TrafficLightColor.RED;
    private TrafficLightColor straightColor = TrafficLightColor.RED;
    private TrafficLightColor rightColor = TrafficLightColor.RED;

    public boolean isLightGreen(TurnDirection direction) {
        return switch (direction) {
            case LEFT -> leftColor == TrafficLightColor.GREEN;
            case STRAIGHT -> straightColor == TrafficLightColor.GREEN;
            case RIGHT -> rightColor == TrafficLightColor.GREEN;
        };
    }

    public void setLights(LightSet lightSet, LightTransitionState lightsState) {
        switch (lightsState) {
            case ACTIVE -> {
                this.leftColor = lightSet.leftLight();
                this.straightColor = lightSet.straightLight();
                this.rightColor = lightSet.rightLight();
            }
            case TRANSITION_TO_RED -> {
                this.leftColor = (lightSet.leftLight()==TrafficLightColor.GREEN) ? TrafficLightColor.YELLOW : TrafficLightColor.RED;
                this.straightColor = (lightSet.straightLight()==TrafficLightColor.GREEN) ? TrafficLightColor.YELLOW : TrafficLightColor.RED;
                this.rightColor = (lightSet.rightLight()==TrafficLightColor.GREEN) ? TrafficLightColor.YELLOW : TrafficLightColor.RED;
            }
            case ALL_RED -> {
                this.leftColor = TrafficLightColor.RED;
                this.straightColor = TrafficLightColor.RED;
                this.rightColor = TrafficLightColor.RED;
            }
            case TRANSITION_TO_GREEN -> {
                this.leftColor = (lightSet.leftLight()==TrafficLightColor.GREEN) ? TrafficLightColor.RED_YELLOW : TrafficLightColor.RED;
                this.straightColor = (lightSet.straightLight()==TrafficLightColor.GREEN) ? TrafficLightColor.RED_YELLOW : TrafficLightColor.RED;
                this.rightColor = (lightSet.rightLight()==TrafficLightColor.GREEN) ? TrafficLightColor.RED_YELLOW : TrafficLightColor.RED;
            }
        }
    }

    public LightSet getLightSet() {
        return new LightSet(
                this.leftColor,
                this.straightColor,
                this.rightColor
        );
    }
}
