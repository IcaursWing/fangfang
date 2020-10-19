package Adapter;

import android.bluetooth.BluetoothGatt;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.fangfangtech.oidbox.R;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.button.switchbutton.SwitchButton;
import com.xuexiang.xui.widget.progress.loading.MiniLoadingView;
import com.xuexiang.xutil.resource.ResUtils;

import ExtraUtil.XToastUtils;

public class RecycleAdapter_Main2 extends BaseRecyclerAdapter<BleDevice> {


    TextView tv_deviceName;
    ImageButton bt_signal;
    ImageButton bt_more;
    MiniLoadingView miniLoadingView;
    SwitchButton switchButton;


    public interface OnDeviceClickListener {
        void onConnect(BleDevice bleDevice, BleGattCallback bleGattCallback);

        void onDisConnect(BleDevice bleDevice);

        void onDetail(BleDevice bleDevice, ImageButton more);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }


    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.listitem_main2_device;
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, BleDevice item) {
        tv_deviceName = holder.findViewById(R.id.listitem_main2_devicename);
        bt_signal = holder.findViewById(R.id.listitem_main2_signal);
        bt_more = holder.findViewById(R.id.listitem_main2_more);
        miniLoadingView = holder.findViewById(R.id.loadingView_main2);
        switchButton = holder.findViewById(R.id.listitem_main2_switch);
        miniLoadingView.setVisibility(View.INVISIBLE);

        if (BleManager.getInstance().isConnected(item)) {
            bt_signal.setImageResource(R.mipmap.icon_wifi2);
            switchButton.setChecked(true);
            switchButton.setThumbColorRes(R.color.xui_btn_green_normal_color);
            switchButton.setTextColor(ResUtils.getColor(R.color.xui_config_color_blue));
        }

        BleGattCallback bleGattCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                bt_signal.setVisibility(View.INVISIBLE);
                miniLoadingView.setVisibility(View.VISIBLE);
                miniLoadingView.start();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                bt_signal.setVisibility(View.VISIBLE);
                miniLoadingView.setVisibility(View.INVISIBLE);
                miniLoadingView.stop();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                bt_signal.setImageResource(R.mipmap.icon_wifi2);
                bt_signal.setVisibility(View.VISIBLE);
                miniLoadingView.setVisibility(View.INVISIBLE);
                miniLoadingView.stop();
                switchButton.setChecked(true);
                switchButton.setThumbColorRes(R.color.xui_btn_green_normal_color);
                switchButton.setTextColor(ResUtils.getColor(R.color.xui_config_color_blue));
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                switchButton.setChecked(false);
                switchButton.setThumbColorRes(R.color.gray);
                switchButton.setTextColor(ResUtils.getColor(R.color.black));
                bt_signal.setImageResource(R.mipmap.icon_wifi);
                XToastUtils.error(device.getName() + "断开连接！");
            }
        };

        tv_deviceName.setText(item.getName());

        bt_signal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BleManager.getInstance().isConnected(item)) {
                    mListener.onDisConnect(item);
                } else {
                    mListener.onConnect(item, bleGattCallback);
                }
            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !BleManager.getInstance().isConnected(item)) {
                    mListener.onConnect(item, bleGattCallback);
                    switchButton.setChecked(true);
                    switchButton.setThumbColorRes(R.color.xui_btn_green_normal_color);
                    switchButton.setTextColor(ResUtils.getColor(R.color.xui_config_color_blue));
                } else if (!isChecked && BleManager.getInstance().isConnected(item)) {
                    mListener.onDisConnect(item);
                    switchButton.setChecked(false);
                    switchButton.setThumbColorRes(R.color.gray);
                    switchButton.setTextColor(ResUtils.getColor(R.color.black));
                }
            }
        });

        bt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDetail(item, bt_more);
            }
        });


    }


}
