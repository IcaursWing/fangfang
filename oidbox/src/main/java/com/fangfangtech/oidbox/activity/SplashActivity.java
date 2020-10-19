package com.fangfangtech.oidbox.activity;

import android.content.Context;
import android.view.KeyEvent;

import com.fangfangtech.oidbox.R;
import com.xuexiang.xui.utils.KeyboardUtils;
import com.xuexiang.xui.widget.activity.BaseSplashActivity;
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

        initSplashView(R.mipmap.splashsctivityfang);
        startSplash(true);

    }

    @Override
    public void onSplashFinished() {

        if (getSharedPreferences("account", Context.MODE_PRIVATE).getInt("state", 0) == 1) {
            ActivityUtils.startActivity(MainActivity.class);
        } else {
            ActivityUtils.startActivity(Login.class);
        }
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
