package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.robotcontrol.R;

public class NormalDialog
        extends PopupWindow
        implements OnClickListener {
    private Button cancel;
    private TextView content;
    private Button ok;
    private OnNormalDialogtClickListener onNormalDialogClickListener;
    private TextView title;
    private View view;

    public NormalDialog(View paramView, int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean) {
        super(paramView, paramInt1, paramInt2, false);
        this.ok = ((Button) paramView.findViewById(R.id.normal_dialog_ok));
        this.cancel = ((Button) paramView.findViewById(R.id.normal_dialog_cancel));
        this.title = ((TextView) paramView.findViewById(R.id.normal_dialog_title));
        this.content = ((TextView) paramView.findViewById(R.id.normal_dialog_content));
        this.title.setText(paramString1);
        this.content.setText(paramString2);
        if (paramBoolean) {
            this.content.setTextColor(-65536);
        }
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
    }

    public static NormalDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean,
                                            OnNormalDialogtClickListener paramOnNormalDialogtClickListener) {
        paramActivity = paramActivity.getLayoutInflater().inflate(R.layout.layout_normal_dialog, null);
        paramString1 = new NormalDialog(paramActivity, paramInt1, paramInt2, paramString1, paramString2, paramBoolean);
        paramString1.view = paramActivity;
        paramString1.setNormalDialogClickListener(paramOnNormalDialogtClickListener);
        paramString1.setOutsideTouchable(true);
        paramString1.setBackgroundDrawable(new ColorDrawable());
        paramString1.setAnimationStyle(2131493221);
        return paramString1;
    }

    public void onClick(View paramView) {
        int i = paramView.getId();
        if (i == 2131165297) {
            this.onNormalDialogClickListener.onNormalDialogClick(true);
            dismiss();
        } else if (i == 2131165295) {
            this.onNormalDialogClickListener.onNormalDialogClick(false);
            dismiss();
        }
    }

    public void setNormalDialogClickListener(OnNormalDialogtClickListener paramOnNormalDialogtClickListener) {
        this.onNormalDialogClickListener = paramOnNormalDialogtClickListener;
    }

    public void showDialog() {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnNormalDialogtClickListener {
        public abstract void onNormalDialogClick(boolean paramBoolean);
    }
}
