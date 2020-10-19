package com.example.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.blefast.DeviceAdapter;
import com.example.blefast.ProgramFangAdapter;
import com.example.fangfang_gai.R;

import java.util.List;


public class ProgramFang extends Activity {

    private ListView listView;
    private ProgramFangAdapter mDeviceAdapter;
    private MenuItem menuItem;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programfang);
        this.setTitle("程小方连接");

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

//                if (bleDevice.getName().contains("程小方")) {
//                    Intent intent = new Intent(ProgramFang.this, ProgramFangControl.class);
//                    intent.putExtra("bleDevice", bleDevice);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(ProgramFang.this, "不是程小方不能控制哦！", Toast.LENGTH_LONG).show();
//                }

                Intent intent = new Intent(ProgramFang.this, ProgramFangControl.class);
                intent.putExtra("bleDevice", bleDevice);
                startActivity(intent);

            }
        });
        listView.setAdapter(mDeviceAdapter);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_menu, menu);
        menu.findItem(R.id.menu_muticontrol).setVisible(false);
        menu.findItem(R.id.menu_mutiscan).setVisible(true);
        menu.findItem(R.id.menu_mutipress).setVisible(false);
        menuItem = menu.findItem(R.id.menu_mutiscan);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_mutiscan:
                if (item.getTitle().toString().equals(getString(R.string.menu_scan))) {
                    setScanRule();
                    startScan();
                } else if (item.getTitle().toString().equals(getString(R.string.scanning))) {
                    BleManager.getInstance().cancelScan();
                }
                break;
            case R.id.menu_muticontrol:
                Intent intent = new Intent(ProgramFang.this, MutiBluetoothControlActivity.class);
                List<BleDevice> mList = BleManager.getInstance().getAllConnectedDevice();
                for (int i = 0; i < mList.size(); i++) {
                    intent.putExtra("device" + i, mList.get(i));
                }
                intent.putExtra("number", mList.size());
                startActivity(intent);
                break;
            default:
                break;

        }

        return true;
    }

    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, null)   // 只扫描指定广播名的设备，可选
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
                menuItem.setTitle(R.string.scanning);
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
                menuItem.setTitle(R.string.menu_scan);
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
                Toast.makeText(ProgramFang.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
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

                if (isActiveDisConnected) {
                    Toast.makeText(ProgramFang.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ProgramFang.this, "连接被断开", Toast.LENGTH_LONG).show();
                    //ObserverManager.getInstance().notifyObserver(bleDevice);
                }
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

}
