package com.example.oidboxgame;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.google.android.material.navigation.NavigationView;
import com.hb.dialog.dialog.ConfirmDialog;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.xuexiang.xhttp2.XHttpSDK;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.alpha.XUIAlphaTextView;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xutil.common.ClickUtils;
import com.xuexiang.xutil.display.CProgressDialogUtils;
import com.xuexiang.xutil.tip.ToastUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import ExtraUtil.AllowX509TrustManager;
import ExtraUtil.XHttpUpdateHttpService;
import me.jessyan.autosize.internal.CancelAdapt;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

public class MainActivity extends Activity implements CancelAdapt, View.OnClickListener {

    private DeviceAdapter mDeviceAdapter;
    //private MenuItem menuItem, menuLogin;
    private ProgressDialog progressDialog;
    private BluetoothAdapter mBluetoothAdapter = null;
    SharedPreferences sharedPreferences;
    boolean isLogin;
    TitleBar titleBar;
    ListView listView;
    TitleBar.Action action1, action2, action3;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    LinearLayout nav_header;


    boolean hasChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        XHttpSDK.setBaseUrl("http://www.fangfangtech.com");  //设置网络请求的基础地址
        XUpdate.get().debug(true).isWifiOnly(false)                                               //默认设置只在wifi下检查版本更新
                .isGet(true)                                                    //默认设置使用get请求检查版本
                .isAutoMode(false)                                              //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(this))         //设置默认公共请求参数
                .param("appKey", getPackageName()).setOnUpdateFailureListener(new OnUpdateFailureListener() {     //设置版本更新出错的监听
            @Override
            public void onFailure(UpdateError error) {
                if (error.getCode() != CHECK_NO_NEW_VERSION) {          //对不同错误进行处理
                    ToastUtils.toast(error.toString());
                } else {
                    if (hasChecked) {
                        ToastUtils.toast("当前已是最新版本，无需更新。");
                    }
                }
                hasChecked = true;
            }
        }).supportSilentInstall(false)                                     //设置是否支持静默安装，默认是true
                .setIUpdateHttpService(new XHttpUpdateHttpService("http://www.fangfangtech.com"))           //这个必须设置！实现网络请求功能。
                .init(getApplication());                                                    //这个必须初始化

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        titleBar = findViewById(R.id.listview_mutiblue_test);
        listView = findViewById(R.id.listview_mutiblue);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
            confirmDialog.setLogoImg(R.drawable.ic_pic_delete).setMsg("非常抱歉，您手机不支持蓝牙，无法使用我们的产品，应用将要退出！");
            confirmDialog.setPositiveBtn(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                    finish();
                }
            });
            confirmDialog.setCancelable(false);
            confirmDialog.show();

            return;
        }

        AndPermission.with(getApplicationContext()).runtime().permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {

            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                Toast.makeText(MainActivity.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                finish();
            }
        }).start();

        //~~~~~~~~~~~~~~~~~~~~
        BleManager.getInstance().init(getApplication());//初始化
        BleManager.getInstance().enableLog(true).setReConnectCount(1, 5000).setConnectOverTime(20000).setOperateTimeout(5000);//设置蓝牙连接属性

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {

            }
        });
        listView.setAdapter(mDeviceAdapter);


        action1 = new TitleBar.TextAction("登录") {
            @Override
            public void performAction(View view) {

                String username = sharedPreferences.getString("username", null);
                if (username != null) {
                    isLogin = true;
                    ((TextView) view).setText("我的");
                } else {
                    isLogin = false;
                }

                if (isLogin) {
                    drawerLayout.openDrawer(navigationView);
                } else {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(intent, 1);
                }

//                if (isLogin) {
//                    final ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
//                    confirmDialog.setMsg("是否重新登录？").setLogoImg(R.mipmap.oidboxicon);
//                    confirmDialog.setCancelable(false);
//                    confirmDialog.setPositiveBtn(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            confirmDialog.dismiss();
//                            Intent enableBtIntent = new Intent(MainActivity.this, Login.class);
//                            startActivityForResult(enableBtIntent, 1);
//                        }
//                    });
//                    confirmDialog.setNegativeBtn(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            confirmDialog.dismiss();
//                        }
//                    });
//                    confirmDialog.show();
//
//                } else {
//                    Intent enableBtIntent = new Intent(MainActivity.this, Login.class);
//                    startActivityForResult(enableBtIntent, 1);
//                }


            }
        };


        action2 = new TitleBar.TextAction("开始游戏") {
            @Override
            public void performAction(View view) {

                final String[] items = {"空白舒尔特", "趣味知识竞赛", "音乐创作"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                listDialog.setTitle("选择游戏");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            final MyAlertInputDialog myAlertInputDialog =
                                    new MyAlertInputDialog(MainActivity.this).builder().setTitle("设置倒计时时间").setCancelable(true).setEditType(InputType.TYPE_CLASS_NUMBER).setEditText("");
                            myAlertInputDialog.edittxt_result.setHint("1~30（秒）");
                            myAlertInputDialog.edittxt_result.setTextSize(20);
                            myAlertInputDialog.txt_title.setTextSize(25);
                            myAlertInputDialog.setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!myAlertInputDialog.getResult().equals("")) {
                                        if (myAlertInputDialog.getResult().equals("0")) {
                                            Toast.makeText(MainActivity.this, "答题连题都不看怎么答？", Toast.LENGTH_LONG).show();
                                        } else if (Integer.valueOf(myAlertInputDialog.getResult()) > 30) {
                                            Toast.makeText(MainActivity.this, "就看9个数字而已，半分钟足够了！", Toast.LENGTH_LONG).show();
                                        } else {
                                            myAlertInputDialog.dismiss();
                                            Intent intent = new Intent(MainActivity.this, OidBoxGameTable.class);
                                            intent.putExtra("time", Integer.valueOf(myAlertInputDialog.getResult()));
                                            startActivity(intent);
                                        }

                                    } else {
                                        Toast.makeText(MainActivity.this, "请填写输入时间", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myAlertInputDialog.dismiss();
                                }
                            });
                            myAlertInputDialog.show();

                        } else if (which == 1) {
                            Intent intent = new Intent(MainActivity.this, OidBoxGameQuestion.class);
                            startActivity(intent);
                        } else if (which == 2) {
                            Intent intent = new Intent(MainActivity.this, OidBoxGameMusic.class);
                            startActivity(intent);
                        }

                    }
                });
                listDialog.show();


            }
        };
        action3 = new TitleBar.TextAction("扫描设备") {
            @Override
            public void performAction(View view) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                } else {
                    if (((TextView) view).getText().toString().equals(getString(R.string.menu_scan))) {
                        setScanRule();//设置扫描属性
                        startScan();//开始扫描
                    } else if (((TextView) view).getText().toString().equals("扫描中...")) {
                        BleManager.getInstance().cancelScan();//取消扫描
                    }
                }
            }
        };


        titleBar.addAction(action1, 0);
        titleBar.addAction(action2, 1);
        titleBar.addAction(action3, 2);


        String username = sharedPreferences.getString("username", null);
        if (username != null) {
            isLogin = true;
            ((XUIAlphaTextView) titleBar.getViewByAction(action1)).setText("我的");
        } else {
            isLogin = false;
        }

        nav_header = navigationView.getHeaderView(0).findViewById(R.id.nav_header);
        RadiusImageView radiusImageView = nav_header.findViewById(R.id.nav_head);
        TextView tv_nav_name = nav_header.findViewById(R.id.nav_name);
        TextView tv_nav_fangid = nav_header.findViewById(R.id.nav_fangid);

        radiusImageView.setOnClickListener(this);
        tv_nav_name.setOnClickListener(this);
        tv_nav_fangid.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_config:
                        Toast.makeText(MainActivity.this, item.getTitle() + " 敬请期待！", Toast.LENGTH_SHORT).show();

                        break;

                    case R.id.nav_update:
                        drawerLayout.closeDrawers();
                        AllowX509TrustManager.allowAllSSL();
                        XUpdate.newBuild(MainActivity.this).updateChecker(new DefaultUpdateChecker() {
                            @Override
                            public void onBeforeCheck() {
                                super.onBeforeCheck();
                                CProgressDialogUtils.showProgressDialog(MainActivity.this, "查询中...");
                            }

                            @Override
                            public void onAfterCheck() {
                                super.onAfterCheck();
                                CProgressDialogUtils.cancelProgressDialog(MainActivity.this);

                            }


                        }).updateUrl("http://www.fangfangtech.com/AppUpdateFile.json").update();

                        break;

                    case R.id.nav_logout:
                        drawerLayout.closeDrawers();
                        if (isLogin) {
                            final ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
                            confirmDialog.setMsg("是否注销登录？").setLogoImg(R.mipmap.oidboxicon);
                            confirmDialog.setCancelable(false);
                            confirmDialog.setPositiveBtn(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("username", null);
                                    editor.putString("password", null);
                                    editor.putString("phone", null);
                                    editor.putString("fangmac", null);
                                    editor.putString("fangid", null);
                                    editor.apply();
                                    isLogin = false;
                                    ((XUIAlphaTextView) titleBar.getViewByAction(action1)).setText("登录");
                                    confirmDialog.dismiss();


                                }
                            });
                            confirmDialog.setNegativeBtn(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmDialog.dismiss();
                                }
                            });
                            confirmDialog.show();

                        } else {
                            Intent enableBtIntent = new Intent(MainActivity.this, Login.class);
                            startActivityForResult(enableBtIntent, 1);
                        }

                        break;

                    case R.id.nav_exit:
                        finish();
                        break;

                    default:
                        break;


                }


                return false;
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (isLogin) {
                    ((TextView) navigationView.findViewById(R.id.nav_name)).setText(sharedPreferences.getString("username", null));
                    ((TextView) navigationView.findViewById(R.id.nav_fangid)).setText("ID：" + sharedPreferences.getString("fangid", null));

                    navigationView.getMenu().findItem(R.id.nav_phone).setTitle("手机：" + sharedPreferences.getString("phone", null));
                    navigationView.getMenu().findItem(R.id.nav_fangmac).setTitle("Mac：" + sharedPreferences.getString("fangmac", null));

                } else {
                    ((TextView) navigationView.findViewById(R.id.nav_name)).setText("未登录");
                    ((TextView) navigationView.findViewById(R.id.nav_fangid)).setText("小方方ID");
                    navigationView.getMenu().findItem(R.id.nav_phone).setTitle("手机");
                    navigationView.getMenu().findItem(R.id.nav_fangmac).setTitle("Mac");
                }


            }
        });


        XUpdate.newBuild(MainActivity.this).updateUrl("http://www.fangfangtech.com/AppUpdateFile.json").update();
    }


    @Override
    protected void onResume() {
        super.onResume();
        showConnectedDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bluetooth_menu, menu);
        menu.findItem(R.id.menu_muticontrol).setVisible(true);
        menu.findItem(R.id.menu_muticontrol).setTitle("开始游戏");
        menu.findItem(R.id.menu_mutiscan).setVisible(true);
        menu.findItem(R.id.menu_login).setVisible(true);
        //menuItem = menu.findItem(R.id.menu_mutiscan);
        // menuLogin = menu.findItem(R.id.menu_login);

        String username = sharedPreferences.getString("username", null);
        if (username != null) {
            isLogin = true;
            menu.findItem(R.id.menu_login).setTitle(username);
        } else {
            isLogin = false;
        }


        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_mutiscan:

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                } else {
                    if (item.getTitle().toString().equals(getString(R.string.menu_scan))) {
                        setScanRule();//设置扫描属性
                        startScan();//开始扫描
                    } else if (item.getTitle().toString().equals(getString(R.string.scanning))) {
                        BleManager.getInstance().cancelScan();//取消扫描
                    }
                }

                break;

            case R.id.menu_login:

                if (isLogin) {
                    final ConfirmDialog confirmDialog = new ConfirmDialog(MainActivity.this);
                    confirmDialog.setMsg("是否重新登录？").setLogoImg(R.mipmap.oidboxicon);
                    confirmDialog.setCancelable(false);
                    confirmDialog.setPositiveBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            Intent enableBtIntent = new Intent(MainActivity.this, Login.class);
                            startActivityForResult(enableBtIntent, 1);
                        }
                    });
                    confirmDialog.setNegativeBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                        }
                    });
                    confirmDialog.show();

                } else {
                    Intent enableBtIntent = new Intent(MainActivity.this, Login.class);
                    startActivityForResult(enableBtIntent, 1);
                }
                break;

            case R.id.menu_muticontrol:

                final String[] items = {"空白舒尔特", "趣味知识竞赛", "音乐创作"};
                AlertDialog.Builder listDialog = new AlertDialog.Builder(MainActivity.this);
                listDialog.setTitle("选择游戏");
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            final MyAlertInputDialog myAlertInputDialog =
                                    new MyAlertInputDialog(MainActivity.this).builder().setTitle("设置倒计时时间").setCancelable(true).setEditType(InputType.TYPE_CLASS_NUMBER).setEditText("");
                            myAlertInputDialog.edittxt_result.setHint("1~30（秒）");
                            myAlertInputDialog.edittxt_result.setTextSize(20);
                            myAlertInputDialog.txt_title.setTextSize(25);
                            myAlertInputDialog.setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!myAlertInputDialog.getResult().equals("")) {
                                        if (myAlertInputDialog.getResult().equals("0")) {
                                            Toast.makeText(MainActivity.this, "答题连题都不看怎么答？", Toast.LENGTH_LONG).show();
                                        } else if (Integer.valueOf(myAlertInputDialog.getResult()) > 30) {
                                            Toast.makeText(MainActivity.this, "就看9个数字而已，半分钟足够了！", Toast.LENGTH_LONG).show();
                                        } else {
                                            myAlertInputDialog.dismiss();
                                            Intent intent = new Intent(MainActivity.this, OidBoxGameTable.class);
                                            intent.putExtra("time", Integer.valueOf(myAlertInputDialog.getResult()));
                                            startActivity(intent);
                                        }

                                    } else {
                                        Toast.makeText(MainActivity.this, "请填写输入时间", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }).setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myAlertInputDialog.dismiss();
                                }
                            });
                            myAlertInputDialog.show();

                        } else if (which == 1) {
                            Intent intent = new Intent(MainActivity.this, OidBoxGameQuestion.class);
                            startActivity(intent);
                        } else if (which == 2) {
                            Intent intent = new Intent(MainActivity.this, OidBoxGameMusic.class);
                            startActivity(intent);
                        }

                    }
                });
                listDialog.show();


                break;

            default:
                break;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            isLogin = true;
            String username = sharedPreferences.getString("username", null);
            ((XUIAlphaTextView) titleBar.getViewByAction(action1)).setText("我的");
        }

    }

    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBox")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();

                ((XUIAlphaTextView) titleBar.getViewByAction(action3)).setText("扫描中...");


            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }


            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                ((XUIAlphaTextView) titleBar.getViewByAction(action3)).setText("扫描设备");

            }
        });
    }

    private void connect(final BleDevice bleDevice) {


        BleGattCallback bleGattCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.setMessage("连接 " + bleDevice.getName() + " 中...");
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, bleDevice.getName() + getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();

                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, bleDevice.getName() + "连接被断开", Toast.LENGTH_LONG).show();
                    //ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        };
        BleManager.getInstance().connect(bleDevice, bleGattCallback);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, "后台连接:" + bleDevice.getName(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showConnectedDevice() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        mDeviceAdapter.clearConnectedDevice();
        for (BleDevice bleDevice : deviceList) {
            mDeviceAdapter.addDevice(bleDevice);
        }
        mDeviceAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_head:
                Toast.makeText(MainActivity.this, "摸了下小方方的头！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_name:
                Toast.makeText(MainActivity.this, " 摸了下小方方的昵称！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_fangid:
                Toast.makeText(MainActivity.this, "摸了下小方方的ID！", Toast.LENGTH_SHORT).show();
                break;

            default:
                Toast.makeText(MainActivity.this, "点歪了！", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        ClickUtils.exitBy2Click();
    }
}
