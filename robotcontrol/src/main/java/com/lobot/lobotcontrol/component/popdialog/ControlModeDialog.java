package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Switch;
import com.lobot.lobotcontrol.NormalModeActivity;

public class ControlModeDialog
        extends PopupWindow
        implements OnClickListener
{
    private Button cancel;
    private Activity context;
    private Switch controlMode;
    private Button ok;
    private OnControlModeClickListener onControlModeClickListener;
    private View view;

    public ControlModeDialog(View paramView, int paramInt1, int paramInt2)
    {
        super(paramView, paramInt1, paramInt2, false);
        this.ok = ((Button)paramView.findViewById(2131165235));
        this.cancel = ((Button)paramView.findViewById(2131165234));
        this.controlMode = ((Switch)paramView.findViewById(2131165232));
        this.controlMode.setChecked(NormalModeActivity.controlMode);
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
    }

    public static ControlModeDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, OnControlModeClickListener paramOnControlModeClickListener)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296298, null);
        ControlModeDialog localControlModeDialog = new ControlModeDialog(localView, paramInt1, paramInt2);
        localControlModeDialog.view = localView;
        localControlModeDialog.context = paramActivity;
        localControlModeDialog.setOnControlModeClickListener(paramOnControlModeClickListener);
        localControlModeDialog.setOutsideTouchable(true);
        localControlModeDialog.setBackgroundDrawable(new ColorDrawable());
        localControlModeDialog.setAnimationStyle(2131493221);
        return localControlModeDialog;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i == 2131165235)
        {
            this.onControlModeClickListener.onControlModeClick(true, this.controlMode.isChecked());
            dismiss();
        }
        else if (i == 2131165234)
        {
            this.onControlModeClickListener.onControlModeClick(false, this.controlMode.isChecked());
            dismiss();
        }
    }

    public void setOnControlModeClickListener(OnControlModeClickListener paramOnControlModeClickListener)
    {
        this.onControlModeClickListener = paramOnControlModeClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnControlModeClickListener
    {
        public abstract void onControlModeClick(boolean paramBoolean1, boolean paramBoolean2);
    }
}
