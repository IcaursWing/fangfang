package com.fangfangtech.oidbox.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.fangfangtech.oidbox.R;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.util.Random;

import me.jessyan.autosize.internal.CancelAdapt;

public class Register extends Activity implements CancelAdapt {
    public static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    EditText et_username, et_phone, et_password1, et_password2, et_verify;
    SuperButton bt_register, bt_verify;
    CountDownButtonHelper Hbt_verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
        NoHttp.initialize(this);
        BleManager.getInstance().init(getApplication());


        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!et_username.getText().toString().equals("") && !et_phone.getText().toString().equals("") && !et_verify.getText().toString().equals("") && !et_password1.getText().toString().equals("") && !et_password2.getText().toString().equals("")) {

                    if (et_password1.getText().toString().equals(et_password2.getText().toString())) {

                        MaterialDialog materialDialog = new MaterialDialog.Builder(Register.this).content("注册中，请稍后...").progress(true, 0).progressIndeterminateStyle(false).cancelable(false).show();


                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                StringRequest request = new StringRequest("http://121.36.30.71:8888/OidBox/appregister", RequestMethod.POST);
                                request.set("username", et_username.getText().toString());
                                request.set("phone", et_phone.getText().toString());
                                request.set("password", et_password1.getText().toString());

                                final Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                                if (response.isSucceed()) {

                                    materialDialog.dismiss();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (response.getHeaders().getValue("state", 0).equals("1")) {

                                                new MaterialDialog.Builder(Register.this).iconRes(R.drawable.ic_checked_right).title("注册成功").content("返回登录！").positiveText("确定").cancelable(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                        finish();
                                                    }
                                                }).show();

                                            } else if (response.getHeaders().getValue("state", 0).equals("2")) {

                                                new MaterialDialog.Builder(Register.this).iconRes(R.drawable.ic_pic_delete).title("注册失败").content("用户名已注册！").positiveText("确定").cancelable(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();


                                            } else {

                                                new MaterialDialog.Builder(Register.this).iconRes(R.drawable.ic_pic_delete).title("注册失败").content("请重新提交注册信息！").positiveText("确定").cancelable(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();


                                            }
                                        }
                                    });


                                } else {
                                    // 请求失败，拿到错误：
                                    Exception e = response.getException();
                                    e.printStackTrace();
                                    materialDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Register.this, "注册失败，请检查网络！", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        };
                        thread.start();


                    } else {
                        Toast.makeText(Register.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Register.this, "昵称、手机号、验证码、密码不能为空！", Toast.LENGTH_SHORT).show();
                }


            }
        });


        bt_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hbt_verify.start();
                Random random = new Random();
                String verify = String.valueOf(random.nextInt(999999) % (999999 - 100000 + 1) + 100000);
                et_verify.setText(verify);
            }
        });
    }

    private void Init() {
        et_username = findViewById(R.id.et_register_username);
        et_phone = findViewById(R.id.et_register_phone);
        et_password1 = findViewById(R.id.et_register_password1);
        et_password2 = findViewById(R.id.et_register_password2);
        et_verify = findViewById(R.id.et_register_verify);
        bt_register = findViewById(R.id.btn_register_commit);
        bt_verify = findViewById(R.id.bt_register_verify);
        Hbt_verify = new CountDownButtonHelper(bt_verify, 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hbt_verify.cancel();
        BleManager.getInstance().disconnectAllDevice();
    }


}
