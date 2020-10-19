package com.lobot.lobotcontrol.connect;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import com.lobot.lobotcontrol.model.ByteCommand;
import com.lobot.lobotcontrol.uitls.LogUtil;
import com.lobot.lobotcontrol.uitls.TimerLog;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BLEManager
{
    public static final String CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    private static final int DATA_MAX_LEN = 20;
    public static final String HC_08_SEND_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static final String HC_08_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    private static final String TAG = "BLEManager";
    private static BLEManager instance;
    private static boolean isRegistered = false;
    private BLEService mBleService;
    private Handler mHandler;
    private HandlerThread mHandlerThread = new HandlerThread("sendThread");
    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
            String str1 = paramAnonymousIntent.getAction();
            String str2 = BLEManager.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("receive action = ");
            stringBuilder.append(str1);
            LogUtil.i(str2, paramAnonymousContext.toString());
            if ("com.lobot.robot.le.ACTION_GATT_CONNECTED".equals(str1))
            {
                LogUtil.i(BLEManager.TAG, "connected !");
                if (BLEManager.this.mHandler != null) {
                    BLEManager.this.mHandler.obtainMessage(3).sendToTarget();
                }
            }
            else if ("com.lobot.robot.le.ACTION_GATT_CONNECT_FAIL".equals(str1))
            {
                LogUtil.w(BLEManager.TAG, "connect failed!");
                if (BLEManager.this.mHandler != null) {
                    BLEManager.this.mHandler.obtainMessage(6).sendToTarget();
                }
            }
            else if ("com.lobot.robot.le.ACTION_GATT_DISCONNECTED".equals(str1))
            {
                LogUtil.w(BLEManager.TAG, "connection break!");
                if (BLEManager.this.mHandler != null) {
                    BLEManager.this.mHandler.obtainMessage(6).sendToTarget();
                }
            }
            else
            {
                if ("com.lobot.robot.le.ACTION_GATT_SERVICE_DISCOVERED".equals(str1))
                {
                    if (BLEManager.this.mBleService == null)
                    {
                        LogUtil.i(BLEManager.TAG, "service null");
                        return;
                    }

               List<BluetoothGattService> list = BLEManager.this.mBleService.getSupportedGattServices().iterator();
                    while (BLEManager.this.mBleService.getSupportedGattServices().iterator().hasNext())
                    {
                        paramAnonymousIntent = (BluetoothGattService)paramAnonymousContext.next();
                        if ("0000ffe0-0000-1000-8000-00805f9b34fb".equals(paramAnonymousIntent.getUuid().toString()))
                        {
                            BLEManager.(BLEManager.this, paramAnonymousIntent.getCharacteristic(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")));
                            if (BLEManager.this.sendCharacteristic == null) {
                                LogUtil.i(BLEManager.TAG, "get send Characteristic null");
                            } else {
                                LogUtil.i(BLEManager.TAG, "get send Characteristic");
                            }
                            BLEManager.this.mBleService.setCharacteristicNotification(BLEManager.this.sendCharacteristic, true, new BLEService.NotificationListener()
                            {
                                public void onNotification(BluetoothGattCharacteristic paramAnonymous2BluetoothGattCharacteristic)
                                {
                                    paramAnonymous2BluetoothGattCharacteristic = paramAnonymous2BluetoothGattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                    paramAnonymous2BluetoothGattCharacteristic.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    BLEManager.this.mBleService.writeDescriptor(paramAnonymous2BluetoothGattCharacteristic);
                                    LogUtil.i(BLEManager.TAG, "config notification");
                                }
                            });
                        }
                    }
                }
                if ("com.lobot.robot.le.ACTION_DATA_AVAILABLE".equals(str1))
                {
                    paramAnonymousContext = paramAnonymousIntent.getByteArrayExtra("com.lobot.robot.le.EXTRA_DATA");
                    if (paramAnonymousContext.length >= 6)
                    {
                        int i = paramAnonymousContext[5];
                        long l1 = paramAnonymousContext[4] & 0xFF;
                        long l2 = (i & 0xFF) * 256 & 0xFFFF;
                        if (paramAnonymousContext[3] == 15) {
                            com.lobot.lobotcontrol.NormalModeActivity.m_Bat = l2 + l1;
                        }
                    }
                }
            }
        }
    };
    private BluetoothGattCharacteristic sendCharacteristic;
    private Handler sendHandler;
    private int writefailCnt = 0;

    private BLEManager()
    {
        this.mHandlerThread.start();
        this.sendHandler = new Handler(this.mHandlerThread.getLooper(), new Callback()
        {
            public boolean handleMessage(Message paramAnonymousMessage)
            {
                try
                {
                    if (BLEManager.this.mHandler != null)
                    {
                        BLEManager.this.mHandler.obtainMessage(13, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2).sendToTarget();
                        int i = paramAnonymousMessage.what;
                        if (i != 5)
                        {
                            if (i != 19)
                            {
                                switch (i)
                                {
                                    default:
                                        break;
                                    case 9:
                                    case 10:
                                        Iterator localIterator = ((List)paramAnonymousMessage.obj).iterator();
                                        while (localIterator.hasNext())
                                        {
                                            paramAnonymousMessage = (ByteCommand)localIterator.next();
                                            if (!BLEManager.this.write(paramAnonymousMessage.getCommandByteBuffer(), paramAnonymousMessage.getDelay()))
                                            {
                                                BLEManager.access$408(BLEManager.this);
                                                if (BLEManager.this.writefailCnt > 3)
                                                {
                                                    BLEManager.this.stop();
                                                    if (BLEManager.this.mBleService != null) {
                                                        BLEManager.this.mBleService.reconnect();
                                                    }
                                                    BLEManager.access$402(BLEManager.this, 0);
                                                    com.lobot.lobotcontrol.NormalModeActivity.noShowConnect = true;
                                                    BLEManager.this.mHandler.obtainMessage(21).sendToTarget();
                                                }
                                            }
                                            else
                                            {
                                                BLEManager.access$402(BLEManager.this, 0);
                                            }
                                        }
                                    case 8:
                                        long l = paramAnonymousMessage.getData().getLong("delayMills", 0L);
                                        BLEManager.this.write((String)paramAnonymousMessage.obj, l);
                                        break;
                                    case 7:
                                        BLEManager.this.write((byte[])paramAnonymousMessage.obj, 0L);
                                        break;
                                }
                            }
                            else
                            {
                                paramAnonymousMessage = (ByteCommand)paramAnonymousMessage.obj;
                                BLEManager.this.write(paramAnonymousMessage.getCommandByteBuffer(), 100L);
                            }
                        }
                        else if (BLEManager.this.mBleService != null)
                        {
                            BLEManager.access$402(BLEManager.this, 0);
                            BLEManager.this.mBleService.reconnect();
                        }
                    }
                    return true;
                }
                finally {}
            }
        });
    }

    public static BLEManager getInstance()
    {
        if (instance == null) {
            instance = new BLEManager();
        }
        return instance;
    }

    private byte[][] separateData(byte[] paramArrayOfByte)
    {
        int i = (int)Math.ceil(paramArrayOfByte.length / 20.0F);
        byte[][] arrayOfByte = new byte[i][];
        int j = paramArrayOfByte.length;
        int k = 0;
        int m = k;
        while (k < i)
        {
            int n = m + 20;
            byte[] arrayOfByte1;
            if (n <= j)
            {
                arrayOfByte1 = new byte[20];
                System.arraycopy(paramArrayOfByte, m, arrayOfByte1, 0, 20);
                m = n;
            }
            else
            {
                n = j - m;
                arrayOfByte1 = new byte[n];
                System.arraycopy(paramArrayOfByte, m, arrayOfByte1, 0, n);
                m = j;
            }
            arrayOfByte[k] = arrayOfByte1;
            k++;
        }
        return arrayOfByte;
    }

    public BluetoothGattCharacteristic buildWriteCharacteristic(byte[] paramArrayOfByte)
    {
        BluetoothGattCharacteristic localBluetoothGattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"), 8, 16);
        localBluetoothGattCharacteristic.setValue(paramArrayOfByte);
        return localBluetoothGattCharacteristic;
    }

    public boolean connect(BluetoothDevice paramBluetoothDevice)
    {
        if ((this.mBleService != null) && (paramBluetoothDevice != null))
        {
            this.writefailCnt = 0;
            this.mBleService.connect(paramBluetoothDevice.getAddress());
            return true;
        }
        return false;
    }

    public void destroy()
    {
        try
        {
            if (this.mHandlerThread != null)
            {
                this.mHandlerThread.quit();
                this.mHandlerThread = null;
            }
            if (this.mBleService != null) {
                this.mBleService.close();
            }
            this.writefailCnt = 0;
            this.mReceiver = null;
            this.sendHandler = null;
            this.mHandler = null;
            instance = null;
            return;
        }
        finally {}
    }

    public void init(BLEService paramBLEService)
    {
        this.mBleService = paramBLEService;
        this.mBleService.init();
    }

    public boolean isConnected()
    {
        boolean bool;
        if ((this.mBleService != null) && (this.mBleService.getConnectState() == 2)) {
            bool = true;
        } else {
            bool = false;
        }
        return bool;
    }

    public void register(Context paramContext)
    {
        if (isRegistered) {
            return;
        }
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("com.lobot.robot.le.ACTION_GATT_CONNECTED");
        localIntentFilter.addAction("com.lobot.robot.le.ACTION_GATT_DISCONNECTED");
        localIntentFilter.addAction("com.lobot.robot.le.ACTION_GATT_SERVICE_DISCOVERED");
        localIntentFilter.addAction("com.lobot.robot.le.ACTION_DATA_AVAILABLE");
        paramContext.registerReceiver(this.mReceiver, localIntentFilter);
        isRegistered = true;
    }

    public void removeAll()
    {
        try
        {
            this.sendHandler.removeMessages(9);
            return;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public void send(ByteCommand paramByteCommand)
    {
        try
        {
            if (!isConnected())
            {
                if (this.mHandler != null) {
                    this.mHandler.obtainMessage(11).sendToTarget();
                }
                return;
            }
            this.sendHandler.obtainMessage(19, paramByteCommand).sendToTarget();
            return;
        }
        finally {}
    }

    public void send(String paramString)
    {
        try
        {
            if (!isConnected())
            {
                if (this.mHandler != null) {
                    this.mHandler.obtainMessage(11).sendToTarget();
                }
                return;
            }
            this.sendHandler.obtainMessage(8, paramString).sendToTarget();
            return;
        }
        finally {}
    }

    public void send(String paramString, long paramLong)
    {
        try
        {
            if (!isConnected())
            {
                if (this.mHandler != null) {
                    this.mHandler.obtainMessage(11).sendToTarget();
                }
                return;
            }
            paramString = this.sendHandler.obtainMessage(8, paramString);
            Bundle localBundle = new android/os/Bundle;
            localBundle.<init>();
            localBundle.putLong("delayMills", paramLong);
            paramString.setData(localBundle);
            this.sendHandler.sendMessage(paramString);
            return;
        }
        finally {}
    }

    public void send(List<ByteCommand> paramList)
    {
        try
        {
            if (!isConnected())
            {
                if (this.mHandler != null) {
                    this.mHandler.obtainMessage(11).sendToTarget();
                }
                return;
            }
            this.sendHandler.obtainMessage(9, paramList).sendToTarget();
            return;
        }
        finally {}
    }

    public void send(List<ByteCommand> paramList, int paramInt)
    {
        try
        {
            if (!isConnected())
            {
                if (this.mHandler != null) {
                    this.mHandler.obtainMessage(11).sendToTarget();
                }
                return;
            }
            this.sendHandler.obtainMessage(9, paramInt, -1, paramList).sendToTarget();
            return;
        }
        finally {}
    }

    public void sendNoRemove(List<ByteCommand> paramList)
    {
        try
        {
            if (!isConnected())
            {
                if (this.mHandler != null) {
                    this.mHandler.obtainMessage(11).sendToTarget();
                }
                return;
            }
            this.sendHandler.obtainMessage(10, paramList).sendToTarget();
            return;
        }
        finally {}
    }

    public void setHandler(Handler paramHandler)
    {
        this.mHandler = paramHandler;
    }

    public void stop()
    {
        try
        {
            LogUtil.d(TAG, "stop");
            this.writefailCnt = 0;
            if (this.mBleService != null) {
                this.mBleService.disconnect();
            }
            return;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public void unregister(Context paramContext)
    {
        if (paramContext != null)
        {
            paramContext.unregisterReceiver(this.mReceiver);
            isRegistered = false;
        }
    }

    public boolean write(String paramString, long paramLong)
    {
        return write(paramString.getBytes(), paramLong);
    }

    public boolean write(ByteBuffer paramByteBuffer, long paramLong)
    {
        if ((this.sendCharacteristic != null) && (paramByteBuffer.capacity() != 0))
        {
            byte[] arrayOfByte = new byte[paramByteBuffer.capacity()];
            paramByteBuffer.get(arrayOfByte, 0, paramByteBuffer.capacity());
            this.sendCharacteristic.setValue(arrayOfByte);
            boolean bool = this.mBleService.writeCharacteristic(this.sendCharacteristic);
            if (paramLong > 0L) {
                try
                {
                    Thread.sleep(paramLong);
                }
                catch (InterruptedException paramByteBuffer)
                {
                    paramByteBuffer.printStackTrace();
                }
            }
            return bool;
        }
        return false;
    }

    public boolean write(byte[] paramArrayOfByte, long paramLong)
    {
        Object localObject = this.sendCharacteristic;
        int i = 0;
        StringBuilder localStringBuilder;
        if (localObject == null)
        {
            localObject = TAG;
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("sendCharacteristic is null, send data[");
            localStringBuilder.append(new String(paramArrayOfByte));
            localStringBuilder.append("] fail");
            LogUtil.w((String)localObject, localStringBuilder.toString());
            return false;
        }
        localObject = separateData(paramArrayOfByte);
        int j = localObject.length;
        while (i < j)
        {
            localStringBuilder = localObject[i];
            this.sendCharacteristic.setValue(localStringBuilder);
            this.mBleService.writeCharacteristic(this.sendCharacteristic);
            i++;
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("send ");
        ((StringBuilder)localObject).append(new String(paramArrayOfByte));
        TimerLog.logTime(((StringBuilder)localObject).toString());
        if (paramLong > 0L) {
            try
            {
                Thread.sleep(paramLong);
            }
            catch (InterruptedException paramArrayOfByte)
            {
                paramArrayOfByte.printStackTrace();
            }
        }
        return true;
    }
}
