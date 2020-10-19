package MyFragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.fangfangtech.oidbox.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xui.widget.statelayout.CustomStateOptions;
import com.xuexiang.xui.widget.statelayout.StatefulLayout;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

import Adapter.RecycleAdapter_Main2;
import butterknife.BindView;
import me.jessyan.autosize.internal.CancelAdapt;

import static com.xuexiang.xutil.app.AppUtils.getPackageName;

@Page(name = "设备列表")
public class Fragment_Main2 extends XPageFragment implements CancelAdapt {
    CustomStateOptions customStateOptions;
    RecycleAdapter_Main2 recycleAdapter;
    List<BleDevice> bleDeviceList;

    @BindView(R.id.titlebar_main2)
    TitleBar titlebar;
    @BindView(R.id.recyclerView_main2)
    RecyclerView recyclerView;
    @BindView(R.id.statefulLayout_main2)
    StatefulLayout statefulLayout;
    @BindView(R.id.refreshLayout_main2)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.classicsHeader_main2)
    ClassicsHeader classicsHeader;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main2;
    }

    @Override
    protected void initViews() {
        titlebar.disableLeftView().addAction(new TitleBar.ImageAction(R.mipmap.icon_question) {
            @Override
            public void performAction(View view) {
                new MaterialDialog.Builder(getContext()).iconRes(R.mipmap.icon_question2).title("连接说明").content("1.打开小方蓝牙，点击刷新按钮\n" + "2.在下方点击您的小方进行连接\n" + "3.点击右侧按钮扫描包装盒上条形码\n" + "4.连接成功").positiveText("我知道了").cancelable(false).show();
            }
        });
        titlebar.addAction(new TitleBar.ImageAction(R.mipmap.scan_icon) {
            @Override
            public void performAction(View view) {
                AndPermission.with(getContext()).runtime().permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE).onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Intent intent = new Intent(getContext(), CaptureActivity.class);
                        /*
                         * ZxingConfig是配置类可以设置是否显示底部布局，闪光灯，相册， 是否播放提示音 震动
                         * 设置扫描框颜色等 也可以不传这个参数
                         */
                        ZxingConfig config = new ZxingConfig();
                        config.setPlayBeep(true);//是否播放扫描声音 默认为true
                        config.setShake(true);//是否震动 默认为true
                        config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                        config.setReactColor(R.color.white);//设置扫描框四个角的颜色
                        // 默认为白色
                        config.setFrameLineColor(R.color.white);//设置扫描框边框颜色
                        // 默认无色
                        config.setScanLineColor(R.color.white);//设置扫描线的颜色
                        // 默认白色
                        config.setFullScreenScan(false);// 是否全屏扫描 默认为true
                        // 设为false则只会在扫描框中扫描
                        config.setShowAlbum(true);
                        config.setShowFlashLight(true);
                        config.setShowbottomLayout(true);
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                        startActivityForResult(intent, 0);
                    }

                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(intent);
                        Toast.makeText(getContext(), "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                    }

                }).start();
            }
        });

        ClassicsHeader.REFRESH_HEADER_PULLING = "下拉可以扫描";
        ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在扫描";
        ClassicsHeader.REFRESH_HEADER_RELEASE = "释放立即扫描";
        ClassicsHeader.REFRESH_HEADER_FINISH = "扫描完成";
        ClassicsHeader.REFRESH_HEADER_FAILED = "扫描失败";


        customStateOptions = new CustomStateOptions();
        customStateOptions.image(R.drawable.empty_icon).message("空").buttonText("去扫描").buttonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });


        recyclerView.setNestedScrollingEnabled(false);
        statefulLayout.showCustom(customStateOptions);

        WidgetUtils.initRecyclerView(recyclerView);
    }

    @Override
    protected void initListeners() {

        InitScan();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                bleDeviceList = BleManager.getInstance().getAllConnectedDevice();
                recyclerView.setAdapter(recycleAdapter);
                refreshLayout.finishRefresh(15000);
                StartScan();
            }
        });


        recycleAdapter = new RecycleAdapter_Main2();
        recyclerView.setAdapter(recycleAdapter);
        recycleAdapter.setOnDeviceClickListener(new RecycleAdapter_Main2.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice, BleGattCallback bleGattCallback) {
                StopScan();
                BleManager.getInstance().connect(bleDevice, bleGattCallback);
            }

            @Override
            public void onDisConnect(BleDevice bleDevice) {
                BleManager.getInstance().disconnect(bleDevice);
            }

            @Override
            public void onDetail(BleDevice bleDevice, ImageButton more) {
                String[] strings = new String[2];
                strings[0] = "MAC：" + bleDevice.getMac();
                strings[1] = "Signal：" + bleDevice.getRssi();

                XUISimplePopup popup = new XUISimplePopup(getContext(), strings).create(800, 500, new XUISimplePopup.OnPopupItemClickListener() {
                    @Override
                    public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {

                    }
                });
                popup.showDown(more);


            }
        });
    }

    @Override
    protected com.xuexiang.xpage.utils.TitleBar initTitleBar() {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Toast.makeText(getContext(), "扫码成功：" + data.getStringExtra(Constant.CODED_CONTENT), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            StartScan();
        }

    }

    private void InitScan() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, "OidBox")   // 只扫描指定广播名的设备，可选
                .setDeviceMac(null)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10000)              // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void StartScan() {

        AndPermission.with(getContext()).runtime().permission(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {

                if (BleManager.getInstance().isBlueEnable()) {
                    bleDeviceList = BleManager.getInstance().getAllConnectedDevice();
                    recycleAdapter.refresh(bleDeviceList);
                    recyclerView.setAdapter(recycleAdapter);

                    BleManager.getInstance().scan(new BleScanCallback() {
                        @Override
                        public void onScanFinished(List<BleDevice> scanResultList) {
                            refreshLayout.finishRefresh();
                        }

                        @Override
                        public void onScanStarted(boolean success) {
                            statefulLayout.showContent();
                        }

                        @Override
                        public void onScanning(BleDevice bleDevice) {
                            bleDeviceList.add(bleDevice);
                            recycleAdapter.refresh(bleDeviceList);
                            recyclerView.setAdapter(recycleAdapter);
                        }
                    });

                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }

            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                Toast.makeText(getContext(), "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                refreshLayout.finishRefresh();
            }
        }).start();


    }

    private void StopScan() {
        BleManager.getInstance().cancelScan();
        refreshLayout.finishRefresh();
    }
}
