package com.example.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.example.fangfang_gai.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import javax.net.ssl.ExtendedSSLSession;

public class MutiBluetoothControlActivity extends Activity {
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";

    public static String Service_uuid1 = "0000ae00-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX1 = "0000ae01-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX2 = "0000ae02-0000-1000-8000-00805f9b34fb";

    public static String Service_uuid2 = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_TX3 = "00002031-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_TX4 = "00002032-1212-efde-1523-785fea6c3593";

    List<BleDevice> bleDevices;
    ListView mListView;

    double degree = 0;
    int distance = 100;

    CheThread cheThread;
    BleDevice bleche;

    BleDevice bleDevice2 = null;
    BleDevice bleDevice3 = null;
    BleDevice bleDevice4 = null;
    Boolean line = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutibluetoothcontrol);


        bleDevices = BleManager.getInstance().getAllConnectedDevice();
        Log.i("test", bleDevices.size() + "个蓝牙");

        mListView = findViewById(R.id.listview_mutibluecontrol);
        MyAdapter myAdapter = new MyAdapter();
        mListView.setAdapter(myAdapter);


        for (int i = 0; i < bleDevices.size(); i++) {
            if (bleDevices.get(i).getName().equals("yaokongche")) {
                bleche = bleDevices.get(i);
                cheThread = new CheThread();
                cheThread.start();
            }
        }

    }


    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bleDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return bleDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listitem_mutiblue, null);

                viewHolder.tv_name = convertView.findViewById(R.id.listitem_mutiblue_name);
                viewHolder.tv_received = convertView.findViewById(R.id.listitem_mutiblue_received);
                viewHolder.et_send = convertView.findViewById(R.id.listitem_mutiblue_send);
                viewHolder.bt_send = convertView.findViewById(R.id.listitem_mutiblue_button_send);
                ViewHolder finalViewHolder = viewHolder;

                viewHolder.tv_name.setText(bleDevices.get(position).getName());
                viewHolder.bt_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BleManager.getInstance().write(bleDevices.get(position), Service_uuid, Characteristic_uuid_TX,
                                //  HexUtil.hexStringToBytes(str2HexStr(finalViewHolder.et_send.getText().toString())), new BleWriteCallback()
                                HexUtil.hexStringToBytes(finalViewHolder.et_send.getText().toString()), new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                        addReceivedText(finalViewHolder, " write success");
                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {
                                        addReceivedText(finalViewHolder, exception.toString());
                                    }
                                });
                    }
                });

                String tempUUID1, tempUUID2;
                if (bleDevices.get(position).getName().equals("car1") || bleDevices.get(position).getName().equals("car2") || bleDevices.get(position).getName().equals("car3") || bleDevices.get(position).getName().equals("BT05")) {
                    tempUUID1 = Service_uuid;
                    tempUUID2 = Characteristic_uuid_TX;
                } else {
                    tempUUID1 = Service_uuid2;
                    tempUUID2 = Characteristic_uuid_TX4;
                }

                BleManager.getInstance().notify(bleDevices.get(position), tempUUID1, tempUUID2, new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addReceivedText(finalViewHolder, "notify success");
                            }
                        });

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        addReceivedText(finalViewHolder, exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {


                        addReceivedText(finalViewHolder, " " + HexUtil.formatHexString(data, true));

                        BleDevice bleDevice1 = bleDevices.get(position);

                        long xy;
                        int Xaxis = 0, Yaxis = 0;
                        //double degree = 0;

                        switch (bleDevice1.getName()) {
                            case "chaoshengbo":
                                distance = Integer.parseInt(HexUtil.formatHexString(data, false).substring(2, 4), 16);
                                Log.i("test", "超声波：" + distance);
                                break;

                            case "tuoluoyi":
                                String temp = HexUtil.formatHexString(data, false).substring(2, 10);
                                temp = temp.substring(6, 8) + temp.substring(4, 6) + temp.substring(2, 4) + temp.substring(0, 2);
                                xy = Long.valueOf(temp, 16);
                                temp = String.valueOf(xy);

                                if (temp.substring(0, 1).equals("1")) {
                                    Yaxis = Integer.valueOf(temp.substring(1, 4));
                                } else if (temp.substring(0, 1).equals("2")) {
                                    Yaxis = Integer.valueOf(temp.substring(1, 4)) * (-1);
                                }

                                if (temp.substring(4, 5).equals("1")) {
                                    Xaxis = Integer.valueOf(temp.substring(5, 8));
                                    degree = degree + (double) Xaxis * 0.1;
                                } else if (temp.substring(4, 5).equals("2")) {
                                    Xaxis = Integer.valueOf(temp.substring(5, 8)) * (-1);
                                    degree = degree + (double) Xaxis * 0.1;
                                }


//                                if (Xaxis > 0 && Yaxis >= 0) {
//                                    degree = Math.toDegrees(Math.acos(Math.abs(Xaxis) / Math.sqrt(Xaxis * Xaxis + Yaxis * Yaxis)));
//                                } else if (Xaxis <= 0 && Yaxis > 0) {
//                                    degree = 180 - Math.toDegrees(Math.acos(Math.abs(Xaxis) / Math.sqrt(Xaxis * Xaxis + Yaxis * Yaxis)));
//                                } else if (Xaxis < 0 && Yaxis <= 0) {
//                                    degree = 180 + Math.toDegrees(Math.acos(Math.abs(Xaxis) / Math.sqrt(Xaxis * Xaxis + Yaxis * Yaxis)));
//                                } else if (Xaxis >= 0 && Yaxis < 0) {
//                                    degree = 360 - Math.toDegrees(Math.acos(Math.abs(Xaxis) / Math.sqrt(Xaxis * Xaxis + Yaxis * Yaxis)));
//                                }


                                Log.i("test", "陀螺仪：" + xy + " " + Xaxis + " " + Yaxis + " degree=" + degree);
                                break;

//                            case "MLT-BT05":
//                                byte[] bytess = {0x01, 0x01, 0x00, 0x00, 0x00};
//                                BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_TX, bytess, new BleWriteCallback() {
//                                    @Override
//                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
//                                        Log.i("test", "小车启动！");
//                                    }
//
//                                    @Override
//                                    public void onWriteFailure(BleException exception) {
//
//                                    }
//                                });
//                                break;

                            default:
                                break;

                        }


                        for (int i = 0; i < bleDevices.size(); i++) {
                            if (bleDevices.get(i).getName().equals("car1")) {
                                bleDevice2 = bleDevices.get(i);
                            } else if (bleDevices.get(i).getName().equals("car2")) {
                                bleDevice3 = bleDevices.get(i);
                            } else if (bleDevices.get(i).getName().equals("car3")) {
                                bleDevice4 = bleDevices.get(i);
                            }
                        }

                        switch (bleDevice1.getMac()) {
                            case "8A:DF:7D:E1:E5:DA":
                                if (bleDevice2 != null) {
                                    // String received = hexStr2Str(HexUtil.formatHexString(data, false));
                                    byte[] send = {data[data.length - 1]};
                                    if (!HexUtil.formatHexString(send).equals("00")) {
                                        reaction2(bleDevice1, bleDevice2, send);
                                    } else {
                                        byte[] temp = {0x68};
                                        reaction2(bleDevice1, bleDevice2, temp);
                                    }
                                }
                                try {
                                    if (line) {
                                        byte[] bytes = {31, data[3], 1, 0, 0};
                                        send(bytes, InetAddress.getByName("192.168.3.6"), 11028);
                                    } else {
                                        byte[] bytes = {31, data[3], 0, 0, 0,};
                                        send(bytes, InetAddress.getByName("192.168.3.6"), 11028);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case "13:8B:45:B6:C3:8F":
                                if (bleDevice3 != null) {
                                    byte[] send = {data[data.length - 1]};
                                    if (!HexUtil.formatHexString(send).equals("00")) {
                                        reaction2(bleDevice1, bleDevice3, send);
                                    } else {
                                        byte[] temp = {0x68};
                                        reaction2(bleDevice1, bleDevice3, temp);
                                    }
                                }
                                try {
                                    if (line) {
                                        byte[] bytes = {32, data[3], 1, 0, 0};
                                        send(bytes, InetAddress.getByName("192.168.3.6"), 11028);
                                    } else {
                                        byte[] bytes = {32, data[3], 0, 0, 0,};
                                        send(bytes, InetAddress.getByName("192.168.3.6"), 11028);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case "86:62:93:E1:AF:43":
                                if (bleDevice4 != null) {
                                    byte[] send = {data[data.length - 1]};
                                    if (!HexUtil.formatHexString(send).equals("00")) {
                                        reaction2(bleDevice1, bleDevice4, send);
                                    } else {
                                        byte[] temp = {0x68};
                                        reaction2(bleDevice1, bleDevice4, temp);
                                    }
                                }
                                try {
                                    if (line) {
                                        byte[] bytes = {33, data[3], 1, 0, 0};
                                        send(bytes, InetAddress.getByName("192.168.3.6"), 11028);
                                    } else {
                                        byte[] bytes = {33, data[3], 0, 0, 0,};
                                        send(bytes, InetAddress.getByName("192.168.3.6"), 11028);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case "2B:A5:EE:F8:03:5F":
                                byte[] send = {21, data[data.length - 1], 0, 0, 0};
                                try {
                                    send(send, InetAddress.getByName("192.168.3.6"), 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            default:
                                break;

                        }

                    }
                });


                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tv_name, tv_received;
        public EditText et_send;
        public Button bt_send;
    }

    private void addReceivedText(ViewHolder viewHolder, String content) {

        TextView textView = viewHolder.tv_received;
        textView.append(content);
        //textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static String str2HexStr(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public void reaction(BleDevice frombleDevice, String received, BleDevice tobleDevice, String send) {

        if (frombleDevice != null && tobleDevice != null) {
            BleManager.getInstance().write(tobleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(str2HexStr(send)), new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }
    }

    public void reaction2(BleDevice frombleDevice, BleDevice tobleDevice, byte[] bytes) {

        if (frombleDevice != null && tobleDevice != null) {
            BleManager.getInstance().write(tobleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });

        }
    }

    class CheThread extends Thread {
        @Override
        public void run() {
            super.run();
            BleDevice cheDevice = bleche;
            int direction = 1;
            byte[] bytess = {0x01, 0x01, 0x00, 0x00, 0x00};
            BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytess, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.i("test", "小车启动！");
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            });


            while (true) {


                if (distance < 30) {
                    if (direction == 1) {
                        direction = 2;
                        byte[] bytes = {0x01, 0x04, 0x00, 0x00, 0x00};
                        BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {

                            }

                            @Override
                            public void onWriteFailure(BleException exception) {

                            }
                        });
                    }
                }

                if (direction == 2) {
                    if (degree < -75) {
                        degree = 0;
                        if (distance <= 30) {
                            direction = 3;
                            byte[] bytes = {0x01, 0x03, 0x00, 0x00, 0x00};
                            BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        } else if (distance > 30) {
                            direction = 1;
                            byte[] bytes = {0x01, 0x01, 0x00, 0x00, 0x00};
                            BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }
                    }

                } else if (direction == 3) {
                    if (degree > 150) {
                        degree = 0;
                        if (distance <= 30) {
                            direction = 0;
                            byte[] bytes = {0x01, 0x02, 0x00, 0x00, 0x00};
                            BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        } else if (distance > 30) {
                            direction = 1;
                            byte[] bytes = {0x01, 0x01, 0x00, 0x00, 0x00};
                            BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }

                    }
                } else if (direction == 0) {
                    direction = -1;
                    try {
                        sleep(3000);
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] bytes = {0x01, 0x00, 0x00, 0x00, 0x00};
                    BleManager.getInstance().write(cheDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                }


            }


        }
    }


    public void send(byte[] command, InetAddress ip, int port) throws IOException {

        new Thread() {
            @Override
            public void run() {
                super.run();

                DatagramPacket dp = new DatagramPacket(command, command.length, ip, port);
                System.out.println(ip);

                try {
                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ds.close();
                    Log.i("test", "发送成功:" + command + " " + ip + " " + port);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public class receive extends Thread {
        DatagramSocket ds;
        Message message;

        @Override
        public void run() {
            super.run();

            try {
                byte[] bufferIn = new byte[1024];
                DatagramPacket dp = new DatagramPacket(bufferIn, bufferIn.length);
                ds = new DatagramSocket(11028, InetAddress.getByName("192.168.2.5"));
                if (ds != null) {
                    while (true) {
                        ds.receive(dp);

                        message.what = 500;
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("in", dp.getData());
                        message.setData(bundle);
                        mHandler.sendMessage(message);


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        //注意下面的“PopupActivity”类是MyHandler类所在的外部类，即所在的activity
        WeakReference<MutiBluetoothControlActivity> mActivity;

        MyHandler(MutiBluetoothControlActivity activity) {
            mActivity = new WeakReference<MutiBluetoothControlActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MutiBluetoothControlActivity theActivity = mActivity.get();
            switch (msg.what) {
                //此处可以根据what的值处理多条信息
                case 500:
                    byte[] bytes = msg.getData().getByteArray("in");
                    byte[] newbytes = new byte[4];
                    for (int i = 0; i < newbytes.length; i++) {
                        newbytes[i] = bytes[i + 1];
                    }
                    BleDevice mbleDevice = null;
                    BleManager.getInstance().write(mbleDevice, Service_uuid, Characteristic_uuid_TX, newbytes, new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {

                        }
                    });
                    break;

                default:
                    break;

            }
        }
    }

    public class car {
        String mac_fang;
        String mac_che;
        int number;

        car(int number, String mac_fang, String mac_che) {
            this.number = number;
            this.mac_fang = mac_che;
            this.mac_che = mac_che;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetoothcontrol_menu, menu);
        menu.findItem(R.id.menu_NET).setVisible(true);
        menu.findItem(R.id.menu_NEU).setVisible(true);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_NET:
                line = true;
                if (bleDevice2 != null && bleDevice3 != null && bleDevice4 != null) {
                    BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("E2"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Toast.makeText(MutiBluetoothControlActivity.this, "开始写NET", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });
                    BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("E4"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });
                    BleManager.getInstance().write(bleDevice4, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("E6"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Toast.makeText(MutiBluetoothControlActivity.this, "开始写NET", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });

                } else {
                    Toast.makeText(MutiBluetoothControlActivity.this, "car1 or car2 or car3未连接", Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.menu_NEU:
                line = true;
                if (bleDevice2 != null && bleDevice3 != null && bleDevice4 != null) {
                    BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("D0"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                            Toast.makeText(MutiBluetoothControlActivity.this, "开始写NEU", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });
                    BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("D1"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });
                    BleManager.getInstance().write(bleDevice4, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("D2"), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        }

                        @Override
                        public void onWriteFailure(BleException exception) {
                        }
                    });

                } else {
                    Toast.makeText(MutiBluetoothControlActivity.this, "car1 or car2 or car3未连接", Toast.LENGTH_LONG).show();

                }
                break;
            default:
                break;

        }

        return true;
    }

}
