package com.lobot.lobotcontrol.uitls;

import android.os.Handler;
import java.lang.ref.WeakReference;

public class BaseUIHandler<T>
        extends Handler
{
    protected WeakReference<T> reference;

    public BaseUIHandler(T paramT)
    {
        this.reference = new WeakReference(paramT);
    }

    protected T get()
    {
        return (T)this.reference.get();
    }
}
