package com.example.main;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.JsonObjectRequest;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

public class OidBoxCollect extends Activity implements CancelAdapt {

    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Service_uuid_fang = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_fang_write1 = "00002031-1212-efde-1523-785fea6c3593";
    //NAME:+BLE
    public static String Characteristic_uuid_fang_notify1 = "00002032-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_write2 = "00002052-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_fang_notify2 = "00002051-1212-efde-1523-785fea6c3593";

    private MenuItem menuItem;
    ListView listView;
    private List<BleDevice> bleDeviceList;
    ListViewAdapter listViewAdapter;
    BleGattCallback mBleGattCallback;
    BleScanCallback mBleScanCallback;
    BleNotifyCallback mBleNotifyCallback;
    BleWriteCallback mBleWriteCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oidboxcollect);
        setTitle("数据采集");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        listView = findViewById(R.id.listview_oidboxcollect);
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance().enableLog(true).setReConnectCount(0, 1000).setConnectOverTime(2000).setOperateTimeout(2000);
        setScanRule();
        NoHttp.initialize(this);

        bleDeviceList = BleManager.getInstance().getAllConnectedDevice();
        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);


        mBleScanCallback = new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }

            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                BleManager.getInstance().cancelScan();
                BleManager.getInstance().connect(bleDevice, mBleGattCallback);
            }
        };

        mBleGattCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                BleManager.getInstance().scan(mBleScanCallback);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                bleDeviceList = BleManager.getInstance().getAllConnectedDevice();
                listView.setAdapter(listViewAdapter);
                BleManager.getInstance().scan(mBleScanCallback);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                bleDeviceList = BleManager.getInstance().getAllConnectedDevice();
                listView.setAdapter(listViewAdapter);
            }
        };
        mBleNotifyCallback = new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {

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

        BleManager.getInstance().scan(mBleScanCallback);

    }

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

    class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return bleDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return bleDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView != null) {
                viewHolder = (ViewHolder) convertView.getTag();

            } else {
                convertView = View.inflate(OidBoxCollect.this, R.layout.listitem_oidboxcollect, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);

                viewHolder.ID = convertView.findViewById(R.id.lv_oidboxcollect_ID);
                viewHolder.MAC = convertView.findViewById(R.id.lv_oidboxcollect_MAC);
                viewHolder.progress = convertView.findViewById(R.id.lv_oidboxcollect_progress);

                viewHolder.vhBleDevice = bleDeviceList.get(position);

                switch (bleDeviceList.get(position).getName()) {
                    case "0idBox01":
                        viewHolder.number = 1;
                        break;

                    case "0idBox02":
                        viewHolder.number = 2;
                        break;

                    default:
                        viewHolder.number = 0;
                        viewHolder.ID.setText(String.valueOf(position));
                        break;


                }


                viewHolder.vhBleNotifyCallback = new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {


                        switch (data[2]) {
                            case 0x04:

                                byte[] bytes1 = {data[3], data[4], data[5], data[6]};
                                Log.i("test", "process:" + byteToInt(bytes1));
                                uplink(viewHolder.number, 4, byteToInt(bytes1));

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00105"),
                                                viewHolder.vhBleWriteCallback);
                                    }
                                }.start();


                                viewHolder.progress.setText("4");
                                break;
                            case 0x05:


                                byte[] bytes2 = {data[3], data[4], data[5], data[6]};
                                Log.i("test", "process:" + byteToInt(bytes2));
                                uplink(viewHolder.number, 5, byteToInt(bytes2));

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00106"),
                                                viewHolder.vhBleWriteCallback);
                                    }
                                }.start();


                                viewHolder.progress.setText("5");
                                break;
                            case 0x06:

                                byte[] bytes3 = {data[3], data[4], data[5], data[6]};
                                Log.i("test", "process:" + byteToInt(bytes3));
                                uplink(viewHolder.number, 6, byteToInt(bytes3));

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00107"),
                                                viewHolder.vhBleWriteCallback);
                                    }
                                }.start();

                                viewHolder.progress.setText("6");
                                break;
                            case 0x07:

                                byte[] bytes4 = {data[3], data[4], data[5], data[6]};
                                Log.i("test", "process:" + byteToInt(bytes4));
                                uplink(viewHolder.number, 7, byteToInt(bytes4));

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(500);
                                            BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f00108"),
                                                    viewHolder.vhBleWriteCallback);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }.start();

                                viewHolder.progress.setText("7");
                                break;

                            case 0x08:

                                byte[] bytes5 = {data[3], data[4], data[5], data[6]};
                                Log.i("test", "process:" + byteToInt(bytes5));
                                uplink(viewHolder.number, 8, byteToInt(bytes5));

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            //sleep(500);

