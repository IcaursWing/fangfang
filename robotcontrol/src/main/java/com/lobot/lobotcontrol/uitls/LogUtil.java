package com.lobot.lobotcontrol.uitls;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogUtil
{
    private static boolean isDebug = true;

    public static int d(String paramString1, String paramString2)
    {
        if (isDebug) {
            Log.d(paramString1, paramString2);
        }
        return 0;
    }

    public static int d(String paramString1, String paramString2, Throwable paramThrowable)
    {
        if (isDebug) {
            return Log.d(paramString1, paramString2, paramThrowable);
        }
        return 0;
    }

    public static int e(String paramString1, String paramString2)
    {
        if (isDebug) {
            Log.e(paramString1, paramString2);
        }
        return 0;
    }

    public static int e(String paramString1, String paramString2, Throwable paramThrowable)
    {
        if (isDebug) {
            return Log.e(paramString1, paramString2, paramThrowable);
        }
        return 0;
    }

    public static int i(String paramString1, String paramString2)
    {
        if (isDebug) {
            return Log.i(paramString1, paramString2);
        }
        return 0;
    }

    public static int i(String paramString1, String paramString2, Throwable paramThrowable)
    {
        if (isDebug) {
            return Log.i(paramString1, paramString2, paramThrowable);
        }
        return 0;
    }

    public static void showToast(Context paramContext, String paramString)
    {
        if (isDebug) {
            Toast.makeText(paramContext, paramString, 0).show();
        }
    }

    public static int v(String paramString1, String paramString2)
    {
        if (isDebug) {
            Log.v(paramString1, paramString2);
        }
        return 0;
    }

    public static int v(String paramString1, String paramString2, Throwable paramThrowable)
    {
        if (isDebug) {
            return Log.v(paramString1, paramString2, paramThrowable);
        }
        return 0;
    }

    public static int w(String paramString1, String paramString2)
    {
        if (isDebug) {
            Log.w(paramString1, paramString2);
        }
        return 0;
    }

    public static int w(String paramString1, String paramString2, Throwable paramThrowable)
    {
        if (isDebug) {
            return Log.w(paramString1, paramString2, paramThrowable);
        }
        return 0;
    }
}
