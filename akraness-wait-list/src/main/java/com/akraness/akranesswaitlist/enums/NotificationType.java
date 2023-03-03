package com.akraness.akranesswaitlist.enums;

public enum NotificationType {
    SMS("SMS"),
    EMAIL("EMAIL");

    public final String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
