package org.traffic.model.infrastructure;

import org.jetbrains.annotations.NotNull;
import org.traffic.model.trafficlight.LightPhase;
import org.traffic.model.trafficlight.LightSet;
import org.traffic.model.trafficlight.LightTransitionState;
import org.traffic.model.trafficlight.RoadTrafficLights;
import org.traffic.model.vehicle.Vehicle;

import java.util.*;

public class Road {

    private final List<Lane> lanes;
    private final Direction roadLocation;
    private final RoadTrafficLights trafficLights;

    /**
     * Create road with one lane that can go in all directions
     * @param roadLocation Cardinal direction of where the road comes from (NORTH, EAST, SOUTH, WEST)
     */
    public Road(Direction roadLocation) {
        this.lanes = new ArrayList<>(List.of(new Lane(LaneType.LEFT_STRAIGHT_RIGHT, roadLocation)));
        this.roadLocation = roadLocation;
        this.trafficLights = new RoadTrafficLights();
    }

    /**
     * Add lane to road
     * <p>
     *     If there is no specialized road then road at the size is used multidirectional.
     *     (e.g. If there is 2 lanes road and one is for left turn and second is straight, then straight becomes straight-right)
     * </p>
     * <p>
     *     Add left:
     *     - If left turn doesn't exist change leftmost lane type to not include left turn
     *     - Add left turn
     *     Add right:
     *     - If right turn exists add right turn
     *     - Else add right turn and change rightmost lane type
     *     Add straight:
     *     - If right turn doesn't exist change rightmost lane type to not include right turn
     *     - Add right turn
     * </p>
     * @param direction Direction where lane is going
     */
    public void addLane(TurnDirection direction) {
        switch (direction) {
            case LEFT -> {
                if (lanes.stream()
                        .noneMatch(lane -> lane.getType() == LaneType.LEFT)) {
                    lanes.forEach(lane -> {
                        if (lane.getType() == LaneType.LEFT_STRAIGHT) lane.setType(LaneType.STRAIGHT);
                        if (lane.getType() == LaneType.LEFT_STRAIGHT_RIGHT) lane.setType(LaneType.STRAIGHT_RIGHT);
                    });
                }

                lanes.add(new Lane(LaneType.LEFT, this.roadLocation));
            }
            case STRAIGHT -> {
                Optional<Lane> allDirectionalLane = lanes.stream()
                        .filter(lane -> lane.getType() == LaneType.LEFT_STRAIGHT_RIGHT)
                        .findAny();

                if (allDirectionalLane.isPresent()) {
                    allDirectionalLane.get().setType(LaneType.LEFT_STRAIGHT);
                    this.lanes.add(new Lane(LaneType.STRAIGHT_RIGHT, this.roadLocation));
                } else {
                    this.lanes.add(new Lane(LaneType.STRAIGHT, this.roadLocation));
                }
            }
            case RIGHT -> {
                if (lanes.stream()
                        .noneMatch(lane -> lane.getType() == LaneType.RIGHT)) {
                    lanes.forEach(lane -> {
                        if (lane.getType() == LaneType.STRAIGHT_RIGHT) lane.setType(LaneType.STRAIGHT);
                        if (lane.getType() == LaneType.LEFT_STRAIGHT_RIGHT) lane.setType(LaneType.LEFT_STRAIGHT);
                    });
                }

                lanes.add(new Lane(LaneType.RIGHT, this.roadLocation));
            }
        }
    }

    /**
     * Adds vehicle to the best available lane based on crowd and specialization.
     * <p>
     *     Select a lane that allows destination.
     *     If multiple available - choose with the least amount of vehicles.
     *     If there is a tie vehicle chooses lane with better specialization
     *     ("LEFT" lane is preferred over "LEFT_STRAIGHT" lane for left turn).
     * <p>
     *     If there is no possible lane choice system prints error.
     * </p>
     * @param vehicle Vehicle to add to road
     */
    public void addVehicle(@NotNull Vehicle vehicle) {
        lanes.stream()
                .filter(lane -> lane.canMoveFromLaneTo(vehicle.endRoad()))
                .min(Comparator
                        .comparingInt(Lane::vehiclesNumber)
                        .thenComparing(lane -> lane.getType().getPriority())
                )
                .ifPresentOrElse(
                    lane -> lane.addVehicle(vehicle),
                     () -> System.err.println("Line not found from " + this.roadLocation + " to " + vehicle.endRoad())
                );
    }

    public void updateTrafficLights(LightPhase lightPhase, LightTransitionState lightState) {
        LightSet lightSet = lightPhase.getLightSetFor(this.roadLocation);
        this.trafficLights.setLights(lightSet, lightState);
    }

    public List<Vehicle> processStep() {
        List<Vehicle> vehiclesLeavingRoad = new LinkedList<>();
        for (Lane lane : lanes) {
            Optional<Vehicle> vehicle = lane.moveIfPossibleAndGetVehicle(trafficLights);
            vehicle.ifPresent(vehiclesLeavingRoad::add);
        }
        return vehiclesLeavingRoad;
    }

    public Direction getRoadLocation() {
        return roadLocation;
    }

    public int getCarsNumberThatCanGo(LightPhase lightPhase) {
        LightSet lightSet = lightPhase.getLightSetFor(this.roadLocation);
        int carsThatCanGo = 0;

        for (Lane lane : lanes) {
            carsThatCanGo += lane.numberOfCarsThatCanGoOnLightSet(lightSet);
        }

        return carsThatCanGo;
    }

    public boolean isEmpty() {
        for (Lane lane : lanes) {
            if (!lane.isEmpty())
                return false;
        }
        return true;
    }

    public List<Lane> getLanes() {
        return Collections.unmodifiableList(this.lanes);
    }

    public int getVehicleCount() {
        return lanes.stream()
                .mapToInt(Lane::getVehicleCount)
                .sum();
    }

    public LightSet getLightSet() {
        return this.trafficLights.getLightSet();
    }
}
