
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.fc.h5dispaly.h5display.hotfix.FixTestActivity;

/**
 * Created by fc on 2020/5/11
 * Describe: 继承Instrumentation，用来控制Activity生命周期
 *
 * @author fc
 */
public class InstrumentationProxy extends Instrumentation {
    private Instrumentation targetInstrumentation;

    public InstrumentationProxy(Instrumentation instrumentation) {
        targetInstrumentation = instrumentation;
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        super.callActivityOnResume(activity);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        // 做自己想做的事情，比如加广告 之类的
        /*String action = activity.getIntent().getAction();
        Log.d("InstrumentationProxy", "callActivityOnCreate action : "+ action);
        if (!TextUtils.isEmpty(action) && action.equals("xxxxxxxxxxxxxxxxxx")){
            activity.startActivity(new Intent(activity, FixTestActivity.class));
            activity.finish();
        }*/
        targetInstrumentation.callActivityOnCreate(activity, icicle);
    }
}
