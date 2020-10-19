package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.example.fangfang_gai.R;

import java.util.List;

public class MutiBluetoothPress extends Activity {
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    List<BleDevice> bleDevices;
    BleDevice curBleDevice, bleDevice1 = null, bleDevice2 = null, bleDevice3 = null;

    Spinner mSpinner;
    Button mswitch, stop, forward, back, left, right, round, n45, y45, n135, y135;
    TextView state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutibluetoothpress);

        bleDevices = BleManager.getInstance().getAllConnectedDevice();


        for (int i = 0; i < bleDevices.size(); i++) {
            if (bleDevices.get(i).getName().equals("car1")) {
                bleDevice1 = bleDevices.get(i);
            } else if (bleDevices.get(i).getName().equals("car2")) {
                bleDevice2 = bleDevices.get(i);
            } else if (bleDevices.get(i).getName().equals("car3")) {
                bleDevice3 = bleDevices.get(i);
            }
        }

        curBleDevice = bleDevice1;

        mSpinner = findViewById(R.id.mutibluepress_spinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        curBleDevice = bleDevice1;
                        break;
                    case 1:
                        curBleDevice = bleDevice2;
                        break;
                    case 2:
                        curBleDevice = bleDevice3;
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mswitch = findViewById(R.id.mutibluepress_switch);
        stop = findViewById(R.id.mutibluepress_stop);
        forward = findViewById(R.id.mutibluepress_forward);
        back = findViewById(R.id.mutibluepress_back);
        left = findViewById(R.id.mutibluepress_left);
        right = findViewById(R.id.mutibluepress_right);
        round = findViewById(R.id.mutibluepress_round);
        n45 = findViewById(R.id.mutibluepress_n45);
        y45 = findViewById(R.id.mutibluepress_y45);
        n135 = findViewById(R.id.mutibluepress_n135);
        y135 = findViewById(R.id.mutibluepress_y135);

        state = findViewById(R.id.mutibluepress_state);

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curBleDevice != null) {
                    byte[] bytes = new byte[1];
                    switch (v.getId()) {
                        case R.id.mutibluepress_switch:
                            bytes[0] = 0x1B;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_stop:
                            bytes[0] = -55;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_forward:
                            bytes[0] = -64;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_back:
                            bytes[0] = -63;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_left:
                            bytes[0] = -62;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_right:
                            bytes[0] = -61;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_n45:
                            bytes[0] = -60;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_y45:
                            bytes[0] = -59;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_n135:
                            bytes[0] = -58;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_y135:
                            bytes[0] = -57;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        case R.id.mutibluepress_round:
                            bytes[0] = -56;
                            BleManager.getInstance().write(curBleDevice, Service_uuid, Characteristic_uuid_TX, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    String string = "Write Success " + HexUtil.formatHexString(justWrite);
                                    state.setText(string);
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;

                        default:
                            break;

                    }
                } else {
                    Toast.makeText(MutiBluetoothPress.this, "当前car未连接", Toast.LENGTH_SHORT).show();
                }
            }
        };

        mswitch.setOnClickListener(mOnClickListener);
        stop.setOnClickListener(mOnClickListener);
        forward.setOnClickListener(mOnClickListener);
        back.setOnClickListener(mOnClickListener);
        left.setOnClickListener(mOnClickListener);
        right.setOnClickListener(mOnClickListener);
        round.setOnClickListener(mOnClickListener);
        n45.setOnClickListener(mOnClickListener);
        y45.setOnClickListener(mOnClickListener);
        n135.setOnClickListener(mOnClickListener);
        y135.setOnClickListener(mOnClickListener);


    }
}
