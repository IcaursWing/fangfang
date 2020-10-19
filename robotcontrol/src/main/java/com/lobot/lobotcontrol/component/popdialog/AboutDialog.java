package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AboutDialog
        extends PopupWindow
{
    private TextView versionCode;
    private View view;

    public AboutDialog(Activity paramActivity, View paramView, int paramInt1, int paramInt2)
    {
        super(paramView, paramInt1, paramInt2, false);
        this.versionCode = ((TextView)paramView.findViewById(2131165382));
        paramView = paramActivity.getPackageManager();
        try
        {
            paramView = paramView.getPackageInfo(paramActivity.getPackageName(), 1).versionName;
        }
        catch (NameNotFoundException paramView)
        {
            paramView.printStackTrace();
            paramView = "5.0";
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramActivity.getString(2131427361));
        localStringBuilder.append(" V");
        localStringBuilder.append(paramView);
        paramView = localStringBuilder.toString();
        paramActivity = new StringBuilder();
        paramActivity.append("versionName:");
        paramActivity.append(paramView);
        Log.e("TAG", paramActivity.toString());
        this.versionCode.setText(paramView);
    }

    public static AboutDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296293, null);
        paramActivity = new AboutDialog(paramActivity, localView, paramInt1, paramInt2);
        paramActivity.view = localView;
        paramActivity.setOutsideTouchable(true);
        paramActivity.setBackgroundDrawable(new ColorDrawable());
        paramActivity.setAnimationStyle(2131493221);
        return paramActivity;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }
}
