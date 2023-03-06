package com.akraness.akranesswaitlist.enums;

public enum PinType {
    TRANSACTION("TRANSACTION"),
    MAGIC("MAGIC");

    public final String type;

    PinType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
