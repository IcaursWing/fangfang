package com.example.main;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fangfang_gai.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

public class IotCamera extends Activity implements CancelAdapt {

    TextView ip1;
    EditText ip2;
    Button camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iot);

        ip1 = findViewById(R.id.et_IOT_Camera1);
        ip2 = findViewById(R.id.et_IOT_Camera2);
        camera = findViewById(R.id.bt_IOT_Camera);


        try {
            WifiManager wifiManager = (WifiManager) IotCamera.this.getApplicationContext().getSystemService(IotCamera.this.getApplicationContext().WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int mip1 = wifiInfo.getIpAddress();
            ip1.setText("本机ip：" + int2ip(mip1));
        } catch (Exception e) {
            e.printStackTrace();
            ip1.setText("获取IP出错鸟!请保证是WIFI,或者请重新打开网络!");
        }


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AndPermission.with(IotCamera.this).runtime().permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {


//                        String path = IotCamera.this.getExternalFilesDir(null).getPath() + File.separator + "images" + File.separator;
//                        File file = new File(path, "test.jpg");
//                        Log.i("test", path);
//
//                        if (!file.getParentFile().exists()) {
//                            file.getParentFile().mkdirs();
//                        }
//                        if (file.exists()) {
//                            file.delete();
//                        }
//
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        // 指定开启系统相机的Action
//                        Uri photoUri = FileProvider.getUriForFile(IotCamera.this, "com.example.fangfang", file);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
//                        startActivity(intent);

                        Intent intent = new Intent(IotCamera.this, IotCamera_On.class);
                        intent.putExtra("IP", ip1.getText().toString().trim());
                        startActivity(intent);


                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(IotCamera.this, "手滑了吧？重点吧！", Toast.LENGTH_SHORT).show();
                    }
                }).start();


            }
        });

    }

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }
}
