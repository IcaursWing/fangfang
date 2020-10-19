package com.example.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaExtractor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.example.fangfang_gai.R;
import com.yanzhenjie.nohttp.Params;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.PublicKey;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.internal.CustomAdapt;

public class yaokongche extends Activity implements CustomAdapt {


    Button bt_up, bt_down, bt_left, bt_right, bt_track, bt_dance;
    Button bt_up_all, bt_down_all, bt_left_all, bt_right_all, bt_dance_all;
    TextView tv_kong1, tv_kong2, tv_kong3, tv_kong4;
    SeekBar sk_kong1, sk_kong2, sk_kong3, sk_kong4;
    Spinner chelist, actionlist;
    CheckBox cb_all;

    SharedPreferences sharedPreferences;

    int CurrentRad;
    int ChePosition;

    ArrayList<Che> ches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yaokongche);
        setTitle("STEAM WiFi连接");

        sharedPreferences = getSharedPreferences("steam", Context.MODE_PRIVATE);

        bt_up = findViewById(R.id.che_up);
        bt_down = findViewById(R.id.che_down);
        bt_left = findViewById(R.id.che_left);
        bt_right = findViewById(R.id.che_right);
        bt_track = findViewById(R.id.che_track);
        bt_dance = findViewById(R.id.che_dance);


        bt_up_all = findViewById(R.id.che_up_all);
        bt_down_all = findViewById(R.id.che_down_all);
        bt_left_all = findViewById(R.id.che_left_all);
        bt_right_all = findViewById(R.id.che_right_all);
        bt_dance_all = findViewById(R.id.che_dance_all);

        tv_kong1 = findViewById(R.id.textView_kong1);
        tv_kong2 = findViewById(R.id.textView_kong2);
        tv_kong3 = findViewById(R.id.textView_kong3);
        tv_kong4 = findViewById(R.id.textView_kong4);

        sk_kong1 = findViewById(R.id.seekBar_kong1);
        sk_kong2 = findViewById(R.id.seekBar_kong2);
        sk_kong3 = findViewById(R.id.seekBar_kong3);
        sk_kong4 = findViewById(R.id.seekBar_kong4);

        chelist = findViewById(R.id.che_spinner);
        actionlist = findViewById(R.id.action_spinner);
        cb_all = findViewById(R.id.checkBox_all);

        ches = new ArrayList<Che>();
        ches.add(new Che("192.168.3.241"));
        ches.add(new Che("192.168.3.242"));
        ches.add(new Che("192.168.3.243"));
        ches.add(new Che("192.168.3.244"));

        for (int i = 0; i < 16; i++) {
            String temp = sharedPreferences.getString("cheIP" + (i + 4), "192.168.1." + i);
            ches.add(new Che(temp));
        }

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.che_up:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x01, 0x00, 0x00, 0x00};
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;


                            default:
                                break;
                        }
                        break;

                    case R.id.che_down:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x02, 0x00, 0x00, 0x00};
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            default:
                                break;
                        }
                        break;

                    case R.id.che_left:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x03, 0x00, 0x00, 0x00};
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            default:
                                break;
                        }
                        break;

                    case R.id.che_right:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x04, 0x00, 0x00, 0x00};
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            default:
                                break;
                        }
                        break;

                    case R.id.che_up_all:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x01, 0x00, 0x00, 0x00};
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            default:
                                break;
                        }
                        break;

                    case R.id.che_down_all:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x02, 0x00, 0x00, 0x00};
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            default:
                                break;
                        }
                        break;

                    case R.id.che_left_all:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x03, 0x00, 0x00, 0x00};
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

                            default:
                                break;
                        }
                        break;

                    case R.id.che_right_all:
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x04, 0x00, 0x00, 0x00};
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_UP:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;

                            case MotionEvent.ACTION_CANCEL:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                        try {
                                            sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            for (int i = 0; i < 4; i++) {
                                                send(bytes, ches.get(i).ip, 11028);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();

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

        bt_up.setOnTouchListener(touchListener);
        bt_down.setOnTouchListener(touchListener);
        bt_left.setOnTouchListener(touchListener);
        bt_right.setOnTouchListener(touchListener);
        bt_up_all.setOnTouchListener(touchListener);
        bt_down_all.setOnTouchListener(touchListener);
        bt_left_all.setOnTouchListener(touchListener);
        bt_right_all.setOnTouchListener(touchListener);

        bt_track.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x05, 0x05, 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        bt_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                        try {
                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        bt_dance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        byte[] bytes = {0x06, 0x01, 0x00, 0x00, 0x00};
                        try {
                            send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        bt_dance_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        byte[] bytes = {0x06, 0x01, 0x00, 0x00, 0x00};
                        try {
                            for (int i = 0; i < 4; i++) {
                                send(bytes, ches.get(i).ip, 11028);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });


        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            Thread th1, th2, th3, th4;


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case R.id.seekBar_kong1:
                        ches.get(chelist.getSelectedItemPosition()).kong1 = sk_kong1.getProgress();
                        tv_kong1.setText("底座（左—右）" + ches.get(chelist.getSelectedItemPosition()).kong1 + "°");
                        CurrentRad = sk_kong1.getProgress();
                        ChePosition = chelist.getSelectedItemPosition();
                        th1 = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x01, intToByteArray(CurrentRad)[3], 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(ChePosition).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        th1.start();
                        break;

                    case R.id.seekBar_kong2:
                        ches.get(chelist.getSelectedItemPosition()).kong2 = sk_kong2.getProgress();
                        tv_kong2.setText("右臂（后—前）" + ches.get(chelist.getSelectedItemPosition()).kong2 + "°");
                        CurrentRad = sk_kong2.getProgress();
                        ChePosition = chelist.getSelectedItemPosition();
                        th2 = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x02, intToByteArray(CurrentRad)[3], 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(ChePosition).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        th2.start();
                        break;

                    case R.id.seekBar_kong3:
                        ches.get(chelist.getSelectedItemPosition()).kong3 = sk_kong3.getProgress();
                        CurrentRad = sk_kong3.getProgress();
                        ChePosition = chelist.getSelectedItemPosition();
                        tv_kong3.setText("左臂（下—上）" + CurrentRad + "°");
                        th3 = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x03, intToByteArray(CurrentRad)[3], 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(ChePosition).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        th3.start();
                        break;

                    case R.id.seekBar_kong4:
                        ches.get(chelist.getSelectedItemPosition()).kong4 = sk_kong4.getProgress();
                        tv_kong4.setText("手爪（开—关）" + ches.get(chelist.getSelectedItemPosition()).kong4 + "°");
                        CurrentRad = sk_kong4.getProgress();
                        ChePosition = chelist.getSelectedItemPosition();
                        th4 = new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x04, intToByteArray(CurrentRad)[3], 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(ChePosition = chelist.getSelectedItemPosition()).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        th4.start();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (seekBar.getId()) {
                    case R.id.seekBar_kong1:
                        break;
                    case R.id.seekBar_kong2:
                        break;
                    case R.id.seekBar_kong3:
                        break;
                    case R.id.seekBar_kong4:
                        break;


                    default:
                        break;

                }


            }
        };

        sk_kong1.setOnSeekBarChangeListener(seekBarChangeListener);
        sk_kong2.setOnSeekBarChangeListener(seekBarChangeListener);
        sk_kong3.setOnSeekBarChangeListener(seekBarChangeListener);
        sk_kong4.setOnSeekBarChangeListener(seekBarChangeListener);

        chelist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        sk_kong1.setProgress(ches.get(chelist.getSelectedItemPosition()).kong1);
                        sk_kong2.setProgress(ches.get(chelist.getSelectedItemPosition()).kong2);
                        sk_kong3.setProgress(ches.get(chelist.getSelectedItemPosition()).kong3);
                        sk_kong4.setProgress(ches.get(chelist.getSelectedItemPosition()).kong4);
                        tv_kong1.setText("底座（左—右）" + ches.get(chelist.getSelectedItemPosition()).kong1 + "°");
                        tv_kong2.setText("右臂（后—前）" + ches.get(chelist.getSelectedItemPosition()).kong2 + "°");
                        tv_kong3.setText("左臂（下—上）" + ches.get(chelist.getSelectedItemPosition()).kong3 + "°");
                        tv_kong4.setText("手爪（开—关）" + ches.get(chelist.getSelectedItemPosition()).kong4 + "°");
                        break;

                    case 1:
                        sk_kong1.setProgress(ches.get(chelist.getSelectedItemPosition()).kong1);
                        sk_kong2.setProgress(ches.get(chelist.getSelectedItemPosition()).kong2);
                        sk_kong3.setProgress(ches.get(chelist.getSelectedItemPosition()).kong3);
                        sk_kong4.setProgress(ches.get(chelist.getSelectedItemPosition()).kong4);
                        tv_kong1.setText("底座（左—右）" + ches.get(chelist.getSelectedItemPosition()).kong1 + "°");
                        tv_kong2.setText("右臂（后—前）" + ches.get(chelist.getSelectedItemPosition()).kong2 + "°");
                        tv_kong3.setText("左臂（下—上）" + ches.get(chelist.getSelectedItemPosition()).kong3 + "°");
                        tv_kong4.setText("手爪（开—关）" + ches.get(chelist.getSelectedItemPosition()).kong4 + "°");
                        break;

                    case 2:
                        sk_kong1.setProgress(ches.get(chelist.getSelectedItemPosition()).kong1);
                        sk_kong2.setProgress(ches.get(chelist.getSelectedItemPosition()).kong2);
                        sk_kong3.setProgress(ches.get(chelist.getSelectedItemPosition()).kong3);
                        sk_kong4.setProgress(ches.get(chelist.getSelectedItemPosition()).kong4);
                        tv_kong1.setText("底座（左—右）" + ches.get(chelist.getSelectedItemPosition()).kong1 + "°");
                        tv_kong2.setText("右臂（后—前）" + ches.get(chelist.getSelectedItemPosition()).kong2 + "°");
                        tv_kong3.setText("左臂（下—上）" + ches.get(chelist.getSelectedItemPosition()).kong3 + "°");
                        tv_kong4.setText("手爪（开—关）" + ches.get(chelist.getSelectedItemPosition()).kong4 + "°");
                        break;

                    case 3:
                        sk_kong1.setProgress(ches.get(chelist.getSelectedItemPosition()).kong1);
                        sk_kong2.setProgress(ches.get(chelist.getSelectedItemPosition()).kong2);
                        sk_kong3.setProgress(ches.get(chelist.getSelectedItemPosition()).kong3);
                        sk_kong4.setProgress(ches.get(chelist.getSelectedItemPosition()).kong4);
                        tv_kong1.setText("底座（左—右）" + ches.get(chelist.getSelectedItemPosition()).kong1 + "°");
                        tv_kong2.setText("右臂（后—前）" + ches.get(chelist.getSelectedItemPosition()).kong2 + "°");
                        tv_kong3.setText("左臂（下—上）" + ches.get(chelist.getSelectedItemPosition()).kong3 + "°");
                        tv_kong4.setText("手爪（开—关）" + ches.get(chelist.getSelectedItemPosition()).kong4 + "°");
                        break;

                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        actionlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x07, 0x00, 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;

                    case 1:
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x08, 0x00, 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;

                    case 2:
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                byte[] bytes = {0x09, 0x00, 0x00, 0x00, 0x00};
                                try {
                                    send(bytes, ches.get(chelist.getSelectedItemPosition()).ip, 11028);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    bt_up_all.setVisibility(View.VISIBLE);
                    bt_down_all.setVisibility(View.VISIBLE);
                    bt_left_all.setVisibility(View.VISIBLE);
                    bt_right_all.setVisibility(View.VISIBLE);
                    bt_dance_all.setVisibility(View.VISIBLE);
                } else {
                    bt_up_all.setVisibility(View.INVISIBLE);
                    bt_down_all.setVisibility(View.INVISIBLE);
                    bt_left_all.setVisibility(View.INVISIBLE);
                    bt_right_all.setVisibility(View.INVISIBLE);
                    bt_dance_all.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return 380;
    }

    private class Che {
        public String ip, ip1 = null, ip2 = null, ip3 = null, ip4 = null;
        public int kong1 = 75, kong2 = 85, kong3 = 85, kong4 = 45;


        Che(String ip) {
            String[] temp = ip.split("\\.");
            this.ip = ip;

            this.ip1 = temp[0];
            this.ip2 = temp[1];
            this.ip3 = temp[2];
            this.ip4 = temp[3];

        }
    }

    public void send(byte[] command, String ip, int port) throws IOException {

        DatagramPacket dp = new DatagramPacket(command, command.length, InetAddress.getByName(ip), port);
        System.out.println(ip);

        DatagramSocket ds = new DatagramSocket();
        ds.send(dp);
        ds.close();
        Log.i("test", "发送成功:" + command + " " + ip + " " + port);
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.yaokongche_menu, menu);
        menu.findItem(R.id.menu_configIP).setVisible(true);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_configIP:
                View view = getLayoutInflater().inflate(R.layout.dialog_ipconfig, null);
                MyDialog mMyDialog = new MyDialog(yaokongche.this, 1500, 0, view, R.style.DialogTheme);
                mMyDialog.setCancelable(true);
                Button ipconfigy = mMyDialog.getWindow().findViewById(R.id.dialog_IPyes);
                Button ipconfigc = mMyDialog.getWindow().findViewById(R.id.dialog_IPcancel);
                ListView dialogListView = mMyDialog.getWindow().findViewById(R.id.dialog_IPlv);
                dialogListView.setAdapter(new MyAdapter());
                mMyDialog.show();

                View.OnClickListener dialogClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.dialog_IPcancel:
                                mMyDialog.dismiss();
                                break;
                            case R.id.dialog_IPyes:

                                for (int i = 0; i < ches.size(); i++) {
                                    ches.get(i).ip = ches.get(i).ip1 + "." + ches.get(i).ip2 + "." + ches.get(i).ip3 + "." + ches.get(i).ip4;
                                    sharedPreferences.edit().putString("cheIP" + i, ches.get(i).ip).commit();

                                }

                                mMyDialog.dismiss();

                                break;
                            default:
                                break;
                        }
                    }
                };
                ipconfigc.setOnClickListener(dialogClickListener);
                ipconfigy.setOnClickListener(dialogClickListener);

                break;

            default:
                break;

        }

        return true;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ches.size();
        }

        @Override
        public Object getItem(int position) {
            if (position > ches.size())
                return null;
            return ches.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.listitem_ipconfig, null);
                convertView.setTag(viewHolder);

                viewHolder.et_IP1 = convertView.findViewById(R.id.listitem_IPconfig_IP1);
                viewHolder.et_IP2 = convertView.findViewById(R.id.listitem_IPconfig_IP2);
                viewHolder.et_IP3 = convertView.findViewById(R.id.listitem_IPconfig_IP3);
                viewHolder.et_IP4 = convertView.findViewById(R.id.listitem_IPconfig_IP4);
                viewHolder.tv_car = convertView.findViewById(R.id.listitem_IPconfig_car);

                viewHolder.tw_IP1 = new mTextWatcher1();
                viewHolder.tw_IP2 = new mTextWatcher2();
                viewHolder.tw_IP3 = new mTextWatcher3();
                viewHolder.tw_IP4 = new mTextWatcher4();
                viewHolder.et_IP1.addTextChangedListener(viewHolder.tw_IP1);
                viewHolder.et_IP2.addTextChangedListener(viewHolder.tw_IP2);
                viewHolder.et_IP3.addTextChangedListener(viewHolder.tw_IP3);
                viewHolder.et_IP4.addTextChangedListener(viewHolder.tw_IP4);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tw_IP1.mposition = position;
            viewHolder.tw_IP2.mposition = position;
            viewHolder.tw_IP3.mposition = position;
            viewHolder.tw_IP4.mposition = position;

            viewHolder.tv_car.setText("Car" + (position + 1) + ":");
            viewHolder.et_IP1.setText(ches.get(position).ip1);
            viewHolder.et_IP2.setText(ches.get(position).ip2);
            viewHolder.et_IP3.setText(ches.get(position).ip3);
            viewHolder.et_IP4.setText(ches.get(position).ip4);
            Log.i("test", "et_" + ches.get(position).ip4);

            Log.i("test", "ls" + position);


            return convertView;
        }

        class ViewHolder {
            public TextView tv_car;
            public EditText et_IP1, et_IP2, et_IP3, et_IP4;

            mTextWatcher1 tw_IP1;
            mTextWatcher2 tw_IP2;
            mTextWatcher3 tw_IP3;
            mTextWatcher4 tw_IP4;
        }

        class mTextWatcher1 implements TextWatcher {
            public int mposition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ches.get(mposition).ip1 = s.toString();
            }
        }

        class mTextWatcher2 implements TextWatcher {
            public int mposition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ches.get(mposition).ip2 = s.toString();
            }
        }

        class mTextWatcher3 implements TextWatcher {
            public int mposition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ches.get(mposition).ip3 = s.toString();
            }
        }

        class mTextWatcher4 implements TextWatcher {
            public int mposition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ches.get(mposition).ip4 = s.toString();
            }
        }
    }


    class MyDialog extends Dialog {

        public MyDialog(Context context, int width, int height, View layout, int style) {
            super(context, style);
            setContentView(layout);
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = width;
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);

        }

    }

}