//                                            BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write1, HexUtil.hexStringToBytes("NAME:OidBoxA"),
//                                                    viewHolder.vhBleWriteCallback);

                                            sleep(500);

                                            BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f001ff"),
                                                    viewHolder.vhBleWriteCallback);

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }.start();

                                viewHolder.progress.setText("8");
                                break;

                            case 0x69:

                                byte[] mygamedata = ReceiveBuffer(data);
                                for (int i = 0; i < (int) (mygamedata.length / 3); i++) {
                                    byte[] temp = {mygamedata[i * 3 + 1], mygamedata[i * 3 + 2]};
                                    uplink(viewHolder.number, mygamedata[(i * 3)], byteToInt(temp));
                                }


                                BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f006680101A7A916"),
                                        viewHolder.vhBleWriteCallback);
                                Log.i("test", "finish collect");
                                break;

                            default:
                                break;
                        }

                        Log.i("test", HexUtil.formatHexString(data));
                        switch (data[0]) {
                            case -16:
                                if (data.length < data[1]) {
                                    viewHolder.tempdata = viewHolder.tempdata + HexUtil.formatHexString(data).substring(4, (data.length) * 2);
                                }
                                break;
                            case -15:

                                viewHolder.tempdata = viewHolder.tempdata + HexUtil.formatHexString(data).substring(2, (data.length) * 2);

                                byte[] mygamedata = ReceiveBuffer(HexUtil.hexStringToBytes(viewHolder.tempdata));
                                Log.i("test", HexUtil.formatHexString(mygamedata));
                                viewHolder.tempdata = "";
                                for (int i = 0; i < (int) ((mygamedata.length - 2) / 3); i++) {
                                    byte[] temp = {mygamedata[i * 3 + 3], mygamedata[i * 3 + 4]};
                                    Log.i("test", HexUtil.formatHexString(temp));
                                    uplink(viewHolder.number, mygamedata[(i * 3 + 2)], byteToInt(temp));
                                }

                                BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f006680101FF0116"),
                                        viewHolder.vhBleWriteCallback);
                                Log.i("test", "finish collect");
                                break;

                            default:
                                break;

                        }


                    }
                };
                viewHolder.vhBleWriteCallback = new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.i("test", "write success " + HexUtil.formatHexString(justWrite));

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.i("test", "write fail! " + exception.toString());
                    }
                };


                new Thread() {

                    @Override
                    public void run() {
                        super.run();


                        BleManager.getInstance().notify(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_notify2, viewHolder.vhBleNotifyCallback);

                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        BleManager.getInstance().write(viewHolder.vhBleDevice, Service_uuid_fang, Characteristic_uuid_fang_write2, HexUtil.hexStringToBytes("f006680101A7A916"),
                                viewHolder.vhBleWriteCallback);
                        Log.i("test", "send collect");


                    }
                }.start();

            }
            viewHolder.ID.setText(viewHolder.number + "");
            viewHolder.MAC.setText(bleDeviceList.get(position).getMac());
            return convertView;
        }
    }

    public class ViewHolder {
        TextView ID, MAC, progress;
        BleNotifyCallback vhBleNotifyCallback;
        BleWriteCallback vhBleWriteCallback;
        BleDevice vhBleDevice;
        int number = 0;
        String tempdata = "";
    }


    private void setScanRule() {

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBoxB", "OidBoxA")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(true)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(0)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    class CheckReceive extends Thread {

        BleDevice mBleDevice;

        CheckReceive(BleDevice bleDevice) {
            this.mBleDevice = bleDevice;

        }

        @Override
        public void run() {
            super.run();


        }
    }

    private void uplink(int fangfang, int game, int result) {
        StringRequest request = new StringRequest("http://192.168.3.199:8888/OidBox/gamedata", RequestMethod.POST);
        request.set("fangfang", String.valueOf(fangfang));
        request.set("game", String.valueOf(game));
        request.set("result", String.valueOf(result));
        Log.i("test", "uplink start: fangfang" + fangfang + " game:" + game + " result" + result);

        Request request1=new Request("") {
            @Override
            public Object parseResponse(Headers responseHeaders, byte[] responseBody) throws Exception {
                return null;
            }
        };



//        new Thread() {
//
//            @Override
//            public void run() {
//                super.run();
//                Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
//                if (response.isSucceed()) {
//                    Log.i("test", "uplink success: fangfang" + fangfang + " game:" + game + " result" + result);
//                    // 请求成功。
//                } else {
//                    // 请求失败，拿到错误：
//                    Exception e = response.getException();
//                }
//            }
//        }.start();


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
        Log.i("test", HexUtil.formatHexString(data, true));
        int len = data[2];
        byte[] result = new byte[len + 1];
        result[0] = data[1];
        for (int i = 0; i < len; i++) {
            result[i + 1] = data[3 + i];
        }

        return result;
    }
}
