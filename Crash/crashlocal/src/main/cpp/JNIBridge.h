//
// Created by darrenzeng on 2021/3/7.
//

#ifndef OPTIMIZE_DAY06_JNIBRIDGE_H
#define OPTIMIZE_DAY06_JNIBRIDGE_H

#include <jni.h>
#include <dlfcn.h>
#include "CrashDefine.h"
#include "CrashAnalyser.h"

class JNIBridge {
private:
    JavaVM *javaVm;
    jobject callbackObj;
    jclass nativeCrashMonitorClass;
public:
    JNIBridge(JavaVM *javaVm, jobject callbackObj, jclass nativeCrashMonitorClass);

public:
    void throwException2Java(struct native_handler_context_struct *handlerContext);
};


#endif //OPTIMIZE_DAY06_JNIBRIDGE_H
