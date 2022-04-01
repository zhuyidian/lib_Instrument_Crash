package com.dunn.instrument.crash;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Copyright (C), 2020, Tencent
 * Author: darrenzeng
 * Date: 2020/12/23 9:21 PM
 * Description:
 * Version: 1.0.0
 */
public class LifecycleCallback implements Application.ActivityLifecycleCallbacks {
    private static final LifecycleCallback mInstance = new LifecycleCallback();
    private ConcurrentLinkedQueue<IForeBackInterface> callbackList = new ConcurrentLinkedQueue();
    private int foregroundCount = 0;
    private int bufferCount = 0;

    private LifecycleCallback(){

    }

    public static LifecycleCallback getInstance() {
        return mInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        for (IForeBackInterface iForeBackInterface : callbackList) {
            iForeBackInterface.onCreate(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (foregroundCount <= 0) {
            for (IForeBackInterface callback : callbackList) {
                callback.onForeground(activity);
            }
        }
        if (bufferCount < 0) {
            bufferCount++;
        } else {
            foregroundCount++;
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        for (IForeBackInterface callback : callbackList) {
            callback.onStop(activity);
        }
        if (activity.isChangingConfigurations()) {
            bufferCount--;
        } else {
            foregroundCount--;
            if (foregroundCount <= 0) {
                for (IForeBackInterface callback : callbackList) {
                    callback.onBackground(activity);
                }
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        for (IForeBackInterface iForeBackInterface : callbackList) {
            iForeBackInterface.onDestroy(activity);
        }
    }

    public void register(IForeBackInterface foreBack) {
        callbackList.add(foreBack);
    }
}
