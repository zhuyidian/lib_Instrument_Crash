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

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.os.DropBoxManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

public class SystemServices {

    private SystemServices() {
    }

    /**
     * 获取电话服务(设备信息、sim卡信息以及 网络信息).
     *
     * @param context context上下文
     * @return 返回电话管理器TelephonyManager
     * @throws ServiceNotReachedException 自定义异常，获取服务可能会发生异常
     */
    @NonNull
    public static TelephonyManager getTelephonyManager(@NonNull Context context) throws ServiceNotReachedException {
        return (TelephonyManager) getService(context, Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取系统日志收集器服务.
     *
     * @param context context上下文
     * @return 返回系统日志服务
     * @throws ServiceNotReachedException 自定义异常，获取服务可能会发生异常
     */
    @NonNull
    public static DropBoxManager getDropBoxManager(@NonNull Context context) throws ServiceNotReachedException {
        return (DropBoxManager) getService(context, Context.DROPBOX_SERVICE);
    }

    /**
     * 获取通知服务,用于管理和运行所有通知.
     *
     * @param context context上下文
     * @return 返回通知服务
     * @throws ServiceNotReachedException 自定义异常，获取服务可能会发生异常
     */
    @NonNull
    public static NotificationManager getNotificationManager(@NonNull Context context) throws ServiceNotReachedException {
        return (NotificationManager) getService(context, Context.NOTIFICATION_SERVICE);
    }

    /**
     * 获取ActivityManager服务,对Activity管理、运行时功能管理和运行时数据结构的封装，进程(Process)、应用程序/包、服务(Service)、任务(Task)信息.
     *
     * @param context context context上下文
     * @return 返回Activity管理服务
     * @throws ServiceNotReachedException 自定义异常，获取服务可能会发生异常
     */
    @NonNull
    public static ActivityManager getActivityManager(@NonNull Context context) throws ServiceNotReachedException {
        return (ActivityManager) getService(context, Context.ACTIVITY_SERVICE);
    }

    /**
     * 获取服务.
     *
     * @param context context上下文
     * @param id 需要开启的服务id
     * @return 返回相应服务
     * @throws ServiceNotReachedException 自定义异常，获取服务可能会发生异常
     */
    @NonNull
    private static Object getService(@NonNull Context context, @NonNull String id) throws ServiceNotReachedException {
        final Object service = context.getSystemService(id);
        if (service == null) {
            throw new ServiceNotReachedException("Unable to load SystemService " + id);
        }
        return service;
    }

    static class ServiceNotReachedException extends Exception {
        ServiceNotReachedException(String message) {
            super(message);
        }
    }
}
