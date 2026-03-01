package org.traffic.model.trafficlight;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.traffic.model.infrastructure.TurnDirection;

import static org.junit.jupiter.api.Assertions.*;

class RoadTrafficLightsTest {

    private static final TrafficLightColor G = TrafficLightColor.GREEN;
    private static final TrafficLightColor R = TrafficLightColor.RED;
    private static final TrafficLightColor Y = TrafficLightColor.YELLOW;
    private static final TrafficLightColor RY = TrafficLightColor.RED_YELLOW;

    private static final LightSet ALL_GREEN = new LightSet(G, G, G);
    private static final LightSet ALL_RED = new LightSet(R, R, R);
    private static final LightSet ALL_YELLOW = new LightSet(Y, Y, Y);
    private static final LightSet ALL_RED_YELLOW = new LightSet(RY, RY, RY);
    private static final LightSet NO_GREEN = new LightSet(R, Y, RY);
    private static final LightSet GREEN_LEFT = new LightSet(G, R, R);
    private static final LightSet GREEN_STRAIGHT = new LightSet(R, G, R);
    private static final LightSet GREEN_RIGHT = new LightSet(R, R, G);

    private RoadTrafficLights roadTrafficLights;

    @BeforeEach
    void setUp() {
        roadTrafficLights = new RoadTrafficLights();
    }

    @AfterEach
    void tearDown() {
        roadTrafficLights = null;
    }

    @Test
    void isLightGreen_afterInitialization() {
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
    }

    @Test
    void isLightGreen_allGreenActive() {
        roadTrafficLights.setLights(ALL_GREEN, LightTransitionState.ACTIVE);

        assertTrue(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
        assertTrue(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
        assertTrue(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
    }

    @Test
    void isLightGreen_allGreenDuringTransition() {
        for (LightTransitionState lightTransitionState : LightTransitionState.values()) {
            roadTrafficLights.setLights(ALL_GREEN, lightTransitionState);

            if (lightTransitionState != LightTransitionState.ACTIVE) {
                assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
                assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
                assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
            }
        }
    }

    @Test
    void isLightGreen_allRed() {
        for (LightTransitionState lightTransitionState : LightTransitionState.values()) {
            roadTrafficLights.setLights(ALL_RED, lightTransitionState);
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
        }
    }

    @Test
    void isLightGreen_allYellow() {
        for (LightTransitionState lightTransitionState : LightTransitionState.values()) {
            roadTrafficLights.setLights(ALL_YELLOW, lightTransitionState);
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
        }
    }

    @Test
    void isLightGreen_allRedYellow() {
        for (LightTransitionState lightTransitionState : LightTransitionState.values()) {
            roadTrafficLights.setLights(ALL_RED_YELLOW, lightTransitionState);
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
        }
    }

    @Test
    void isLightGreen_noGreenLight() {
        for (LightTransitionState lightTransitionState : LightTransitionState.values()) {
            roadTrafficLights.setLights(NO_GREEN, lightTransitionState);
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
            assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
        }
    }

    @Test
    void isLightGreen_greenLeft() {
        roadTrafficLights.setLights(GREEN_LEFT, LightTransitionState.ACTIVE);
        assertTrue(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
    }

    @Test
    void isLightGreen_greenStraight() {
        roadTrafficLights.setLights(GREEN_STRAIGHT, LightTransitionState.ACTIVE);
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
        assertTrue(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
    }

    @Test
    void isLightGreen_greenRight() {
        roadTrafficLights.setLights(GREEN_RIGHT, LightTransitionState.ACTIVE);
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.LEFT));
        assertFalse(roadTrafficLights.isLightGreen(TurnDirection.STRAIGHT));
        assertTrue(roadTrafficLights.isLightGreen(TurnDirection.RIGHT));
    }
}