package com.dunn.instrument.crashlocal;

public interface CrashHandlerListener {
    void onCrash(String threadName, Error error);
}
