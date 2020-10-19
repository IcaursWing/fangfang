package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.clj.fastble.utils.HexUtil;
import com.example.fangfang_gai.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class House extends Activity {

    Spinner house_LED;
    Button house_fan1, house_fan2, house_window1, house_window2, house_curtain1, house_curtain2, house_fire1, house_fire2, house_light1, house_light2;
    Switch house_door;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluehouse);


        house_LED = findViewById(R.id.house_spinner);
        house_door = findViewById(R.id.house_door);
        house_fan1 = findViewById(R.id.button_fan1);
        house_fan2 = findViewById(R.id.button_fan2);
        house_window1 = findViewById(R.id.button_window1);
        house_window2 = findViewById(R.id.button_window2);
        house_curtain1 = findViewById(R.id.button_curtain1);
        house_curtain2 = findViewById(R.id.button_curtain2);
        house_fire1 = findViewById(R.id.button_fire1);
        house_fire2 = findViewById(R.id.button_fire2);
        house_light1 = findViewById(R.id.button_light1);
        house_light2 = findViewById(R.id.button_light2);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_fan1:
                        try {
                            byte[] bytes = {2, 30, 0, 0, 0};
                            send(bytes, "192.168.3.21", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.button_fan2:
                        try {
                            byte[] bytes = {2, 1, 0, 0, 0};
                            send(bytes, "192.168.3.21", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_window1:
                        try {
                            byte[] bytes1 = {4, 1, 0, 0, 0};
                            send(bytes1, "192.168.3.23", 11028);
                            byte[] bytes2 = {14, 1, 0, 0, 0};
                            send(bytes2, "192.168.3.6", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.button_window2:
                        try {
                            byte[] bytes1 = {4, 0, 0, 0, 0};
                            send(bytes1, "192.168.3.23", 11028);
                            byte[] bytes2 = {14, 0, 0, 0, 0};
                            send(bytes2, "192.168.3.6", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_curtain1:
                        try {
                            byte[] bytes1 = {5, 1, 0, 0, 0};
                            send(bytes1, "192.168.3.24", 11028);
                            byte[] bytes2 = {15, 1, 0, 0, 0};
                            send(bytes2, "192.168.3.6", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_curtain2:
                        try {
                            byte[] bytes1 = {5, -12, 1, 0, 0};
                            send(bytes1, "192.168.3.24", 11028);
                            byte[] bytes2 = {15, 0, 0, 0, 0};
                            send(bytes2, "192.168.3.6", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_fire1:
                        try {
                            byte[] bytes = {6, 1, 0, 0, 0};
                            send(bytes, "192.168.3.25", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_fire2:
                        try {
                            byte[] bytes = {6, -48, 7, 0, 0};
                            send(bytes, "192.168.3.25", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_light1:
                        try {
                            byte[] bytes = {7, 1, 0, 0, 0};
                            send(bytes, "192.168.3.26", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    case R.id.button_light2:
                        try {
                            byte[] bytes = {7, -12, 1, 0, 0};
                            send(bytes, "192.168.3.26", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        house_fan1.setOnClickListener(onClickListener);
        house_fan2.setOnClickListener(onClickListener);
        house_window1.setOnClickListener(onClickListener);
        house_window2.setOnClickListener(onClickListener);
        house_curtain1.setOnClickListener(onClickListener);
        house_curtain2.setOnClickListener(onClickListener);
        house_fire1.setOnClickListener(onClickListener);
        house_fire2.setOnClickListener(onClickListener);
        house_light1.setOnClickListener(onClickListener);
        house_light2.setOnClickListener(onClickListener);

        house_LED.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        try {
                            byte[] bytes = {1, 0, 0, 0, 0};
                            send(bytes, "192.168.3.20", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            byte[] bytes = {1, 50, 0, 0, 0};
                            send(bytes, "192.168.3.20", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            byte[] bytes = {1, -56, 0, 0, 0};
                            send(bytes, "192.168.3.20", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            byte[] bytes = {1, 0x5E, 1, 0, 0};
                            send(bytes, "192.168.3.20", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            byte[] bytes = {1, -12, 1, 0, 0};
                            send(bytes, "192.168.3.20", 11028);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        house_door.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        byte[] bytes1 = {8, 1, 0, 0, 0};
                        send(bytes1, "192.168.3.22", 11028);
                        byte[] bytes2 = {18, 1, 0, 0, 0};
                        send(bytes2, "192.168.3.6", 11028);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        byte[] bytes1 = {8, 0, 0, 0, 0};
                        send(bytes1, "192.168.3.22", 11028);
                        byte[] bytes2 = {18, 0, 0, 0, 0};
                        send(bytes2, "192.168.3.6", 11028);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void send(byte[] command, String ip, int port) throws IOException {

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    DatagramPacket dp = new DatagramPacket(command, command.length, InetAddress.getByName(ip), port);

                    DatagramSocket ds = new DatagramSocket();
                    ds.send(dp);
                    ds.close();

                    Log.i("test", "发送成功:" + HexUtil.formatHexString(command, true) + " " + ip + " " + port);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }
}
