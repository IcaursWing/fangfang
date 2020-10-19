package com.example.main;

import android.app.Activity;
import android.media.MediaPlayer;
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
import com.hb.dialog.myDialog.MyAlertInputDialog;

import java.util.List;

import me.jessyan.autosize.internal.CancelAdapt;

public class MutiFangfangControl extends Activity implements CancelAdapt {

    ImageButton up, down, left, right, cube, human, sit, dance, tetrapod, actionID;
    public static String Service_uuid = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "0000ffe1-0000-1000-8000-00805f9b34fb";
    List<BleDevice> bleDevices;
    MediaPlayer mMediaPlayer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_activity_test);

        bleDevices = BleManager.getInstance().getAllConnectedDevice();

        up = findViewById(R.id.GrayUp);
        down = findViewById(R.id.GrayDown);
        left = findViewById(R.id.GrayLeft);
        right = findViewById(R.id.GrayRight);
        cube = findViewById(R.id.GrayCube);
        human = findViewById(R.id.GrayHumanoid);
        sit = findViewById(R.id.GraySit);
        tetrapod = findViewById(R.id.GrayTetrapod);
        dance = findViewById(R.id.BlueDance);
        actionID = findViewById(R.id.blue_actionid);
        dance.setVisibility(View.VISIBLE);


        up.setImageResource(R.drawable.blue_up);
        down.setImageResource(R.drawable.blue_down);
        left.setImageResource(R.drawable.blue_left);
        right.setImageResource(R.drawable.blue_right);
        cube.setImageResource(R.drawable.blue_cube);
        human.setImageResource(R.drawable.blue_humanoid);
        sit.setImageResource(R.drawable.blue_sit);
        tetrapod.setImageResource(R.drawable.blue_tetrapod);


        ImageButton.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.blue_actionid:
                        final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(MutiFangfangControl.this).builder().setTitle("请输入要发送的动作ID").setEditText("");
                        myAlertInputDialog.setPositiveButton("发送", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // showMsg(myAlertInputDialog.getResult());
                                int mID = Integer.valueOf(myAlertInputDialog.getResult());
                                if (mID > 17) {

                                    if(mID==23){
                                        if (mMediaPlayer != null) {
                                            mMediaPlayer.stop();
                                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.banana);
                                            mMediaPlayer.start();
                                        }else {
                                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.banana);
                                            mMediaPlayer.start();
                                        }
                                    }


                                    for (int i = 0; i < bleDevices.size(); i++) {
                                        BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(sendActionbyID(mID)), new BleWriteCallback() {
                                            @Override
                                            public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                            }

                                            @Override
                                            public void onWriteFailure(BleException exception) {

                                            }
                                        });

                                    }


                                }
                                myAlertInputDialog.dismiss();
                            }
                        }).setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // showMsg("取消");
                                myAlertInputDialog.dismiss();
                            }
                        });
                        myAlertInputDialog.show();
                        break;


                    case R.id.BlueDance:
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.happynewyear);
                            mMediaPlayer.start();
                        }else {
                            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.happynewyear);
                            mMediaPlayer.start();
                        }
                        for (int i = 0; i < bleDevices.size(); i++) {
                            BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(sendActionbyID(16)), new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });

                        }
                        break;


                    case R.id.GrayCube:
                        for (int i = 0; i < bleDevices.size(); i++) {
                            BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(sendActionbyID(1)), new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });

                        }
                        break;

                    case R.id.GrayHumanoid:
                        for (int i = 0; i < bleDevices.size(); i++) {
                            BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(sendActionbyID(0)), new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                        }
                        break;


                    default:
                        break;
                }

            }
        };

        ImageButton.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.GrayUp:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(1, 30)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(0, 0)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case R.id.GrayDown:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(2, 30)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(0, 0)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    case R.id.GrayLeft:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(3, 30)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(0, 0)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;


                            default:
                                break;
                        }
                        break;

                    case R.id.GrayRight:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(4, 30)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                for (int i = 0; i < bleDevices.size(); i++) {
                                    BleManager.getInstance().write(bleDevices.get(i), Service_uuid, Characteristic_uuid_TX, HexUtil.hexStringToBytes(MoveControl(0, 0)), new BleWriteCallback() {
                                        @Override
                                        public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                        }

                                        @Override
                                        public void onWriteFailure(BleException exception) {

                                        }
                                    });
                                }
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

        up.setOnTouchListener(onTouchListener);
        down.setOnTouchListener(onTouchListener);
        left.setOnTouchListener(onTouchListener);
        right.setOnTouchListener(onTouchListener);
        cube.setOnClickListener(onClickListener);
        human.setOnClickListener(onClickListener);
        sit.setOnClickListener(onClickListener);
        tetrapod.setOnClickListener(onClickListener);
        dance.setOnClickListener(onClickListener);

        actionID.setOnClickListener(onClickListener);

    }

    public String sendActionbyID(int actionID) {
        String maction = "01" + str2HexStr(String.valueOf(actionID)) + str2HexStr("-");


        int length = 2 + String.valueOf(actionID).length();
        int check = 0;

        if (actionID > 9) {
            check = (length + 21 + 1 + Integer.parseInt(str2HexStr(String.valueOf((actionID - actionID % 10) / 10)), 16) + Integer.parseInt(str2HexStr(String.valueOf(actionID % 10)), 16) + Integer.parseInt(str2HexStr("-"), 16)) % 256;
        } else {
            check = (length + 21 + 1 + Integer.parseInt(str2HexStr(String.valueOf(actionID)), 16) + Integer.parseInt(str2HexStr("-"), 16)) % 256;
        }


        String hex_check = Integer.toHexString(check);

        String result = "6897" + "0" + Integer.toHexString(length) + "15" + maction + hex_check.substring(hex_check.length() - 2, hex_check.length()) + "16";
        return result;
    }

    public static String str2HexStr(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    public String MoveControl(int direction, int speed) {
        /** 1 = w, 2 = s, 3 = a, 4 = d , 0 = stop */
        int temp = speed;
        String speed_hex = Integer.toHexString(temp);
        String speed_hex_ = Integer.toHexString(temp + 128);
        String final_control = null;
        if (speed < 10) {
            speed_hex = "0" + speed_hex;
        }
        int check = 0;
        switch (direction) {
            case 0:
                final_control = speed_hex + speed_hex;
                check = (2 + 0x13 + 0 + 0) % 256;
                break;
            case 1:
                final_control = speed_hex_ + speed_hex;
                check = (2 + 0x13 + speed + 128 + speed) % 256;
                break;
            case 2:
                final_control = speed_hex + speed_hex_;
                check = (2 + 0x13 + speed + speed + 128) % 256;
                break;
            case 3:
                final_control = speed_hex + speed_hex;
                check = (2 + 0x13 + speed + speed) % 256;
                break;
            case 4:
                final_control = speed_hex_ + speed_hex_;
                check = (2 + 0x13 + speed + 128 + speed + 128) % 256;
                break;

            default:
                break;
        }

        String result = "68970213" + final_control + Integer.toHexString(check) + "16";
        return result;
    }

}
