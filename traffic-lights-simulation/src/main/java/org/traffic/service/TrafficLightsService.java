package org.traffic.service;

import org.traffic.model.Intersection;
import org.traffic.model.LightTransitionState;
import org.traffic.model.LightPhase;

public class TrafficLightsService {
    private static final int MIN_ACTIVE_PHASE_TIME = 3;
    private static final int MAX_ACTIVE_PHASE_TIME = 10;

    private static final int TRANSITION_TO_RED_TIME = 3;
    private static final int ALL_RED_TIME = 2;
    private static final int TRANSITION_TO_GREEN_TIME = 3;


    private static final LightPhase BEGINNING_LIGHT_PHASE = LightPhase.NS_STRAIGHT_RIGHT;
    private static final LightTransitionState BEGINNING_LIGHT_STATE = LightTransitionState.ACTIVE;

    private final Intersection intersection;
    private LightPhase lightPhase;
    private LightPhase nextLightPhase;
    private LightTransitionState lightState;

    private int currentPhaseTimer = 0;
    private int transitionTime = 0;

    public TrafficLightsService(Intersection intersection) {
        this.intersection = intersection;

        this.lightPhase = BEGINNING_LIGHT_PHASE;
        this.lightState = BEGINNING_LIGHT_STATE;
        this.nextLightPhase = null;
        intersection.updateLights(BEGINNING_LIGHT_PHASE, BEGINNING_LIGHT_STATE);
    }

    /**
     * Choose light phase that currently most cars can go
     * @return
     */
    private LightPhase calculateNextLightPhase() {
        LightPhase bestLightPhase = LightPhase.NS_STRAIGHT_RIGHT;
        int bestPhaseCarsNumber = -1;

        for (LightPhase lightPhase : LightPhase.values()) {
            int lightPhaseCarsNumber = intersection.getCarsNumberThatCanLeaveIntersection(lightPhase);
            if (lightPhaseCarsNumber > bestPhaseCarsNumber) {
                bestPhaseCarsNumber = lightPhaseCarsNumber;
                bestLightPhase = lightPhase;
            }
        }

        return bestLightPhase;
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
            currentPhaseTimer++;
            this.nextLightPhase = calculateNextLightPhase();
        }

        if (this.nextLightPhase != null) {
            handleTransitionStep();
        }
    }

    public boolean canCarGo() {
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
            this.lightPhase = this.nextLightPhase;
            this.nextLightPhase = null;
        }
        intersection.updateLights(this.lightPhase, this.lightState);
    }


}
