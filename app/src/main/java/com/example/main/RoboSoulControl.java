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
import android.app.Dialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ble.BluetoothLeService;
import com.example.fangfang_gai.R;
import com.google.gson.Gson;
import com.hb.dialog.myDialog.ActionSheetDialog;


import java.io.UnsupportedEncodingException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import ai.olami.android.example.Config;
import ai.olami.cloudService.APIConfiguration;
import ai.olami.cloudService.APIResponse;
import ai.olami.cloudService.TextRecognizer;
import ai.olami.nli.NLIResult;
import ai.olami.util.GsonFactory;
import me.jessyan.autosize.internal.CancelAdapt;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public class RoboSoulControl extends Activity implements SeekBar.OnSeekBarChangeListener, CancelAdapt {
    private final static String TAG = RoboSoulControl.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private StringBuffer sbValues;

    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    boolean connect_status_bit = false;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Handler mHandler;
    String ReceivedDATA;
    int ReceivedKey;
    int ReceivedValue;

    private Gson mJsonDump;
    private TextRecognizer mRecognizer;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;

    private int i = 0;
    private int TIME = 1000;

    int tx_count = 0;
    int connect_count = 0;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                // mConnected = true;

                connect_status_bit = true;
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;

                updateConnectionState(R.string.disconnected);
                connect_status_bit = false;
                invalidateOptionsMenu();
                clearUI();

                if (connect_count == 0) {
                    connect_count = 1;
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) // 接收FFE1串口透传数据通道数据
            {

                displayData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE1.equals(action)) // 接收FFE2功能配置返回的数据
            {

            }
        }
    };


    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

            return false;
        }
    };

    private void clearUI() {
        // mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
    }

    int robot_type = 1;
    int pressed_action = 1;


    ImageButton button_stand, button_bow, button_fly, button_squat, button_lie, button_sit, button_shake;
    ImageButton button_dance, button_bluetooth, button_more;
    Switch mSwitch;
    ImageView ImageView_man;
    ImageButton button_up, button_down, button_left, button_right;

    Boolean isup = false;
    Boolean isdown = false;
    Boolean isleft = false;
    Boolean isright = false;

    Menu mMenu;
    TextView bat_p;


    boolean pass_en = false;

    Timer timer = new Timer();


    boolean send_hex = true;// HEX格式发送数据 透传
    boolean rx_hex = true;// HEX格式接收数据 透传

    public void delay(int ms) {
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robosoulcontrol);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        button_bluetooth = findViewById(R.id.robo_bluetooth);
        button_dance = findViewById(R.id.robo_dance_button);
        button_stand = findViewById(R.id.robo_stand_button);
        button_bow = findViewById(R.id.robo_bow_button);
        button_fly = findViewById(R.id.robo_fly_button);
        button_squat = findViewById(R.id.robo_squat_button);
        button_lie = findViewById(R.id.robo_lie_button);
        button_sit = findViewById(R.id.robo_sit_button);
        button_shake = findViewById(R.id.robo_shake_button);
        mSwitch = findViewById(R.id.robo_man_switch);
        ImageView_man = findViewById(R.id.robo_man);
        button_more = findViewById(R.id.robo_more_button);
        button_up = findViewById(R.id.robo_up);
        button_down = findViewById(R.id.robo_down);
        button_left = findViewById(R.id.robo_left);
        button_right = findViewById(R.id.robo_right);


        button_bluetooth.setOnClickListener(imagelistener);
        button_bluetooth.setOnTouchListener(touchlistener);
        button_dance.setOnClickListener(imagelistener);
        button_dance.setOnTouchListener(touchlistener);
        button_stand.setOnClickListener(imagelistener);
        button_stand.setOnTouchListener(touchlistener);
        button_bow.setOnClickListener(imagelistener);
        button_bow.setOnTouchListener(touchlistener);
        button_fly.setOnClickListener(imagelistener);
        button_fly.setOnTouchListener(touchlistener);
        button_squat.setOnClickListener(imagelistener);
        button_squat.setOnTouchListener(touchlistener);
        button_lie.setOnClickListener(imagelistener);
        button_lie.setOnTouchListener(touchlistener);
        button_sit.setOnClickListener(imagelistener);
        button_sit.setOnTouchListener(touchlistener);
        button_shake.setOnClickListener(imagelistener);
        button_shake.setOnTouchListener(touchlistener);
        button_more.setOnClickListener(imagelistener);
        button_more.setOnTouchListener(touchlistener);

        button_up.setOnClickListener(imagelistener);
        button_up.setOnTouchListener(touchlistener);
        button_down.setOnClickListener(imagelistener);
        button_down.setOnTouchListener(touchlistener);
        button_left.setOnClickListener(imagelistener);
        button_left.setOnTouchListener(touchlistener);
        button_right.setOnClickListener(imagelistener);
        button_right.setOnTouchListener(touchlistener);


        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSwitch.isChecked()) {
                    ImageView_man.setImageResource(R.drawable.men);
                    mSwitch.setText("多台控制");
                } else {
                    ImageView_man.setImageResource(R.drawable.man);
                    mSwitch.setText("单台控制");
                }
            }
        });


        //bat_p = (TextView) findViewById(R.id.bat_precent);


        sbValues = new StringBuffer();

        mJsonDump = GsonFactory.getDebugGson(false);

        // * Step 1: Configure your key and localize option.
        APIConfiguration config = new APIConfiguration(Config.getAppKey(), Config.getAppSecret(), Config.getLocalizeOption());

        // * Step 2: Create the text recognizer.
        mRecognizer = new TextRecognizer(config);
        mRecognizer.setSdkType("android");

        // * Optional steps: Setup some other configurations.
        mRecognizer.setEndUserIdentifier("Someone");
        mRecognizer.setTimeout(10000);

        mHandler = new Handler();

        // timer.schedule(task, 3000, 3000); // 1s后执行task,经过1s再次执行
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {

            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        boolean sg;

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        sg = bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        // getActionBar().setTitle( "="+BluetoothLeService );
        // mDataField.setText("="+sg );
        updateConnectionState(R.string.connecting);

        get_pass();

    }

    public void enable_pass() {
        mBluetoothLeService.Delay_ms(100);
        mBluetoothLeService.set_APP_PASSWORD(password_value);
    }

    String password_value = "123456";

    public void get_pass() {
        password_value = getSharedPreference("DEV_PASSWORD_LEY_1000");
        if (password_value != null || password_value != "") {
            if (password_value.length() == 6) {

            } else
                password_value = "123456";
        } else
            password_value = "123456";

    }

    // ---------------------------------------------------------------------------------应用于存储选择TAB的列表index
    public String getSharedPreference(String key) {
        // 同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
        SharedPreferences sharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String name = sharedPreferences.getString(key, "");
        return name;
    }

    public void setSharedPreference(String key, String values) {
        // 实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = getSharedPreferences("test", Activity.MODE_PRIVATE);
        // 实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        // 用putString的方法保存数据
        editor.putString(key, values);
        // 提交当前数据
        editor.commit();
        // 使用toast信息提示框提示成功写入数据
        // Toast.makeText(this, values ,
        // Toast.LENGTH_LONG).show();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                // tvShow.setText(Integer.toString(i++));
                // scanLeDevice(true);
                Boolean ble = mBluetoothLeService != null;
                Log.i("test", msg.what + " " + ble + " " + mConnected);
                if (mBluetoothLeService != null) {
                    if (mConnected == false) {
                        updateConnectionState(R.string.connecting);
                        final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                        Log.d(TAG, "Connect request result=" + result);
                    }
                }
            }
            if (msg.what == 2) {
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enable_JDY_ble(0);
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enable_JDY_ble(0);
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mBluetoothLeService.enable_JDY_ble(1);
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] WriteBytes = new byte[2];
                WriteBytes[0] = (byte) 0xE7;
                WriteBytes[1] = (byte) 0xf6;
                mBluetoothLeService.function_data(WriteBytes);// 发送读取所有IO状态
            }
            super.handleMessage(msg);

            switch (msg.what) {
                case 11:
                    mBluetoothLeService.txxx(actionCmdFromID(1), false);
                    break;

                case 12:
                    mBluetoothLeService.txxx(actionCmdFromID(11), false);
                    break;

                case 13:
                    mBluetoothLeService.txxx(actionCmdFromID(2), false);
                    break;

                case 14:
                    mBluetoothLeService.txxx(actionCmdFromID(12), false);
                    break;

                default:
                    break;
            }
        }
    };

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };

    OnClickListener OnClickListener_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mConnected) {
                byte bit = (byte) 0x00;
                if (v.getId() == R.id.toggleButton1) {
                    bit = (byte) 0xf1;
                } else if (v.getId() == R.id.toggleButton2) {
                    bit = (byte) 0xf2;
                } else if (v.getId() == R.id.toggleButton3) {
                    bit = (byte) 0xf3;
                } else if (v.getId() == R.id.toggleButton4) {
                    bit = (byte) 0xf4;

                    // byte[] WriteBytes = new byte[2];
                    // WriteBytes[0] = (byte) 0xE7;
                    // WriteBytes[1] = (byte) 0xf6;
                    // //WriteBytes[2] = (byte)0x01;
                    // mBluetoothLeService.function_data( WriteBytes );

                }
                if (bit != (byte) 0x00) {
                    boolean on = ((ToggleButton) v).isChecked();
                    if (on) {
                        // Enable here
                        // Toast.makeText(jdy_Activity.this, "Enable here",
                        // Toast.LENGTH_SHORT).show();
                        // E7F101
                        byte[] WriteBytes = new byte[3];
                        WriteBytes[0] = (byte) 0xE7;
                        WriteBytes[1] = bit;
                        WriteBytes[2] = (byte) 0x01;
                        mBluetoothLeService.function_data(WriteBytes);
                    } else {
                        // Disable here
                        // Toast.makeText(jdy_Activity.this, "Disable here",
                        // Toast.LENGTH_SHORT).show();
                        byte[] WriteBytes = new byte[3];
                        WriteBytes[0] = (byte) 0xE7;
                        WriteBytes[1] = bit;
                        WriteBytes[2] = (byte) 0x00;
                        mBluetoothLeService.function_data(WriteBytes);
                    }
                }
            }
        }

    };

    OnClickListener listener = new OnClickListener() {// 创建监听对象
        public void onClick(View v) {
            // String strTmp="点击Button02";
            // Ev1.setText(strTmp);
            switch (v.getId()) {
                case R.id.tx_button:// uuid1002 数传通道发送数据
                    if (connect_status_bit) {
                        if (mConnected) {

                        }
                    } else {
                        // Toast.makeText(this, "Deleted Successfully!",
                        // Toast.LENGTH_LONG).show();
                        Toast toast = Toast.makeText(RoboSoulControl.this, "设备没有连接！", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                case R.id.clear_button: {
                    sbValues.delete(0, sbValues.length());
                    len_g = 0;
                    da = "";

                }
                break;
                default:
                    break;
            }
        }

    };

    OnTouchListener touchlistener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (v.getId()) {
                case R.id.robo_stand_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_stand.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_stand.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_stand.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_dance_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_dance.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_dance.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_dance.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_more_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_more.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_more.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_more.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_bow_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_bow.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_bow.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_bow.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_fly_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_fly.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_fly.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_fly.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_squat_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_squat.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_squat.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_squat.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_lie_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_lie.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_lie.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_lie.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_sit_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_sit.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_sit.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_sit.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_shake_button:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_shake.setVisibility(View.INVISIBLE);
                            break;
                        case MotionEvent.ACTION_UP:
                            button_shake.setVisibility(View.VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_shake.setVisibility(View.VISIBLE);
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_up:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_up.setImageResource(R.drawable.ball2);
                            Message message = new Message();
                            message.what = 11;
                            handler.sendMessage(message);
                            isup = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            button_up.setImageResource(R.drawable.scroll_ball_up);
                            isup = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_up.setImageResource(R.drawable.scroll_ball_up);
                            isup = false;
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_left:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_left.setImageResource(R.drawable.ball2);
                            Message message = new Message();
                            message.what = 12;
                            handler.sendMessage(message);
                            isleft = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            button_left.setImageResource(R.drawable.scroll_ball_left);
                            isleft = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_left.setImageResource(R.drawable.scroll_ball_left);
                            isleft = false;
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_down:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_down.setImageResource(R.drawable.ball2);
                            Message message = new Message();
                            message.what = 13;
                            handler.sendMessage(message);
                            isdown = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            button_down.setImageResource(R.drawable.scroll_ball_down);
                            isdown = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_down.setImageResource(R.drawable.scroll_ball_down);
                            isdown = false;
                            break;
                        default:
                            break;
                    }
                    break;

                case R.id.robo_right:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            button_right.setImageResource(R.drawable.ball2);
                            Message message = new Message();
                            message.what = 14;
                            handler.sendMessage(message);
                            isright = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            button_right.setImageResource(R.drawable.scroll_ball_right);
                            isright = false;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            button_right.setImageResource(R.drawable.scroll_ball_right);
                            isright = false;
                            break;
                        default:
                            break;
                    }
                    break;

                default:
                    break;
            }

            return false;
        }
    };

    OnClickListener imagelistener = new OnClickListener() {

        @Override
        public void onClick(View v) {


            if (connect_status_bit) {
                if (mConnected) {

                    switch (v.getId()) {
                        case R.id.robo_bluetooth:
                            finish();
                            break;

                        case R.id.robo_stand_button:
                            mBluetoothLeService.txxx(actionSTOP(), false);
                            mBluetoothLeService.txxx(actionCmdFromID(0), false);
                            break;

                        case R.id.robo_more_button:

                            ActionSheetDialog actionSheetDialog1 = new ActionSheetDialog(RoboSoulControl.this).builder().setTitle("选择其他动作").addSheetItem("欢呼", null,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(15), false);
                                }
                            }).addSheetItem("前翻滚", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(5), false);
                                }
                            }).addSheetItem("后翻滚", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(6), false);
                                }
                            });
                            actionSheetDialog1.txt_title.setTextColor(Color.parseColor("#00CCFF"));
                            actionSheetDialog1.show();
                            break;

                        case R.id.robo_dance_button:
                            ActionSheetDialog actionSheetDialog = new ActionSheetDialog(RoboSoulControl.this).builder().setTitle("选择舞蹈动作").addSheetItem("街舞", null,
                                    new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(16), false);
                                }
                            }).addSheetItem("江南style", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(17), false);
                                }
                            }).addSheetItem("小苹果", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(18), false);
                                }
                            }).addSheetItem("La Song", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(19), false);
                                }
                            }).addSheetItem("倍儿爽", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(20), false);
                                }
                            }).addSheetItem("Fantastic Baby", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(21), false);
                                }
                            }).addSheetItem("超级冠军", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(22), false);
                                }
                            }).addSheetItem("青春修炼手册", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(23), false);
                                }
                            }).addSheetItem("爱出发", null, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    mBluetoothLeService.txxx(actionCmdFromID(24), false);
                                }
                            });
                            actionSheetDialog.txt_title.setTextColor(Color.parseColor("#00CCFF"));
                            actionSheetDialog.show();

                            break;

                        case R.id.robo_bow_button:
                            mBluetoothLeService.txxx(actionCmdFromID(10), false);
                            break;

                        case R.id.robo_fly_button:
                            mBluetoothLeService.txxx(actionCmdFromID(13), false);
                            break;

                        case R.id.robo_squat_button:
                            mBluetoothLeService.txxx(actionCmdFromID(14), false);
                            break;

                        case R.id.robo_lie_button:
                            mBluetoothLeService.txxx(actionCmdFromID(7), false);
                            break;

                        case R.id.robo_sit_button:
                            mBluetoothLeService.txxx(actionCmdFromID(8), false);
                            break;

                        case R.id.robo_shake_button:
                            mBluetoothLeService.txxx(actionCmdFromID(9), false);
                            break;

                        default:
                            break;
                    }
                }
            } else {
                Toast toast = Toast.makeText(RoboSoulControl.this, "设备没有连接！", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregisterReceiver(mGattUpdateReceiver);
        // mBluetoothLeService.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        mBluetoothLeService = null;
        timer.cancel();
        timer = null;
        RoboSoulControl.this.unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.control_menu, menu);
        mMenu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.battery_menu:
                if (connect_status_bit) {
                    if (mConnected) {
                        mBluetoothLeService.txxx("68970102000316", false);
                    }
                } else {
                    Toast toast = Toast.makeText(RoboSoulControl.this, "设备没有连接！", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    String da = "";
    int len_g = 0;

    private void displayData(byte[] data1) // 接收FFE1串口透传数据通道数据
    {
        // String head1,data_0;
        /*
         * head1=data1.substring(0,2); data_0=data1.substring(2);
         */
        // da = da+data1+"\n";
        if (data1 != null && data1.length > 0) {
            // //sbValues.insert(0, data1);
            // //sbValues.indexOf( data1 );

            // //mDataField.setText( data1 ); getStringByBytes
            // len_g += data1.length;
            // //da = data1+da;

            // rx_data_id_1.setText( mBluetoothLeService.bytesToHexString(data1)
            // );//
            if (rx_hex) {
                final StringBuilder stringBuilder = new StringBuilder(sbValues.length());
                byte[] WriteBytes = mBluetoothLeService.hex2byte(stringBuilder.toString().getBytes());

                for (byte byteChar : data1)
                    stringBuilder.append(String.format(" %02X", byteChar));

                String da = stringBuilder.toString();
                // sbValues.append( stringBuilder.toString() ) ;
                // rx_data_id_1.setText(
                // mBluetoothLeService.String_to_HexString(sbValues.toString())
                // );

                // String res = new String( da.getBytes() );
                sbValues.append(da);
                ReceivedDATA = da;
                Thread th = new HandleReceivedMessage();
                th.start();
            } else {
                String res = new String(data1);
                sbValues.append(res);
            }

            len_g += data1.length;

            // // data1 );

            if (sbValues.length() >= 5000)
                sbValues.delete(0, sbValues.length());

            // rx_data_id_1.setGravity(Gravity.BOTTOM);
            // rx_data_id_1.setSelection(rx_data_id_1.getText().length());
        }

    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (resourceId) {
                    case R.string.connected:
                        button_bluetooth.setImageResource(R.drawable.connect_4);

                        break;

                    case R.string.disconnected:
                        button_bluetooth.setImageResource(R.drawable.disconnect_4);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;

        if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) == 2)// 表示为JDY-06、JDY-08系列蓝牙模块
        {
            connect_count = 0;
            if (connect_status_bit) {
                mConnected = true;
                mBluetoothLeService.Delay_ms(100);
                mBluetoothLeService.enable_JDY_ble(0);
                mBluetoothLeService.Delay_ms(100);
                mBluetoothLeService.enable_JDY_ble(1);
                mBluetoothLeService.Delay_ms(100);

                byte[] WriteBytes = new byte[2];
                WriteBytes[0] = (byte) 0xE7;
                WriteBytes[1] = (byte) 0xf6;
                mBluetoothLeService.function_data(WriteBytes);// 发送读取所有IO状态

                updateConnectionState(R.string.connected);

                enable_pass();
            } else {
                // Toast.makeText(this, "Deleted Successfully!",
                // Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(RoboSoulControl.this, "设备没有连接！", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (gattServices.size() > 0 && mBluetoothLeService.get_connected_status(gattServices) == 1)// 表示为JDY-09、JDY-10系列蓝牙模块
        {
            connect_count = 0;
            if (connect_status_bit) {
                mConnected = true;

                mBluetoothLeService.Delay_ms(100);
                mBluetoothLeService.enable_JDY_ble(0);

                updateConnectionState(R.string.connected);

                // enable_pass();
            } else {
                // Toast.makeText(this, "Deleted Successfully!",
                // Toast.LENGTH_LONG).show();
                Toast toast = Toast.makeText(RoboSoulControl.this, "设备没有连接！", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(RoboSoulControl.this, "提示！此设备不为JDY系列BLE模块", Toast.LENGTH_SHORT);
            toast.show();
        }
        // SimpleExpandableListAdapter gattServiceAdapter = new
        // SimpleExpandableListAdapter(
        // this,
        // gattServiceData,
        // android.R.layout.simple_expandable_list_item_2,
        // new String[] {LIST_NAME, LIST_UUID},
        // new int[] { android.R.id.text1, android.R.id.text2 },
        // gattCharacteristicData,
        // android.R.layout.simple_expandable_list_item_2,
        // new String[] {LIST_NAME, LIST_UUID},
        // new int[] { android.R.id.text1, android.R.id.text2 }
        // );
        //
        // mGattServicesList.setAdapter(gattServiceAdapter);

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE1);
        return intentFilter;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mConnected) {
            mBluetoothLeService.set_PWM_ALL_pulse(seekBar.getProgress(), seekBar.getProgress(), seekBar.getProgress(), seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // mBluetoothLeService.set_PWM_ALL_pulse( seekBar.getProgress(),
        // seekBar.getProgress(), seekBar.getProgress(), seekBar.getProgress()
        // );
        // Toast.makeText(jdy_Activity.this, "pulse"+seekBar.getProgress(),
        // Toast.LENGTH_SHORT).show();
    }

    public static String str2HexStr(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    private static String hexString = "0123456789abcdef";

    public static String str2HexStr_chinese(String str) {
        byte[] bytes = null;
        try {
            bytes = str.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f)));
        }
        return sb.toString();
    }


    class HandleReceivedMessage extends Thread {

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            Log.i("test", "Receive = " + ReceivedDATA);

            ReceivedDATA = ReceivedDATA.replaceAll(" ", "");
            ReceivedKey = Integer.valueOf(ReceivedDATA.substring(6, 8), 16);
            ReceivedValue = 0;
            if (ReceivedKey == 8) {
                ReceivedValue = Integer.valueOf(ReceivedDATA.substring(8, 10), 16);
                switch (ReceivedValue) {
                    case 1:
                        if (isup) {
                            Message message = new Message();
                            message.what = 11;
                            handler.sendMessage(message);
                        }
                        break;

                    case 2:
                        if (isdown) {
                            Message message = new Message();
                            message.what = 13;
                            handler.sendMessage(message);
                        }
                        break;

                    case 11:
                        if (isleft) {
                            Message message = new Message();
                            message.what = 12;
                            handler.sendMessage(message);
                        }
                        break;

                    case 12:
                        if (isright) {
                            Message message = new Message();
                            message.what = 14;
                            handler.sendMessage(message);
                        }
                        break;

                    default:
                        break;
                }
            }

            ReceivedKey = 0;
            ReceivedValue = 0;
            Looper.loop();
        }
    }


    class MyDialog extends Dialog {

        public MyDialog(Context context, int width, int height, View layout, int style) {
            super(context, style);
            setContentView(layout);
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);

        }

    }

    public String actionCmdFromID(int id) {
        String result = null;
        String id_hex = null;

        if (id > 15) {
            id_hex = Integer.toHexString(id);
        } else {
            id_hex = "0" + Integer.toHexString(id);
        }

        if (mSwitch.isChecked()) {
            result = "55550509" + id_hex + "0100";
        } else {
            result = "55550506" + id_hex + "0100";
        }

        return result;
    }

    public String actionSTOP() {
        String result = null;
        if (mSwitch.isActivated()) {
            result = "55550207";
        } else {
            result = "55550207";
        }
        return result;
    }

}
