package com.example.oidboxgame;

import android.view.KeyEvent;

import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityUtils;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 渐近式启动页
 *
 * @author xuexiang
 * @since 2018/11/27 下午5:24
 */
public class SplashActivity extends BaseSplashActivity implements CancelAdapt {


    @Override
    protected long getSplashDurationMillis() {
        return 3000;
    }

    @Override
    public void onCreateActivity() {

        initSplashView(R.drawable.splashsctivityfang);
        startSplash(true);

        XUI.init(getApplication()); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        XUtil.init(getApplication());
//        XUI.initTheme(this);

        XHttpSDK.init(getApplication());   //初始化网络请求框架，必须首先执行
        XHttpSDK.debug("XHttp");  //需要调试的时候执行
    }

    @Override
    public void onSplashFinished() {

        ActivityUtils.startActivity(MainActivity.class);
        finish();
    }

    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return KeyboardUtils.onDisableBackKeyDown(keyCode) && super.onKeyDown(keyCode, event);
    }

}
