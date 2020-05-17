## 问题引入
我们在Activity中访问资源（图片，字符串，颜色等）只需要getResources()获取一个Resources对象，
然后就可以访问各种资源了，那这些资源到底是怎么被加载的呢？

## 源码追踪

*基于的android-23的源码*

1. context.getResources()  ->  Context.getResources()

2. 根据[Context继承关系图](../png/Context继承关系图.png)得出Context的getResources()肯定实在ContextImpl中实现的。
    ContextImpl.getResources()  -> mResources的赋值的地方是在ContextImpl的构造函数中。
    
    ```
      Resources resources = packageInfo.getResources(mainThread);
      mResources = resources;
    ```
    
3. LoadedApk.getResources()  ->  ActivityThread.getTopLevelResources()  ->  ResourcesManager.getTopLevelResources()

    ```
    看是否有缓存，如果有则返回缓存的 resources，如果没有就重新构建 Resources;
    这个方法内部创建了AssetManager assets = new AssetManager()；
    然后调用assets.addAssetPath添加资源地址；
    最后返回 r = new Resources(assets, dm, config, compatInfo);
    ```

4. 我们平时常用的方法

    ```
     Resources resources = getResources();
     resources.getString();
     resources.getAssets();
     resources.getColor();
     resources.getDrawable()
    ```

    看Resource源码发现，最后都是调用的mAssets变量去加载对应的资源

    

## 结论总结
Apk的资源是通过AssetManager.addAssetPath方法来完成加载的。

### 1. 动态加载资源加载的原理：
通过反射调用AssetManager中的addAssetPath方法，我们可以将一个apk中的资源加载到Resources中


   *DynamicLoadApk中的使用方法*

   ```
    AssetManager assetManager = createAssetManager(dexPath);
    Resources resources = createResources(assetManager);
    
    private AssetManager createAssetManager(String dexPath) {
         try {
               AssetManager assetManager = AssetManager.class.newInstance();
               //反射调用
               Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
               addAssetPath.invoke(assetManager, dexPath);
               return assetManager;
           } catch (Exception e) {
               e.printStackTrace();
               return null;
           }
       }
       
    private Resources createResources(AssetManager assetManager) {
           Resources superRes = mContext.getResources();
           Resources resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
           return resources;
       }
   ```

   ​	

### 2. 热修复的资源修复的原理：
通过反射构建自己的AssetManager对象，然后把调用addAssetPath加载自己的资源，然后把自己构建的AssetManager通过反射设置给mAssets变量，这样下次加载资源就是用的我们AssetManager。











