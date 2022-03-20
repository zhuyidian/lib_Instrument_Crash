#include <jni.h>
#include <string>
#include <unistd.h>
#include "SignalHandler.h"
#include "JNIBridge.h"
#include "CrashAnalyser.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_dunn_instrument_crash_local_NativeCrashMonitor_nativeInit(JNIEnv *env, jobject nativeCrashMonitor,
                                                             jobject callback) {
    //  主要是把 callback 保存起来，方便监听监听到异常时回调给 java 层
    // 做一个线程的监听
    callback = env->NewGlobalRef(callback);
    JavaVM *javaVm;
    env->GetJavaVM(&javaVm);
    // 为了避免子线程为空的情况
    jclass  nativeCrashMonitorClass = env->GetObjectClass(nativeCrashMonitor);
    nativeCrashMonitorClass = (jclass)env->NewGlobalRef(nativeCrashMonitorClass);
    JNIBridge *jniBridge = new JNIBridge(javaVm, callback, nativeCrashMonitorClass);
    // 创建一个线程去监听是否有异常
    initCondition();
    pthread_t pthread;
    int ret = pthread_create(&pthread, nullptr, threadCrashMonitor, jniBridge);
    if(ret){
        LOGE("pthread_create error, ret: %d", ret);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dunn_instrument_crash_local_NativeCrashMonitor_nativeSetup(JNIEnv *env, jobject thiz) {
    // 设置监听信号量回调处理
    installSignalHandlers();
    // 设置额外的栈空间，让信号处理在单独的栈中处理
    installAlternateStack();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_dunn_instrument_crash_local_NativeCrashMonitor_nativeCrash(JNIEnv *env, jclass clazz) {
    int *num = (int*)0x100;// 0x0
    *num = 100;
}