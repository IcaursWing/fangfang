package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.lobot.lobotcontrol.component.CircularProgressView;
import com.lobot.lobotcontrol.uitls.BluetoothUtils;
import com.lobot.lobotcontrol.uitls.LogUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class SearchDeviceDialog
        extends PopupWindow
        implements OnClickListener, OnItemClickListener
{
    private static final int SCAN_TIMEOUT = 60000;
    private static final String TAG = "SearchDeviceDialog";
    private static OnDeviceSelectedListener onDeviceSelectedListener;
    private Activity context;
    private LeScanCallback leCallBack = new LeScanCallback()
    {
        public void onLeScan(BluetoothDevice paramAnonymousBluetoothDevice, int paramAnonymousInt, byte[] paramAnonymousArrayOfByte)
        {
            String str = SearchDeviceDialog.TAG;
            paramAnonymousArrayOfByte = new StringBuilder();
            paramAnonymousArrayOfByte.append("found device : ");
            paramAnonymousArrayOfByte.append(paramAnonymousBluetoothDevice.getName());
            paramAnonymousArrayOfByte.append(", address = ");
            paramAnonymousArrayOfByte.append(paramAnonymousBluetoothDevice.getAddress());
            LogUtil.i(str, paramAnonymousArrayOfByte.toString());
            if (paramAnonymousBluetoothDevice.getBondState() != 12) {
                SearchDeviceDialog.this.mAdapter.add(paramAnonymousBluetoothDevice);
            }
        }
    };
    private BluetoothDataAdapter mAdapter;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Handler mHandler;
    private CircularProgressView progressView;
    private boolean scanning = false;
    private TextView titleTV;
    private View view;

    public SearchDeviceDialog(Activity paramActivity, View paramView, int paramInt1, int paramInt2)
    {
        super(paramView, paramInt1, paramInt2, false);
        this.titleTV = ((TextView)paramView.findViewById(2131165251));
        ListView localListView = (ListView)paramView.findViewById(2131165248);
        this.progressView = ((CircularProgressView)paramView.findViewById(2131165250));
        Button localButton = (Button)paramView.findViewById(2131165333);
        paramView = (Button)paramView.findViewById(2131165313);
        this.context = paramActivity;
        this.mAdapter = new BluetoothDataAdapter(paramActivity);
        localListView.setAdapter(this.mAdapter);
        localListView.setOnItemClickListener(this);
        localButton.setOnClickListener(this);
        paramView.setOnClickListener(this);
        this.mHandler = new Handler();
        scanBLEDevice();
    }

    public static SearchDeviceDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, OnDeviceSelectedListener paramOnDeviceSelectedListener)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296296, null);
        paramActivity = new SearchDeviceDialog(paramActivity, localView, paramInt1, paramInt2);
        paramActivity.view = localView;
        onDeviceSelectedListener = paramOnDeviceSelectedListener;
        paramActivity.setOutsideTouchable(true);
        paramActivity.setBackgroundDrawable(new ColorDrawable());
        paramActivity.setAnimationStyle(2131493221);
        return paramActivity;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i != 2131165313)
        {
            if (i == 2131165333) {
                dismiss();
            }
        }
        else
        {
            stopScan();
            this.mAdapter.clear();
            scanBLEDevice();
        }
    }

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        if (onDeviceSelectedListener != null)
        {
            stopScan();
            paramAdapterView = this.mAdapter.getItem(paramInt);
            onDeviceSelectedListener.onDeviceSelected(paramAdapterView);
        }
        dismiss();
    }

    public void scanBLEDevice()
    {
        if ((this.mHandler != null) && (this.mBluetoothAdapter != null))
        {
            this.mHandler.postDelayed(new Runnable()
            {
                public void run()
                {
                    SearchDeviceDialog.this.stopScan();
                }
            }, 60000L);
            this.mBluetoothAdapter.startLeScan(this.leCallBack);
            this.titleTV.setText(2131427447);
            this.progressView.setVisibility(0);
            this.progressView.resetAnimation();
            this.scanning = true;
        }
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public void stopScan()
    {
        if ((this.mBluetoothAdapter != null) && (this.scanning))
        {
            this.mBluetoothAdapter.stopLeScan(this.leCallBack);
            this.titleTV.setText(2131427443);
            this.progressView.setVisibility(8);
            this.titleTV.setText(2131427443);
            this.scanning = false;
        }
    }

    class BluetoothDataAdapter
            extends BaseAdapter
    {
        private Context context;
        private ArrayList<BluetoothDevice> devices;

        public BluetoothDataAdapter(Context paramContext)
        {
            this.context = paramContext;
            this$1 = SearchDeviceDialog.this.mBluetoothAdapter.getBondedDevices();
            this.devices = new ArrayList();
            if (SearchDeviceDialog.this.size() > 0)
            {
                this$1 = SearchDeviceDialog.this.iterator();
                while (SearchDeviceDialog.this.hasNext())
                {
                    paramContext = (BluetoothDevice)SearchDeviceDialog.this.next();
                    this.devices.add(paramContext);
                }
            }
        }

        public void add(BluetoothDevice paramBluetoothDevice)
        {
            if (!this.devices.contains(paramBluetoothDevice))
            {
                this.devices.add(paramBluetoothDevice);
                notifyDataSetChanged();
            }
        }

        public void clear()
        {
            this.devices.clear();
            Object localObject = SearchDeviceDialog.this.mBluetoothAdapter.getBondedDevices();
            this.devices = new ArrayList();
            if (((Set)localObject).size() > 0)
            {
                Iterator localIterator = ((Set)localObject).iterator();
                while (localIterator.hasNext())
                {
                    localObject = (BluetoothDevice)localIterator.next();
                    this.devices.add(localObject);
                }
            }
            notifyDataSetChanged();
        }

        public int getCount()
        {
            return this.devices.size();
        }

        public BluetoothDevice getItem(int paramInt)
        {
            return (BluetoothDevice)this.devices.get(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            Object localObject = new ViewHolder();
            if (paramView == null)
            {
                paramViewGroup = LayoutInflater.from(this.context).inflate(2131296291, paramViewGroup, false);
                ((ViewHolder)localObject).titleView = ((TextView)paramViewGroup.findViewById(2131165275));
                ((ViewHolder)localObject).contentView = ((TextView)paramViewGroup.findViewById(2131165273));
                ((ViewHolder)localObject).deleteBtn = ((ImageButton)paramViewGroup.findViewById(2131165274));
                paramViewGroup.setTag(localObject);
                paramView = (View)localObject;
            }
            else
            {
                localObject = (ViewHolder)paramView.getTag();
                paramViewGroup = paramView;
                paramView = (View)localObject;
            }
            BluetoothDevice localBluetoothDevice = (BluetoothDevice)this.devices.get(paramInt);
            if (localBluetoothDevice.getBondState() == 12)
            {
                localObject = this.context.getString(2131427444, new Object[] { localBluetoothDevice.getName() });
                paramView.deleteBtn.setVisibility(0);
                paramView.deleteBtn.setOnClickListener(new OnClickListener()
                {
                    public void onClick(View paramAnonymousView)
                    {
                        int i = ((Integer)paramAnonymousView.getTag()).intValue();
                        if (BluetoothUtils.removePaired(BluetoothDataAdapter.this.getItem(i))) {
                            BluetoothDataAdapter.this.removePair(i);
                        }
                    }
                });
            }
            else
            {
                localObject = localBluetoothDevice.getName();
                paramView.deleteBtn.setVisibility(4);
            }
            paramView.titleView.setText((CharSequence)localObject);
            paramView.contentView.setText(localBluetoothDevice.getAddress());
            paramView.deleteBtn.setTag(Integer.valueOf(paramInt));
            LogUtil.i(SearchDeviceDialog.TAG, "view refresh");
            return paramViewGroup;
        }

        public void removePair(int paramInt)
        {
            this.devices.remove(paramInt);
            notifyDataSetChanged();
        }

        class ViewHolder
        {
            TextView contentView;
            ImageButton deleteBtn;
            TextView titleView;

            ViewHolder() {}
        }
    }

    public static abstract interface OnDeviceSelectedListener
    {
        public abstract void onDeviceSelected(BluetoothDevice paramBluetoothDevice);
    }
}
