## 知识铺垫

SystemServer和zygote是Android Framework里面两大最重要的进程

### 1.zygote
Android是基于Linux系统的，而在Linux中，所有的进程都是由init进程直接或者是间接fork出来的，zygote进程也不例外。

zygote是整个系统创建新进程的核心进程。zygote进程在内部会先启动Dalvik虚拟机，继而加载一些必要的系统资源和系统类，最后进入一种监听状态。

在之后的运作中，当其他系统模块（比如AMS）希望创建新进程时，只需向zygote进程发出请求，zygote进程监听到该请求后，会相应地fork出新的进程，于是这个新进程在初生之时，就先天具有了自己的Dalvik虚拟机以及系统资源。

### 2.SystemServer
SystemServer 也是一个进程，而且是由zygote进程fork出来的。 

ActivityManagerService 、 PackageManagerService 、 WindowManagerService 等系统服务都是在这个进程里面开启的。

### 3.ActivityManagerService
简称AMS，服务端对象，负责系统中所有Activity的生命周期。 在 SystemServer 进程起来的时候初始化

### 4.Launcher
Android系统启动的最后一步是启动一个Home应用程序，这个应用程序用来显示系统中已经安装的应用程序，这个Home应用程序就叫做Launcher。

应用程序Launcher在启动过程中会请求 PackageManagerService 返回系统中已经安装的应用程序的信息，并将这些信息封装成一个快捷图标列表显示在系统屏幕上，这样用户可以通过点击这些快捷图标来启动相应的应用程序。

### 5.Instrumentation 和 ActivityThread
每个Activity都持有 Instrumentation 对象的一个引用，但是整个进程只会存在一个 Instrumentation 对象。这个类就是完成对 Application和Activity初始化和生命周期的工具类。

ActivityThread 就是UI线程。应用的入口类，通过调用main方法，开启消息循环队列。



## 启动流程

 **涉及的三个进程**
- Launcher 进程
- SystemServer 进程
- app所在的进程

### 六个步骤
1.  Launcher响应用户点击，通知AMS （所有组件的启动，切换，调度都由AMS来负责的）

2. AMS 得到Launcher的通知，就需要响应这个通知，主要就是新建一个Task去准备启动Activity，并且告诉Launcher你可以休息了（Paused）；

3. Launcher得到AMS让自己“休息”的消息，那么就直接挂起，并告诉AMS我已经Paused了；

4. **AMS创建新的进程**：AMS知道了Launcher已经挂起之后，检测App是否已经启动了。是，则唤App。否，就要fork一个新的进程。AMS在新进程中创建一个ActivityThread对象，启动其中的main函数。

5. **应用进程初始化**：通过调用上述的ActivityThread的main方法，这是应用程序的入口，在这里开启消息循环队列，这也是主线程默认绑定Looper的原因；

6. **将上述的应用进程信息注册到AMS中，启动启动栈顶页面**

- [x] 可以参考 [App启动的整体流程图](../png/App启动的整体流程图.png)


### 常见问题

#### Launcher响应用户点击的详细过程
- [x] 参考[Launcher响应用户点击详细流程图](../png/Launcher响应用户点击详细流程.png)

####  AMS是如何和具体的Activity通信的
- 让Activity进入Pause状态 ApplicationThreadNative.schedulePauseActivity()
- 让Activity进入Resume状态 ApplicationThreadNative.scheduleResumeActivity()
- [x] 参考[AMS和Activity的通信图](../png/AMS和Activity的通信.png)

#### H类的handleLaunchActivity方法做的啥
- 通过Instrumentation的newActivity方法，创建出来要启动的Activity实例。
- 为这个Activity创建一个上下文Context对象，并与Activity进行关联。
- 通过Instrumentation的callActivityOnCreate方法，执行Activity的onCreate方法，从而启动Activity。

#### 如果我们想在一个应用的Activity启动之前插入自己的Activity，应该在哪个阶段进行hook
   原理：hook的最佳时间是Activity的onCreate方法被调用前，也就是Instrumentation的callActivityOnCreate方法，我们把它拦截掉
         Instrumentation 相关的引用关系: ActivityThread -> Instrumentation
         
   具体步骤：
        1.先反射 currentActivityThread() 方法拿到 ActivityThread 当前实例
        2.反射 mInstrumentation 拿到 Instrumentation 对象
        3.自己创建一个 Instrumentation 的子类,用来代替原来的 Instrumentation
    [参考实现](code/HookUtil.java)



参考文献：

[1] https://blog.csdn.net/pgg_cold/article/details/79491791 

[2] https://www.cnblogs.com/Jax/p/6880604.html