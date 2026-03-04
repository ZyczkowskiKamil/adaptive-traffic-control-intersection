package org.traffic.service;

import javafx.beans.property.IntegerProperty;
import org.traffic.model.infrastructure.Intersection;
import org.traffic.model.trafficlight.LightTransitionState;
import org.traffic.model.trafficlight.LightPhase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntSupplier;

public class TrafficLightsService {
    private static final int MIN_ACTIVE_PHASE_TIME = 3;
    private static final int MAX_ACTIVE_PHASE_TIME = 10;

    private static final int TRANSITION_TO_RED_TIME = 3;
    private static final int ALL_RED_TIME = 2;
    private static final int TRANSITION_TO_GREEN_TIME = 3;

    private static final int ALGORITHM_CAR_WEIGHT = 10;
    private static final int ALGORITHM_WAIT_TIME_WEIGHT = 1;
    private static final int ALGORITHM_LIGHT_STABILITY_BONUS = 30;

    private static final LightPhase BEGINNING_LIGHT_PHASE = LightPhase.NS_STRAIGHT_RIGHT;
    private static final LightTransitionState BEGINNING_LIGHT_STATE = LightTransitionState.ACTIVE;

    private final Intersection intersection;
    private final IntSupplier getSimulationTime;

    private LightPhase currentLightPhase;
    private LightPhase nextLightPhase;
    private LightTransitionState lightState;

    private int currentPhaseTimer = 0;
    private int transitionTime = 0;


    private final Map<LightPhase,Integer> lightPhaseLastTimeActive;

    public TrafficLightsService(Intersection intersection, IntSupplier getSimulationTime) {
        this.intersection = intersection;
        this.getSimulationTime = getSimulationTime;

        this.currentLightPhase = BEGINNING_LIGHT_PHASE;
        this.lightState = BEGINNING_LIGHT_STATE;
        this.nextLightPhase = null;
        intersection.updateLights(BEGINNING_LIGHT_PHASE, BEGINNING_LIGHT_STATE);

        lightPhaseLastTimeActive = new HashMap<>();
        for (LightPhase lightPhase : LightPhase.values())
            lightPhaseLastTimeActive.put(lightPhase, 0);
    }

    /**
     * Choose light phase based on number of cars and time when light phase was last active
     * <p>
     *     It ensured fairness - every phase will go (one phase with lots of cars will finally change - no starvation)
     *     Prevents rapid switching between lights - MIN_ACTIVE_PHASE_TIME
     *
     *
     * </p>
     */
    private void calculateNextLightPhase() {
        if (currentPhaseTimer < MIN_ACTIVE_PHASE_TIME) {
            this.nextLightPhase = null;
            return;
        }

        LightPhase bestLightPhase = this.currentLightPhase.next(); // to ensure light switching when no cars are detected
        int bestLightPhaseImportance = 0;

        boolean isMaxTimeForCurrentPhaseExceeded = (currentPhaseTimer > MAX_ACTIVE_PHASE_TIME);

        for (LightPhase lightPhase : LightPhase.values()) {
            if (isMaxTimeForCurrentPhaseExceeded && lightPhase == currentLightPhase)
                continue;

            int importance = this.calculateImportanceForLightPhase(lightPhase);


            if (importance > bestLightPhaseImportance) {
                bestLightPhaseImportance = importance;
                bestLightPhase = lightPhase;
            }
        }

        if (bestLightPhase != this.currentLightPhase)
            this.nextLightPhase = bestLightPhase;
        else
            this.nextLightPhase = null;
    }

    /**
     * Calculate importance of changing to lightPhase based on number of cars, last time lightPhase was active and if it is current light phase
     * <p>
     *     importance = (carsNumber * weight1) + (timeSinceLastActive * weight2) + light_stability_bonus
     *     light_stability_bonus - to not switch back and forth when there is one or two cars difference
     * </p>
     */
    private int calculateImportanceForLightPhase(LightPhase lightPhase) {
        if (lightPhase == LightPhase.ALL_DIRECTIONS_STOP)
            return 0;

        int carsWaiting = intersection.getCarsNumberThatCanLeaveIntersection(lightPhase);
        int phaseWaitTime = this.lightPhaseTimeFromLastActive(lightPhase);

        int stabilityBonus = (lightPhase == this.currentLightPhase) ? ALGORITHM_LIGHT_STABILITY_BONUS : 0;

        return (carsWaiting * ALGORITHM_CAR_WEIGHT) + (phaseWaitTime * ALGORITHM_WAIT_TIME_WEIGHT) + stabilityBonus;
    }

    private int lightPhaseTimeFromLastActive(LightPhase lightPhase) {
        int lastActive = lightPhaseLastTimeActive.get(lightPhase);
        return this.getSimulationTime.getAsInt() - lastActive;
    }

    /**
     * Handle 1 simulation step
     * <p>
     *     If ACTIVE phase (lights are not changing) then calculate if it is optimal to change lights
     *     Then transition lights if needed
     * </p>
     */
    public void handleSimulationStep() {
        if (this.lightState == LightTransitionState.ACTIVE) {
            lightPhaseLastTimeActive.put(currentLightPhase, getSimulationTime.getAsInt());
            currentPhaseTimer++;
            calculateNextLightPhase();
        }

        if (this.nextLightPhase != null) {
            handleTransitionStep();
        }
    }

    public boolean isLightTransitionStateActive() {
        return this.lightState == LightTransitionState.ACTIVE;
    }

    private void handleTransitionStep() {
        transitionTime++;
        if (transitionTime <= TRANSITION_TO_RED_TIME) {
            this.lightState = LightTransitionState.TRANSITION_TO_RED;
        } else if (transitionTime <= TRANSITION_TO_RED_TIME + ALL_RED_TIME) {
            this.lightState = LightTransitionState.ALL_RED;
        } else if (transitionTime <= TRANSITION_TO_RED_TIME + ALL_RED_TIME + TRANSITION_TO_GREEN_TIME) {
            this.lightState = LightTransitionState.TRANSITION_TO_GREEN;
        } else {
            this.lightState = LightTransitionState.ACTIVE;

            this.transitionTime = 0;
            this.currentPhaseTimer = 0;
            this.currentLightPhase = this.nextLightPhase;
            this.nextLightPhase = null;
        }
        intersection.updateLights(this.currentLightPhase, this.lightState);
    }

    public LightTransitionState getLightTransitionState() {
        return this.lightState;
    }

    public LightPhase getLightPhase() {
        return this.currentLightPhase;
    }

}
