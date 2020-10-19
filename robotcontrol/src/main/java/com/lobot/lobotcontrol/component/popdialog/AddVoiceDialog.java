package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.lobot.lobotcontrol.db.VoiceDAO;
import com.lobot.lobotcontrol.model.VoiceModel;
import com.lobot.lobotcontrol.uitls.ChineseUtils;
import java.util.Iterator;
import java.util.List;

public class AddVoiceDialog
        extends PopupWindow
        implements OnClickListener
{
    private EditText actionET;
    private boolean addMode;
    protected boolean autoCancel = true;
    private Button cancel;
    private Activity context;
    private Handler handler;
    private EditText nameET;
    private Button ok;
    private OnAddVoiceDialogClickListener onAddVoiceDialogClickListener;
    private RadioGroup radioGroup;
    private View view;
    private EditText voiceET;
    private VoiceModel voiceModel;

    public AddVoiceDialog(View paramView, int paramInt1, int paramInt2, VoiceModel paramVoiceModel)
    {
        super(paramView, paramInt1, paramInt2, true);
        this.ok = ((Button)paramView.findViewById(2131165207));
        this.cancel = ((Button)paramView.findViewById(2131165206));
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
        this.nameET = ((EditText)paramView.findViewById(2131165386));
        this.actionET = ((EditText)paramView.findViewById(2131165385));
        this.voiceET = ((EditText)paramView.findViewById(2131165390));
        this.radioGroup = ((RadioGroup)paramView.findViewById(2131165389));
        this.voiceModel = paramVoiceModel;
        if (paramVoiceModel == null) {
            this.addMode = true;
        } else {
            this.addMode = false;
        }
        if (!this.addMode)
        {
            this.nameET.setText(this.voiceModel.getName());
            this.actionET.setText(this.voiceModel.getAction());
            this.voiceET.setText(this.voiceModel.getAcceptVoice());
            if (this.voiceModel.isUsePinyin()) {
                this.radioGroup.check(2131165387);
            } else {
                this.radioGroup.check(2131165388);
            }
        }
    }

    public static AddVoiceDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, VoiceModel paramVoiceModel, Handler paramHandler)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296295, null);
        paramVoiceModel = new AddVoiceDialog(localView, paramInt1, paramInt2, paramVoiceModel);
        paramVoiceModel.view = localView;
        paramVoiceModel.context = paramActivity;
        paramVoiceModel.autoCancel = false;
        paramVoiceModel.handler = paramHandler;
        paramVoiceModel.setFocusable(true);
        paramVoiceModel.setOutsideTouchable(true);
        paramVoiceModel.setBackgroundDrawable(new ColorDrawable());
        paramVoiceModel.setAnimationStyle(2131493221);
        return paramVoiceModel;
    }

    public void onClick(View paramView)
    {
        switch (paramView.getId())
        {
            default:
                break;
            case 2131165207:
                String str1 = this.nameET.getText().toString();
                if (TextUtils.isEmpty(str1))
                {
                    Toast.makeText(this.context, 2131427470, 0).show();
                    return;
                }
                String str2 = this.actionET.getText().toString();
                if (TextUtils.isEmpty(str2))
                {
                    Toast.makeText(this.context, 2131427465, 0).show();
                    return;
                }
                String str3 = this.voiceET.getText().toString();
                if (TextUtils.isEmpty(str3))
                {
                    Toast.makeText(this.context, 2131427476, 0).show();
                    return;
                }
                Object localObject = new VoiceDAO(this.context);
                for (String str4 : str3.split(" "))
                {
                    paramView = null;
                    if (!this.addMode) {
                        paramView = Integer.valueOf(this.voiceModel.getId());
                    }
                    paramView = ((VoiceDAO)localObject).containKeys(str4, paramView);
                    if (!paramView.isEmpty())
                    {
                        localObject = new StringBuilder();
                        paramView = paramView.iterator();
                        while (paramView.hasNext())
                        {
                            ((StringBuilder)localObject).append(((VoiceModel)paramView.next()).getName());
                            ((StringBuilder)localObject).append("��");
                        }
                        ((StringBuilder)localObject).delete(((StringBuilder)localObject).length() - 1, ((StringBuilder)localObject).length());
                        Toast.makeText(this.context, 2131427466, 1).show();
                        return;
                    }
                }
                paramView = str3.replace(" ", ";");
                boolean bool;
                if (this.radioGroup.getCheckedRadioButtonId() == 2131165387) {
                    bool = true;
                } else {
                    bool = false;
                }
                paramView = new VoiceModel(str1, paramView, ChineseUtils.chinese2Spell(paramView), bool, Integer.valueOf(str2), Integer.valueOf(4), true);
                if (this.addMode)
                {
                    if (((VoiceDAO)localObject).insert(paramView))
                    {
                        Toast.makeText(this.context, 2131427389, 0).show();
                        this.handler.obtainMessage(1).sendToTarget();
                        dismiss();
                    }
                    else
                    {
                        Toast.makeText(this.context, 2131427386, 0).show();
                    }
                }
                else
                {
                    paramView.setId(this.voiceModel.getId());
                    if (((VoiceDAO)localObject).update(paramView))
                    {
                        Toast.makeText(this.context, 2131427400, 0).show();
                        this.handler.obtainMessage(1).sendToTarget();
                        dismiss();
                    }
                    else
                    {
                        Toast.makeText(this.context, 2131427386, 0).show();
                    }
                }
                dismiss();
                break;
            case 2131165206:
                dismiss();
        }
    }

    public void setAddVoiceDialogClickListener(OnAddVoiceDialogClickListener paramOnAddVoiceDialogClickListener)
    {
        this.onAddVoiceDialogClickListener = paramOnAddVoiceDialogClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnAddVoiceDialogClickListener
    {
        public abstract void onAddVoiceDialogClick(boolean paramBoolean);
    }
}
