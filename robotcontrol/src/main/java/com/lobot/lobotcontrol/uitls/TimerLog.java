package com.lobot.lobotcontrol.uitls;

import android.util.Log;

public class TimerLog
{
    private static long currentTime = ;

    public static void logTime(String paramString)
    {
        long l = System.currentTimeMillis();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramString);
        localStringBuilder.append(" cost ");
        localStringBuilder.append(l - currentTime);
        localStringBuilder.append(" ms");
        Log.i("TimerLog", localStringBuilder.toString());
        currentTime = l;
    }
}
