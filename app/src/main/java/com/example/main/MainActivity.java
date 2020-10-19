package com.example.main;

import java.util.List;
import java.util.UUID;

import com.example.fangfang_gai.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import me.jessyan.autosize.internal.CancelAdapt;

public class MainActivity extends Activity implements CancelAdapt {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ENABLE_GPS = 2;
    private static final int REQUEST_Open_Blue = 3;
    private static final int REQUEST_CODE_SCAN = 4;
    private static final int REQUEST_CONNECT_DEVICE = 5;

    private int REQUEST_CODE_FANGFANG = 0;
    private int REQUEST_CODE_ROBO = 0;

    private static final String DECODED_CONTENT_KEY = "codedContent";

    private ImageButton BlueBar;
    private ImageButton upload;
    private ImageButton RobolSoul;
    private ImageButton yaokongche;
    private ImageButton bluetooth;
    private ImageButton grogramfang;
    private ImageButton mutifangfang;
    private ImageButton house;
    private ImageButton oidbox;
    private ImageButton hongzheng;
    private ImageButton iot;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mSendtoDevice = null;
    private BluetoothServerSocket mSendSocket = null;

    protected static final String CONNECT_FAILED = null;
    // private ArrayAdapter<String> mNewDevicesArrayAdapter;

    public String target_mac;
    public String server_uuid;
    public String target_uuid;
    public String Blue_Result;
    private UUID MY_UUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getActionBar().setIcon(R.drawable.ic_launcher);

