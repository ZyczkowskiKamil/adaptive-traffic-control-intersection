package org.traffic.model.trafficlight;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.traffic.model.infrastructure.TurnDirection;

import static org.junit.jupiter.api.Assertions.*;

class LightSetTest {

    private static final TrafficLightColor G = TrafficLightColor.GREEN;
    private static final TrafficLightColor R = TrafficLightColor.RED;
    private static final TrafficLightColor Y = TrafficLightColor.YELLOW;
    private static final TrafficLightColor RY = TrafficLightColor.RED_YELLOW;

    private static final LightSet allGreenLightSet = new LightSet(G, G, G);
    private static final LightSet allRedLightSet = new LightSet(R, R, R);
    private static final LightSet allYellowLightSet = new LightSet(Y, Y, Y);
    private static final LightSet allRedYellowLightSet = new LightSet(RY, RY, RY);
    private static final LightSet noGreenLightSet = new LightSet(R, Y, RY);

    @ParameterizedTest(name = "Color with set of ({1},{2},{3}) in {4} turn direction should be {5}")
    @CsvSource({
        // LEFT_COLOR, STRAIGHT_COLOR, RIGHT_COLOR, TURN_DIRECTION, EXPECTED_COLOR
            // test all green
            "GREEN,GREEN,GREEN,LEFT,GREEN",
            "GREEN,GREEN,GREEN,STRAIGHT,GREEN",
            "GREEN,GREEN,GREEN,RIGHT,GREEN",
            // test all red
            "RED,RED,RED,LEFT,RED",
            "RED,RED,RED,STRAIGHT,RED",
            "RED,RED,RED,RIGHT,RED",
            // test all yellow
            "YELLOW,YELLOW,YELLOW,LEFT,YELLOW",
            "YELLOW,YELLOW,YELLOW,STRAIGHT,YELLOW",
            "YELLOW,YELLOW,YELLOW,RIGHT,YELLOW",
            // test all red_yellow
            "RED_YELLOW,RED_YELLOW,RED_YELLOW,LEFT,RED_YELLOW",
            "RED_YELLOW,RED_YELLOW,RED_YELLOW,STRAIGHT,RED_YELLOW",
            "RED_YELLOW,RED_YELLOW,RED_YELLOW,RIGHT,RED_YELLOW",
            // test colors without green
            "RED,YELLOW,RED_YELLOW,LEFT,RED",
            "RED,YELLOW,RED_YELLOW,STRAIGHT,YELLOW",
            "RED,YELLOW,RED_YELLOW,RIGHT,RED_YELLOW"
    })
    void getColorInDirection(TrafficLightColor leftColor,
                             TrafficLightColor straightColor,
                             TrafficLightColor rightColor,
                             TurnDirection turnDirection,
                             TrafficLightColor expectedColor) {

        LightSet lightSet = new LightSet(leftColor, straightColor, rightColor);
        assertEquals(expectedColor, lightSet.getColorInDirection(turnDirection));
    }
}