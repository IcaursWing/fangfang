package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fangfang_gai.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class yaokongchetest extends Activity {


    Button test1, test2;
    TextView tv_command, tv_ip, tv_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yaokongchetest);

        test1 = findViewById(R.id.test1);
        test2 = findViewById(R.id.test2);
        tv_command = findViewById(R.id.tv_command);
        tv_ip = findViewById(R.id.tv_ip);
        tv_port = findViewById(R.id.tv_port);


        test1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    byte[] bytes = {0x05, 0x00, 0x00, 0x00, 0x00};
                    send(bytes, InetAddress.getByName(tv_ip.getText().toString().trim()), Integer.valueOf(tv_port.getText().toString().trim()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        test2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        byte[] bytes = {0x05, 0x01, 0x00, 0x00, 0x00};
                        try {
                            send(bytes, InetAddress.getByName(tv_ip.getText().toString().trim()), Integer.valueOf(tv_port.getText().toString().trim()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        byte[] bytes2 = {0x05, 0x00, 0x00, 0x00, 0x00};
                        try {
                            send(bytes2, InetAddress.getByName(tv_ip.getText().toString().trim()), Integer.valueOf(tv_port.getText().toString().trim()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;

                }


                return false;
            }
        });


    }

    public void send(byte[] command, InetAddress ip, int port) throws IOException {

        DatagramPacket dp = new DatagramPacket(command, command.length, ip, port);
        System.out.println(ip);

        DatagramSocket ds = new DatagramSocket();
        ds.send(dp);
        ds.close();
        Log.i("test", "发送成功:" + command + " " + ip + " " + port);
    }

}
