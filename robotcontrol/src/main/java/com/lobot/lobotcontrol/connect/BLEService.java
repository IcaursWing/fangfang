package com.lobot.lobotcontrol.connect;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import com.lobot.lobotcontrol.uitls.LogUtil;
import java.util.List;

public class BLEService
        extends Service
{
    public static final String ACTION_DATA_AVAILABLE = "com.lobot.robot.le.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.lobot.robot.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_CONNECT_FAIL = "com.lobot.robot.le.ACTION_GATT_CONNECT_FAIL";
    public static final String ACTION_GATT_DISCONNECTED = "com.lobot.robot.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.lobot.robot.le.ACTION_GATT_SERVICE_DISCOVERED";
    private static final String BLE_NAME = "com.lobot.robot.le";
    public static final String EXTRA_DATA = "com.lobot.robot.le.EXTRA_DATA";
    private static final String TAG = "BLEService";
    boolean connectState = false;
    boolean descriptorWrite = false;
    private final BLEBinder mBinder = new BLEBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private int mConnectState = 0;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback()
    {
        public void onCharacteristicChanged(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic)
        {
            LogUtil.i(BLEService.TAG, "onCharacteristicRead status");
            paramAnonymousBluetoothGatt = paramAnonymousBluetoothGattCharacteristic.getValue();
            BLEService.this.sendBroadcast(paramAnonymousBluetoothGatt, "com.lobot.robot.le.ACTION_DATA_AVAILABLE");
        }

        public void onCharacteristicRead(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic, int paramAnonymousInt)
        {
            paramAnonymousBluetoothGatt = BLEService.TAG;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("onCharacteristicRead status = ");
            localStringBuilder.append(paramAnonymousInt);
            LogUtil.i(paramAnonymousBluetoothGatt, localStringBuilder.toString());
            paramAnonymousBluetoothGatt = paramAnonymousBluetoothGattCharacteristic.getValue();
            BLEService.this.sendBroadcast(paramAnonymousBluetoothGatt, "com.lobot.robot.le.ACTION_DATA_AVAILABLE");
        }

        public void onCharacteristicWrite(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattCharacteristic paramAnonymousBluetoothGattCharacteristic, int paramAnonymousInt)
        {
            paramAnonymousBluetoothGatt = BLEService.TAG;
            paramAnonymousBluetoothGattCharacteristic = new StringBuilder();
            paramAnonymousBluetoothGattCharacteristic.append("onCharacteristicWrite status = ");
            paramAnonymousBluetoothGattCharacteristic.append(paramAnonymousInt);
            LogUtil.i(paramAnonymousBluetoothGatt, paramAnonymousBluetoothGattCharacteristic.toString());
        }

        public void onConnectionStateChange(BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt1, int paramAnonymousInt2)
        {
            String str = BLEService.TAG;
            paramAnonymousBluetoothGatt = new StringBuilder();
            paramAnonymousBluetoothGatt.append("onConnectionStateChange status = ");
            paramAnonymousBluetoothGatt.append(paramAnonymousInt1);
            paramAnonymousBluetoothGatt.append(" state = ");
            paramAnonymousBluetoothGatt.append(paramAnonymousInt2);
            LogUtil.i(str, paramAnonymousBluetoothGatt.toString());
            switch (paramAnonymousInt2)
            {
                default:
                    break;
                case 3:
                    LogUtil.i(BLEService.TAG, "onConnectionStateChange --- disconnecting!");
                    break;
                case 2:
                    BLEService.this.connectState = true;
                    if ((BLEService.this.connectState == true) && (BLEService.this.servicesDiscovered == true) && (BLEService.this.descriptorWrite == true)) {
                        BLEService.this.sendBroadcast("com.lobot.robot.le.ACTION_GATT_CONNECTED");
                    }
                    LogUtil.i(BLEService.TAG, "onConnectionStateChange --- connected!");
                    str = BLEService.TAG;
                    paramAnonymousBluetoothGatt = new StringBuilder();
                    paramAnonymousBluetoothGatt.append("Attempting to start service discovery:");
                    paramAnonymousBluetoothGatt.append(BLEService.this.mBluetoothGatt.discoverServices());
                    LogUtil.i(str, paramAnonymousBluetoothGatt.toString());
                    break;
                case 1:
                    LogUtil.i(BLEService.TAG, "onConnectionStateChange --- connecting!");
                    break;
                case 0:
                    BLEService.this.connectState = false;
                    BLEService.this.servicesDiscovered = false;
                    BLEService.this.descriptorWrite = false;
                    if (BLEService.this.mConnectState == 1)
                    {
                        BLEService.this.sendBroadcast("com.lobot.robot.le.ACTION_GATT_CONNECT_FAIL");
                        LogUtil.i(BLEService.TAG, "onConnectionStateChange --- connect failed!");
                    }
                    else
                    {
                        BLEService.this.sendBroadcast("com.lobot.robot.le.ACTION_GATT_DISCONNECTED");
                        LogUtil.i(BLEService.TAG, "onConnectionStateChange --- disconnected!");
                    }
                    break;
            }
            BLEService.access$302(BLEService.this, paramAnonymousInt2);
        }

        public void onDescriptorWrite(BluetoothGatt paramAnonymousBluetoothGatt, BluetoothGattDescriptor paramAnonymousBluetoothGattDescriptor, int paramAnonymousInt)
        {
            super.onDescriptorWrite(paramAnonymousBluetoothGatt, paramAnonymousBluetoothGattDescriptor, paramAnonymousInt);
            BLEService.this.descriptorWrite = true;
            if ((BLEService.this.connectState == true) && (BLEService.this.servicesDiscovered == true) && (BLEService.this.descriptorWrite == true)) {
                BLEService.this.sendBroadcast("com.lobot.robot.le.ACTION_GATT_CONNECTED");
            }
            paramAnonymousBluetoothGatt = BLEService.TAG;
            paramAnonymousBluetoothGattDescriptor = new StringBuilder();
            paramAnonymousBluetoothGattDescriptor.append("onDescriptorWrite status = ");
            paramAnonymousBluetoothGattDescriptor.append(paramAnonymousInt);
            LogUtil.i(paramAnonymousBluetoothGatt, paramAnonymousBluetoothGattDescriptor.toString());
        }

        public void onServicesDiscovered(BluetoothGatt paramAnonymousBluetoothGatt, int paramAnonymousInt)
        {
            paramAnonymousBluetoothGatt = BLEService.TAG;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("onServicesDiscovered status = ");
            localStringBuilder.append(paramAnonymousInt);
            LogUtil.i(paramAnonymousBluetoothGatt, localStringBuilder.toString());
            BLEService.this.servicesDiscovered = true;
            if ((BLEService.this.connectState == true) && (BLEService.this.servicesDiscovered == true) && (BLEService.this.descriptorWrite == true)) {
                BLEService.this.sendBroadcast("com.lobot.robot.le.ACTION_GATT_CONNECTED");
            }
            if (paramAnonymousInt == 0) {
                BLEService.this.sendBroadcast("com.lobot.robot.le.ACTION_GATT_SERVICE_DISCOVERED");
            }
        }
    };
    boolean servicesDiscovered = false;

    private void sendBroadcast(String paramString)
    {
        sendBroadcast(paramString, null);
    }

    private void sendBroadcast(String paramString1, String paramString2)
    {
        paramString1 = new Intent(paramString1);
        if (!TextUtils.isEmpty(paramString2)) {
            paramString1.putExtra("com.lobot.robot.le.EXTRA_DATA", paramString2);
        }
        sendBroadcast(paramString1);
    }

    private void sendBroadcast(byte[] paramArrayOfByte, String paramString)
    {
        paramString = new Intent(paramString);
        if (paramArrayOfByte.length > 0) {
            paramString.putExtra("com.lobot.robot.le.EXTRA_DATA", paramArrayOfByte);
        }
        sendBroadcast(paramString);
    }

    public void close()
    {
        if (this.mBluetoothGatt == null) {
            return;
        }
        this.mBluetoothGatt.close();
        this.mBluetoothGatt = null;
    }

    public boolean connect(String paramString)
    {
        if ((this.mBluetoothAdapter != null) && (paramString != null))
        {
            if ((this.mBluetoothDeviceAddress != null) && (paramString.equals(this.mBluetoothDeviceAddress)) && (this.mBluetoothGatt != null))
            {
                LogUtil.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
                if (this.mBluetoothGatt.connect())
                {
                    this.mConnectState = 1;
                    return true;
                }
                return false;
            }
            BluetoothDevice localBluetoothDevice = this.mBluetoothAdapter.getRemoteDevice(paramString);
            if (localBluetoothDevice == null)
            {
                LogUtil.w(TAG, "Device not found.  Unable to connect.");
                return false;
            }
            this.mBluetoothGatt = localBluetoothDevice.connectGatt(this, false, this.mGattCallback);
            LogUtil.d(TAG, "Trying to create a new connection.");
            this.mBluetoothDeviceAddress = paramString;
            this.mConnectState = 1;
            return true;
        }
        LogUtil.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
        return false;
    }

    public void disconnect()
    {
        if ((this.mBluetoothAdapter != null) && (this.mBluetoothGatt != null))
        {
            this.mBluetoothGatt.disconnect();
            return;
        }
        LogUtil.w(TAG, "BluetoothAdapter not initialized");
    }

    public int getConnectState()
    {
        return this.mConnectState;
    }

    public List<BluetoothGattService> getSupportedGattServices()
    {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        return this.mBluetoothGatt.getServices();
    }

    public boolean init()
    {
        if (this.mBluetoothManager == null)
        {
            this.mBluetoothManager = ((BluetoothManager)getSystemService("bluetooth"));
            if (this.mBluetoothManager == null)
            {
                LogUtil.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null)
        {
            LogUtil.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public IBinder onBind(Intent paramIntent)
    {
        return this.mBinder;
    }

    public void onCreate() {}

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
    {
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }

    public void readCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
    {
        if ((this.mBluetoothAdapter != null) && (this.mBluetoothGatt != null))
        {
            this.mBluetoothGatt.readCharacteristic(paramBluetoothGattCharacteristic);
            return;
        }
        LogUtil.w(TAG, "BluetoothAdapter not initialized");
    }

    public boolean reconnect()
    {
        if ((this.mBluetoothDeviceAddress != null) && (this.mBluetoothGatt != null))
        {
            LogUtil.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (this.mBluetoothGatt.connect())
            {
                this.mConnectState = 1;
                return true;
            }
        }
        return false;
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic, boolean paramBoolean, NotificationListener paramNotificationListener)
    {
        if ((this.mBluetoothAdapter != null) && (this.mBluetoothGatt != null))
        {
            this.mBluetoothGatt.setCharacteristicNotification(paramBluetoothGattCharacteristic, paramBoolean);
            if (paramNotificationListener != null) {
                paramNotificationListener.onNotification(paramBluetoothGattCharacteristic);
            } else {
                LogUtil.i(TAG, "listener null");
            }
            return;
        }
        LogUtil.w(TAG, "BluetoothAdapter not initialized");
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
    {
        if ((this.mBluetoothAdapter != null) && (this.mBluetoothGatt != null))
        {
            if (paramBluetoothGattCharacteristic != null)
            {
                if (this.mBluetoothGatt.writeCharacteristic(paramBluetoothGattCharacteristic))
                {
                    LogUtil.i(TAG, "write success");
                    return true;
                }
                LogUtil.i(TAG, "write fail");
                return false;
            }
            LogUtil.w(TAG, "characteristic is null");
            return false;
        }
        LogUtil.w(TAG, "BluetoothAdapter not initialized");
        return false;
    }

    public void writeDescriptor(BluetoothGattDescriptor paramBluetoothGattDescriptor)
    {
        if ((this.mBluetoothAdapter != null) && (this.mBluetoothGatt != null))
        {
            this.mBluetoothGatt.writeDescriptor(paramBluetoothGattDescriptor);
            return;
        }
        LogUtil.w(TAG, "BluetoothAdapter not initialized");
    }

    public class BLEBinder
            extends Binder
    {
        public BLEBinder() {}

        public BLEService getService()
        {
            return BLEService.this;
        }
    }

    public static abstract interface NotificationListener
    {
        public abstract void onNotification(BluetoothGattCharacteristic paramBluetoothGattCharacteristic);
    }
}
