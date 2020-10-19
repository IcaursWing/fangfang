package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.fangfangtech.oidbox.R;
import com.hjq.base.BaseDialog;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import MusicUtil.MusicSendObject;
import MusicUtil.Yin;
import me.jessyan.autosize.internal.CustomAdapt;
import myutil.MyBase64tUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ui.MessageDialog;

public class GameMusicActivity extends Activity implements CustomAdapt {

    public static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    TextView tv_yin;
    RoundButton bt_create, bt_midi, bt_mp3, bt_stop;
    ImageButton bt_exit;

    List<BleDevice> bleDevices;
    BleDevice bleDevice = null;
    Yin[] Fangyin;

    MediaPlayer mediaPlayer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏顶部的状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_musicgame);
        setTitle("音乐创作");
        Init();
        bleDevices = BleManager.getInstance().getAllConnectedDevice();

        for (int i = 0; i < bleDevices.size(); i++) {
            if (bleDevices.get(i).getName().contains("OidBox")) {
                bleDevice = bleDevices.get(i);
                break;
            }
        }

        BleManager.getInstance().notify(bleDevice, Service_uuid, Characteristic_uuid_notify, new BleNotifyCallback() {
            @Override
            public void onNotifySuccess() {

            }

            @Override
            public void onNotifyFailure(BleException exception) {

            }

            @Override
            public void onCharacteristicChanged(byte[] data) {
                //F0 12 68 01 0D 34 01 02 03 04 05 06 07 08 09 0A 0B 0C CS 16
                Log.i("test", HexUtil.formatHexString(data, true));
                if (data[6] != 0) {
                    for (int i = 0; i < 12; i++) {
                        if ((data[i + 6] == 0) || i == 11) {
                            Fangyin = new Yin[i];
                            break;
                        }
                    }
                    StringBuilder sb = new StringBuilder();

                    if (Fangyin.length < 13) {
                        for (int i = 0; i < Fangyin.length; i++) {
                            if (data[i + 6] == 8) {
                                sb.append("1+");
                            } else {
                                sb.append((int) data[i + 6] + " ");
                            }

                        }
                    } else {
                        for (int i = 0; i < 12; i++) {
                            if (data[i + 6] == 8) {
                                sb.append("1+");
                            } else {
                                sb.append((int) data[i + 6] + " ");
                            }
                        }
                    }

                    tv_yin.setText(sb.toString());

                    for (int i = 0; i < Fangyin.length; i++) {
                        switch (data[6 + i]) {
                            case 1:
                                Fangyin[i] = new Yin(60, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 2:
                                Fangyin[i] = new Yin(62, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 3:
                                Fangyin[i] = new Yin(64, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 4:
                                Fangyin[i] = new Yin(65, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 5:
                                Fangyin[i] = new Yin(67, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 6:
                                Fangyin[i] = new Yin(69, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 7:
                                Fangyin[i] = new Yin(71, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;
                            case 8:
                                Fangyin[i] = new Yin(72, 2.0 + 0.5 * i, 2.5 + 0.5 * i, 100);
                                break;

                            default:
                                break;
                        }
                    }

                    MusicSendObject musicSendObject = new MusicSendObject(120, Fangyin);

                    RequestBody requestBody =
                            new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("midi", JSON.toJSONString(musicSendObject)).addFormDataPart("giveMidi", "true").build();

                    final Request mRequest = new Request.Builder().url("https://api.lazycomposer.com/v1/melody_arrange/continue").addHeader("Content-Type", "multipart/form-data").addHeader("accept"
                            , "application/json").
                            post(requestBody).build();


                    new Thread() {
                        @Override
                        public void run() {
                            super.run();

                            final OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
                            OkHttpClient okHttpClient = httpBuilder.connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();


                            okHttpClient.newCall(mRequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(GameMusicActivity.this, "云创作失败！请检查网络连接！", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        String s = response.body().string();
                                        JSONObject jsonObject = JSONObject.parseObject(s);
                                        String path = getExternalFilesDir(null).getPath() + File.separator + "api.midi";
                                        Log.i("test", s);
                                        MyBase64tUtil.decoderBase64File(jsonObject.getString("midi"), path);
                                        path = getExternalFilesDir(null).getPath() + File.separator + "api.mp3";
                                        MyBase64tUtil.decoderBase64File(jsonObject.getString("audio"), path);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GameMusicActivity.this, "创作成功！", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GameMusicActivity.this, "创作失败！", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }.start();

                } else {
                    Toast.makeText(GameMusicActivity.this, "还未创作音乐！", Toast.LENGTH_LONG).show();
                }


            }
        });


        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(GameMusicActivity.this, "创作中，请稍等！", Toast.LENGTH_LONG).show();

                BleManager.getInstance().write(bleDevice, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F006680101343616"), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Toast.makeText(GameMusicActivity.this, "读取音符失败！", Toast.LENGTH_LONG).show();
                    }
                });

//                Yin[] yins = new Yin[4];
//                yins[0] = new Yin(64, 2.0, 2.5, 100);
//                yins[1] = new Yin(65, 2.5, 3.0, 100);
//                yins[2] = new Yin(66, 3.0, 3.5, 100);
//                yins[3] = new Yin(67, 3.5, 4.0, 100);


            }
        });
        bt_midi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                }

                String path = getExternalFilesDir(null).getPath() + File.separator + "api.midi";
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        bt_mp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                } else {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = new MediaPlayer();
                }

                String path = getExternalFilesDir(null).getPath() + File.separator + "api.mp3";
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }
            }
        });

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MessageDialog.Builder(GameMusicActivity.this)
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

    }

    private void Init() {
        tv_yin = findViewById(R.id.tv_OidBoxMusic_yin);
        bt_create = findViewById(R.id.bt_OidBoxMusic_create);
        bt_midi = findViewById(R.id.bt_OidBoxMusic_midi);
        bt_mp3 = findViewById(R.id.bt_OidBoxMusic_mp3);
        bt_stop = findViewById(R.id.bt_OidBoxMusic_stop);
        bt_exit = findViewById(R.id.imagebutton_music_exit);
    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return (1080f / 2.875f);
    }
}
