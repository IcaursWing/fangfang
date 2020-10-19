package com.lobot.lobotcontrol;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

public class SysApplication
        extends Application {
    private static SysApplication instance;
    private List<Activity> mList = new LinkedList();

    public static SysApplication getInstance() {
        try {
            if (instance == null) {
                SysApplication localSysApplication = new SysApplication();
                instance = localSysApplication;
            }
            SysApplication localSysApplication = instance;
            return localSysApplication;
        } finally {
        }
    }

    public void addActivity(Activity paramActivity) {
        this.mList.add(paramActivity);
    }

    /* Error */
    public void exit() {
        // Byte code:
        //   0: aload_0
        //   1: getfield 18	com/lobot/lobotcontrol/SysApplication:mList	Ljava/util/List;
        //   4: invokeinterface 39 1 0
        //   9: astore_1
        //   10: aload_1
        //   11: invokeinterface 45 1 0
        //   16: ifeq +33 -> 49
        //   19: aload_1
        //   20: invokeinterface 49 1 0
        //   25: checkcast 51	android/app/Activity
        //   28: astore_2
        //   29: aload_2
        //   30: ifnull -20 -> 10
        //   33: aload_2
        //   34: invokevirtual 54	android/app/Activity:finish	()V
        //   37: goto -27 -> 10
        //   40: astore_2
        //   41: goto +13 -> 54
        //   44: astore_2
        //   45: aload_2
        //   46: invokevirtual 57	java/lang/Exception:printStackTrace	()V
        //   49: iconst_0
        //   50: invokestatic 62	java/lang/System:exit	(I)V
        //   53: return
        //   54: iconst_0
        //   55: invokestatic 62	java/lang/System:exit	(I)V
        //   58: aload_2
        //   59: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	60	0	this	SysApplication
        //   9	11	1	localIterator	java.util.Iterator
        //   28	6	2	localActivity	Activity
        //   40	1	2	localObject	Object
        //   44	15	2	localException	Exception
        // Exception table:
        //   from	to	target	type
        //   0	10	40	finally
        //   10	29	40	finally
        //   33	37	40	finally
        //   45	49	40	finally
        //   0	10	44	java/lang/Exception
        //   10	29	44	java/lang/Exception
        //   33	37	44	java/lang/Exception
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
