package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.lobot.lobotcontrol.db.CommandDAO;
import com.lobot.lobotcontrol.model.CommandModel;

public class AddDanceDialog
        extends PopupWindow
        implements OnClickListener
{
    private EditText actionEdit;
    private boolean addMode;
    private Button cancel;
    private Activity context;
    private int danceId;
    private EditText danceNameEdit;
    private Button ok;
    private OnAddDanceDialogClickListener onAddDanceDialogClickListener;
    private TextView title;
    private int type;
    private View view;

    public AddDanceDialog(Activity paramActivity, View paramView, int paramInt1, int paramInt2, String paramString1, String paramString2, int paramInt3, int paramInt4)
    {
        super(paramView, paramInt1, paramInt2, false);
        this.ok = ((Button)paramView.findViewById(2131165207));
        this.cancel = ((Button)paramView.findViewById(2131165206));
        this.title = ((TextView)paramView.findViewById(2131165208));
        this.danceNameEdit = ((EditText)paramView.findViewById(2131165242));
        this.actionEdit = ((EditText)paramView.findViewById(2131165241));
        this.type = paramInt3;
        this.context = paramActivity;
        this.danceId = paramInt4;
        boolean bool = true;
        if (paramInt3 == 0)
        {
            if (!TextUtils.isEmpty(paramString1))
            {
                this.title.setText(2131427397);
                this.danceNameEdit.setText(paramString1);
                this.actionEdit.setText(paramString2);
                this.danceNameEdit.setSelection(this.danceNameEdit.length());
                this.actionEdit.setSelection(this.actionEdit.length());
            }
            else
            {
                this.title.setText(2131427390);
                this.danceNameEdit.setHint(2131427388);
                this.actionEdit.setHint(2131427387);
            }
        }
        else if ((paramInt3 != 1) && (paramInt3 != 3))
        {
            if (paramInt3 == 2)
            {
                this.title.setText(2131427382);
                this.danceNameEdit.setText(paramString1);
                this.actionEdit.setText(paramString2);
                this.danceNameEdit.setSelection(this.danceNameEdit.length());
                this.actionEdit.setSelection(this.actionEdit.length());
            }
        }
        else if (!TextUtils.isEmpty(paramString1))
        {
            this.title.setText(2131427380);
            this.danceNameEdit.setText(paramString1);
            this.actionEdit.setText(paramString2);
            this.danceNameEdit.setSelection(this.danceNameEdit.length());
            this.actionEdit.setSelection(this.actionEdit.length());
        }
        else
        {
            this.title.setText(2131427378);
            this.danceNameEdit.setHint(2131427377);
            this.actionEdit.setHint(2131427376);
        }
        if ((!TextUtils.isEmpty(paramString1)) || (!TextUtils.isEmpty(paramString2))) {
            bool = false;
        }
        this.addMode = bool;
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
    }

    public static AddDanceDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString1, String paramString2, OnAddDanceDialogClickListener paramOnAddDanceDialogClickListener)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296294, null);
        paramActivity = new AddDanceDialog(paramActivity, localView, paramInt1, paramInt2, paramString1, paramString2, paramInt3, paramInt4);
        paramActivity.view = localView;
        paramActivity.setAddDanceDialogClickListener(paramOnAddDanceDialogClickListener);
        paramActivity.setOutsideTouchable(true);
        paramActivity.setFocusable(true);
        paramActivity.setBackgroundDrawable(new ColorDrawable());
        paramActivity.setAnimationStyle(2131493221);
        return paramActivity;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i == 2131165207)
        {
            if (TextUtils.isEmpty(this.danceNameEdit.getText()))
            {
                if (this.type == 0) {
                    Toast.makeText(this.context, 2131427391, 0).show();
                } else if (this.type == 1) {
                    Toast.makeText(this.context, 2131427379, 0).show();
                }
                dismiss();
                return;
            }
            if (TextUtils.isEmpty(this.actionEdit.getText()))
            {
                if (this.type == 0) {
                    Toast.makeText(this.context, 2131427384, 0).show();
                } else if (this.type == 1) {
                    Toast.makeText(this.context, 2131427375, 0).show();
                }
                dismiss();
                return;
            }
            CommandModel localCommandModel = new CommandModel(this.danceNameEdit.getText().toString(), this.danceNameEdit.getText().toString(), this.danceNameEdit.getText().toString(), Integer.valueOf(this.actionEdit.getText().toString()).intValue(), true, this.type);
            paramView = new CommandDAO(this.context);
            if (this.addMode)
            {
                if (paramView.insert(localCommandModel))
                {
                    this.onAddDanceDialogClickListener.onAddDanceDialogClick(true);
                    Toast.makeText(this.context, 2131427389, 0).show();
                }
                else
                {
                    this.onAddDanceDialogClickListener.onAddDanceDialogClick(false);
                    Toast.makeText(this.context, 2131427386, 0).show();
                }
            }
            else
            {
                localCommandModel.setId(this.danceId);
                if (paramView.update(localCommandModel))
                {
                    this.onAddDanceDialogClickListener.onAddDanceDialogClick(true);
                    Toast.makeText(this.context, 2131427400, 0).show();
                }
                else
                {
                    this.onAddDanceDialogClickListener.onAddDanceDialogClick(false);
                    Toast.makeText(this.context, 2131427386, 0).show();
                }
            }
            dismiss();
        }
        else if (i == 2131165206)
        {
            dismiss();
        }
    }

    public void setAddDanceDialogClickListener(OnAddDanceDialogClickListener paramOnAddDanceDialogClickListener)
    {
        this.onAddDanceDialogClickListener = paramOnAddDanceDialogClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnAddDanceDialogClickListener
    {
        public abstract void onAddDanceDialogClick(boolean paramBoolean);
    }
}
