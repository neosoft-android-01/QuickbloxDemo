package com.webwerks.qbcore.models;

/**
 * Created by webwerks on 1/6/17.
 */

public enum Presence {

    ONLINE("ONLINE"),
    OFFLINE("OFFLINE");

    private final String text;

    private Presence(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
