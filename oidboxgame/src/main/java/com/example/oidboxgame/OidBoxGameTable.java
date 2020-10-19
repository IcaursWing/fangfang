package com.example.oidboxgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;


import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import me.jessyan.autosize.internal.CustomAdapt;
import myutil.DataTypeConversion;
import myutil.RandomSort;
import myutil.UnsignedUtil;

public class OidBoxGameTable extends Activity implements CustomAdapt {


    public static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    List<BleDevice> bleDevices;
    TableLayout tableLayout;
    TextView table1, table2, table3, table4, table5, table6, table7, table8, table9;
    TextView tv_TableTime, tv_Result, tv_State, tv_Rank;
    Button bt_Reset, bt_Start, bt_Result, bt_Rank;
    ScrollView scrollView;
    NiceSpinner spinner;

    String[] RandomTable;
    RandomSort randomSort;
    int TotalTime = 20;
    int CurTime;
    ArrayList<Float> RankList;
    ArrayList<String> RankListID;
    ArrayList<String> RankListTime;
    SharedPreferences sharedPreferences;

    BleDevice bleDevice1 = null;
    BleDevice bleDevice2 = null;
    BleDevice bleDevice3 = null;
    float FangResult1;
    float FangResult2;
    float FangResult3;
    boolean Fang1Transfer = false;
    boolean Fang2Transfer = false;
    boolean Fang3Transfer = false;

    Handler mHandler;
    Handler sendHandler;
    SendThread sendThread;
    BleWriteCallback Fang1BleWriteCallback1;
    BleWriteCallback Fang1BleWriteCallback2;
    BleWriteCallback Fang1BleWriteCallback3;
    BleWriteCallback Fang2BleWriteCallback1;
    BleWriteCallback Fang2BleWriteCallback2;
    BleWriteCallback Fang2BleWriteCallback3;
    BleWriteCallback Fang3BleWriteCallback1;
    BleWriteCallback Fang3BleWriteCallback2;
    BleWriteCallback Fang3BleWriteCallback3;

