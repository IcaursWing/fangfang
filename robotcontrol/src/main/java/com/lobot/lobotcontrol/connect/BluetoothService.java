package com.lobot.lobotcontrol.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import com.lobot.lobotcontrol.model.Command;
import com.lobot.lobotcontrol.uitls.Configs;
import com.lobot.lobotcontrol.uitls.LogUtil;
import com.lobot.lobotcontrol.uitls.TimerLog;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class BluetoothService
{
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;
    private static final String TAG = "BluetoothService";
    private static BluetoothService instance;
    private BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private Handler mHandler;
    private HandlerThread mHandlerThread = new HandlerThread("sendThread");
    private int mState = 0;
    private Handler sendHandler;

    private BluetoothService()
    {
        this.mHandlerThread.start();
        this.sendHandler = new Handler(this.mHandlerThread.getLooper(), new Callback()
        {
            public boolean handleMessage(Message paramAnonymousMessage)
            {
                try
                {
                    if (BluetoothService.this.mHandler != null) {
                        BluetoothService.this.mHandler.obtainMessage(13, paramAnonymousMessage.arg1, paramAnonymousMessage.arg2).sendToTarget();
                    }
                    Iterator localIterator;
                    switch (paramAnonymousMessage.what)
                    {
                        default:
                            break;
                        case 9:
                        case 10:
                            if (BluetoothService.this.mConnectedThread != null) {
                                localIterator = ((List)paramAnonymousMessage.obj).iterator();
                            }
                            break;
                        case 8:
                        case 7:
                            while (localIterator.hasNext())
                            {
                                paramAnonymousMessage = (Command)localIterator.next();
                                BluetoothService.this.mConnectedThread.write(paramAnonymousMessage.getCommandStr(), paramAnonymousMessage.getDelay());
                                continue;
                                if (BluetoothService.this.mConnectedThread != null)
                                {
                                    long l = paramAnonymousMessage.getData().getLong("delayMills", 0L);
                                    BluetoothService.this.mConnectedThread.write((String)paramAnonymousMessage.obj, l);
                                    break;
                                    if (BluetoothService.this.mConnectedThread != null) {
                                        BluetoothService.this.mConnectedThread.write((byte[])paramAnonymousMessage.obj);
                                    }
                                }
                            }
                    }
                    return true;
                }
                finally {}
            }
        });
    }

    private void connectionFailed()
    {
        if (this.mHandler != null) {
            this.mHandler.obtainMessage(4).sendToTarget();
        }
        start();
    }

    private void connectionLost()
    {
        if (this.mHandler != null) {
            this.mHandler.obtainMessage(6).sendToTarget();
        }
        start();
    }

    public static BluetoothService getInstance()
    {
        if (instance == null) {
            instance = new BluetoothService();
        }
        return instance;
    }

    private void setState(int paramInt)
    {
        try
        {
            String str = TAG;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("setState() ");
            localStringBuilder.append(this.mState);
            localStringBuilder.append(" -> ");
            localStringBuilder.append(paramInt);
            LogUtil.d(str, localStringBuilder.toString());
            this.mState = paramInt;
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(2, paramInt, -1).sendToTarget();
            }
            return;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public void connect(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
    {
        try
        {
            String str = TAG;
            Object localObject = new java/lang/StringBuilder;
            ((StringBuilder)localObject).<init>();
            ((StringBuilder)localObject).append("connect to: ");
            ((StringBuilder)localObject).append(paramBluetoothDevice);
            LogUtil.d(str, ((StringBuilder)localObject).toString());
            if ((this.mState == 2) && (this.mConnectThread != null))
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            localObject = new com/lobot/lobotcontrol/connect/BluetoothService$ConnectThread;
            ((ConnectThread)localObject).<init>(this, paramBluetoothDevice, paramBoolean);
            this.mConnectThread = ((ConnectThread)localObject);
            this.mConnectThread.start();
            setState(2);
            return;
        }
        finally {}
    }

    public void connected(BluetoothSocket paramBluetoothSocket, BluetoothDevice paramBluetoothDevice, String paramString)
    {
        try
        {
            paramBluetoothDevice = TAG;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localStringBuilder.append("connected, Socket Type:");
            localStringBuilder.append(paramString);
            LogUtil.d(paramBluetoothDevice, localStringBuilder.toString());
            if (this.mConnectThread != null)
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            paramBluetoothDevice = new com/lobot/lobotcontrol/connect/BluetoothService$ConnectedThread;
            paramBluetoothDevice.<init>(this, paramBluetoothSocket, paramString);
            this.mConnectedThread = paramBluetoothDevice;
            this.mConnectedThread.start();
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(3).sendToTarget();
            }
            setState(3);
            return;
        }
        finally {}
    }

    public void destroy()
    {
        try
        {
            stop();
            if (this.mHandlerThread != null)
            {
                this.mHandlerThread.quit();
                this.mHandlerThread = null;
            }
            this.sendHandler = null;
            this.mHandler = null;
            instance = null;
            return;
        }
        finally {}
    }

    public Handler getHandler()
    {
        return this.mHandler;
    }

    public int getState()
    {
        try
        {
            int i = this.mState;
            return i;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
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

    public void send(String paramString)
    {
        try
        {
            if (this.mState != 3)
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
            if (this.mState != 3)
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

    public void send(List<Command> paramList)
    {
        try
        {
            if (this.mState != 3)
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

    public void send(List<Command> paramList, int paramInt)
    {
        try
        {
            if (this.mState != 3)
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

    public void sendNoRemove(List<Command> paramList)
    {
        try
        {
            if (this.mState != 3)
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

    public void start()
    {
        try
        {
            LogUtil.d(TAG, "start");
            if (this.mConnectThread != null)
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            setState(1);
            return;
        }
        finally {}
    }

    public void stop()
    {
        try
        {
            LogUtil.d(TAG, "stop");
            if ((this.mAdapter != null) && (this.mAdapter.isDiscovering())) {
                this.mAdapter.cancelDiscovery();
            }
            if (this.mConnectThread != null)
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            setState(0);
            return;
        }
        finally {}
    }

    class ConnectThread
            extends Thread
    {
        private String mSocketType;
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
        {
            this.mmDevice = paramBluetoothDevice;
            if (paramBoolean) {
                this$1 = "Secure";
            } else {
                this$1 = "Insecure";
            }
            this.mSocketType = BluetoothService.this;
            if (paramBoolean)
            {
                try
                {
                    this$1 = paramBluetoothDevice.createRfcommSocketToServiceRecord(Configs.LOROT_UUID);
                }
                catch (IOException paramBluetoothDevice)
                {
                    break label62;
                }
            }
            else
            {
                this$1 = paramBluetoothDevice.createInsecureRfcommSocketToServiceRecord(Configs.LOROT_UUID);
                break label111;
            }
            label62:
            String str = BluetoothService.TAG;
            this$1 = new StringBuilder();
            BluetoothService.this.append("Socket Type: ");
            BluetoothService.this.append(this.mSocketType);
            BluetoothService.this.append("create() failed");
            LogUtil.e(str, BluetoothService.this.toString(), paramBluetoothDevice);
            this$1 = null;
            label111:
            this.mmSocket = BluetoothService.this;
        }

        public void cancel()
        {
            try
            {
                this.mmSocket.close();
            }
            catch (IOException localIOException)
            {
                String str = BluetoothService.TAG;
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append("close() of connect ");
                localStringBuilder.append(this.mSocketType);
                localStringBuilder.append(" socket failed");
                LogUtil.e(str, localStringBuilder.toString(), localIOException);
            }
        }

        public void run()
        {
      ??? = BluetoothService.TAG;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("BEGIN mConnectThread SocketType:");
            localStringBuilder.append(this.mSocketType);
            LogUtil.i((String)???, localStringBuilder.toString());
      ??? = new StringBuilder();
            ((StringBuilder)???).append("ConnectThread");
            ((StringBuilder)???).append(this.mSocketType);
            setName(((StringBuilder)???).toString());
            BluetoothService.this.mAdapter.cancelDiscovery();
            try
            {
                this.mmSocket.connect();
                synchronized (BluetoothService.this)
                {
                    BluetoothService.access$502(BluetoothService.this, null);
                    BluetoothService.this.connected(this.mmSocket, this.mmDevice, this.mSocketType);
                    return;
                }
                Object localObject3;
                return;
            }
            catch (IOException localIOException1)
            {
        ??? = BluetoothService.TAG;
                localObject3 = new StringBuilder();
                ((StringBuilder)localObject3).append("connect failure --- ");
                ((StringBuilder)localObject3).append(localIOException1);
                LogUtil.e((String)???, ((StringBuilder)localObject3).toString());
                try
                {
                    this.mmSocket.close();
                }
                catch (IOException localIOException2)
                {
                    localObject3 = BluetoothService.TAG;
          ??? = new StringBuilder();
                    ((StringBuilder)???).append("unable to close() ");
                    ((StringBuilder)???).append(this.mSocketType);
                    ((StringBuilder)???).append(" socket during connection failure");
                    LogUtil.e((String)localObject3, ((StringBuilder)???).toString(), localIOException2);
                }
                BluetoothService.this.connectionFailed();
            }
        }
    }

    class ConnectedThread
            extends Thread
    {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket paramBluetoothSocket, String paramString)
        {
            String str = BluetoothService.TAG;
            this$1 = new StringBuilder();
            BluetoothService.this.append("create ConnectedThread: ");
            BluetoothService.this.append(paramString);
            LogUtil.d(str, BluetoothService.this.toString());
            this.mmSocket = paramBluetoothSocket;
            paramString = null;
            try
            {
                this$1 = paramBluetoothSocket.getInputStream();
                try
                {
                    paramBluetoothSocket = paramBluetoothSocket.getOutputStream();
                }
                catch (IOException paramBluetoothSocket) {}
                LogUtil.e(BluetoothService.TAG, "temp sockets not created", paramBluetoothSocket);
            }
            catch (IOException paramBluetoothSocket)
            {
                this$1 = null;
            }
            paramBluetoothSocket = paramString;
            this.mmInStream = BluetoothService.this;
            this.mmOutStream = paramBluetoothSocket;
        }

        public void cancel()
        {
            try
            {
                if (this.mmSocket != null)
                {
                    LogUtil.i(BluetoothService.TAG, "mmSocket.close()");
                    this.mmSocket.close();
                    this.mmSocket = null;
                }
            }
            catch (IOException localIOException)
            {
                LogUtil.e(BluetoothService.TAG, "close() of connect socket failed", localIOException);
            }
        }

        public void run()
        {
            LogUtil.i(BluetoothService.TAG, "BEGIN mConnectedThread");
            byte[] arrayOfByte = new byte['?'];
            try
            {
                for (;;)
                {
                    int i = this.mmInStream.read(arrayOfByte);
                    if (BluetoothService.this.mHandler != null) {
                        BluetoothService.this.mHandler.obtainMessage(15, i, -1, arrayOfByte).sendToTarget();
                    }
                }
                return;
            }
            catch (IOException localIOException)
            {
                LogUtil.e(BluetoothService.TAG, "disconnected", localIOException);
                BluetoothService.this.connectionLost();
                LogUtil.i(BluetoothService.TAG, "connected thread end");
            }
        }

        public void write(String paramString)
        {
            try
            {
                this.mmOutStream.write(paramString.getBytes());
                StringBuilder localStringBuilder = new java/lang/StringBuilder;
                localStringBuilder.<init>();
                localStringBuilder.append("send ");
                localStringBuilder.append(paramString);
                TimerLog.logTime(localStringBuilder.toString());
                if (BluetoothService.this.mHandler != null) {
                    BluetoothService.this.mHandler.obtainMessage(12, paramString).sendToTarget();
                }
            }
            catch (IOException localIOException)
            {
                LogUtil.e(BluetoothService.TAG, "Exception during send", localIOException);
                if (BluetoothService.this.mHandler != null) {
                    BluetoothService.this.mHandler.obtainMessage(14, -1, -1, paramString).sendToTarget();
                }
            }
        }

        public void write(String paramString, long paramLong)
        {
            try
            {
                this.mmOutStream.write(paramString.getBytes());
                StringBuilder localStringBuilder = new java/lang/StringBuilder;
                localStringBuilder.<init>();
                localStringBuilder.append("send ");
                localStringBuilder.append(paramString);
                TimerLog.logTime(localStringBuilder.toString());
                try
                {
                    Thread.sleep(paramLong);
                }
                catch (InterruptedException localInterruptedException)
                {
                    localInterruptedException.printStackTrace();
                }
                if (BluetoothService.this.mHandler != null) {
                    BluetoothService.this.mHandler.obtainMessage(12, paramString).sendToTarget();
                }
            }
            catch (IOException localIOException)
            {
                LogUtil.e(BluetoothService.TAG, "Exception during send", localIOException);
                if (BluetoothService.this.mHandler != null) {
                    BluetoothService.this.mHandler.obtainMessage(14, -1, -1, paramString).sendToTarget();
                }
            }
        }

        public void write(byte[] paramArrayOfByte)
        {
            try
            {
                this.mmOutStream.write(paramArrayOfByte);
                StringBuilder localStringBuilder = new java/lang/StringBuilder;
                localStringBuilder.<init>();
                localStringBuilder.append("send ");
                localStringBuilder.append(paramArrayOfByte);
                TimerLog.logTime(localStringBuilder.toString());
                if (BluetoothService.this.mHandler != null) {
                    BluetoothService.this.mHandler.obtainMessage(12, paramArrayOfByte).sendToTarget();
                }
            }
            catch (IOException localIOException)
            {
                LogUtil.e(BluetoothService.TAG, "Exception during send", localIOException);
                if (BluetoothService.this.mHandler != null) {
                    BluetoothService.this.mHandler.obtainMessage(14, -1, -1, paramArrayOfByte).sendToTarget();
                }
            }
        }
    }
}
