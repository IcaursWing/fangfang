package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class ChooseDialog
        extends PopupWindow
        implements OnClickListener
{
    private Button cancel;
    private Button ok;
    private OnChooseDialogClickListener onChooseDialogClickListener;
    private View view;

    public ChooseDialog(View paramView, int paramInt1, int paramInt2)
    {
        super(paramView, paramInt1, paramInt2, false);
        this.ok = ((Button)paramView.findViewById(2131165228));
        this.cancel = ((Button)paramView.findViewById(2131165227));
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
    }

    public static ChooseDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, OnChooseDialogClickListener paramOnChooseDialogClickListener)
    {
        paramActivity = paramActivity.getLayoutInflater().inflate(2131296297, null);
        ChooseDialog localChooseDialog = new ChooseDialog(paramActivity, paramInt1, paramInt2);
        localChooseDialog.view = paramActivity;
        localChooseDialog.setChooseDialogClickListener(paramOnChooseDialogClickListener);
        localChooseDialog.setOutsideTouchable(true);
        localChooseDialog.setBackgroundDrawable(new ColorDrawable());
        localChooseDialog.setAnimationStyle(2131493221);
        return localChooseDialog;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i == 2131165228)
        {
            this.onChooseDialogClickListener.onChooseDialogClick(true);
            dismiss();
        }
        else if (i == 2131165227)
        {
            this.onChooseDialogClickListener.onChooseDialogClick(false);
            dismiss();
        }
    }

    public void setChooseDialogClickListener(OnChooseDialogClickListener paramOnChooseDialogClickListener)
    {
        this.onChooseDialogClickListener = paramOnChooseDialogClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnChooseDialogClickListener
    {
        public abstract void onChooseDialogClick(boolean paramBoolean);
    }
}
