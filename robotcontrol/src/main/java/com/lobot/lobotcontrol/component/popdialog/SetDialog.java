package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class SetDialog
        extends PopupWindow
        implements OnClickListener
{
    private OnSetDialogtClickListener onSetDialogtClickListener;
    private View view;

    public SetDialog(View paramView, int paramInt1, int paramInt2)
    {
        super(paramView, paramInt1, paramInt2, false);
        Button[] arrayOfButton = new Button[5];
        arrayOfButton[0] = ((Button)paramView.findViewById(2131165184));
        arrayOfButton[1] = ((Button)paramView.findViewById(2131165233));
        arrayOfButton[2] = ((Button)paramView.findViewById(2131165399));
        arrayOfButton[3] = ((Button)paramView.findViewById(2131165314));
        arrayOfButton[4] = ((Button)paramView.findViewById(2131165222));
        for (paramInt1 = i; paramInt1 < arrayOfButton.length; paramInt1++) {
            arrayOfButton[paramInt1].setOnClickListener(this);
        }
    }

    public static SetDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, OnSetDialogtClickListener paramOnSetDialogtClickListener)
    {
        paramActivity = paramActivity.getLayoutInflater().inflate(2131296302, null);
        SetDialog localSetDialog = new SetDialog(paramActivity, paramInt1, paramInt2);
        localSetDialog.onSetDialogtClickListener = paramOnSetDialogtClickListener;
        localSetDialog.view = paramActivity;
        localSetDialog.setFocusable(true);
        localSetDialog.setOutsideTouchable(true);
        localSetDialog.setBackgroundDrawable(new ColorDrawable());
        localSetDialog.setAnimationStyle(2131493223);
        return localSetDialog;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i != 2131165184)
        {
            if (i != 2131165222)
            {
                if (i != 2131165233)
                {
                    if (i != 2131165314)
                    {
                        if (i == 2131165399) {
                            this.onSetDialogtClickListener.onSetDialogClick(2);
                        }
                    }
                    else {
                        this.onSetDialogtClickListener.onSetDialogClick(3);
                    }
                }
                else {
                    this.onSetDialogtClickListener.onSetDialogClick(1);
                }
            }
            else {
                this.onSetDialogtClickListener.onSetDialogClick(4);
            }
        }
        else {
            this.onSetDialogtClickListener.onSetDialogClick(0);
        }
    }

    public void setSetDialogtClickListener(OnSetDialogtClickListener paramOnSetDialogtClickListener)
    {
        this.onSetDialogtClickListener = paramOnSetDialogtClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 53, 0, 0);
    }

    public static abstract interface OnSetDialogtClickListener
    {
        public abstract void onSetDialogClick(int paramInt);
    }
}