        System.out.println("OK!");
        Log.i("test", "OK!");

//        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//            finish();
//        }
//
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // mChatService = new BluetoothChatService(this, mHandler);
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        BlueBar = (ImageButton) findViewById(R.id.BlueBar);
        BlueBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AndPermission.with(MainActivity.this).runtime().permission(Permission.ACCESS_FINE_LOCATION).onGranted(new Action<List<String>>() {

                    @Override
                    public void onAction(List<String> data) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            REQUEST_CODE_FANGFANG = 1;
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        } else if (!isGpsEnable(MainActivity.this)) {

                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_ENABLE_GPS);
                        } else {
                            // Intent intent = new
                            // Intent(MainActivity.this,
                            // CaptureActivity.class);
                            // startActivityForResult(intent,
                            // REQUEST_CODE_SCAN);
                            // Intent intent = new
                            // Intent(MainActivity.this,
                            // DeviceListActivity.class);
                            // startActivityForResult(intent,
                            // REQUEST_CONNECT_DEVICE);
                            Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                            startActivity(intent);
                        }
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }

                }).start();

            }
        });
        BlueBar.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        BlueBar.setImageResource(R.drawable.blue_bar_pressed);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        BlueBar.setImageResource(R.drawable.blue_bar);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        upload = findViewById(R.id.BlueUpload);
        upload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AndPermission.with(MainActivity.this).runtime().permission(Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action<List<String>>() {

                    @Override
                    public void onAction(List<String> data) {
                        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                        startActivity(intent);
                    }
                }).onDenied(new Action<List<String>>() {

                    @Override
                    public void onAction(List<String> data) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(MainActivity.this, "没有权限无法上传呦", Toast.LENGTH_LONG).show();
                    }
                }).start();

            }
        });

        upload.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        upload.setImageResource(R.drawable.blue_upload_pressed);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        upload.setImageResource(R.drawable.blue_upload);
                        break;
                    default:
                        break;
                }
                return false;

            }
        });

        RobolSoul = findViewById(R.id.RoboSoul);
        RobolSoul.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AndPermission.with(MainActivity.this).runtime().permission(Permission.ACCESS_FINE_LOCATION).onGranted(new Action<List<String>>() {

                    @Override
                    public void onAction(List<String> data) {
                        if (!mBluetoothAdapter.isEnabled()) {
                            REQUEST_CODE_ROBO = 1;
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        } else if (!isGpsEnable(MainActivity.this)) {

                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_ENABLE_GPS);
                        } else {
                            Intent intent = new Intent(MainActivity.this, RoboSoulConnect.class);
                            startActivity(intent);
                        }
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }

                }).start();
            }
        });

        RobolSoul.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        RobolSoul.setImageResource(R.drawable.robosoul_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        RobolSoul.setImageResource(R.drawable.robosoul);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        yaokongche = findViewById(R.id.yaokongche);
        yaokongche.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"控制", "调试"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                listDialog.setTitle("选择模式");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, yaokongche.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, yaokongchetest.class);
                            startActivity(intent);
                        }
                    }
                });
                listDialog.show();


            }
        });

        yaokongche.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        yaokongche.setImageResource(R.drawable.steamwifi_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        yaokongche.setImageResource(R.drawable.steamwifi);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        bluetooth = findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MutiBluetoothConnectActivity.class);
                startActivity(intent);
            }
        });
        bluetooth.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        bluetooth.setImageResource(R.drawable.bluetooth_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        bluetooth.setImageResource(R.drawable.bluetooth);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        grogramfang = findViewById(R.id.programfang);
        grogramfang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProgramFang.class);
                startActivity(intent);
            }
        });
        grogramfang.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        grogramfang.setImageResource(R.drawable.blue_programfang_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        grogramfang.setImageResource(R.drawable.blue_programfang);
                        break;
                    default:
                        break;
                }
                return false;
            }


        });

        mutifangfang = findViewById(R.id.mutifangfang);
        mutifangfang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MutiFangfang.class);
                startActivity(intent);
            }
        });
        mutifangfang.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mutifangfang.setImageResource(R.drawable.blue_mutifangfang_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mutifangfang.setImageResource(R.drawable.blue_mutifangfang);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        house = findViewById(R.id.bluehouse);
        house.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"总控", "作弊"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                listDialog.setTitle("选择模式");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, House.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, HouseTest.class);
                            startActivity(intent);
                        }
                    }
                });
                listDialog.show();
            }
        });
        house.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        house.setImageResource(R.drawable.blue_house_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        house.setImageResource(R.drawable.blue_house);
                        break;
                    default:
                        break;
                }
                return false;
            }


        });

        oidbox = findViewById(R.id.blueoidbox);
        oidbox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"套件游戏", "数据采集", "竞赛游戏"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                listDialog.setTitle("选择模式");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, OidBoxGame.class);
                            startActivity(intent);
                        } else if (which == 1) {
                            Intent intent = new Intent(MainActivity.this, OidBoxCollect.class);
                            startActivity(intent);
                        } else if (which == 2) {

                            if (!mBluetoothAdapter.isEnabled()) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivity(enableBtIntent);
                            } else {
                                Intent intent = new Intent(MainActivity.this, OidBoxGameConnect.class);
                                startActivity(intent);
                            }


                        }

                    }
                });
                listDialog.show();
            }
        });
        oidbox.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oidbox.setImageResource(R.drawable.blue_oidbox_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        oidbox.setImageResource(R.drawable.blue_oidbox);
                        break;
                    default:
                        break;
                }
                return false;
            }


        });

        hongzheng = findViewById(R.id.bluehongzheng);
        hongzheng.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"改名", "上课"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                listDialog.setTitle("选择模式");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, HongzhengName.class);
                            startActivity(intent);
                        } else {

                        }
                    }
                });
                listDialog.show();
            }
        });
        hongzheng.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hongzheng.setImageResource(R.drawable.blue_hongzheng_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        hongzheng.setImageResource(R.drawable.blue_hongzheng);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        iot = findViewById(R.id.blueiot);
        iot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, IotCamera.class);
                startActivity(intent);


//                new Thread() {
//                    @Override
//                    public void run() {
//                        super.run();
//
//                        File destination = new File(MainActivity.this.getExternalFilesDir(null).getPath() + "/actionlist.xml");
//                        new FtpClient("fangfangtech.ftp-gz01.bcehost.com", "fangfangtech", "elwohdoa", "/webroot/", "test.xml", destination).Ftpupload();
//                    }
//                }.start();


