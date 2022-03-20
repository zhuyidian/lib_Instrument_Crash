package com.dunn.instrument.crash.local;

public interface CrashHandlerListener {
    void onCrash(String threadName, Error error);
}
