package com.fangfangtech.oidbox.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fangfangtech.oidbox.R;
import com.hb.dialog.dialog.ConfirmDialog;
import com.hb.dialog.dialog.LoadingDialog;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xutil.app.ActivityUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;
import com.yanzhenjie.nohttp.tools.BasicMultiValueMap;

import me.jessyan.autosize.internal.CancelAdapt;

public class Login extends Activity implements CancelAdapt {

    EditText et_username, et_password;
    Button bt_login;
    TextView tv_toRegister;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        NoHttp.initialize(this);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!et_username.getText().toString().equals("") && !et_password.getText().toString().equals("")) {

                    final LoadingDialog loadingDialog = new LoadingDialog(Login.this);
                    loadingDialog.setMessage("登录中...");
                    loadingDialog.setCancelable(true);
                    loadingDialog.show();

                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            StringRequest request = new StringRequest("http://121.36.30.71:8888/OidBox/applogin", RequestMethod.POST);
                            request.set("phone", et_username.getText().toString());
                            request.set("password", et_password.getText().toString());

                            final Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                            if (response.isSucceed()) {
                                loadingDialog.dismiss();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String state = response.getHeaders().getValue("state", 0);

                                        if (state.equals("0")) {
                                            Toast.makeText(Login.this, "手机号未注册！", Toast.LENGTH_LONG).show();

                                        } else if (state.equals("-1")) {
                                            Toast.makeText(Login.this, "登录密码错误！", Toast.LENGTH_LONG).show();
                                        } else {
                                            final ConfirmDialog confirmDialog = new ConfirmDialog(Login.this);

                                            BasicMultiValueMap<String, String> info = response.getHeaders();

                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            editor.putString("username", info.getValue("username", 0));
                                            editor.putString("password", info.getValue("password", 0));
                                            editor.putString("phone", info.getValue("phone", 0));
                                            editor.putInt("state", 1);
                                            editor.apply();

                                            new MaterialDialog.Builder(Login.this).iconRes(R.drawable.ic_checked_right).title("登录成功").positiveText("确定").cancelable(false).onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    dialog.dismiss();
                                                    ActivityUtils.startActivity(MainActivity.class);
                                                    finish();
                                                }
                                            }).show();


                                        }

                                    }
                                });


                            } else {
                                Exception e = response.getException();
                                loadingDialog.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Login.this, "登录失败，请检查网络！", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }

                        }
                    };
                    thread.start();


                } else {
                    Toast.makeText(Login.this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
                }


            }
        });

        tv_toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

    }

    private void Init() {
        et_username = findViewById(R.id.et_login_phone);
        et_password = findViewById(R.id.et_login_password);
        bt_login = findViewById(R.id.btn_login_commit);
        tv_toRegister = findViewById(R.id.tv_login_forget);

    }
}