//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        FTPClientFunctions ftpClient = new FTPClientFunctions();
//                        boolean connectResult = ftpClient.ftpConnect("fangfangtech.ftp-gz01.bcehost.com", "fangfangtech", "elwohdoa", 8010);
//                        if (connectResult) {
//                            boolean changeDirResult = ftpClient.ftpChangeDir("/webroot/");
//                            if (changeDirResult) {
//                                boolean uploadResult = ftpClient.ftpUpload(MainActivity.this.getExternalFilesDir(null).getPath() + "/actionlist.xml", "actionlist.xml", "");
//                                if (uploadResult) {
//                                    Log.w("test", "上传成功");
//                                    boolean disConnectResult = ftpClient.ftpDisconnect();
//                                    if (disConnectResult) {
//                                        Log.e("test", "关闭ftp连接成功");
//                                    } else {
//                                        Log.e("test", "关闭ftp连接失败");
//                                    }
//                                } else {
//                                    Log.w("test", "上传失败");
//                                }
//                            } else {
//                                Log.w("test", "切换ftp目录失败");
//                            }
//
//                        } else {
//                            Log.w("test", "连接ftp服务器失败");
//                        }
//                    }
//                }).start();


            }
        });
        iot.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iot.setImageResource(R.drawable.blue_iot_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        iot.setImageResource(R.drawable.blue_iot);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        AndPermission.with(MainActivity.this).runtime().permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_FINE_LOCATION).onGranted(new Action<List<String>>() {

            @Override
            public void onAction(List<String> data) {

            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);

                Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
            }

        }).start();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK && REQUEST_CODE_FANGFANG == 1) {
            REQUEST_CODE_FANGFANG = 0;
            Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
            startActivity(intent);
        } else if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK && REQUEST_CODE_ROBO == 1) {
            REQUEST_CODE_ROBO = 0;
            Intent intent = new Intent(MainActivity.this, RoboSoulConnect.class);
            startActivity(intent);
        }

        if (requestCode == REQUEST_ENABLE_GPS) {

        }

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Blue_Result = data.getStringExtra(DECODED_CONTENT_KEY);
                Toast.makeText(getApplicationContext(), "解码结果:" + Blue_Result, Toast.LENGTH_SHORT).show();
                // qrCoded.setText("解码结果： \n" + content);
                String[] splitstr = Blue_Result.split("#");
                target_mac = splitstr[0];
                server_uuid = splitstr[1];
                target_uuid = splitstr[2];
                UUID.fromString(server_uuid);
                Log.i("test", target_mac + " " + server_uuid + " " + target_uuid);

            }
        }

        if (requestCode == REQUEST_CONNECT_DEVICE && resultCode == Activity.RESULT_OK) {
        }
    }

    // private Handler mHandler = new Handler() {
    // public void handleMessage(Message msg) {
    // super.handleMessage(msg);
    // Gson gson = new GsonBuilder()
    // .excludeFieldsWithoutExposeAnnotation().create();
    // switch (msg.what) {
    // case Constants.MESSAGE_DEVICE_NAME:
    // Bundle bundle = msg.getData();
    //
    // break;
    // case Constants.MESSAGE_READ:
    // String jsonData = (String) msg.obj;
    //
    // }
    // }
    // };

    public static final boolean isGpsEnable(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }
        return false;
    }
    // {
    //
    // @Override
    // public void handleMessage(Message msg) {
    // super.handleMessage(msg);
    // switch (msg.what) {
    // case 1:
    //
    // break;
    //
    // default:
    // break;
    // }
    // }
    //
    // };

    // private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // String action = intent.getAction();
    // if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    // BluetoothDevice device = intent
    // .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    // if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
    // mNewDevicesArrayAdapter.add(device.getName() + "\n"
    // + device.getAddress());
    // mNewDevicesArrayAdapter.add(device.getName() + "\n"
    // + device.getAddress());
    // }
    // } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
    // .equals(action)) {
    //
    // }
    // }
    // };
    // IntentFilter filter =new IntentFilter(BluetoothDevice.ACTION_FOUND);
}
