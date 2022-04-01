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
## 2.项目引用
* 1，root build.gradle中
```groovy
classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.8'
```
* 2，module build.gradle中
```groovy
apply plugin: 'android-aspectjx'
implementation 'com.github.zhuyidian.lib_Instrument:excel:V1.1.8'
```
## 3.版本更新
* V1.0.7
```
成功运行版本
```
