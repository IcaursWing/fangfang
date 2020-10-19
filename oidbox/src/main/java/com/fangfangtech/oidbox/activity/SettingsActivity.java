package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.dyhdyh.manager.ActivityManager;
import com.fangfangtech.oidbox.R;
import com.hb.dialog.dialog.ConfirmDialog;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.app.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CancelAdapt;

public class SettingsActivity extends Activity implements CancelAdapt, View.OnClickListener {
    @BindView(R.id.titlebar_settings)
    TitleBar titlebar;
    private SharedPreferences sharedPreferences;
    @BindView(R.id.supertextview_exit_settings)
    SuperTextView tvsp_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);


        tvsp_exit.setOnClickListener(this);
        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supertextview_exit_settings:
                final ConfirmDialog confirmDialog = new ConfirmDialog(this);
                confirmDialog.setMsg("是否注销登录？").setLogoImg(R.mipmap.icon_question2);
                confirmDialog.setCancelable(false);
                confirmDialog.setPositiveBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", null);
                        editor.putString("password", null);
                        editor.putString("phone", null);
                        editor.putInt("state", 0);
                        editor.apply();
                        confirmDialog.dismiss();

                        ActivityUtils.startActivity(Login.class);
                        ActivityManager.getInstance().finishAllActivityByWhitelist(Login.class);

                    }
                });
                confirmDialog.setNegativeBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                    }
                });
                confirmDialog.show();
                break;

            default:
                break;
        }
    }
}
