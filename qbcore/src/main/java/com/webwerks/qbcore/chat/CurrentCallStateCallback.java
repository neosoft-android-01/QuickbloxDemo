package com.webwerks.qbcore.chat;

/**
 * Created by webwerks on 30/5/17.
 */

public interface CurrentCallStateCallback {

    void onCallStarted();

    void onCallStopped();

    void onCallConnectionClose();

}