    BleWriteCallback Fang1BleWriteCallback0;
    BleWriteCallback Fang2BleWriteCallback0;
    BleWriteCallback Fang3BleWriteCallback0;


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oidboxtable);


        bleDevices = BleManager.getInstance().getAllConnectedDevice();
        Init();
        NoHttp.initialize(this);

        TotalTime = getIntent().getIntExtra("time", 20);

        bt_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                table1.setText(RandomTable[0]);
                table2.setText(RandomTable[1]);
                table3.setText(RandomTable[2]);
                table4.setText(RandomTable[3]);
                table5.setText(RandomTable[4]);
                table6.setText(RandomTable[5]);
                table7.setText(RandomTable[6]);
                table8.setText(RandomTable[7]);
                table9.setText(RandomTable[8]);


                bt_Start.setVisibility(View.INVISIBLE);

                new CountThread().start();


            }
        });
        bt_Start.setVisibility(View.INVISIBLE);
        bt_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reset();
            }
        });
        bt_Result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);

                bt_Reset.setVisibility(View.VISIBLE);
                bt_Rank.setVisibility(View.VISIBLE);
                tv_Result.setVisibility(View.VISIBLE);
                tv_State.setText("成绩");
                tv_TableTime.setVisibility(View.INVISIBLE);
                tv_Result.setText("1号：" + FangResult1 + "秒\n" + "2号：" + FangResult2 + "秒\n" + "3号：" + FangResult3 + "秒");
                table1.setText(RandomTable[0]);
                table2.setText(RandomTable[1]);
                table3.setText(RandomTable[2]);
                table4.setText(RandomTable[3]);
                table5.setText(RandomTable[4]);
                table6.setText(RandomTable[5]);
                table7.setText(RandomTable[6]);
                table8.setText(RandomTable[7]);
                table9.setText(RandomTable[8]);
            }
        });
        bt_Result.setVisibility(View.INVISIBLE);

        bt_Rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TotalTime > 10 && TotalTime <= 15) {
                    Rank(11);
                    spinner.setSelectedIndex(10);
                } else if (TotalTime > 15 && TotalTime <= 20) {
                    Rank(12);
                    spinner.setSelectedIndex(11);
                } else if (TotalTime > 20) {
                    Rank(13);
                    spinner.setSelectedIndex(12);
                } else {
                    Rank(TotalTime);
                    spinner.setSelectedIndex(TotalTime - 1);
                }

            }
        });

        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Rank(position + 1);
            }
        });


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 1:
                        tv_TableTime.setText(CurTime + "秒");
                        break;
                    case 100:


                        if (bleDevice1 != null) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {

//                                        Message message1 = new Message();
//                                        message1.what = 11;
//                                        sendHandler.sendMessage(message1);
//                                        sleep(200);
//                                        Message message2 = new Message();
//                                        message2.what = 12;
//                                        sendHandler.sendMessage(message2);
//                                        sleep(200);
//                                        Message message3 = new Message();
//                                        message3.what = 13;
//                                        sendHandler.sendMessage(message3);

                                        Message message = new Message();
                                        message.what = 10;
                                        sendHandler.sendMessage(message);

                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    sleep(2000);
                                                    int i = 0;
                                                    while (!Fang1Transfer && i < 3) {
                                                        Message message = new Message();
                                                        message.what = 10;
                                                        sendHandler.sendMessage(message);
                                                        i++;
                                                        sleep(2000);
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }.start();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }.start();
                        } else {
                            Fang1Transfer = true;
                        }

                        if (bleDevice2 != null) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {

//                                        Message message1 = new Message();
//                                        message1.what = 21;
//                                        sendHandler.sendMessage(message1);
//                                        sleep(200);
//                                        Message message2 = new Message();
//                                        message2.what = 22;
//                                        sendHandler.sendMessage(message2);
//                                        sleep(200);
//                                        Message message3 = new Message();
//                                        message3.what = 23;
//                                        sendHandler.sendMessage(message3);
//                                        Fang2Transfer = true;

                                        Message message = new Message();
                                        message.what = 20;
                                        sendHandler.sendMessage(message);

                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    sleep(2000);
                                                    int i = 0;
                                                    while (!Fang2Transfer && i < 3) {
                                                        Message message = new Message();
                                                        message.what = 20;
                                                        sendHandler.sendMessage(message);
                                                        i++;
                                                        sleep(2000);
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }.start();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }.start();
                        } else {
                            Fang2Transfer = true;
                        }

                        if (bleDevice3 != null) {
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    try {

//                                        Message message1 = new Message();
//                                        message1.what = 31;
//                                        sendHandler.sendMessage(message1);
//                                        sleep(200);
//                                        Message message2 = new Message();
//                                        message2.what = 32;
//                                        sendHandler.sendMessage(message2);
//                                        sleep(200);
//                                        Message message3 = new Message();
//                                        message3.what = 33;
//                                        sendHandler.sendMessage(message3);
//                                        Fang3Transfer = true;

                                        Message message = new Message();
                                        message.what = 30;
                                        sendHandler.sendMessage(message);

                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                try {
                                                    sleep(2000);
                                                    int i = 0;
                                                    while (!Fang3Transfer && i < 3) {
                                                        Message message = new Message();
                                                        message.what = 30;
                                                        sendHandler.sendMessage(message);
                                                        i++;
                                                        sleep(2000);
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }.start();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }.start();
                        } else {
                            Fang3Transfer = true;
                        }


                        break;
                    case 101:
                        tv_Result.setText("1号：" + FangResult1 + "秒\n" + "2号：" + FangResult2 + "秒\n" + "3号：" + FangResult3 + "秒");
                        break;
                    default:
                        break;
                }

            }
        };

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
                        Log.i("test", "fang01result " + HexUtil.formatHexString(data, true));
                        if (data[4] == 0x03) {
                            FangResult1 = (UnsignedUtil.getUnsignedByte(data[6])) * 256 + UnsignedUtil.getUnsignedByte(data[7]);

                            final String upresult = String.valueOf((int) FangResult1);
                            final String username = sharedPreferences.getString("username", null);

                            if (username != null) {
                                new Thread() {

                                    @Override
                                    public void run() {
                                        super.run();

                                        StringRequest request = new StringRequest("http://36.155.102.74:8888/OidBox/oidboxdata", RequestMethod.POST);


                                        if (TotalTime > 10 && TotalTime <= 15) {
                                            request.set("game", String.valueOf(11));
                                        } else if (TotalTime > 15 && TotalTime <= 20) {
                                            request.set("game", String.valueOf(12));
                                        } else if (TotalTime > 20) {
                                            request.set("game", String.valueOf(13));
                                        } else {
                                            request.set("game", String.valueOf(TotalTime));
                                        }


                                        request.set("result", String.valueOf(upresult));
                                        request.set("username", String.valueOf(username));
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
                            RankList.add(FangResult1);
                            RankListID.add("1");
                            Time curTime = new Time();
                            curTime.setToNow();
                            String timetemp = curTime.hour + "时" + curTime.minute + "秒";
                            RankListTime.add(timetemp);
                        } else if (data[5] == 0x35 && data[6] == 0) {
                            Message msg = new Message();
                            msg.what = 10;
                            sendHandler.sendMessage(msg);
                        } else if (data[5] == 0x35 && data[6] == 1) {
                            Fang1Transfer = true;
                        }


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
                        Log.i("test", "fang02result " + HexUtil.formatHexString(data, true));

                        if (data[4] == 0x03) {
                            FangResult2 = (UnsignedUtil.getUnsignedByte(data[6])) * 256 + UnsignedUtil.getUnsignedByte(data[7]);
                            FangResult2 = FangResult2 / 10;
                            RankList.add(FangResult2);
                            RankListID.add("2");
                            Time curTime = new Time();
                            curTime.setToNow();
                            String timetemp = curTime.hour + "时" + curTime.minute + "秒";
                            RankListTime.add(timetemp);
                        } else if (data[5] == 0x35 && data[6] == 0) {
                            Message msg = new Message();
                            msg.what = 20;
                            sendHandler.sendMessage(msg);
                        } else if (data[5] == 0x35 && data[6] == 1) {
                            Fang2Transfer = true;
                        }
                    }
                });
            } else if (bleDevices.get(i).getName().contains("03")) {
                Log.i("test", bleDevices.get(i).getName());
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
                        Log.i("test", "fang03result " + HexUtil.formatHexString(data, true));

                        if (data[4] == 0x03) {
                            FangResult3 = (UnsignedUtil.getUnsignedByte(data[6])) * 256 + UnsignedUtil.getUnsignedByte(data[7]);
                            FangResult3 = FangResult3 / 10;
                            RankList.add(FangResult3);
                            RankListID.add("3");
                            Time curTime = new Time();
                            curTime.setToNow();
                            String timetemp = curTime.hour + "时" + curTime.minute + "秒";
                            RankListTime.add(timetemp);
                        } else if (data[5] == 0x35 && data[6] == 0) {
                            Message msg = new Message();
                            msg.what = 30;
                            sendHandler.sendMessage(msg);
                        } else if (data[5] == 0x35 && data[6] == 1) {
                            Fang3Transfer = true;
                        }
                    }
                });
            }
        }
        BleInit();

    }

    private void Init() {
        table1 = findViewById(R.id.tb_1);
        table2 = findViewById(R.id.tb_2);
        table3 = findViewById(R.id.tb_3);
        table4 = findViewById(R.id.tb_4);
        table5 = findViewById(R.id.tb_5);
        table6 = findViewById(R.id.tb_6);
        table7 = findViewById(R.id.tb_7);
        table8 = findViewById(R.id.tb_8);
        table9 = findViewById(R.id.tb_9);
        tableLayout = findViewById(R.id.tablelayout_OidBoxGame);
        tv_TableTime = findViewById(R.id.tv_oidboxtime);
        tv_Result = findViewById(R.id.tv_OidBoxTable_Result);
        tv_State = findViewById(R.id.tv_OidBoxTable_State);
        tv_Rank = findViewById(R.id.tv_OidBoxTable_Rank);
        bt_Reset = findViewById(R.id.bt_OidBoxTable_Reset);
        bt_Start = findViewById(R.id.bt_OidBoxTable_Start);
        bt_Result = findViewById(R.id.bt_OidBoxTable_Result);
        bt_Rank = findViewById(R.id.bt_OidBoxTable_Rank);
        tv_Result.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.VISIBLE);

        scrollView = findViewById(R.id.scroll_OidBoxTable_Rank);
        scrollView.setVisibility(View.INVISIBLE);

        spinner = findViewById(R.id.sp_OidBoxTable);
        spinner.setBackgroundColor(getResources().getColor(R.color.transparent));
        List<String> tempList = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.TableTime)));
        spinner.attachDataSource(tempList);
        spinner.setPadding(getResources().getDimensionPixelSize(R.dimen.three_grid_unit), 2, 2, 2);
        spinner.setVisibility(View.INVISIBLE);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.TableTime));
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

        RankList = new ArrayList<Float>();
        RankListID = new ArrayList<String>();
        RankListTime = new ArrayList<String>();

        sendThread = new SendThread();
        sendThread.start();

        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
    }

    private void Reset() {
        randomSort = new RandomSort(9);
        randomSort.changePosition();
        RandomTable = randomSort.getStringPositions();

        table1.setText(" ");
        table2.setText(" ");
        table3.setText(" ");
        table4.setText(" ");
        table5.setText(" ");
        table6.setText(" ");
        table7.setText(" ");
        table8.setText(" ");
        table9.setText(" ");

        FangResult1 = 0;
        FangResult2 = 0;
        FangResult3 = 0;


        tv_TableTime.setText(TotalTime + "秒");
        bt_Reset.setVisibility(View.INVISIBLE);
        bt_Start.setVisibility(View.VISIBLE);
        tv_State.setText("倒计时");
        tv_TableTime.setVisibility(View.VISIBLE);
        bt_Result.setVisibility(View.INVISIBLE);
        tv_Result.setVisibility(View.INVISIBLE);
        bt_Rank.setVisibility(View.INVISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.INVISIBLE);
        tableLayout.setVisibility(View.VISIBLE);

        Fang1Transfer = false;
        Fang2Transfer = false;
        Fang3Transfer = false;

        Message message = new Message();
        message.what = 100;
        mHandler.sendMessage(message);

    }

    private void Rank(int GameID) {

        final StringRequest request = new StringRequest("http://36.155.102.74:8888/OidBox/oidboxgame", RequestMethod.POST);
        request.add("game", String.valueOf(GameID));

        new Thread() {

            @Override
            public void run() {
                super.run();
                Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                if (response.isSucceed()) {
                    String body = response.get();
                    String[] result = body.split(";");
                    ArrayList<String> resulttime = new ArrayList<>();
                    ArrayList<String> resultname = new ArrayList<>();
                    for (int i = 0; i < (result.length) / 2; i++) {
                        resulttime.add(result[i]);
                        resultname.add(result[((result.length) / 2) + i]);

                    }

                    final StringBuilder stringBuilder = new StringBuilder();
                    int k = 0;
                    for (int i = -0; i < resulttime.size(); i++) {
                        if (!resulttime.get(i).equals("0")) {
                            stringBuilder.append("第" + (k + 1) + "名：" + resultname.get(i) + "：" + Float.valueOf(resulttime.get(i)) / 10 + "秒" + "\n");
                            k++;
                        }
                    }
                    final String temp = stringBuilder.toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.setVisibility(View.VISIBLE);
                            spinner.setVisibility(View.VISIBLE);
                            tableLayout.setVisibility(View.INVISIBLE);
                            tv_Rank.setText(temp);
                        }
                    });

                    // 请求成功。
                } else {
                    // 请求失败，拿到错误：
                    Exception e = response.getException();
                }
            }
        }.start();

    }


    private class CountThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                for (int i = 0; i < TotalTime; i++) {
                    sleep(1000);
                    Message message = new Message();
                    message.what = 1;
                    CurTime = TotalTime - i - 1;
                    mHandler.sendMessage(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    table1.setText(" ");
                    table2.setText(" ");
                    table3.setText(" ");
                    table4.setText(" ");
                    table5.setText(" ");
                    table6.setText(" ");
                    table7.setText(" ");
                    table8.setText(" ");
                    table9.setText(" ");
                    bt_Result.setVisibility(View.VISIBLE);
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

    private class SendThread extends Thread {
        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            super.run();

            Looper.prepare();
            sendHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    int CS;
                    byte[] bycs = new byte[1];
                    byte[] bytes;

                    int[] randomsort;
                    int temp;
                    byte[] intbyte;
                    switch (msg.what) {
                        case 10:
                            randomsort = randomSort.getIntPositions();
                            temp = 0;
                            for (int i = 0; i < 9; i++) {
                                temp = (int) (randomsort[i]) * (int) (Math.pow(10, 8 - i)) + temp;
                            }

                            intbyte = DataTypeConversion.int2byteH2L(temp);
                            bytes = HexUtil.hexStringToBytes("F00A68010531010203040016");
                            CS = (1 + 5 + 0x31 + intbyte[0] + intbyte[1] + intbyte[2] + intbyte[3]) % 256;
                            bytes[10] = int2byte(CS)[0];
                            bytes[6] = intbyte[0];
                            bytes[7] = intbyte[1];
                            bytes[8] = intbyte[2];
                            bytes[9] = intbyte[3];
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, Fang1BleWriteCallback0);
                            break;
                        case 20:
                            randomsort = randomSort.getIntPositions();
                            temp = 0;
                            for (int i = 0; i < 9; i++) {
                                temp = (int) (randomsort[i]) * (int) (Math.pow(10, 8 - i)) + temp;
                            }

                            intbyte = DataTypeConversion.int2byteH2L(temp);
                            bytes = HexUtil.hexStringToBytes("F00A68020531010203040016");
                            CS = (2 + 5 + 0x31 + intbyte[0] + intbyte[1] + intbyte[2] + intbyte[3]) % 256;
                            bytes[10] = int2byte(CS)[0];
                            bytes[6] = intbyte[0];
                            bytes[7] = intbyte[1];
                            bytes[8] = intbyte[2];
                            bytes[9] = intbyte[3];
                            BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, bytes, Fang2BleWriteCallback0);
                            break;
                        case 30:
                            randomsort = randomSort.getIntPositions();
                            temp = 0;
                            for (int i = 0; i < 9; i++) {
                                temp = (int) (randomsort[i]) * (int) (Math.pow(10, 8 - i)) + temp;
                            }

                            intbyte = DataTypeConversion.int2byteH2L(temp);
                            bytes = HexUtil.hexStringToBytes("F00A68030531010203040016");
                            CS = (3 + 5 + 0x31 + intbyte[0] + intbyte[1] + intbyte[2] + intbyte[3]) % 256;
                            bytes[10] = int2byte(CS)[0];
                            bytes[6] = intbyte[0];
                            bytes[7] = intbyte[1];
                            bytes[8] = intbyte[2];
                            bytes[9] = intbyte[3];
                            BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, bytes, Fang3BleWriteCallback0);
                            break;


                        case 11:
                            CS = 1 + 5 + 0x31 + 1 + randomSort.getIntPositions()[0] + randomSort.getIntPositions()[1] + randomSort.getIntPositions()[2];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6801053101" + "0" + RandomTable[0] + "0" + RandomTable[1] + "0" + RandomTable[2] + HexUtil.formatHexString(bycs) + "16");
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, Fang1BleWriteCallback1);
                            break;
                        case 12:
                            CS = 1 + 5 + 0x31 + 2 + randomSort.getIntPositions()[3] + randomSort.getIntPositions()[4] + randomSort.getIntPositions()[5];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6801053102" + "0" + RandomTable[3] + "0" + RandomTable[4] + "0" + RandomTable[5] + HexUtil.formatHexString(bycs) + "16");
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, Fang1BleWriteCallback2);
                            break;
                        case 13:
                            CS = 1 + 5 + 0x31 + 3 + randomSort.getIntPositions()[6] + randomSort.getIntPositions()[7] + randomSort.getIntPositions()[8];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6801053103" + "0" + RandomTable[6] + "0" + RandomTable[7] + "0" + RandomTable[8] + HexUtil.formatHexString(bycs) + "16");
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, Fang1BleWriteCallback3);
                            break;
                        case 15:
                            bytes = HexUtil.hexStringToBytes("F00768010231053916");
                            BleManager.getInstance().write(bleDevice1, Service_uuid, Characteristic_uuid_write, bytes, new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {

                                }
                            });
                            break;


                        case 21:
                            CS = 2 + 5 + 0x31 + 1 + randomSort.getIntPositions()[0] + randomSort.getIntPositions()[1] + randomSort.getIntPositions()[2];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6802053101" + "0" + RandomTable[0] + "0" + RandomTable[1] + "0" + RandomTable[2] + HexUtil.formatHexString(bycs) + "16");

                            BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, bytes, Fang2BleWriteCallback1);
                            break;
                        case 22:
                            CS = 2 + 5 + 0x31 + 2 + randomSort.getIntPositions()[3] + randomSort.getIntPositions()[4] + randomSort.getIntPositions()[5];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6802053102" + "0" + RandomTable[3] + "0" + RandomTable[4] + "0" + RandomTable[5] + HexUtil.formatHexString(bycs) + "16");
                            BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, bytes, Fang2BleWriteCallback2);
                            break;
                        case 23:
                            CS = 2 + 5 + 0x31 + 3 + randomSort.getIntPositions()[6] + randomSort.getIntPositions()[7] + randomSort.getIntPositions()[8];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6802053103" + "0" + RandomTable[6] + "0" + RandomTable[7] + "0" + RandomTable[8] + HexUtil.formatHexString(bycs) + "16");
                            BleManager.getInstance().write(bleDevice2, Service_uuid, Characteristic_uuid_write, bytes, Fang2BleWriteCallback3);
                            break;


                        case 31:
                            CS = 3 + 5 + 0x31 + 1 + randomSort.getIntPositions()[0] + randomSort.getIntPositions()[1] + randomSort.getIntPositions()[2];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6803053101" + "0" + RandomTable[0] + "0" + RandomTable[1] + "0" + RandomTable[2] + HexUtil.formatHexString(bycs) + "16");

                            BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, bytes, Fang3BleWriteCallback1);
                            break;
                        case 32:
                            CS = 3 + 5 + 0x31 + 2 + randomSort.getIntPositions()[3] + randomSort.getIntPositions()[4] + randomSort.getIntPositions()[5];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6803053102" + "0" + RandomTable[3] + "0" + RandomTable[4] + "0" + RandomTable[5] + HexUtil.formatHexString(bycs) + "16");

                            BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, bytes, Fang3BleWriteCallback2);
                            break;
                        case 33:
                            CS = 3 + 5 + 0x31 + 3 + randomSort.getIntPositions()[6] + randomSort.getIntPositions()[7] + randomSort.getIntPositions()[8];
                            bycs[0] = int2byte(CS)[0];
                            bytes = HexUtil.hexStringToBytes("F00A6803053103" + "0" + RandomTable[6] + "0" + RandomTable[7] + "0" + RandomTable[8] + HexUtil.formatHexString(bycs) + "16");

                            BleManager.getInstance().write(bleDevice3, Service_uuid, Characteristic_uuid_write, bytes, Fang3BleWriteCallback3);
                            break;

                        default:
                            break;
                    }

                }
            };
            Looper.loop();
        }
    }


    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 380;
    }

    public void SortL2H(ArrayList<String> key, ArrayList<Float> value, ArrayList<String> time) {
        for (int i = 0; i < value.size() - 1; i++) {
            // 每次内循环的比较，从0索引开始，每次都在递减。注意内循环的次数应该是(arr.length - 1 - i)。
            for (int j = 0; j < value.size() - 1 - i; j++) {
                // 比较的索引是j和j+1
                if (value.get(j) > value.get(j + 1)) {
                    float temp = value.get(j);
                    value.set(j, value.get(j + 1));
                    value.set(j + 1, temp);

                    String temp2 = key.get(j);
                    key.set(j, key.get(j + 1));
                    key.set(j + 1, temp2);

                    String temp3 = time.get(j);
                    time.set(j, time.get(j + 1));
                    time.set(j + 1, temp3);

                }
            }
        }
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
                    Message message = new Message();
                    message.what = 10;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang2BleWriteCallback0 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table2 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang2：" + exception.toString());
                if (times < 4) {
                    Message message = new Message();
                    message.what = 20;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang3BleWriteCallback0 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table3 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang3：" + exception.toString());
                if (times < 4) {
                    Message message = new Message();
                    message.what = 30;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang1BleWriteCallback1 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table01 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang1 01：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 11;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }


            }
        };

        Fang1BleWriteCallback2 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table01 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang1 02：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 12;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang1BleWriteCallback3 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table01 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang1 03：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 13;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang2BleWriteCallback1 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table02 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang2 01：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 21;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang2BleWriteCallback2 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table02 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang2 02：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 22;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang2BleWriteCallback3 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table02 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang2 03：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 23;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang3BleWriteCallback1 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table03 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang3 01：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 31;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang3BleWriteCallback2 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table03 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang3 02：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 32;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };

        Fang3BleWriteCallback3 = new BleWriteCallback() {
            int times = 0;

            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                Log.i("test", "table03 " + HexUtil.formatHexString(justWrite, true));
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.i("test", "fang3 03：" + exception.toString());
                if (times < 3) {
                    Message message = new Message();
                    message.what = 33;
                    sendHandler.sendMessage(message);
                    times++;
                } else {
                    times = 0;
                }
            }
        };


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
