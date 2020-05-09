## 知识铺垫

SystemServer和zygote是Android Framework里面两大最重要的进程

### 1.zygote
Android是基于Linux系统的，而在Linux中，所有的进程都是由init进程直接或者是间接fork出来的，zygote进程也不例外。

zygote是整个系统创建新进程的核心进程。zygote进程在内部会先启动Dalvik虚拟机，继而加载一些必要的系统资源和系统类，最后进入一种监听状态。
在之后的运作中，当其他系统模块（比如AMS）希望创建新进程时，只需向zygote进程发出请求，zygote进程监听到该请求后，会相应地fork出新的进程，于是这个新进程在初生之时，就先天具有了自己的Dalvik虚拟机以及系统资源。

### 2.SystemServer
SystemServer 也是一个进程，而且是由zygote进程fork出来的。 ActivityManagerService 、 PackageManagerService 、 WindowManagerService 等系统服务都是在这个进程里面开启的

### 3.ActivityManagerService
简称AMS，服务端对象，负责系统中所有Activity的生命周期。 在 SystemServer 进程起来的时候初始化

### 4.Launcher
Android系统启动的最后一步是启动一个Home应用程序，这个应用程序用来显示系统中已经安装的应用程序，这个Home应用程序就叫做Launcher。
应用程序Launcher在启动过程中会请求 PackageManagerService 返回系统中已经安装的应用程序的信息，并将这些信息封装成一个快捷图标列表显示在系统屏幕上，这样用户可以通过点击这些快捷图标来启动相应的应用程序。

### 5.Instrumentation 和 ActivityThread
每个Activity都持有 Instrumentation 对象的一个引用，但是整个进程只会存在一个 Instrumentation 对象。这个类就是完成对 Application和Activity初始化和生命周期的工具类。
ActivityThread 就是UI线程。应用的入口类，通过调用main方法，开启消息循环队列。