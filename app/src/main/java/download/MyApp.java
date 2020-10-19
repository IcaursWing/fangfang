package download;

import android.app.Application;
import android.content.Context;

/**
 * Created by lws on 2017/3/14.
 */

public class MyApp extends Application {

    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
