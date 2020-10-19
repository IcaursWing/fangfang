package com.example.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;
import com.example.fangfang_gai.R;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.util.Iterator;
import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

public class OidBoxGame extends Activity implements CancelAdapt {

    private MenuItem menuItem;
    TextView game1_state, game2_state, game3_state;
    CheckBox game1_checkbox, game2_checkbox, game3_checkbox;
    Button game1_send, game2_send, game3_send;
    EditText game1_ID, game2_ID, game3_ID;
    EditText game1_data, game2_data, game3_data;

    int data1 = 0, data2 = 0, data3 = 0;

    boolean game1_isconnect = false, game2_isconnect = false, game3_isconnect = false;
    boolean Bleisfree = true;


    Handler mHandler;
    BleDevice game1BleDevice, game2BleDevice, game3BleDevice;
    BleDevice fang1BleDevice, fang2BleDevice, fang3BleDevice, fangBleDevice;
    BleNotifyCallback mBleNotifyCallback1, mBleNotifyCallback2, mBleNotifyCallback3;
    BleWriteCallback mBleWriteCallback;
    BleScanCallback mBleScanCallback;
    BleNotifyCallback fangBleNotifyCallback;
    BleWriteCallback fangBleWriteCallback;

    CheckConnect mCheckConnect;
    ConnectGame mConnectGame;

    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String Service_uuid_fang = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_write1 = "00002031-1212-efde-1523-785fea6c3593";
    //NAME:+BLE
    public static String Characteristic_uuid_fang_notify1 = "00002032-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_write2 = "00002052-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_notify2 = "00002051-1212-efde-1523-785fea6c3593";


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oidboxgame);
        setTitle("套件游戏");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true).setReConnectCount(0, 1000).setConnectOverTime(5000).setOperateTimeout(5000);
        setScanRule();
        init();
        NoHttp.initialize(this);


        mBleScanCallback = new BleScanCallback() {

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
//                    Iterator<BleDevice> iterator = scanResultList.iterator();
//                    while (iterator.hasNext()) {
//                        BleDevice next = iterator.next();
//
//                    }//end while

            }

            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                switch (bleDevice.getName()) {
                    case "OidBoxGame1":
                        if (game1_isconnect) {
                            BleManager.getInstance().cancelScan();
                            BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                    Message message = new Message();
                                    message.what = 10;
                                    game1BleDevice = bleDevice;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {

                                    Message message = new Message();
                                    message.what = 11;
                                    game1BleDevice = bleDevice;
                                    mHandler.sendMessage(message);

                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                                    Message message = new Message();
                                    message.what = 10;
                                    mHandler.sendMessage(message);

                                }
                            });
                        }
                        break;
                    case "OidBoxGame2":
                        if (game2_isconnect) {
                            BleManager.getInstance().cancelScan();
                            BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                    Message message = new Message();
                                    message.what = 20;
                                    game2BleDevice = bleDevice;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                                    Message message = new Message();
                                    message.what = 21;
                                    game2BleDevice = bleDevice;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                                    Message message = new Message();
                                    message.what = 30;
                                    mHandler.sendMessage(message);

                                }
                            });
                        }
                        break;
                    case "OidBoxGame3":
                        if (game3_isconnect) {

                            BleManager.getInstance().cancelScan();
                            BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                                @Override
                                public void onStartConnect() {

                                }

                                @Override
                                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                    Message message = new Message();
                                    message.what = 30;
                                    game3BleDevice = bleDevice;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                                    Message message = new Message();
                                    message.what = 31;
                                    game3BleDevice = bleDevice;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                                    Message message = new Message();
                                    message.what = 30;
                                    mHandler.sendMessage(message);

                                }
                            });
                        }
                        break;
                    case "OidBoxA":

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                BleManager.getInstance().removeConnectGattCallback(bleDevice);
                                BleManager.getInstance().cancelScan();
                                BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                                    @Override
                                    public void onStartConnect() {

                                    }

                                    @Override
                                    public void onConnectFail(BleDevice bleDevice, BleException exception) {
                                        Message message = new Message();
                                        message.what = 1000;
                                        mHandler.sendMessage(message);
                                    }

                                    @Override
                                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                                        Message message = new Message();
                                        message.what = 101;
                                        fangBleDevice = bleDevice;
                                        mHandler.sendMessage(message);

                                    }

                                    @Override
                                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                                    }
                                });
                            }
                        }.start();
                        break;

                    default:
                        break;


                }
            }
        };


        mBleNotifyCallback1 = new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                byte[] temp1 = {data[0]};
                byte[] temp2 = {data[4], data[3], data[2], data[1]};
                game1_ID.setText(String.valueOf(Integer.valueOf(bytes2HexString(temp1, 1), 16)));
                game1_data.setText(String.valueOf(byteToInt(temp2)));
                data1 = byteToInt(temp2);
            }
        };
        mBleNotifyCallback2 = new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                byte[] temp1 = {data[0]};
                byte[] temp2 = {data[4], data[3], data[2], data[1]};
                game2_ID.setText(String.valueOf(Integer.valueOf(bytes2HexString(temp1, 1), 16)));
                game2_data.setText(String.valueOf(byteToInt(temp2)));
                data2 = byteToInt(temp2);
            }
        };
        mBleNotifyCallback3 = new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                byte[] temp1 = {data[0]};
                byte[] temp2 = {data[4], data[3], data[2], data[1]};
                game3_ID.setText(String.valueOf(Integer.valueOf(bytes2HexString(temp1, 1), 16)));
                game3_data.setText(String.valueOf(byteToInt(temp2)));
                data3 = byteToInt(temp2);
            }
        };
        mBleWriteCallback = new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {

            }

            @Override
            public void onWriteFailure(BleException exception) {

            }
        };


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 11:
                        game1_state.setText("已连接");
                        BleManager.getInstance().notify(game1BleDevice, Service_uuid, Characteristic_uuid, mBleNotifyCallback1);
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;
                    case 10:
                        game1_state.setText("未连接");
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;
                    case 21:
                        game2_state.setText("已连接");
                        BleManager.getInstance().notify(game2BleDevice, Service_uuid, Characteristic_uuid, mBleNotifyCallback2);
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;
                    case 20:
                        game2_state.setText("未连接");
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;
                    case 31:
                        game3_state.setText("已连接");
                        BleManager.getInstance().notify(game3BleDevice, Service_uuid, Characteristic_uuid, mBleNotifyCallback3);
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;
                    case 30:
                        game3_state.setText("未连接");
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;

                    case 101:
                        BleManager.getInstance().scan(mBleScanCallback);
                        CheckWhichGame checkWhichGame = new CheckWhichGame(fangBleDevice);
                        checkWhichGame.start();
                        //CheckWhichGame(fangBleDevice);
                        break;

                    case 1000:
                        BleManager.getInstance().cancelScan();
                        BleManager.getInstance().scan(mBleScanCallback);
                        break;
                    default:
                        break;

                }

            }
        };

        BleManager.getInstance().scan(mBleScanCallback);


    }//end oncreate

    @Override
    protected void onStop() {
        super.onStop();
        BleManager.getInstance().cancelScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    private void init() {
        game1_state = findViewById(R.id.game1_state);
        game2_state = findViewById(R.id.game2_state);
        game3_state = findViewById(R.id.game3_state);

        game1_checkbox = findViewById(R.id.game1_checkBox);
        game2_checkbox = findViewById(R.id.game2_checkBox);
        game3_checkbox = findViewById(R.id.game3_checkBox);

        game1_send = findViewById(R.id.game1_send);
        game2_send = findViewById(R.id.game2_send);
        game3_send = findViewById(R.id.game3_send);

        game1_ID = findViewById(R.id.game1_ID);
        game2_ID = findViewById(R.id.game2_ID);
        game3_ID = findViewById(R.id.game3_ID);

        game1_data = findViewById(R.id.game1_data);
        game2_data = findViewById(R.id.game2_data);
        game3_data = findViewById(R.id.game3_data);

        game1_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                game1_isconnect = isChecked;


                if (isChecked) {
                    BleManager.getInstance().cancelScan();
                    BleManager.getInstance().scan(mBleScanCallback);
                } else {
                    mCheckConnect = new CheckConnect();
                    mCheckConnect.start();
                }


            }
        });
        game2_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                game2_isconnect = isChecked;


                if (isChecked) {
                    BleManager.getInstance().cancelScan();
                    BleManager.getInstance().scan(mBleScanCallback);
                } else {
                    mCheckConnect = new CheckConnect();
                    mCheckConnect.start();
                }


            }
        });
        game3_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                game3_isconnect = isChecked;


                if (isChecked) {
                    BleManager.getInstance().cancelScan();
                    BleManager.getInstance().scan(mBleScanCallback);
                } else {
                    mCheckConnect = new CheckConnect();
                    mCheckConnect.start();
                }


            }
        });

        game1_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp1 = game1_ID.getText().toString().trim();
                String temp2 = game1_data.getText().toString().trim();

                if (!temp1.isEmpty() && !temp2.isEmpty()) {
                    int mgame1_ID = Integer.valueOf(temp1);
                    int mdata = Integer.valueOf(temp2);
                    uplink(mgame1_ID, 1, mdata);
                } else {
                    Toast.makeText(OidBoxGame.this, "模拟数据不能为空！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        game2_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp1 = game2_ID.getText().toString().trim();
                String temp2 = game2_data.getText().toString().trim();

                if (!temp1.isEmpty() && !temp2.isEmpty()) {
                    int mgame1_ID = Integer.valueOf(temp1);
                    int mdata = Integer.valueOf(temp2);
                    uplink(mgame1_ID, 2, mdata);
                } else {
                    Toast.makeText(OidBoxGame.this, "模拟数据不能为空！", Toast.LENGTH_SHORT).show();
                }

            }
        });
        game3_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp1 = game3_ID.getText().toString().trim();
                String temp2 = game3_data.getText().toString().trim();

                if (!temp1.isEmpty() && !temp2.isEmpty()) {
                    int mgame1_ID = Integer.valueOf(temp1);
                    int mdata = Integer.valueOf(temp2);
                    uplink(mgame1_ID, 3, mdata);
                } else {
                    Toast.makeText(OidBoxGame.this, "模拟数据不能为空！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    class LoopBle extends Thread {
        @Override
        public void run() {
            super.run();

            Log.i("test", "ble loop!");
            if (Bleisfree) {
                try {
                    mCheckConnect = new CheckConnect();
                    mConnectGame = new ConnectGame();
                    mCheckConnect.start();
                    sleep(1000);
                    mConnectGame.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class CheckConnect extends Thread {

        @Override
        public void run() {
            super.run();

            Log.i("test", "CheckConnect Thread!");
            List<BleDevice> bleList = BleManager.getInstance().getAllConnectedDevice();
            if (bleList != null) {
                Iterator<BleDevice> iterator = bleList.iterator();
                while (iterator.hasNext()) {
                    BleDevice next = iterator.next();
                    switch (next.getName()) {
                        case "OidBoxGame1":
                            if (game1_isconnect) {

                            } else {
                                BleManager.getInstance().cancelScan();
                                BleManager.getInstance().disconnect(next);
                                Message message = new Message();
                                message.what = 10;
                                mHandler.sendMessage(message);
                            }
                            break;
                        case "OidBoxGame2":
                            if (game2_isconnect) {

                            } else {
                                BleManager.getInstance().cancelScan();
                                BleManager.getInstance().disconnect(next);
                                Message message = new Message();
                                message.what = 20;
                                mHandler.sendMessage(message);
                            }
                            break;
                        case "OidBoxGame3":
                            if (game3_isconnect) {

                            } else {
                                BleManager.getInstance().cancelScan();
                                BleManager.getInstance().disconnect(next);
                                Message message = new Message();
                                message.what = 30;
                                mHandler.sendMessage(message);
                            }
                        case "OidBoxA":


                            break;
                        default:
                            break;


                    }
                }//end while
            }

        }
    }

    class ConnectGame extends Thread {

        @Override
        public void run() {
            super.run();

            Bleisfree = false;


            BleManager.getInstance().scan(mBleScanCallback);
        }
    }

    class CheckWhichGame extends Thread {
        BleDevice mBleDevice;


        CheckWhichGame(BleDevice bleDevice) {
            mBleDevice = bleDevice;
        }

        @Override
        public void run() {
            super.run();

            fangBleWriteCallback = new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.i("test", "just write " + HexUtil.formatHexString(justWrite));
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    Log.i("test", "fang write fail! " + exception.toString());
                    BleManager.getInstance().disconnect(mBleDevice);

                    Message message = new Message();
                    message.what = 1000;
                    mHandler.sendMessage(message);
                }
            };

            fangBleNotifyCallback = new BleNotifyCallback() {
                @Override
                public void onNotifySuccess() {

                }

                @Override
                public void onNotifyFailure(BleException exception) {

                }

                @Override
                public void onCharacteristicChanged(byte[] data) {
                    Log.i("test", "step2 " + HexUtil.formatHexString(data));
                    switch (HexUtil.formatHexString(data)) {
                        case "f00101":

                            uplink(1, 1, data1);
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00102"), fangBleWriteCallback);
                            break;
                        case "f00102":
                            uplink(1, 2, data2);
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00102"), fangBleWriteCallback);
                            break;
                        case "f00103":
                            uplink(1, 3, data3);
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00102"), fangBleWriteCallback);
                            break;
                        case "f001ff":
                            BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write1, HexUtil.hexStringToBytes(str2HexStr("NAMEOidBoxB")), new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    try {
                                        sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00103"), fangBleWriteCallback);

                                    Message message = new Message();
                                    message.what = 1000;
                                    mHandler.sendMessage(message);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;
                        case "f00100":
                            try {
                                sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.i("test", "step3 " + HexUtil.formatHexString(data));
                            BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00103"), fangBleWriteCallback);

                            Message message = new Message();
                            message.what = 1000;
                            mHandler.sendMessage(message);

                            break;
                        default:
                            break;


                    }


                }
            };

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            BleManager.getInstance().notify(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_notify2, fangBleNotifyCallback);

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i("test", "step1 ");
            BleManager.getInstance().write(mBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00101"), fangBleWriteCallback);

        }
    }


    private void setScanRule() {

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBoxGame1", "OidBoxGame2", "OidBoxGame3", "OidBoxA")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(0)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void uplink(int fangfang, int game, int result) {
        StringRequest request = new StringRequest("http://192.168.3.199:8888/OidBox/gamedata", RequestMethod.POST);
        request.set("fangfang", String.valueOf(fangfang));
        request.set("game", String.valueOf(game));
        request.set("result", String.valueOf(result));

        new Thread() {

            @Override
            public void run() {
                super.run();
                Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                if (response.isSucceed()) {
                    Log.i("test", "uplink success: fangfang" + fangfang + " game:" + game + " result" + result);
                    // 请求成功。
                } else {
                    // 请求失败，拿到错误：
                    Exception e = response.getException();
                }
            }
        }.start();


    }

    public static String str2HexStr(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    public static int byteToInt(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int n = 0;
        for (int i = 0; i < b.length; i++) {
            n <<= 8;
            temp = b[i] & mask;
            n |= temp;
        }
        return n;
    }

    private String bytes2HexString(byte[] b, int length) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            r.append(hex.toUpperCase());
        }
        return r.toString();
    }

    public static final int itou(byte[] b, int p) {
        return ((btou(b[p]) * 256 + btou(b[p + 1])) * 256 + btou(b[p + 2])) * 256 + btou(b[p + 3]);
    }

    public static final int btou(byte b) {
        if (b >= 0)
            return (b + 0);
        else
            return (256 + b);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        menu.findItem(R.id.menu_game_scan).setVisible(true);
        menuItem = menu.findItem(R.id.menu_mutiscan);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_game_scan:
                BleManager.getInstance().cancelScan();
                BleManager.getInstance().scan(mBleScanCallback);
                break;

            default:
                break;

        }
        return true;
    }

}
