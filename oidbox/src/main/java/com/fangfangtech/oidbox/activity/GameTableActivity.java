package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.fangfangtech.oidbox.R;
import com.hjq.base.BaseDialog;
import com.xuexiang.constant.DateFormatConstants;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xui.widget.picker.widget.OptionsPickerView;
import com.xuexiang.xui.widget.picker.widget.builder.OptionsPickerBuilder;
import com.xuexiang.xui.widget.picker.widget.listener.OnOptionsSelectListener;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.data.DateUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import java.text.SimpleDateFormat;
import java.util.List;

import ExtraUtil.XToastUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CustomAdapt;
import myutil.DataTypeConversion;
import myutil.RandomSort;
import myutil.UnsignedUtil;
import ui.MessageDialog;

public class GameTableActivity extends Activity implements CustomAdapt {

    @BindView(R.id.imagebutton_table_exit)
    ImageButton bt_TableExit;
    @BindView(R.id.imagebutton_table_refresh)
    ImageButton bt_TableRefresh;
    @BindView(R.id.imagebutton_table_result)
    ImageButton bt_TableResult;
    @BindView(R.id.textview_table_time)
    TextView tv_TableTime;
    @BindView(R.id.linearlayout_tablegame_timeinfo)
    LinearLayout linearlayout_Timeinfo;
    @BindView(R.id.switchbutton_gametime)
    SwitchButton switchbutton_Gametime;
    @BindView(R.id.supertextview_tabletimeset)
    SuperTextView supertextview_Tabletimeset;
    @BindView(R.id.imagebutton_table_start)
    ImageButton bt_TableStart;
    @BindView(R.id.linearlayout_tablegame_timeset)
    LinearLayout linearlayout_Timeset;
    @BindView(R.id.textview_tablegame_table1)
    TextView tv_Table1;
    @BindView(R.id.textview_tablegame_table2)
    TextView tv_Table2;
    @BindView(R.id.textview_tablegame_table3)
    TextView tv_Table3;
    @BindView(R.id.textview_tablegame_table4)
    TextView tv_Table4;
    @BindView(R.id.textview_tablegame_table5)
    TextView tv_Table5;
    @BindView(R.id.textview_tablegame_table6)
    TextView tv_Table6;
    @BindView(R.id.textview_tablegame_table7)
    TextView tv_Table7;
    @BindView(R.id.textview_tablegame_table8)
    TextView tv_Table8;
    @BindView(R.id.textview_tablegame_table9)
    TextView tv_Table9;
    @BindView(R.id.framelayout_tablegame_table)
    FrameLayout framelayout_Table;

    Context context = GameTableActivity.this;
    String[] TimeStrings;
    int TimeOption = 4;
    RandomSort randomSort;
    String[] RandomTable;
    CountThread countThread;

