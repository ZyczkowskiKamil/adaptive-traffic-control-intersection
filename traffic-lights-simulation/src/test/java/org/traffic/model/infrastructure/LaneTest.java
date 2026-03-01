package org.traffic.model.infrastructure;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.traffic.model.trafficlight.LightSet;
import org.traffic.model.trafficlight.LightTransitionState;
import org.traffic.model.trafficlight.RoadTrafficLights;
import org.traffic.model.trafficlight.TrafficLightColor;
import org.traffic.model.vehicle.Vehicle;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LaneTest {

    @ParameterizedTest(name = "canMoveFromLaneTo Turning possibility from {1} lane type in {2} direction should be {3}")
    @CsvSource({
            // LANE_TYPE, LANE_LOCATION, DESTINATION_LOCATION, EXPECTED_CAN_MOVE
            // left lane
            "LEFT, SOUTH, WEST, TRUE",
            "LEFT, SOUTH, NORTH, FALSE",
            "LEFT, SOUTH, EAST, FALSE",
            "LEFT, SOUTH, SOUTH, FALSE",
            "LEFT, WEST, NORTH, TRUE",
            "LEFT, EAST, NORTH, FALSE",
            // straight lane
            "STRAIGHT, SOUTH, WEST, FALSE",
            "STRAIGHT, SOUTH, NORTH, TRUE",
            "STRAIGHT, SOUTH, EAST, FALSE",
            "STRAIGHT, SOUTH, SOUTH, FALSE",
            "STRAIGHT, NORTH, SOUTH, TRUE",
            "STRAIGHT, EAST, NORTH, FALSE",
            "STRAIGHT, WEST, EAST, TRUE",
            // right lane
            "RIGHT, SOUTH, WEST, FALSE",
            "RIGHT, SOUTH, NORTH, FALSE",
            "RIGHT, SOUTH, EAST, TRUE",
            "RIGHT, SOUTH, SOUTH, FALSE",
            "RIGHT, NORTH, WEST, TRUE",
            "RIGHT, EAST, NORTH, TRUE",
            "RIGHT, WEST, NORTH, FALSE",
            // left and straight lane
            "LEFT_STRAIGHT, SOUTH, WEST, TRUE",
            "LEFT_STRAIGHT, SOUTH, NORTH, TRUE",
            "LEFT_STRAIGHT, SOUTH, EAST, FALSE",
            "LEFT_STRAIGHT, SOUTH, SOUTH, FALSE",
            "LEFT_STRAIGHT, EAST, NORTH, FALSE",
            "LEFT_STRAIGHT, WEST, NORTH, TRUE",
            // straight and right lane
            "STRAIGHT_RIGHT, SOUTH, WEST, FALSE",
            "STRAIGHT_RIGHT, SOUTH, NORTH, TRUE",
            "STRAIGHT_RIGHT, SOUTH, EAST, TRUE",
            "STRAIGHT_RIGHT, SOUTH, SOUTH, FALSE",
            "STRAIGHT_RIGHT, EAST, NORTH, TRUE",
            "STRAIGHT_RIGHT, WEST, NORTH, FALSE",
            // all direction lane
            "LEFT_STRAIGHT_RIGHT, SOUTH, WEST, TRUE",
            "LEFT_STRAIGHT_RIGHT, SOUTH, NORTH, TRUE",
            "LEFT_STRAIGHT_RIGHT, SOUTH, EAST, TRUE",
            "LEFT_STRAIGHT_RIGHT, SOUTH, SOUTH, FALSE",
            "LEFT_STRAIGHT_RIGHT, EAST, EAST, FALSE",
            "LEFT_STRAIGHT_RIGHT, WEST, NORTH, TRUE"
    })
    void canMoveFromLaneTo(LaneType laneType, Direction laneLocation, Direction destinationLocation, boolean expectedCanMove) {
        var lane = new Lane(laneType, laneLocation);
        assertEquals(expectedCanMove, lane.canMoveFromLaneTo(destinationLocation));
    }

    @Test
    void numberOfCarsThatCanGoOnLightSet_leftLane() {
        var allGreenSet = new LightSet(TrafficLightColor.GREEN, TrafficLightColor.GREEN, TrafficLightColor.GREEN);
        var allRedSet = new LightSet(TrafficLightColor.RED, TrafficLightColor.RED, TrafficLightColor.RED);
        var leftGreen = new LightSet(TrafficLightColor.GREEN, TrafficLightColor.YELLOW, TrafficLightColor.RED_YELLOW);
        var straightRightGreen = new LightSet(TrafficLightColor.YELLOW, TrafficLightColor.GREEN, TrafficLightColor.GREEN);

        Lane lane = new Lane(LaneType.LEFT, Direction.SOUTH);
        for (int i = 1; i <= 12; i++)
            lane.addVehicle(new Vehicle("test", Direction.SOUTH, Direction.WEST));

        assertEquals(12, lane.numberOfCarsThatCanGoOnLightSet(allGreenSet));
        assertEquals(12, lane.numberOfCarsThatCanGoOnLightSet(leftGreen));
        assertEquals(0, lane.numberOfCarsThatCanGoOnLightSet(allRedSet));
        assertEquals(0, lane.numberOfCarsThatCanGoOnLightSet(straightRightGreen));
    }

    @Test
    void numberOfCarsThatCanGoOnLightSet_leftStraightLane() {
        var lightSet = new LightSet(TrafficLightColor.GREEN, TrafficLightColor.RED, TrafficLightColor.RED);

        Lane lane = new Lane(LaneType.LEFT_STRAIGHT, Direction.SOUTH);
        for (int i = 1; i <= 6; i++)
            lane.addVehicle(new Vehicle("test", Direction.SOUTH, Direction.WEST));
        lane.addVehicle(new Vehicle("test", Direction.SOUTH, Direction.NORTH));
        for (int i = 1; i <= 6; i++)
            lane.addVehicle(new Vehicle("test", Direction.SOUTH, Direction.WEST));

        assertEquals(6, lane.numberOfCarsThatCanGoOnLightSet(lightSet));
    }

    @Test
    void numberOfCarsThatCanGoOnLightSet_emptyLane() {
        var lightSet = new LightSet(TrafficLightColor.GREEN, TrafficLightColor.GREEN, TrafficLightColor.GREEN);
        Lane lane = new Lane(LaneType.LEFT_STRAIGHT, Direction.SOUTH);

        assertEquals(0, lane.numberOfCarsThatCanGoOnLightSet(lightSet));
    }

    @Test
    @DisplayName("Retrieved vehicles should come in correct order and should return empty with empty lane")
    void moveIfPossibleAndGetVehicleTest_greenLight() {
        var lane = new Lane(LaneType.LEFT_STRAIGHT, Direction.SOUTH);
        var lightSet = new LightSet(TrafficLightColor.RED, TrafficLightColor.GREEN, TrafficLightColor.GREEN);
        var trafficLights = new RoadTrafficLights();
        var vehicle1 = new Vehicle("test", Direction.SOUTH, Direction.NORTH);
        var vehicle2 = new Vehicle("test2", Direction.SOUTH, Direction.NORTH);

        trafficLights.setLights(lightSet, LightTransitionState.ACTIVE);
        lane.addVehicle(vehicle1);
        lane.addVehicle(vehicle2);

        Optional<Vehicle> retrievedVehicle1 = lane.moveIfPossibleAndGetVehicle(trafficLights);
        Optional<Vehicle> retrievedVehicle2 = lane.moveIfPossibleAndGetVehicle(trafficLights);
        Optional<Vehicle> retrievedVehicle3 = lane.moveIfPossibleAndGetVehicle(trafficLights);


        assertTrue(retrievedVehicle1.isPresent());
        assertEquals(retrievedVehicle1.get(), vehicle1);

        assertTrue(retrievedVehicle2.isPresent());
        assertEquals(retrievedVehicle2.get(), vehicle2);

        assertTrue(retrievedVehicle3.isEmpty());
    }

    @Test
    @DisplayName("Should not move and retrieve during light change")
    void moveIfPossibleAndGetVehicleTest_greenLight_duringLightChange() {
        var lane = new Lane(LaneType.LEFT_STRAIGHT, Direction.SOUTH);
        var lightSet = new LightSet(TrafficLightColor.RED, TrafficLightColor.GREEN, TrafficLightColor.GREEN);
        var trafficLights = new RoadTrafficLights();
        var vehicle1 = new Vehicle("test", Direction.SOUTH, Direction.NORTH);
        var vehicle2 = new Vehicle("test2", Direction.SOUTH, Direction.NORTH);

        trafficLights.setLights(lightSet, LightTransitionState.TRANSITION_TO_GREEN);
        lane.addVehicle(vehicle1);
        lane.addVehicle(vehicle2);

        Optional<Vehicle> retrievedVehicle1 = lane.moveIfPossibleAndGetVehicle(trafficLights);
        Optional<Vehicle> retrievedVehicle2 = lane.moveIfPossibleAndGetVehicle(trafficLights);

        trafficLights.setLights(lightSet, LightTransitionState.ACTIVE);
        Optional<Vehicle> retrievedVehicle3 = lane.moveIfPossibleAndGetVehicle(trafficLights);

        assertFalse(retrievedVehicle1.isPresent());
        assertFalse(retrievedVehicle2.isPresent());
        assertTrue(retrievedVehicle3.isPresent());
        assertEquals(retrievedVehicle3.get(), vehicle1);
    }
}