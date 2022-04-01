# lib_Instrument_Crash

## 1.local crash使用
* 1，初始化
```xml
CrashMonitor.getInstance().init(getApplication());
```
* 2，测试native崩溃
```xml
CrashMonitor.getInstance().nativeCrashTest();
```
