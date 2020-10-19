package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.fangfangtech.oidbox.R;
import com.hjq.base.BaseDialog;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.util.List;

import ExtraUtil.XToastUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CustomAdapt;
import myutil.QuestionBank;
import myutil.RandomSort;

public class GameQuestionActivity extends Activity implements CustomAdapt {

    @BindView(R.id.Titlebar_questiongame)
    TitleBar titlebar;
    @BindView(R.id.TextView_questiongame_number)
    TextView tv_Number;
    @BindView(R.id.RoundButton_questiongame_competition)
    RoundButton bt_Competition;
    @BindView(R.id.RoundButton_questiongame_exercise)
    RoundButton bt_Exercise;
    @BindView(R.id.TextView_questiongame_time)
    TextView tv_Time;
    @BindView(R.id.TextView_questiongame_question)
    TextView tv_Question;
    @BindView(R.id.ImageButton_question_refresh)
    ImageButton bt_Refresh;
    @BindView(R.id.imagebutton_question_next)
    ImageButton bt_Next;

    public static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    Context context = GameQuestionActivity.this;
    List<BleDevice> bleDevices;
    int[] RandomQuestion;
    RandomSort randomSort;
    SharedPreferences sharedPreferences;
    int TotalTime = 10;
    int CurTime = 0;
    int CurQuestion = 0;
    int CurQuestionID = 0;

