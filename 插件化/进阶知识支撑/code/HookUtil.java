

import android.app.Instrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fc on 2020/5/11
 * Describe: fc
 *
 * @author lenovo
 */
public class HookUtil {

    /**
     * 把自定义的Instrumentation设置给ActivityThread
     * 原理：ActivityThread 持有 Instrumentation 对象
     */
    public static void hookInstrumentation() {

        try {
            //1.通过反射的方式获取当前的ActivityThread对象
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = null;
            try {
                try {
                    currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
                    currentActivityThreadMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                try {
                    Object currentActivityThread = currentActivityThreadMethod.invoke(null);
                    // 2.拿到在ActivityThread类里面的原始mInstrumentation对象
                    Field mInstrumentationField = null;
                    mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
                    mInstrumentationField.setAccessible(true);
                    Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);


                    //3.创建自己的Instrumentation并设置给ActivityThread
                    Instrumentation evilInstrumentation = new InstrumentationProxy(mInstrumentation);
                    mInstrumentationField.set(currentActivityThread, evilInstrumentation);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
