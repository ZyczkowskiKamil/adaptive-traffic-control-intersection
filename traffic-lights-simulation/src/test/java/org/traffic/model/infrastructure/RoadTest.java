package org.traffic.model.infrastructure;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoadTest {

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingLeftTurn() {
        Road road = new Road(Direction.SOUTH);

        road.addLane(TurnDirection.LEFT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(2, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT_RIGHT));
    }

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingStraightTurn() {
        Road road = new Road(Direction.SOUTH);

        road.addLane(TurnDirection.STRAIGHT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(2, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT_STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT_RIGHT));
    }

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingRightTurn() {
        Road road = new Road(Direction.WEST);

        road.addLane(TurnDirection.RIGHT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(2, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT_STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.RIGHT));
    }

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingLeftTurnAndStraightTurn() {
        Road road = new Road(Direction.NORTH);

        road.addLane(TurnDirection.LEFT);
        road.addLane(TurnDirection.STRAIGHT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(3, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT_RIGHT));
    }

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingStraightTurnAndRightTurn() {
        Road road = new Road(Direction.NORTH);

        road.addLane(TurnDirection.RIGHT);
        road.addLane(TurnDirection.STRAIGHT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(3, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT_STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.RIGHT));
    }

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingStraightTurnAndStraightTurn() {
        Road road = new Road(Direction.EAST);

        road.addLane(TurnDirection.STRAIGHT);
        road.addLane(TurnDirection.STRAIGHT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(3, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT_STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT));
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.STRAIGHT_RIGHT));
    }

    @Test
    void addLane_shouldSplitCorrectlyInitialAllDirectionalLane_whenAddingLeftStraightRightTurns() {
        Road road = new Road(Direction.EAST);

        road.addLane(TurnDirection.STRAIGHT);
        road.addLane(TurnDirection.LEFT);
        road.addLane(TurnDirection.RIGHT);

        List<Lane> currentLanes = road.getLanes();
        assertEquals(4, currentLanes.size());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.LEFT));
        assertEquals(2, currentLanes.stream().filter(lane -> lane.getType() == LaneType.STRAIGHT).count());
        assertTrue(currentLanes.stream().anyMatch(lane -> lane.getType() == LaneType.RIGHT));
    }
}