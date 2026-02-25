package org.traffic.model;

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

    public TrafficLightColor getLeftColor() {
        return leftColor;
    }

    public TrafficLightColor getStraightColor() {
        return straightColor;
    }

    public TrafficLightColor getRightColor() {
        return rightColor;
    }

    public void setLeftColor(TrafficLightColor leftColor) {
        this.leftColor = leftColor;
    }

    public void setStraightColor(TrafficLightColor straightColor) {
        this.straightColor = straightColor;
    }

    public void setRightColor(TrafficLightColor rightColor) {
        this.rightColor = rightColor;
    }
}
