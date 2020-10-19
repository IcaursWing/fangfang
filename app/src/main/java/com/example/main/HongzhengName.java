package com.example.main;

import android.app.Activity;
import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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
import com.example.blefast.ProgramFangAdapter;
import com.example.fangfang_gai.R;

import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

public class HongzhengName extends Activity implements CancelAdapt {

    public static String Service_uuid_fang = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_write1 = "00002031-1212-efde-1523-785fea6c3593";
    //NAME:+BLE
    public static String Characteristic_uuid_fang_notify1 = "00002032-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_write2 = "00002052-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_notify2 = "00002051-1212-efde-1523-785fea6c3593";

    private ListView listView;
    private ProgramFangAdapter mDeviceAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programfang);
        this.setTitle("选择改名听小方");

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true).setReConnectCount(1, 5000).setConnectOverTime(20000).setOperateTimeout(5000);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        listView = findViewById(R.id.listview_programfang);
        mDeviceAdapter = new ProgramFangAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new ProgramFangAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {
                BleManager.getInstance().notify(bleDevice, Service_uuid_fang, Characteristic_uuid_fang_notify2, new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        byte[] data = {0x01, 0x00};
                        String send = HexUtil.formatHexString(SendBuffer(1, data));
                        byte[] lastsend = HexUtil.hexStringToBytes("f007" + send);
                        BleManager.getInstance().write(bleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, lastsend, new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                Log.i("test", HexUtil.formatHexString(justWrite, true));
                            }

                            @Override
                            public void onWriteFailure(BleException exception) {

                            }
                        });
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i("test", HexUtil.formatHexString(data, true));
                        String blename = bleDevice.getName();
                        String myname = "";
                        if (data[3] < 10) {
                            myname = "OidBox0" + data[3];
                        } else {
                            myname = "OidBox" + data[3];
                        }

                        if (blename != myname) {
                            String changename = "NAME:" + myname;
                            BleManager.getInstance().write(bleDevice, Service_uuid_fang, Characteristic_uuid_fang_write1, changename.getBytes(), new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }
                        Toast.makeText(HongzhengName.this, "改名成功！", Toast.LENGTH_LONG).show();

                    }
                });


            }
        });
        listView.setAdapter(mDeviceAdapter);
        setScanRule();
        startScan();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showConnectedDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }


    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBox")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }


            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.setMessage("连接 " + bleDevice.getName() + " 中...");
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(HongzhengName.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();


            }
        });
    }

    private void showConnectedDevice() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        mDeviceAdapter.clearConnectedDevice();
        for (BleDevice bleDevice : deviceList) {
            mDeviceAdapter.addDevice(bleDevice);
        }
        mDeviceAdapter.notifyDataSetChanged();
    }

    public byte[] SendBuffer(int id, byte[] data) {

        int len = data.length;
        int total = id + len;
        byte[] temp = new byte[1];
        for (int i = 0; i < len; i++) {
            temp[0] = data[i];
            total = total + Integer.valueOf(HexUtil.formatHexString(temp), 16);
        }
        int cs = total % 256;

        byte[] result = new byte[data.length + 5];
        result[0] = 0x68;
        result[1] = (byte) id;
        result[2] = (byte) len;
        for (int i = 0; i < len; i++) {
            result[3 + i] = data[i];
        }
        result[3 + len] = (byte) cs;
        result[4 + len] = 0x16;

        return result;
    }

    public byte[] ReceiveBuffer(byte[] data) {
        int len = data[2];
        byte[] result = new byte[len + 1];
        result[0] = data[1];
        for (int i = 0; i < len; i++) {
            result[i + 1] = data[3 + i];
        }

        return result;
    }
}
