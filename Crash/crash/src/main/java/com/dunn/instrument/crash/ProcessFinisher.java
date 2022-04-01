/*
 * Copyright (C) 2019 THL A29 Limited, a Tencent company. All rights reserved.
 * DO NOT ALTER OR REMOVE NOTICES OR THIS FILE HEADER.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dunn.instrument.crash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

public class ProcessFinisher {
    private static final String LOG_TAG = "ProcessFinisher";
    private final Context context;
    private final ActivityManager lastActivityManager;
    private final Boolean stopServiceOnCrash;

    /**
     * 构造方法.
     *
     * @param context context上下文
     * @param stopServiceOnCrash 控制发生crash时是否杀掉服务
     * @param lastActivityManager activity管理器，用于发生crash时销毁所有未销毁的activity
     */
    public ProcessFinisher(@NonNull Context context, @NonNull Boolean stopServiceOnCrash, @NonNull ActivityManager lastActivityManager) {
        this.context = context;
        this.lastActivityManager = lastActivityManager;
        this.stopServiceOnCrash = stopServiceOnCrash;
    }

    /**
     * 杀掉服务和进程.
     */
    public void endApplication() {
        stopServices();
        killProcessAndExit();
    }

    /**
     * 结束所有还未销毁的activity.
     *
     * @param uncaughtExceptionThread uncaughtExceptionThread处理器
     */
    public void finishLastActivity(@Nullable Thread uncaughtExceptionThread) {
        Log.i(LOG_TAG, "Finishing activities prior to killing the Process");
        boolean wait = false;
        for (final Activity activity : lastActivityManager.getLastActivities()) {
            final boolean isMainThread = uncaughtExceptionThread == activity.getMainLooper().getThread();
            final Runnable finisher = new Runnable() {
                @Override
                public void run() {
                    activity.finish();
                    Log.d(LOG_TAG, "Finished " + activity.getClass());
                }
            };

            if (isMainThread) {
                finisher.run();
            } else {
                // A crashed activity won't continue its lifecycle. So we only wait if something else crashed
                wait = true;
                activity.runOnUiThread(finisher);
            }
        }
        if (wait) {
            final int timeOut = 100;
            lastActivityManager.waitForAllActivitiesDestroy(timeOut);
        }
        lastActivityManager.clearLastActivities();
    }

    /**
     * 判断crash时Activity的前后台状态
     *
     * @return true为前台，false为后台
     */
    public boolean isForeground() {
        return lastActivityManager.isForeground();
    }

    /**
     * 关闭所有服务.
     */
    private void stopServices() {
        if (stopServiceOnCrash) {
            Log.i(LOG_TAG, "Stopping all active services.");
            try {
                final android.app.ActivityManager activityManager = SystemServices.getActivityManager(context);
                final List<android.app.ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
                final int pid = Process.myPid();
                if (runningServices == null) {
                    return;
                }
                for (android.app.ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
                    // kill service. 注意排除上报service
                    // if (serviceInfo.pid == pid
                    //         && !LegacySenderService.class.getName().equals(serviceInfo.service.getClassName())
                    //         && !JobSenderService.class.getName().equals(serviceInfo.service.getClassName())) {
                    if (serviceInfo.pid == pid) {
                        try {
                            final Intent intent = new Intent();
                            intent.setComponent(serviceInfo.service);
                            context.stopService(intent);
                        } catch (SecurityException e) {
                            Log.e(LOG_TAG, "Unable to stop Service " + serviceInfo.service.getClassName() + ". Permission denied.");
                        }
                    }
                }
            } catch (SystemServices.ServiceNotReachedException e) {
                Log.e(LOG_TAG, "Unable to stop services", e);
            }
        }
    }

    /**
     * 杀掉进程.
     */
    private void killProcessAndExit() {
        Log.w(LOG_TAG, "kill process and exit.");
        final int exitType = 10;
        Process.killProcess(Process.myPid());
        System.exit(exitType);
    }
}
