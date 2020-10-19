package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.lobot.lobotcontrol.NormalModeActivity;

public class WorkModeDialog
        extends PopupWindow
        implements OnClickListener
{
    private Activity context;
    private ImageButton fightBtn;
    private ImageButton footballBtn;
    private ImageButton normalBtn;
    private OnWorkModelickListener onWorkModelickListener;
    private TextView text;
    private View view;

    public WorkModeDialog(Activity paramActivity, View paramView, int paramInt1, int paramInt2)
    {
        super(paramView, paramInt1, paramInt2, false);
        if (NormalModeActivity.workMode == 0) {
            this.text = ((TextView)paramView.findViewById(2131165300));
        } else if (NormalModeActivity.workMode == 1) {
            this.text = ((TextView)paramView.findViewById(2131165262));
        } else if (NormalModeActivity.workMode == 2) {
            this.text = ((TextView)paramView.findViewById(2131165260));
        }
        if (this.text != null) {
            this.text.setTextColor(paramActivity.getResources().getColor(2130968619));
        }
        this.normalBtn = ((ImageButton)paramView.findViewById(2131165299));
        this.footballBtn = ((ImageButton)paramView.findViewById(2131165261));
        this.fightBtn = ((ImageButton)paramView.findViewById(2131165259));
        this.normalBtn.setOnClickListener(this);
        this.footballBtn.setOnClickListener(this);
        this.fightBtn.setOnClickListener(this);
    }

    public static WorkModeDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, OnWorkModelickListener paramOnWorkModelickListener)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296303, null);
        WorkModeDialog localWorkModeDialog = new WorkModeDialog(paramActivity, localView, paramInt1, paramInt2);
        localWorkModeDialog.view = localView;
        localWorkModeDialog.context = paramActivity;
        localWorkModeDialog.setOnWorkModelickListenerListener(paramOnWorkModelickListener);
        localWorkModeDialog.setOutsideTouchable(true);
        localWorkModeDialog.setBackgroundDrawable(new ColorDrawable());
        localWorkModeDialog.setAnimationStyle(2131493221);
        return localWorkModeDialog;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i == 2131165299)
        {
            this.onWorkModelickListener.onWorkModeClick(0);
            dismiss();
        }
        else if (i == 2131165261)
        {
            this.onWorkModelickListener.onWorkModeClick(1);
            dismiss();
        }
        else if (i == 2131165259)
        {
            this.onWorkModelickListener.onWorkModeClick(2);
            dismiss();
        }
    }

    public void setOnWorkModelickListenerListener(OnWorkModelickListener paramOnWorkModelickListener)
    {
        this.onWorkModelickListener = paramOnWorkModelickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnWorkModelickListener
    {
        public abstract void onWorkModeClick(int paramInt);
    }
}
