package com.example.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.blefast.DeviceAdapter;
import com.example.fangfang_gai.R;
import com.hb.dialog.myDialog.MyAlertInputDialog;

import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;


public class OidBoxGameConnect extends Activity implements CancelAdapt {

    private ListView listView;
    private DeviceAdapter mDeviceAdapter;
    private MenuItem menuItem;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutibluetoothconnect);
        this.setTitle("竞赛游戏");

        //~~~~~~~~~~~~~~~~~~~~
        BleManager.getInstance().init(getApplication());//初始化
        BleManager.getInstance().enableLog(true).setReConnectCount(1, 5000).setConnectOverTime(20000).setOperateTimeout(5000);//设置蓝牙连接属性

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        listView = findViewById(R.id.listview_mutiblue);
        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
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
        menu.findItem(R.id.menu_muticontrol).setVisible(true);
        menu.findItem(R.id.menu_muticontrol).setTitle("开始游戏");
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
                    setScanRule();//设置扫描属性
                    startScan();//开始扫描
                } else if (item.getTitle().toString().equals(getString(R.string.scanning))) {
                    BleManager.getInstance().cancelScan();//取消扫描
                }
                break;
            case R.id.menu_muticontrol:

                final String[] items = {"空白舒尔特", "趣味知识竞赛", "音乐创作"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(OidBoxGameConnect.this);
                listDialog.setTitle("选择游戏");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            MyAlertInputDialog myAlertInputDialog =
                                    new MyAlertInputDialog(OidBoxGameConnect.this).builder().setTitle("输入时间").setCancelable(true).setEditType(InputType.TYPE_CLASS_NUMBER).setEditText("");
                            myAlertInputDialog.setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!myAlertInputDialog.getResult().equals("")) {
                                        myAlertInputDialog.dismiss();
                                        Intent intent = new Intent(OidBoxGameConnect.this, OidBoxGameTable.class);
                                        intent.putExtra("time", Integer.valueOf(myAlertInputDialog.getResult()));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(OidBoxGameConnect.this, "请填写输入时间", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myAlertInputDialog.dismiss();
                                }
                            });
                            myAlertInputDialog.show();

                        } else if (which == 1) {
                            Intent intent = new Intent(OidBoxGameConnect.this, OidBoxGameQuestion.class);
                            startActivity(intent);
                        } else if (which == 2) {
                            Intent intent = new Intent(OidBoxGameConnect.this, OidBoxGameMusic.class);
                            startActivity(intent);
                        }

                    }
                });
                listDialog.show();


                break;

            default:
                break;

        }

        return true;
    }

    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBox")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
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


//        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
//            @Override
//            public void onStartConnect() {
//                progressDialog.setMessage("连接 " + bleDevice.getName() + " 中...");
//                progressDialog.show();
//                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        Toast.makeText(MutiBluetoothConnectActivity.this, "取消连接:" + bleDevice.getName(), Toast.LENGTH_SHORT).show();
//                        BleManager.getInstance().disconnect(bleDevice);
//
//                    }
//                });
//            }
//
//            @Override
//            public void onConnectFail(BleDevice bleDevice, BleException exception) {
//                progressDialog.dismiss();
//                Toast.makeText(MutiBluetoothConnectActivity.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                progressDialog.dismiss();
//                mDeviceAdapter.addDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
//                progressDialog.dismiss();
//
//                mDeviceAdapter.removeDevice(bleDevice);
//                mDeviceAdapter.notifyDataSetChanged();
//
//                if (isActiveDisConnected) {
//                    Toast.makeText(MutiBluetoothConnectActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MutiBluetoothConnectActivity.this, "连接被断开", Toast.LENGTH_LONG).show();
//                    //ObserverManager.getInstance().notifyObserver(bleDevice);
//                }
//            }
//        });

        BleGattCallback bleGattCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.setMessage("连接 " + bleDevice.getName() + " 中...");
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(OidBoxGameConnect.this, bleDevice.getName() + getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(OidBoxGameConnect.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(OidBoxGameConnect.this, bleDevice.getName() + "连接被断开", Toast.LENGTH_LONG).show();
                    //ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        };
        BleManager.getInstance().connect(bleDevice, bleGattCallback);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(OidBoxGameConnect.this, "后台连接:" + bleDevice.getName(), Toast.LENGTH_SHORT).show();

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
