package com.epam.esm.model.entity;

import lombok.Value;

/**
 * Object encapsulating information about sorting. Used for Service Layer <-> Repository layer
 * communication
 */
@Value
public class SortOption {
    Field field;
    Direction direction;

    public enum Field {
        NAME, LAST_UPDATE_DATE, CREATE_DATE
    }

    public enum Direction {
        ASC, DESC
    }
}
