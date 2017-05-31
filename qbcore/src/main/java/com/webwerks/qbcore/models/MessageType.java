package com.webwerks.qbcore.models;

/**
 * Created by webwerks on 5/5/17.
 */

public enum MessageType {

    TEXT("TEXT"),
    IMAGE("IMAGE"),
    AUDIO("AUDIO"),
    VIDEO("VIDEO"),
    LOCATION("LOCATION"),
    CALL("CALL");

    private final String text;

    private MessageType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
