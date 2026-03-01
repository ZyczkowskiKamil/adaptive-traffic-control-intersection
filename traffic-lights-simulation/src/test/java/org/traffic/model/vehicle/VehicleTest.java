package org.traffic.model.vehicle;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.traffic.model.infrastructure.Direction;
import org.traffic.model.infrastructure.TurnDirection;
import org.traffic.model.trafficlight.LightSet;
import org.traffic.model.trafficlight.LightTransitionState;
import org.traffic.model.trafficlight.RoadTrafficLights;
import org.traffic.model.trafficlight.TrafficLightColor;

import static org.junit.jupiter.api.Assertions.*;

class  VehicleTest {

    @ParameterizedTest(name = "Vehicle with road traffic lights of ({1},{2},{3}) with light transition state {4} should have {5}")
    @CsvSource({
        // LEFT_COLOR, STRAIGHT_COLOR, RIGHT_COLOR, LIGHT_TRANSITION_STATE, EXPECTED_HAS_GREEN
            "GREEN,GREEN,GREEN, ACTIVE, TRUE",
            "GREEN,GREEN,GREEN, TRANSITION_TO_RED, FALSE",
            "GREEN,GREEN,GREEN, ALL_RED, FALSE",
            "GREEN,GREEN,GREEN, TRANSITION_TO_GREEN, FALSE",
            "RED,RED,RED, ACTIVE, FALSE",
            "RED,GREEN,RED, ACTIVE, TRUE",
            "RED,RED,GREEN, ACTIVE, FALSE",
    })
    void hasGreenLight(TrafficLightColor leftColor,
                       TrafficLightColor straightColor,
                       TrafficLightColor rightColor,
                       LightTransitionState lightTransitionState,
                       boolean expectedGreen) {

        var vehicle = new Vehicle("test", Direction.SOUTH, Direction.NORTH);

        LightSet lightSet = new LightSet(leftColor, straightColor, rightColor);

        var trafficLights = new RoadTrafficLights();
        trafficLights.setLights(lightSet, lightTransitionState);

        assertEquals(expectedGreen, vehicle.hasGreenLight(trafficLights));
    }

    @ParameterizedTest(name = "Vehicle moving from {0} to {1} should turn {2}")
    @CsvSource({
        // "START, END, EXPECTED_TURN"
            "SOUTH, WEST, LEFT",
            "SOUTH, NORTH, STRAIGHT",
            "SOUTH, EAST, RIGHT",
            "NORTH, WEST, RIGHT",
            "NORTH, SOUTH, STRAIGHT",
            "NORTH, EAST, LEFT",
            "EAST, SOUTH, LEFT",
            "EAST, WEST, STRAIGHT",
            "EAST, NORTH, RIGHT",
            "WEST, NORTH, LEFT",
            "WEST, EAST, STRAIGHT",
            "WEST, SOUTH, RIGHT"
    })
    void calculateTurnDirectionCorrectly(Direction startRoad, Direction endRoad, TurnDirection expectedTurn) {
        Vehicle vehicle = new Vehicle("test", startRoad, endRoad);

        assertEquals(expectedTurn, vehicle.turnDirection());
    }

}