package com.dunn.instrument.crash;

import android.app.Application;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;

import com.dunn.instrument.crashlocal.CrashHandlerListener;
import com.dunn.instrument.crashlocal.NativeCrashMonitor;

public class CrashMonitor implements MessageQueue.IdleHandler {
    private static final CrashMonitor instance = new CrashMonitor();
    private Application mApplication;

    private CrashMonitor(){
    }

    public static CrashMonitor getInstance() {
        return instance;
    }

    /**
     * 放在主页初始化，这样不会清理掉主页activity，只清理掉其它activity
     * @param application
     */
    public void init(Application application){
        Looper.myQueue().addIdleHandler(this);
        this.mApplication = application;

        NativeCrashMonitor nativeCrashMonitor = new NativeCrashMonitor();
        nativeCrashMonitor.init(new CrashHandlerListener() {
            @Override
            public void onCrash(String threadName, Error error) {

            }
        });
    }

    public void nativeCrashTest(){
        NativeCrashMonitor.nativeCrash();
    }

    @Override
    public boolean queueIdle() {
        // 监听所有的 Activity
        mApplication.registerActivityLifecycleCallbacks(LifecycleCallback.getInstance());
        ActivityManager activityManager = new ActivityManager();
        final ProcessFinisher processFinisher = new ProcessFinisher(mApplication, true, activityManager);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                // 如果大家想把这套集成到自己的项目中，一定要配合 bugly
                // 先走 bugly 逻辑然后再走我们这里
                // 少了没写上传崩溃信息到服务器
                // 如果自己写一套，这在里应该先上报到自己的后台服务器
                e.printStackTrace();
                // 1. 把所有的状态信息清空，service ，activity 这些尽量都干掉
                // 2. 然后退到首页，但是不闪退到桌面 （记录所有的 activity）
                processFinisher.finishLastActivity(t);
                // 3. 也不触发系统的检测（提示卸载 app），不走系统的默认逻辑
                processFinisher.endApplication();
            }
        });
        return false;
    }
}