    BleDevice bleDevice1 = null;
    BleDevice bleDevice2 = null;
    BleDevice bleDevice3 = null;
    boolean isSend1 = false;
    byte[] curSend1;
    int FangResultGrade1 = 0;
    int FangResultGrade2 = 0;
    int FangResultGrade3 = 0;
    float FangResultTime1 = 0;
    float FangResultTime2 = 0;
    float FangResultTime3 = 0;
    BleWriteCallback FangBleWriteCallback;
    BleNotifyCallback FangBleNotifyCallback;
    CountThread countThread;
    CheckSendThread checkSendThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_questiongame);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);

        NoHttp.initialize(this);
        Init();
        InitListener();
        bt_Exercise.setVisibility(View.GONE);
        Refresh();
    }

    private void Init() {
        bleDevices = BleManager.getInstance().getAllConnectedDevice();

        for (int i = 0; i < bleDevices.size(); i++) {

            if ((bleDevices.get(i).getName().contains("OidBox") && bleDevices.size() == 1) || bleDevices.get(i).getName().contains("01")) {
                bleDevice1 = bleDevices.get(i);

                FangBleNotifyCallback = new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {

                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {

                        Log.i("test", "fang1 receive " + HexUtil.formatHexString(data, true));

                        switch (data[5]) {
                            case 0x32://F0 06 68 01 01 32  34 16
                                isSend1 = true;
                                CurQuestion = RandomQuestion[CurQuestionID];
                                String temp =
                                        getResources().getString(QuestionBank.getStringResId("Que" + CurQuestion + "_Q", context)) + "\n" + getResources().getString(QuestionBank.getStringResId("Que"
                                                + CurQuestion + "_A", context));
                                tv_Question.setText(temp);
                                CurQuestionID++;
                                tv_Number.setText("第" + CurQuestionID + "题");

                                if (countThread != null) {
                                    if (!countThread.isInterrupted()) {
                                        countThread.interrupt();
                                    }
                                }
                                countThread = new CountThread();
                                countThread.start();
                                break;

                            case 0x33://F0 0B 68 01 06 33 05 01 02 03 04 05 CS 16
                                FangResultGrade1 = data[6];
                                FangResultTime1 = data[7] + data[8] + data[9] + data[10] + data[11];
                                FangResultTime1 = FangResultTime1 / 10;

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        String phone = sharedPreferences.getString("phone", null);
                                        if (phone != null) {
                                            StringRequest request = new StringRequest("http://121.36.30.71:8888/OidBox/oidboxdata", RequestMethod.POST);
                                            request.set("game", "question");
                                            request.set("result", String.valueOf((int) (FangResultTime1 * 10)));
                                            request.set("phone", phone);
                                            Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                                            if (response.isSucceed()) {

                                                // 请求成功。
                                            } else {
                                                // 请求失败，拿到错误：
                                                Exception e = response.getException();
                                            }
                                        }
                                    }
                                }.start();

                                tv_Number.setText("答题结束");
                                tv_Question.setText("谢谢答题");

                                if (!isDestroyed()) {
                                    BaseDialog baseDialog = new BaseDialog(context);
                                    baseDialog.setContentView(R.layout.dialog_table_result);
                                    TextView textView = baseDialog.getContentView().findViewById(R.id.dialogtv_table_result);
                                    textView.setText(FangResultGrade1 + "分\n" + FangResultTime1 + "秒");
                                    baseDialog.show();
                                }
                                break;

                            default:
                                break;
                        }


                    }
                };
                BleManager.getInstance().notify(bleDevice1, Service_uuid, Characteristic_uuid_notify, FangBleNotifyCallback);

            } else {

            }
        }

        FangBleWriteCallback = new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "fang send:" + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang send failed:" + exception.toString());
            }
        };


    }

    private void InitListener() {

        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bt_Competition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CurQuestionID = 0;
                randomSort = new RandomSort(171);
                randomSort.changePosition();
                randomSort.changePosition();
                randomSort.changePosition();
                RandomQuestion = randomSort.getIntPositions();

                tv_Number.setText("准备答题");
                tv_Time.setText("倒计时 " + TotalTime);
                tv_Question.setText("这里是题目\n这里是选项");

                bt_Competition.clearAnimation();
                bt_Competition.setVisibility(View.GONE);
                bt_Exercise.setVisibility(View.GONE);
                tv_Number.setVisibility(View.VISIBLE);
                tv_Time.setVisibility(View.VISIBLE);
                tv_Question.setVisibility(View.VISIBLE);
                bt_Refresh.setVisibility(View.VISIBLE);

                curSend1 = HexUtil.hexStringToBytes("F00B6801063201020304050016");
                if (bleDevice1 != null) {
                    curSend1[6] = R_Send(getResources().getString(QuestionBank.getStringResId("Que" + RandomQuestion[0] + "_R", context)));
                    curSend1[7] = R_Send(getResources().getString(QuestionBank.getStringResId("Que" + RandomQuestion[1] + "_R", context)));
                    curSend1[8] = R_Send(getResources().getString(QuestionBank.getStringResId("Que" + RandomQuestion[2] + "_R", context)));
                    curSend1[9] = R_Send(getResources().getString(QuestionBank.getStringResId("Que" + RandomQuestion[3] + "_R", context)));
                    curSend1[10] = R_Send(getResources().getString(QuestionBank.getStringResId("Que" + RandomQuestion[4] + "_R", context)));
                    curSend1[11] = (byte) ((1 + 6 + 0x32 + curSend1[6] + curSend1[7] + curSend1[8] + curSend1[9] + curSend1[10]) % 256);

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680101333516"), FangBleWriteCallback);
                                sleep(500);
                                BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, curSend1, FangBleWriteCallback);

                                checkSendThread = new CheckSendThread();
                                checkSendThread.start();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();


                } else {
                    XToastUtils.warning("没有小方连接！");
                }


            }
        });

        bt_Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Refresh();
            }
        });

    }


    private class CountThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                for (int i = 0; i < TotalTime; i++) {
                    CurTime = TotalTime - i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_Time.setText("倒计时 " + CurTime);
                        }
                    });
                    sleep(1000);
                }
                CurTime = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (CurQuestionID > 5) {
                            tv_Number.setText("答题结束");
                            tv_Question.setText("谢谢答题");
                        } else {
                            tv_Time.setText("倒计时 " + CurTime);
                        }
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    private class CheckSendThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                int count = 0;
                sleep(300);
                while (!isSend1 && count < 2) {
                    BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, curSend1, FangBleWriteCallback);
                    count++;
                    sleep(300);
                }
                if (!isSend1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            XToastUtils.error("小方方答题开始失败!");
                        }
                    });
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void Refresh() {

        bt_Competition.setVisibility(View.VISIBLE);
        bt_Exercise.setVisibility(View.INVISIBLE);
        tv_Number.setVisibility(View.INVISIBLE);
        tv_Time.setVisibility(View.INVISIBLE);
        tv_Question.setVisibility(View.INVISIBLE);

//        if (bt_Competition.getVisibility() == View.GONE) {
//            ViewUtils.fadeIn(bt_Competition, 1000, null);
//        }
//        if (bt_Exercise.getVisibility() == View.GONE) {
//            //ViewUtils.fadeIn(bt_Exercise, 1000, null);
//        }
//        if (tv_Number.getVisibility() == View.VISIBLE) {
//            ViewUtils.fadeOut(tv_Number, 1000, null);
//        }
//
//        if (tv_Time.getVisibility() == View.VISIBLE) {
//            ViewUtils.fadeOut(tv_Time, 1000, null);
//        }
//        if (tv_Question.getVisibility() == View.VISIBLE) {
//            ViewUtils.fadeOut(tv_Question, 1000, null);
//        }
        bt_Refresh.setVisibility(View.INVISIBLE);

        if (countThread != null) {
            if (!countThread.isInterrupted()) {
                countThread.interrupt();
                BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680101333516"), FangBleWriteCallback);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countThread != null) {
            if (!countThread.isInterrupted()) {
                countThread.interrupt();
                XToastUtils.warning("游戏停止！");
            }
        }
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return (1920f / 2.875f);
    }

    byte R_Send(String number) {
        byte mbyte = 0;

        switch (number) {
            case "1":
                mbyte = 0x04;
                break;
            case "2":
                mbyte = 0x03;
                break;
            case "3":
                mbyte = 0x02;
                break;
            case "4":
                mbyte = 0x01;
                break;

            default:
                break;
        }


        return mbyte;
    }
}