    private static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    private static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    private static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    List<BleDevice> bleDevices;
    BleDevice bleDevice1 = null;
    BleDevice bleDevice2 = null;
    BleDevice bleDevice3 = null;
    float FangResult1;
    float FangResult2;
    float FangResult3;
    boolean Fang1Transfer = false;
    boolean Fang2Transfer = false;
    boolean Fang3Transfer = false;
    BleWriteCallback Fang1BleWriteCallback0;
    BleWriteCallback Fang2BleWriteCallback0;
    BleWriteCallback Fang3BleWriteCallback0;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tablegame);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);

        NoHttp.initialize(this);
        Init();
        InitListener();
        BleInit();
        bleDevices = BleManager.getInstance().getAllConnectedDevice();

        for (int i = 0; i < bleDevices.size(); i++) {
            if ((bleDevices.get(i).getName().contains("OidBox") && bleDevices.size() == 1) || bleDevices.get(i).getName().contains("01")) {
                bleDevice1 = bleDevices.get(i);
                BleManager.getInstance().notify(bleDevice1, Service_uuid, Characteristic_uuid_notify, new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {

                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        XToastUtils.error("1号小方方连接失败！");
                        Log.i("test", exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Log.i("test", "fang01result " + HexUtil.formatHexString(data, true));


                        if (data[4] == 0x03) {
                            FangResult1 = (UnsignedUtil.getUnsignedByte(data[6])) * 256 + UnsignedUtil.getUnsignedByte(data[7]);

                            final String upresult = String.valueOf((int) FangResult1);

                            final String phone = sharedPreferences.getString("phone", null);
                            if (phone != null) {
                                new Thread() {

                                    @Override
                                    public void run() {
                                        super.run();

                                        StringRequest request = new StringRequest("http://121.36.30.71:8888/OidBox/oidboxdata", RequestMethod.POST);
                                        int TotalTime = Integer.valueOf(TimeStrings[TimeOption]);

                                        if (TotalTime > 10 && TotalTime <= 15) {
                                            request.set("game", String.valueOf(11));
                                        } else if (TotalTime > 15 && TotalTime <= 20) {
                                            request.set("game", String.valueOf(12));
                                        } else if (TotalTime > 20) {
                                            request.set("game", String.valueOf(13));
                                        } else {
                                            request.set("game", String.valueOf(TotalTime));
                                        }

                                        request.set("mode", "table");
                                        request.set("result", upresult);
                                        request.set("phone", phone);
                                        request.set("time", DateUtils.getNowString(new SimpleDateFormat(DateFormatConstants.yyyyMMddHHmmss)));

                                        Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                                        if (response.isSucceed()) {

                                            // 请求成功。
                                        } else {
                                            // 请求失败，拿到错误：
                                            Exception e = response.getException();
                                        }


                                    }
                                }.start();
                            }

                            FangResult1 = FangResult1 / 10;

                        } else if (data[5] == 0x35 && data[6] == 0) {
                            FangInteractor(1, 1);
                        } else if (data[5] == 0x35 && data[6] == 1) {
                            Fang1Transfer = true;
                        }

                    }
                });

            } else {


            }

        }


    }

    private void Init() {
        //StatusBarUtils.fullScreen(this);
        TimeStrings = ResUtils.getStringArray(R.array.TimeCount);

    }

    private void InitListener() {

        bt_TableExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MessageDialog.Builder(context)
                        // 标题可以不用填写
                        //.setTitle("退出")
                        // 内容必须要填写
                        .setMessage("是否退出游戏？")
                        // 确定按钮文本
                        .setConfirm("确定")
                        // 设置 null 表示不显示取消按钮
                        .setCancel("取消")
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setListener(new MessageDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog) {
                                finish();
                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                            }
                        }).show();
            }
        });

        bt_TableRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_Table1.setText(" ");
                tv_Table2.setText(" ");
                tv_Table3.setText(" ");
                tv_Table4.setText(" ");
                tv_Table5.setText(" ");
                tv_Table6.setText(" ");
                tv_Table7.setText(" ");
                tv_Table8.setText(" ");
                tv_Table9.setText(" ");

                FangResult1 = 0;
                FangResult2 = 0;
                FangResult3 = 0;

                if (linearlayout_Timeinfo.getVisibility() == View.VISIBLE) {
                    ViewUtils.fadeOut(linearlayout_Timeinfo, 1000, null);
                }
                if (framelayout_Table.getVisibility() == View.VISIBLE) {
                    ViewUtils.slideOut(framelayout_Table, 1000, null, ViewUtils.Direction.LEFT_TO_RIGHT);
                }
                if (linearlayout_Timeset.getVisibility() == View.GONE) {
                    ViewUtils.fadeIn(linearlayout_Timeset, 1000, null);
                }
                if (bt_TableResult.getVisibility() == View.VISIBLE) {
                    ViewUtils.fadeOut(bt_TableResult, 1000, null);
                }


                Fang1Transfer = false;
                Fang2Transfer = false;
                Fang3Transfer = false;


            }
        });

        bt_TableStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_Table1.setText(" ");
                tv_Table2.setText(" ");
                tv_Table3.setText(" ");
                tv_Table4.setText(" ");
                tv_Table5.setText(" ");
                tv_Table6.setText(" ");
                tv_Table7.setText(" ");
                tv_Table8.setText(" ");
                tv_Table9.setText(" ");

                randomSort = new RandomSort(9);
                randomSort.changePosition();
                RandomTable = randomSort.getStringPositions();
                tv_TableTime.setText(TimeStrings[TimeOption]);
                FangInteractor(1, 1);
                FangInteractor(1, 2);

                if (linearlayout_Timeinfo.getVisibility() == View.GONE) {
                    ViewUtils.fadeIn(linearlayout_Timeinfo, 1000, null);
                }
                if (framelayout_Table.getVisibility() == View.GONE) {
                    ViewUtils.slideIn(framelayout_Table, 1000, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            tv_Table1.setText(RandomTable[0]);
                            tv_Table2.setText(RandomTable[1]);
                            tv_Table3.setText(RandomTable[2]);
                            tv_Table4.setText(RandomTable[3]);
                            tv_Table5.setText(RandomTable[4]);
                            tv_Table6.setText(RandomTable[5]);
                            tv_Table7.setText(RandomTable[6]);
                            tv_Table8.setText(RandomTable[7]);
                            tv_Table9.setText(RandomTable[8]);
                            countThread = new CountThread();
                            countThread.start();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    }, ViewUtils.Direction.RIGHT_TO_LEFT);
                }
                if (linearlayout_Timeset.getVisibility() == View.VISIBLE) {
                    ViewUtils.fadeOut(linearlayout_Timeset, 1000, null);
                }


            }
        });

        bt_TableResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_Table1.setText(RandomTable[0]);
                tv_Table2.setText(RandomTable[1]);
                tv_Table3.setText(RandomTable[2]);
                tv_Table4.setText(RandomTable[3]);
                tv_Table5.setText(RandomTable[4]);
                tv_Table6.setText(RandomTable[5]);
                tv_Table7.setText(RandomTable[6]);
                tv_Table8.setText(RandomTable[7]);
                tv_Table9.setText(RandomTable[8]);

                BaseDialog baseDialog = new BaseDialog(context);
                baseDialog.setContentView(R.layout.dialog_table_result);
                TextView textView = baseDialog.getContentView().findViewById(R.id.dialogtv_table_result);
                textView.setText("1号\n" + FangResult1 + "秒");
                baseDialog.show();

            }
        });

        switchbutton_Gametime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switchbutton_Gametime.setThumbDrawableRes(R.mipmap.button_custom);
                    supertextview_Tabletimeset.setClickable(true);
                    supertextview_Tabletimeset.setBackgroundColor(getResources().getColor(R.color.white));

                } else {
                    switchbutton_Gametime.setThumbDrawableRes(R.mipmap.button_default);
                    supertextview_Tabletimeset.setClickable(false);
                    TimeOption = 4;
                    supertextview_Tabletimeset.getCenterTextView().setText(TimeStrings[TimeOption]);
                    supertextview_Tabletimeset.setBackgroundColor(getResources().getColor(R.color.xui_config_color_gray_7));
                }


            }
        });


        supertextview_Tabletimeset.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                OptionsPickerView optionsPickerView = new OptionsPickerBuilder(GameTableActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public boolean onOptionsSelect(View view, int options1, int options2, int options3) {
                        supertextview_Tabletimeset.getCenterTextView().setText(TimeStrings[options1]);
                        TimeOption = options1;
                        return false;
                    }
                }).setTitleText("倒计时设置").setSelectOptions(TimeOption).isDialog(true).build();
                optionsPickerView.setPicker(TimeStrings);
                optionsPickerView.show();

            }
        });


    }

    private void BleInit() {
        Fang1BleWriteCallback0 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {

                Log.i("test", "table1 " + HexUtil.formatHexString(justWrite, true));

            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang1：" + exception.toString());
                if (times < 4) {
                    FangInteractor(1, 1);
                    times++;
                } else {
                    XToastUtils.error("1号小方方发送失败！");
                    times = 0;
                }
            }
        };
    }


    private void FangInteractor(int whichFang, int action) {

        if (whichFang == 1 && bleDevice1 != null) {
            switch (action) {
                case 1:
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            int CS;
                            byte[] bytes;
                            int[] randomsort;
                            int temp;
                            byte[] intbyte;
                            randomsort = randomSort.getIntPositions();
                            temp = 0;
                            for (int i = 0; i < 9; i++) {
                                temp = (int) (randomsort[i]) * (int) (Math.pow(10, 8 - i)) + temp;
                            }

                            intbyte = DataTypeConversion.int2byteH2L(temp);
                            bytes = HexUtil.hexStringToBytes("F00A68010531010203040016");
                            CS = (1 + 5 + 0x31 + intbyte[0] + intbyte[1] + intbyte[2] + intbyte[3]) % 256;
                            bytes[10] = DataTypeConversion.int2byteL2H(CS)[0];
                            bytes[6] = intbyte[0];
                            bytes[7] = intbyte[1];
                            bytes[8] = intbyte[2];
                            bytes[9] = intbyte[3];
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, Fang1BleWriteCallback0);

                        }
                    }.start();

                case 2:

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(500);
                                int i = 0;
                                while (!Fang1Transfer && i < 3) {
                                    FangInteractor(1, 1);
                                    i++;
                                    sleep(500);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (!Fang1Transfer) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        XToastUtils.error("1号小方方接收失败！");
                                        Fang1Transfer = true;
                                    }
                                });
                            }


                        }
                    }.start();

                    break;

                default:
                    break;
            }


        }


    }

    private class CountThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                int CurrentTime = Integer.valueOf(TimeStrings[TimeOption]);

                for (int i = 0; i < Integer.valueOf(TimeStrings[TimeOption]); i++) {
                    sleep(1000);
                    CurrentTime--;
                    int finalCurrentTime = CurrentTime;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_TableTime.setText(finalCurrentTime + "");
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_Table1.setText(" ");
                    tv_Table2.setText(" ");
                    tv_Table3.setText(" ");
                    tv_Table4.setText(" ");
                    tv_Table5.setText(" ");
                    tv_Table6.setText(" ");
                    tv_Table7.setText(" ");
                    tv_Table8.setText(" ");
                    tv_Table9.setText(" ");
                    bt_TableResult.setVisibility(View.VISIBLE);
                }
            });

            while (!Fang1Transfer && !Fang2Transfer && !Fang3Transfer) {
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            BleWriteCallback StartBleWriteCallback = new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.i("test", "table start " + HexUtil.formatHexString(justWrite, true));
                }

                @Override
                public void onWriteFailure(BleException exception) {

                }
            };

            try {
                for (int i = 0; i < 5; i++) {
                    if (bleDevice1 != null) {
                        BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680101353716"), StartBleWriteCallback);
                    }
                    if (bleDevice2 != null) {
                        BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680201353816"), StartBleWriteCallback);
                    }
                    if (bleDevice3 != null) {
                        BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680301353916"), StartBleWriteCallback);
                    }
                    sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        return (1080f / 2.875f);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration configuration = res.getConfiguration();
        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }
        return res;
    }
}
