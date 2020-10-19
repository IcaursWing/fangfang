package com.lobot.lobotcontrol.uitls;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.util.TypedValue;

public class ColorUtil
{
    public static int getAccentColor(Context paramContext)
    {
        TypedValue localTypedValue = new TypedValue();
        int i;
        try
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                paramContext.getTheme().resolveAttribute(16843829, localTypedValue, true);
                i = localTypedValue.data;
            }
            else
            {
                RuntimeException localRuntimeException = new java/lang/RuntimeException;
                localRuntimeException.<init>("SDK_INT less than LOLLIPOP");
                throw localRuntimeException;
            }
        }
        catch (Exception localException)
        {
            try
            {
                i = paramContext.getResources().getIdentifier("colorAccent", "attr", paramContext.getPackageName());
                if (i != 0)
                {
                    paramContext.getTheme().resolveAttribute(i, localTypedValue, true);
                    i = localTypedValue.data;
                }
                else
                {
                    paramContext = new java/lang/RuntimeException;
                    paramContext.<init>("colorAccent not found");
                    throw paramContext;
                }
            }
            catch (Exception paramContext)
            {
                i = -16776961;
            }
        }
        return i;
    }

    public static int getBaseColor(int paramInt)
    {
        if (isLight(paramInt)) {
            return -16777216;
        }
        return -1;
    }

    public static int getPrimaryColor(Context paramContext)
    {
        TypedValue localTypedValue = new TypedValue();
        int i;
        try
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                paramContext.getTheme().resolveAttribute(16843827, localTypedValue, true);
                i = localTypedValue.data;
            }
            else
            {
                RuntimeException localRuntimeException = new java/lang/RuntimeException;
                localRuntimeException.<init>("SDK_INT less than LOLLIPOP");
                throw localRuntimeException;
            }
        }
        catch (Exception localException)
        {
            try
            {
                i = paramContext.getResources().getIdentifier("colorPrimary", "attr", paramContext.getPackageName());
                if (i != 0)
                {
                    paramContext.getTheme().resolveAttribute(i, localTypedValue, true);
                    i = localTypedValue.data;
                }
                else
                {
                    paramContext = new java/lang/RuntimeException;
                    paramContext.<init>("colorPrimary not found");
                    throw paramContext;
                }
            }
            catch (Exception paramContext)
            {
                i = -16776961;
            }
        }
        return i;
    }

    public static int getPrimaryDarkColor(Context paramContext)
    {
        TypedValue localTypedValue = new TypedValue();
        int i;
        try
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                paramContext.getTheme().resolveAttribute(16843828, localTypedValue, true);
                i = localTypedValue.data;
            }
            else
            {
                RuntimeException localRuntimeException = new java/lang/RuntimeException;
                localRuntimeException.<init>("SDK_INT less than LOLLIPOP");
                throw localRuntimeException;
            }
        }
        catch (Exception localException)
        {
            try
            {
                i = paramContext.getResources().getIdentifier("colorPrimaryDark", "attr", paramContext.getPackageName());
                if (i != 0)
                {
                    paramContext.getTheme().resolveAttribute(i, localTypedValue, true);
                    i = localTypedValue.data;
                }
                else
                {
                    paramContext = new java/lang/RuntimeException;
                    paramContext.<init>("colorPrimaryDark not found");
                    throw paramContext;
                }
            }
            catch (Exception paramContext)
            {
                i = -16776961;
            }
        }
        return i;
    }

    public static boolean isLight(int paramInt)
    {
        boolean bool;
        if (Math.sqrt(Color.red(paramInt) * Color.red(paramInt) * 0.241D + Color.green(paramInt) * Color.green(paramInt) * 0.691D + Color.blue(paramInt) * Color.blue(paramInt) * 0.068D) > 130.0D) {
            bool = true;
        } else {
            bool = false;
        }
        return bool;
    }
}
