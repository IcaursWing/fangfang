/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ble.DeviceListAdapter;
import com.example.ble.RoboSoulListAdapter;
import com.example.fangfang_gai.R;
import com.example.jdy_touchuang.AV_Stick;
import com.example.jdy_touchuang.jdy_Activity;
import com.example.jdy_touchuang.jdy_switch_Activity;
import com.example.jdy_touchuang.shengjiangji;
import com.example.jdy_type.Get_type;
import com.example.sensor.jdy_ibeacon_Activity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;
import java.util.Timer;
import java.util.UUID;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class RoboSoulConnect extends Activity implements OnClickListener {
    // private LeDeviceListAdapter mLeDeviceListAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_GPS = 2;
    private static final int REQUEST_Open_Blue = 3;
    private static final int REQUEST_CODE_SCAN = 4;
    private static final int REQUEST_CONNECT_DEVICE = 5;
    private static final String DECODED_CONTENT_KEY = "codedContent";

    public String target_mac;
    public String server_uuid;
    public String target_uuid;
    public String Blue_Result;
    private UUID MY_UUID;

    Get_type mGet_type;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 5000;

    private RoboSoulListAdapter mDevListAdapter;
    ToggleButton tb_on_off;
    TextView btn_searchDev;
    Button btn_aboutUs;
    ListView lv_bleList;

    byte dev_bid;

    Timer timer;

    String APP_VERTION = "1002";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jdy_activity_main);
        this.getWindow().getDecorView().setBackgroundResource(R.drawable.startpage);

        this.setTitle("Robo机器人连接");
        // getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
            // 那么将会收到RESULT_OK的结果，
            // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, 1);
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
            // mBluetoothAdapter.enable();
            // mBluetoothAdapter.disable();//关闭蓝牙
        }

        lv_bleList = (ListView) findViewById(R.id.lv_bleList);


        mDevListAdapter = new RoboSoulListAdapter(mBluetoothAdapter, RoboSoulConnect.this);
        dev_bid = (byte) 0x88;// 88 是JDY厂家VID码
        mDevListAdapter.set_vid(dev_bid);// 用于识别自家的VID相同的设备，只有模块的VID与APP的VID相同才会被搜索得到
        lv_bleList.setAdapter(mDevListAdapter.init_adapter());


        lv_bleList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDevListAdapter.get_count() > 0) {

                    Byte vid_byte = mDevListAdapter.get_vid(position);// 返回136表示是JDY厂家模块


                    if (vid_byte == dev_bid)// JDY厂家VID为0X88，
                        // 用户的APP不想搜索到其它厂家的JDY-08模块的话，可以设备一下
                        // APP的VID，此时模块也需要设置，
                        // 模块的VID与厂家APP的VID要一样，APP才可以搜索得到模块VID与APP一样的设备
                        switch (mDevListAdapter.get_item_type(position)) {
                            case JDY:// //为标准透传模块
                            {
                                BluetoothDevice device1 = mDevListAdapter.get_item_dev(position);
                                if (device1 == null)
                                    return;
                                Intent intent1 = new Intent(RoboSoulConnect.this, RoboSoulControl.class);
                                ;
                                intent1.putExtra(jdy_Activity.EXTRAS_DEVICE_NAME, device1.getName());
                                intent1.putExtra(jdy_Activity.EXTRAS_DEVICE_ADDRESS, device1.getAddress());
                                // if (mScanning)
                                {
                                    mDevListAdapter.scan_jdy_ble(false);
                                    mScanning = false;
                                }
                                startActivity(intent1);
                            }
                            break;

                            default:
                                break;
                        }

                }
            }
        });

        Message message = new Message();
        message.what = 100;
        handler.sendMessage(message);

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 100) {
            }

            super.handleMessage(msg);
        }

        private void setTitle(String hdf) {

        }

        ;
    };

    public static boolean turnOnBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.enable();
        }
        return false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 0:
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        menu.findItem(R.id.scan_menu_set).setVisible(false);
        menu.findItem(R.id.scan_menu_id).setActionView(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_menu_set:

                AndPermission.with(this).runtime().permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Intent intent = new Intent(RoboSoulConnect.this, com.yzq.zxinglibrary.android.CaptureActivity.class);
                        /*
                         * ZxingConfig是配置类可以设置是否显示底部布局，闪光灯，相册， 是否播放提示音 震动
                         * 设置扫描框颜色等 也可以不传这个参数
                         */
                        ZxingConfig config = new ZxingConfig();
                        // config.setPlayBeep(false);//是否播放扫描声音 默认为true
                        // config.setShake(false);//是否震动 默认为true
                        // config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                        // config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色
                        // 默认为白色
                        // config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色
                        // 默认无色
                        // config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色
                        // 默认白色
                        config.setFullScreenScan(false);// 是否全屏扫描 默认为true
                        // 设为false则只会在扫描框中扫描
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                    }

                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(RoboSoulConnect.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }

                }).start();
                break;
            case R.id.scan_menu_set1: {
                mDevListAdapter.clear();
                scanLeDevice(true);
            }
            break;
        }
        return true;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mDevListAdapter.scan_jdy_ble(false);
                    // invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mDevListAdapter.scan_jdy_ble(true);
        } else {
            mScanning = false;
            mDevListAdapter.scan_jdy_ble(false);
        }
        // invalidateOptionsMenu();
    }


    @Override
    protected void onResume() {// 打开APP时扫描设备
        super.onResume();
        scanLeDevice(true);

        // mDevListAdapter.scan_jdy_ble( false );
    }

    @Override
    protected void onPause() {// 停止扫描
        super.onPause();
        // scanLeDevice(false);
        mDevListAdapter.scan_jdy_ble(false);
    }


    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {

        }

        if (requestCode == REQUEST_ENABLE_GPS) {

        }

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Blue_Result = data.getStringExtra(Constant.CODED_CONTENT);
                Toast.makeText(getApplicationContext(), "扫码成功！", Toast.LENGTH_SHORT).show();
                // qrCoded.setText("解码结果： \n" + content);
                String[] splitstr = Blue_Result.split("#");
                target_mac = splitstr[0];
                server_uuid = splitstr[1];
                target_uuid = splitstr[2];
                UUID.fromString(server_uuid);
                Log.i("test", target_mac + " " + server_uuid + " " + target_uuid);

                Intent intent1 = new Intent(RoboSoulConnect.this, ControlActivityTest.class);
                ;
                intent1.putExtra(jdy_Activity.EXTRAS_DEVICE_NAME, target_mac);
                intent1.putExtra(jdy_Activity.EXTRAS_DEVICE_ADDRESS, target_mac);
                // if (mScanning)
                {
                    mDevListAdapter.scan_jdy_ble(false);
                    ;
                    mScanning = false;
                }
                startActivity(intent1);

            }
        }

        if (requestCode == REQUEST_CONNECT_DEVICE && resultCode == Activity.RESULT_OK) {
        }
    }

}