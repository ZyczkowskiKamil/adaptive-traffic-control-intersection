package org.traffic.model.infrastructure;

/**
 * Lane Direction with priority value
 * <p>
 *      Priority value is used to determine which lane is chosen.
 *      ("STRAIGHT" lane direction is preferred over "LEFT_STRAIGHT").
 *      Lower number means higher priority("1" preferred over "2").
 * </p>
 */
public enum LaneType {
    LEFT(1),
    STRAIGHT(1),
    RIGHT(1),
    LEFT_STRAIGHT(2),
    STRAIGHT_RIGHT(2),
    LEFT_STRAIGHT_RIGHT(3);

    private final int priority;

    LaneType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
