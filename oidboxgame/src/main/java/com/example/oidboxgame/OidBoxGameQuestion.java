package com.example.oidboxgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.List;

import me.jessyan.autosize.internal.CustomAdapt;
import myutil.DataTypeConversion;
import myutil.QuestionBank;
import myutil.RandomSort;

public class OidBoxGameQuestion extends Activity implements CustomAdapt {

    public static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    List<BleDevice> bleDevices;
    TextView tv_id, tv_question, tv_answer;
    TextView tv_Time, tv_Result, tv_State;
    Button bt_Start, bt_Reset, bt_Result, bt_Rank;

    int[] RandomQuestion;
    RandomSort randomSort;
    int TotalTime = 10;
    int CurTime = 0;
    int CurQuestion = 0;
    int CurQuestionID = 0;

    BleDevice bleDevice1 = null;
    BleDevice bleDevice2 = null;
    BleDevice bleDevice3 = null;
    int FangResultGrade1 = 0;
    int FangResultGrade2 = 0;
    int FangResultGrade3 = 0;
    float FangResultTime1 = 0;
    float FangResultTime2 = 0;
    float FangResultTime3 = 0;

    CountThread mCountThread = null;
    Handler mHandler;
    BleWriteCallback FangBleWriteCallback;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oidboxquestion);
        Init();
        bleDevices = BleManager.getInstance().getAllConnectedDevice();

        bt_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_Start.setVisibility(View.INVISIBLE);
                mCountThread = new CountThread();
                mCountThread.start();
            }
        });

        bt_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCountThread != null) {
                    if (!mCountThread.isInterrupted()) {
                        mCountThread.interrupt();
                    }
                }

                randomSort = new RandomSort(300);
                randomSort.changePosition();
                randomSort.changePosition();
                randomSort.changePosition();
                RandomQuestion = randomSort.getIntPositions();

                tv_Result.setVisibility(View.INVISIBLE);
                tv_State.setText("倒计时");
                tv_Time.setVisibility(View.VISIBLE);
                bt_Start.setVisibility(View.VISIBLE);
                bt_Reset.setVisibility(View.INVISIBLE);
                bt_Result.setVisibility(View.INVISIBLE);


                tv_Time.setText(TotalTime + "秒");


                FangResultGrade1 = 0;
                FangResultGrade2 = 0;
                FangResultGrade3 = 0;
                FangResultTime1 = 0;
                FangResultTime2 = 0;
                FangResultTime3 = 0;
                tv_id.setText(" ");
                tv_question.setText(" ");
                tv_answer.setText(" ");
                tv_Time.setText(TotalTime + "秒");

            }
        });

        bt_Result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_State.setText("成绩");
                tv_State.setVisibility(View.VISIBLE);
                tv_Time.setVisibility(View.INVISIBLE);
                tv_Result.setText("1号：" + FangResultGrade1 + "/5 " + FangResultTime1 + "秒\n2号：" + FangResultGrade2 + "/5 " + FangResultTime2 + "秒\n3号：" + FangResultGrade3 + "/5 " + FangResultTime3 + "秒");
                tv_Result.setVisibility(View.VISIBLE);
            }
        });

        bt_Rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        for (int i = 0; i < bleDevices.size(); i++) {
            if ((bleDevices.get(i).getName().contains("OidBox") && bleDevices.size() == 1) || bleDevices.get(i).getName().contains("01")) {
                bleDevice1 = bleDevices.get(i);
                BleManager.getInstance().notify(bleDevice1, Service_uuid, Characteristic_uuid_notify, new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i("test", "fang1 receive " + HexUtil.formatHexString(data, true));
                        FangResultGrade1 = data[6];
                        FangResultTime1 = data[7] + data[8] + data[9] + data[10] + data[11];
                        FangResultTime1 = FangResultTime1 / 10;
                    }
                });
            } else if (bleDevices.get(i).getName().contains("02")) {
                bleDevice2 = bleDevices.get(i);
                BleManager.getInstance().notify(bleDevice2, Service_uuid, Characteristic_uuid_notify, new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i("test", "fang2 receive " + HexUtil.formatHexString(data, true));
                        FangResultGrade2 = data[6];
                        FangResultTime2 = data[7] + data[8] + data[9] + data[10] + data[11];
                        FangResultTime2 = FangResultTime2 / 10;

                    }
                });
            } else if (bleDevices.get(i).getName().contains("03")) {
                bleDevice3 = bleDevices.get(i);
                BleManager.getInstance().notify(bleDevice3, Service_uuid, Characteristic_uuid_notify, new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i("test", "fang3 receive " + HexUtil.formatHexString(data, true));
                        FangResultGrade3 = data[6];
                        FangResultTime3 = data[7] + data[8] + data[9] + data[10] + data[11];
                        FangResultTime3 = FangResultTime3 / 10;
                    }
                });
            }
        }//end for blelists

        FangBleWriteCallback = new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "fang" + (int) justWrite[3] + "send " + HexUtil.formatHexString(justWrite, true));
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
                    case 1:
                        tv_Time.setText(CurTime + "秒");
                        break;
                    case 2:
                        tv_id.setText("第" + CurQuestionID + "题：");
                        tv_question.setText(QuestionBank.getStringResId("Que" + (CurQuestion + 1) + "_Q", OidBoxGameQuestion.this));
                        tv_answer.setText(QuestionBank.getStringResId("Que" + (CurQuestion + 1) + "_A", OidBoxGameQuestion.this));


                        byte[] bytes = new byte[9];
                        byte[] byteint = DataTypeConversion.int2byteH2L(CurQuestion + 1);

                        if (bleDevice1 != null) {
                            bytes = HexUtil.hexStringToBytes("F0086801033200001116");
                            bytes[6] = byteint[2];
                            bytes[7] = byteint[3];
                            bytes[8] = (byte) ((1 + 3 + 0x32 + bytes[6] + bytes[7]) % 256);
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, FangBleWriteCallback);
                        }

                        if (bleDevice2 != null) {
                            bytes = HexUtil.hexStringToBytes("F0086802033200001116");
                            bytes[6] = byteint[2];
                            bytes[7] = byteint[3];
                            bytes[8] = (byte) ((2 + 3 + 0x32 + bytes[6] + bytes[7]) % 256);
                            BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, bytes, FangBleWriteCallback);
                        }

                        if (bleDevice3 != null) {
                            bytes = HexUtil.hexStringToBytes("F0086803033200001116");
                            bytes[6] = byteint[2];
                            bytes[7] = byteint[3];
                            bytes[8] = (byte) ((3 + 3 + 0x32 + bytes[6] + bytes[7]) % 256);
                            BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, bytes, FangBleWriteCallback);
                        }
                        break;

                    case 3:

                        break;
                    default:
                        break;
                }
            }
        };


        bt_Start.setVisibility(View.INVISIBLE);
        bt_Result.setVisibility(View.INVISIBLE);
    }


    private void Init() {
        tv_id = findViewById(R.id.tv_oidboxquestionid);
        tv_question = findViewById(R.id.tv_oidboxquestion);
        tv_answer = findViewById(R.id.tv_oidboxanswer);
        tv_Time = findViewById(R.id.tv_oidboxquestiontime);
        tv_Result = findViewById(R.id.tv_OidBoxQuestion_Result);
        bt_Start = findViewById(R.id.bt_OidBoxQuestion_Start);
        bt_Reset = findViewById(R.id.bt_OidBoxQuestion_Reset);
        bt_Result = findViewById(R.id.bt_OidBoxQuestion_Result);
        bt_Rank = findViewById(R.id.bt_OidBoxQuestion_Rank);
        tv_State = findViewById(R.id.tv_OidBoxQuestion_State);
        tv_Result.setVisibility(View.INVISIBLE);

    }

    private class CountThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                for (int i = 0; i < 5; i++) {
                    CurQuestion = RandomQuestion[i];
                    CurQuestionID = i + 1;
                    Message message = new Message();
                    message.what = 2;
                    mHandler.sendMessage(message);


                    for (int j = 0; j < TotalTime; j++) {
                        CurTime = TotalTime - j;
                        message = new Message();
                        message.what = 1;
                        mHandler.sendMessage(message);
                        sleep(1000);
                    }
                    CurTime = 0;
                    message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_id.setText("答题结束");
                    tv_question.setText("测试完毕");
                    tv_answer.setText("谢谢！");
                    bt_Reset.setVisibility(View.VISIBLE);
                    bt_Result.setVisibility(View.VISIBLE);
                }
            });

            if (bleDevice1 != null) {
                BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680101333516"), FangBleWriteCallback);
            }
            if (bleDevice2 != null) {
                BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680201333616"), FangBleWriteCallback);
            }
            if (bleDevice3 != null) {
                BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680301333716"), FangBleWriteCallback);
            }


        }


    }


    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {


        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Service.WINDOW_SERVICE);
        if (manager != null) {
            manager.getDefaultDisplay().getMetrics(metrics);
        }
        float density = metrics.density;
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        float scaledDensity = metrics.scaledDensity;
        Log.i("test", screenHeight + " " + screenWidth + "\\" + density + " " + scaledDensity);

        return 380;
    }
}
