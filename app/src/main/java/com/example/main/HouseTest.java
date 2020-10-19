package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clj.fastble.utils.HexUtil;
import com.example.fangfang_gai.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.PublicKey;

public class HouseTest extends Activity {

    EditText mIP, channel, mdata;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluehousetest);

        mIP = findViewById(R.id.editText_houseIP);
        channel = findViewById(R.id.editText_houseChannel);
        mdata = findViewById(R.id.editText_DATA);
        send = findViewById(R.id.button_houseSend);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t_ip = mIP.getText().toString().trim().replaceAll(" ", "");
                int t_channel = Integer.valueOf(channel.getText().toString());
                int t_data = Integer.valueOf(mdata.getText().toString());
                byte[] bytes = int2byte(t_data);
                byte[] send = {(byte) t_channel, 0, 0, 0, 0};

                for (int i = 1; i < send.length; i++) {
                    send[i] = bytes[i - 1];
                }
                try {
                    new Thread();
                    send(send, InetAddress.getByName(t_ip), 11028);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void send(byte[] command, InetAddress ip, int port) throws IOException {

        new Thread() {
            @Override
            public void run() {
                super.run();
                DatagramPacket dp = new DatagramPacket(command, command.length, ip, port);

                try {
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

    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }
}
