package com.henrique.nookio_api.modules.schedules.models;

public enum ScheduleStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED;

    public static ScheduleStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        return ScheduleStatus.valueOf(value.toUpperCase());
    }
}