package com.example.oidboxgame;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;
import com.hb.dialog.dialog.ConfirmDialog;
import com.hb.dialog.dialog.LoadingDialog;
import com.hb.dialog.myDialog.MultiListViewDialog;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import adapter.CommonAdapter;
import adapter.ViewHolder;
import me.jessyan.autosize.internal.CancelAdapt;
import myutil.Str2HexStr;

public class Register extends Activity implements CancelAdapt {
    public static String Service_uuid = "00002030-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_notify = "00002051-1212-efde-1523-785fea6c3593";
    public static String Characteristic_uuid_write = "00002052-1212-efde-1523-785fea6c3593";

    EditText et_username, et_phone, et_password1, et_password2, et_verify;
    TextView tv_fangmac, tv_fangid;
    SuperButton bt_register, bt_fangmac, bt_fangid, bt_verify;
    CountDownButtonHelper Hbt_verify;
    List<BleDevice> blelist;
    MultiListViewDialog multiListViewDialogMac;
    CommonAdapter<BleDevice> commonAdapter;
    BleDevice myBleDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
        NoHttp.initialize(this);
        BleManager.getInstance().init(getApplication());


        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!et_username.getText().toString().equals("") && !et_phone.getText().toString().equals("") && !et_verify.getText().toString().equals("") && !et_password1.getText().toString().equals("") && !et_password2.getText().toString().equals("") && !tv_fangmac.getText().toString().equals("寻找周围的小方获取信息") && !tv_fangid.getText().toString().equals("获取小方产品编码")) {

                    if (et_password1.getText().toString().equals(et_password2.getText().toString())) {

                        final LoadingDialog loadingDialog = new LoadingDialog(Register.this);
                        loadingDialog.setMessage("注册中...");
                        loadingDialog.setCancelable(true);
                        loadingDialog.show();

                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                StringRequest request = new StringRequest("http://36.155.102.74:8888/OidBox/appregister", RequestMethod.POST);
                                request.set("username", et_username.getText().toString());
                                request.set("phone", et_phone.getText().toString());
                                request.set("password", et_password1.getText().toString());
                                request.set("fangmac", tv_fangmac.getText().toString());
                                request.set("fangid", tv_fangid.getText().toString());

                                final Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
                                if (response.isSucceed()) {

                                    loadingDialog.dismiss();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (response.getHeaders().getValue("state", 0).equals("1")) {
                                                final ConfirmDialog confirmDialog = new ConfirmDialog(Register.this);

                                                confirmDialog.setMsg("注册成功，\n返回登录！").setLogoImg(R.drawable.ic_dialog_finish);
                                                confirmDialog.setCancelable(false);
                                                confirmDialog.setPositiveBtn(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        confirmDialog.dismiss();
                                                        finish();
                                                    }
                                                });
                                                confirmDialog.show();

                                            } else if (response.getHeaders().getValue("state", 0).equals("2")) {
                                                final ConfirmDialog confirmDialog = new ConfirmDialog(Register.this);
                                                confirmDialog.setMsg("注册失败，用户名已注册！").setLogoImg(R.mipmap.close);
                                                confirmDialog.setCancelable(false);
                                                confirmDialog.setPositiveBtn(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        confirmDialog.dismiss();
                                                    }
                                                });
                                                confirmDialog.show();
                                            } else {
                                                final ConfirmDialog confirmDialog = new ConfirmDialog(Register.this);
                                                confirmDialog.setMsg("注册失败，注册信息有误。\n请重新提交注册信息！").setLogoImg(R.mipmap.close);
                                                confirmDialog.setCancelable(false);
                                                confirmDialog.setPositiveBtn(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        confirmDialog.dismiss();
                                                    }
                                                });
                                                confirmDialog.show();
                                            }
                                        }
                                    });


                                } else {
                                    // 请求失败，拿到错误：
                                    Exception e = response.getException();
                                    e.printStackTrace();
                                    loadingDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Register.this, "注册失败，请检查网络！", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        };
                        thread.start();


                    } else {
                        Toast.makeText(Register.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Register.this, "用户名、验证码、密码、小方信息或编号不能为空！", Toast.LENGTH_SHORT).show();
                }


            }
        });
        bt_fangmac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AndPermission.with(Register.this).runtime().permission(Permission.ACCESS_COARSE_LOCATION).onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                        if (!BleManager.getInstance().isBlueEnable()) {
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, 2);
                        } else {
                            ShowFangList();
                        }

                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(Register.this, "没有权限无法用蓝牙扫描呦", Toast.LENGTH_LONG).show();
                    }
                }).start();


            }
        });

        bt_fangid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AndPermission.with(Register.this).runtime().permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Intent intent = new Intent(Register.this, CaptureActivity.class);
                        /*
                         * ZxingConfig是配置类可以设置是否显示底部布局，闪光灯，相册， 是否播放提示音 震动
                         * 设置扫描框颜色等 也可以不传这个参数
                         */
                        ZxingConfig config = new ZxingConfig();
                        // config.setPlayBeep(false);//是否播放扫描声音 默认为true
                        // config.setShake(false);//是否震动 默认为true
                        // config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                        // config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色
                        // 默认为白色
                        // config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色
                        // 默认无色
                        // config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色
                        // 默认白色
                        config.setFullScreenScan(false);// 是否全屏扫描 默认为true
                        // 设为false则只会在扫描框中扫描
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, 1);
                    }

                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);

                        Toast.makeText(Register.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }

                }).start();

            }
        });

        bt_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hbt_verify.start();
                Random random = new Random();
                String verify = String.valueOf(random.nextInt(999999) % (999999 - 100000 + 1) + 100000);
                et_verify.setText(verify);
            }
        });
    }

    private void Init() {
        et_username = findViewById(R.id.et_register_username);
        et_phone = findViewById(R.id.et_register_phone);
        et_password1 = findViewById(R.id.et_register_password1);
        et_password2 = findViewById(R.id.et_register_password2);
        et_verify = findViewById(R.id.et_register_verify);
        tv_fangid = findViewById(R.id.tv_register_fangid);
        tv_fangmac = findViewById(R.id.tv_register_fangmac);
        bt_register = findViewById(R.id.btn_register_commit);
        bt_fangid = findViewById(R.id.bt_register_fangid);
        bt_fangmac = findViewById(R.id.bt_register_fangmac);
        bt_verify = findViewById(R.id.bt_register_verify);
        Hbt_verify = new CountDownButtonHelper(bt_verify, 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hbt_verify.cancel();
        BleManager.getInstance().disconnectAllDevice();
    }

    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBox")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(5000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                blelist.add(bleDevice);
                multiListViewDialogMac.setAdapter(commonAdapter);
            }


            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
            }
        });
    }

    private void ShowFangList() {
        blelist = new ArrayList<>();
        commonAdapter = new CommonAdapter<BleDevice>(Register.this, blelist, R.layout.item_list) {
            @Override
            public void convert(ViewHolder helper, BleDevice item) {
                helper.setText(R.id.tv_name, item.getName());
            }
        };

        multiListViewDialogMac = new MultiListViewDialog(Register.this).builder().setAdapter(commonAdapter).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BleManager.getInstance().cancelScan();
                String tempmac = blelist.get(position).getMac();
                tv_fangmac.setText(tempmac);
                BleManager.getInstance().connect(tempmac, new BleGattCallback() {
                    @Override
                    public void onStartConnect() {

                    }

                    @Override
                    public void onConnectFail(BleDevice bleDevice, BleException exception) {

                    }

                    @Override
                    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                        myBleDevice = bleDevice;

                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                    }
                });
                multiListViewDialogMac.dismiss();
            }
        });
        multiListViewDialogMac.setTitle("请选择您的听小方：（若没有找到小方请确认小方蓝牙已开启并处于未连接状态！）");
        multiListViewDialogMac.sureTv.setVisibility(View.GONE);
        multiListViewDialogMac.cancelTv.setVisibility(View.GONE);
        multiListViewDialogMac.show();
        setScanRule();
        startScan();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {

            String NumResult = data.getStringExtra(Constant.CODED_CONTENT);


            if (NumResult.length() == 10) {

                try {
                    int year = Integer.valueOf(NumResult.substring(0, 2));
                    int month = Integer.valueOf(NumResult.substring(2, 4));
                    int day = Integer.valueOf(NumResult.substring(4, 6));
                    int id = Integer.valueOf(NumResult.substring(6, 10));

                    if (year > 19 && month > 0 && month < 13 && day > 0 && day < 32 && id > 0) {


                        if (myBleDevice != null) {
                            Toast.makeText(getApplicationContext(), "扫码成功！", Toast.LENGTH_SHORT).show();
                            tv_fangid.setText(NumResult);

                            BleManager.getInstance().write(myBleDevice, Service_uuid, Characteristic_uuid_write, HexUtil.hexStringToBytes("F00F68500A" + Str2HexStr.str2HexStr(NumResult) + "0016"),
                                    new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                }

                                @Override
                                public void onWriteFailure(BleException exception) {
                                    Toast.makeText(getApplicationContext(), "绑定失败，请检查后重新扫码！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "请先获取小方MAC再扫码绑定！", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "错误的序列号！", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "错误的序列号！", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "错误的序列号！", Toast.LENGTH_LONG).show();
            }


        } else if (requestCode == 2) {
            ShowFangList();
        }

    }


}
