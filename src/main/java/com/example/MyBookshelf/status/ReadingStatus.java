package com.example.MyBookshelf.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReadingStatus {
    PLANNING_TO_READ("Planning to read"),
    READING("Reading"),
    READ("Read");

    private final String label;

    ReadingStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static ReadingStatus fromString(String value) {
        for (ReadingStatus status : ReadingStatus.values()) {
            if (status.label.equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid reading status: " + value);
    }
}
