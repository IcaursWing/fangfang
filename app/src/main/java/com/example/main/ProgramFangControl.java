package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.example.fangfang_gai.R;

import me.jessyan.autosize.internal.CancelAdapt;

public class ProgramFangControl extends Activity implements CancelAdapt {
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";

    BleDevice bleDevice;
    ImageButton up, down, left, right, handup, handdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programfangcontrol);
        this.setTitle("程小方");

        bleDevice = getIntent().getParcelableExtra("bleDevice");

        up = findViewById(R.id.programfang_up);
        down = findViewById(R.id.programfang_down);
        left = findViewById(R.id.programfang_left);
        right = findViewById(R.id.programfang_right);
        handup = findViewById(R.id.programfang_handup);
        handdown = findViewById(R.id.programfang_handdown);

        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.programfang_handup:
                        BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000516"), new BleWriteCallback() {
                            @Override
                            public void onWriteSuccess(int current, int total, byte[] justWrite) {

                            }

                            @Override
                            public void onWriteFailure(BleException exception) {

                            }
                        });
                        break;

                    case R.id.programfang_handdown:
                        BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000616"), new BleWriteCallback() {
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
        };

        ImageButton.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        switch (v.getId()) {
                            case R.id.programfang_up:
                                BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000116"), new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });
                                break;

                            case R.id.programfang_down:
                                BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000216"), new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });
                                break;

                            case R.id.programfang_left:
                                BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000316"), new BleWriteCallback() {
                                    @Override
                                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                    }

                                    @Override
                                    public void onWriteFailure(BleException exception) {

                                    }
                                });
                                break;

                            case R.id.programfang_right:
                                BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000416"), new BleWriteCallback() {
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
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes("68000016"), new BleWriteCallback() {
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


                return false;
            }
        };

        up.setOnTouchListener(onTouchListener);
        down.setOnTouchListener(onTouchListener);
        left.setOnTouchListener(onTouchListener);
        right.setOnTouchListener(onTouchListener);
        handup.setOnClickListener(onClickListener);
        handdown.setOnClickListener(onClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
