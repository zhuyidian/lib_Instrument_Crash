//
// Created by darrenzeng on 2021/3/7.
//

#ifndef OPTIMIZE_DAY06_CRASHANALYSER_H
#define OPTIMIZE_DAY06_CRASHANALYSER_H

#include "JNIBridge.h"
#include <pthread.h>
#include <stdlib.h>
#include "Utils.h"
#include <unistd.h>
#include <unwind.h>

typedef struct native_handler_context_struct{
    int code;
    siginfo_t *si;
    void *sc;
    pid_t pid;
    pid_t tid;
    const char *processName;
    const char *threadName;
    int frame_size;
    uintptr_t frames[BACKTRACE_FRAMES_MAX];
} native_handler_context;

extern void initCondition();

void* threadCrashMonitor(void *argv);

extern void waitForSignal();

extern void notifyCaughtSignal(int code, siginfo_t *si, void *sc);

extern void analysisNativeException();

extern void copyInfo2Context(int code, siginfo_t *si, void *sc);

#endif //OPTIMIZE_DAY06_CRASHANALYSER_H
