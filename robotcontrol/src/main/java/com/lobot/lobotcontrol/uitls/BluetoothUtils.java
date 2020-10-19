package com.lobot.lobotcontrol.uitls;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothUtils
{
    private static final String TAG = "BluetoothUtils";

    public static boolean cancelPairingUserInput(BluetoothDevice paramBluetoothDevice)
    {
        try
        {
            boolean bool = ((Boolean)paramBluetoothDevice.getClass().getMethod("cancelPairingUserInput", new Class[0]).invoke(paramBluetoothDevice, new Object[0])).booleanValue();
            return bool;
        }
        catch (Exception paramBluetoothDevice)
        {
            paramBluetoothDevice.printStackTrace();
        }
        return false;
    }

    public static boolean createBound(BluetoothDevice paramBluetoothDevice)
    {
        try
        {
            boolean bool = ((Boolean)paramBluetoothDevice.getClass().getMethod("createBond", new Class[0]).invoke(paramBluetoothDevice, new Object[0])).booleanValue();
            return bool;
        }
        catch (Exception paramBluetoothDevice)
        {
            paramBluetoothDevice.printStackTrace();
        }
        return false;
    }

    public static boolean isSupport(BluetoothAdapter paramBluetoothAdapter)
    {
        if (paramBluetoothAdapter == null)
        {
            LogUtil.w(TAG, "device do not support bluetooth!");
            return false;
        }
        return true;
    }

    public static boolean removePaired(BluetoothDevice paramBluetoothDevice)
    {
        try
        {
            boolean bool = ((Boolean)paramBluetoothDevice.getClass().getMethod("removeBond", new Class[0]).invoke(paramBluetoothDevice, new Object[0])).booleanValue();
            return bool;
        }
        catch (IllegalAccessException paramBluetoothDevice)
        {
            paramBluetoothDevice.printStackTrace();
            return false;
        }
        catch (InvocationTargetException paramBluetoothDevice)
        {
            paramBluetoothDevice.printStackTrace();
            return false;
        }
        catch (NoSuchMethodException paramBluetoothDevice)
        {
            paramBluetoothDevice.printStackTrace();
        }
        return false;
    }

    public static boolean setPin(BluetoothDevice paramBluetoothDevice, String paramString)
    {
        try
        {
            boolean bool = ((Boolean)paramBluetoothDevice.getClass().getMethod("setPin", new Class[] { byte[].class }).invoke(paramBluetoothDevice, new Object[] { paramString.getBytes() })).booleanValue();
            return bool;
        }
        catch (Exception paramBluetoothDevice)
        {
            paramBluetoothDevice.printStackTrace();
        }
        return false;
    }
}
