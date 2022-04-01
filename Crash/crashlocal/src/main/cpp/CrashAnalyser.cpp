//
// Created by darrenzeng on 2021/3/7.
//

#include "CrashAnalyser.h"

// 锁的条件变量
pthread_cond_t signalCond;
pthread_mutex_t signalLock;
pthread_cond_t exceptionCond;
pthread_mutex_t exceptionLock;

bool isSignalCaught = false;
native_handler_context *handlerContext;

_Unwind_Reason_Code unwind_callback(struct _Unwind_Context *context, void *arg) {
    native_handler_context *const s = static_cast<native_handler_context *const>(arg);
    const uintptr_t pc = _Unwind_GetIP(context);
    if (pc != 0x0) {
        // 把 pc 值保存到 native_handler_context
        s->frames[s->frame_size++] = pc;
        LOGE("pc = %p", pc);
    }
    if (s->frame_size == BACKTRACE_FRAMES_MAX) {
        return _URC_END_OF_STACK;
    } else {
        return _URC_NO_REASON;
    }
}

void initCondition() {
    handlerContext = (native_handler_context *) malloc(sizeof(native_handler_context_struct));
    pthread_mutex_init(&signalLock, NULL);
    pthread_mutex_init(&exceptionLock, NULL);
    pthread_cond_init(&signalCond, NULL);
    pthread_cond_init(&exceptionCond, NULL);
}

void *threadCrashMonitor(void *argv) {
    // 等待有 crash 捕获的监听
    JNIBridge *jniBridge = static_cast<JNIBridge *>(argv);
    while (true) {
        // 等待被唤醒
        waitForSignal();
        // 解析异常信息，堆栈
        analysisNativeException();
        // 抛给 java 层
        jniBridge->throwException2Java(handlerContext);
    }
    int status = 1;
    return &status;
}

void waitForSignal() {
    pthread_mutex_lock(&signalLock);
    LOGD("waitForSignal start.");
    pthread_cond_wait(&signalCond, &signalLock);
    LOGD("waitForSignal finish.");
    pthread_mutex_unlock(&signalLock);
}

// 唤醒等待
void notifyCaughtSignal(int code, siginfo_t *si, void *sc) {
    // 创建一个结构体来保存这几个参数
    copyInfo2Context(code, si, sc);
    pthread_mutex_lock(&signalLock);
    LOGD("notifyCaughtSignal");
    pthread_cond_signal(&signalCond);
    pthread_mutex_unlock(&signalLock);
}

void copyInfo2Context(int code, siginfo_t *si, void *sc) {
    handlerContext->code = code;
    handlerContext->si = si;
    handlerContext->sc = sc;
    // 解析进程与线程 processName，threadName
    // 如果有二次闪退，非常尴尬，可以思考？
    handlerContext->pid = getpid();
    handlerContext->tid = gettid();
    handlerContext->processName = getProcessName(handlerContext->pid);
    if (handlerContext->pid == handlerContext->tid) {
        handlerContext->threadName = "main";
    } else {
        handlerContext->threadName = getThreadName(handlerContext->tid);
    }
    // native crash 堆栈解析留着下次讲，作业
    handlerContext->frame_size = 0;
    _Unwind_Backtrace(unwind_callback, handlerContext);
}

void analysisNativeException() {
    // 解析， 先来讲简单的，native 解析留着下次讲
    // 原因，空指针
    const char *posixDesc = desc_sig(handlerContext->si->si_signo, handlerContext->si->si_code);
    LOGD("posixDesc -> %s", posixDesc);
    LOGD("signal -> %d", handlerContext->si->si_signo);
    LOGD("address -> %p", handlerContext->si->si_addr);
    LOGD("processName -> %s", handlerContext->processName);
    LOGD("threadName -> %s", handlerContext->threadName);
    LOGD("pid -> %d", handlerContext->pid);
    LOGD("tid -> %d", handlerContext->tid);
    // 技术真正高级，可以带项目
}

