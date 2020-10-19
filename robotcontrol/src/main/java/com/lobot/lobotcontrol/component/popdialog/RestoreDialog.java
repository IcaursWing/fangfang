package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.lobot.lobotcontrol.db.CommandDAO;
import com.lobot.lobotcontrol.db.VoiceDAO;
import com.lobot.lobotcontrol.model.CommandModel;
import com.lobot.lobotcontrol.model.VoiceModel;
import java.util.List;
import java.util.Locale;

public class RestoreDialog
        extends PopupWindow
        implements OnClickListener
{
    private static final String COU_C = "CN";
    private static final String COU_HK = "HK";
    private static final String COU_TW = "TW";
    private static final String LAN_CN = "zh";
    private static final String LAN_EN = "en";
    int[] action = { 16, 17, 18, 19, 20, 21, 22, 23, 24, 15, 7, 8, 9, 10, 13, 14, 30, 31, 32, 33, 34, 35, 36, 37, 60, 255, 50, 51, 52, 53, 255, 255, 255, 255, 5, 6 };
    int[] actionType = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1 };
    private Button cancel;
    private TextView content;
    private Activity context;
    boolean[] editFlag = new boolean[36];
    private int[] idArray = { 2131427368, 2131427412, 2131427362, 2131427413, 2131427408, 2131427407, 2131427371, 2131427483, 2131427461, 2131427372, 2131427426, 2131427453, 2131427480, 2131427367, 2131427481, 2131427454, 2131427414, 2131427437, 2131427415, 2131427438, 2131427411, 2131427455, 2131427482, 2131427419, 2131427457, 2131427462, 2131427417, 2131427440, 2131427416, 2131427439, 2131427462, 2131427462, 2131427462, 2131427462, 2131427442, 2131427441 };
    private Locale mCurLocale;
    private Button ok;
    private OnRestoreDialogtClickListener onRestoreDialogClickListener;
    private TextView title;
    private View view;

    public RestoreDialog(View paramView, int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean)
    {
        super(paramView, paramInt1, paramInt2, false);
        this.ok = ((Button)paramView.findViewById(2131165317));
        this.cancel = ((Button)paramView.findViewById(2131165315));
        this.title = ((TextView)paramView.findViewById(2131165318));
        this.content = ((TextView)paramView.findViewById(2131165316));
        this.title.setText(paramString1);
        this.content.setText(paramString2);
        if (paramBoolean) {
            this.content.setTextColor(-65536);
        }
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
    }

    private boolean RestoreDataBase()
    {
        CommandDAO localCommandDAO = new CommandDAO(this.context);
        List localList1 = localCommandDAO.query(0);
        Object localObject1 = localCommandDAO.query(1);
        List localList2 = localCommandDAO.query(2);
        List localList3 = localCommandDAO.query(3);
        setEditFlag();
        Resources localResources1 = this.context.getResources();
        if (localResources1 != null)
        {
            this.mCurLocale = localResources1.getConfiguration().locale;
            Resources localResources2 = getResourcesByLocale(localResources1, "zh", "CN");
            Object localObject2 = new String[36];
            String[] arrayOfString1 = new String[36];
            String[] arrayOfString2 = new String[36];
            for (int i = 0; i < 36; i++) {
                localObject2[i] = localResources2.getString(this.idArray[i]);
            }
            resetLocale(localResources1);
            localResources2 = getResourcesByLocale(localResources1, "en", null);
            for (i = 0; i < 36; i++) {
                arrayOfString1[i] = localResources2.getString(this.idArray[i]);
            }
            resetLocale(localResources1);
            localResources2 = getResourcesByLocale(localResources1, "zh", "TW");
            for (i = 0; i < 36; i++) {
                arrayOfString2[i] = localResources2.getString(this.idArray[i]);
            }
            resetLocale(localResources1);
            for (i = 1; i <= localList1.size() + ((List)localObject1).size() + localList2.size() + localList3.size(); i++) {
                if (!localCommandDAO.delete(i))
                {
                    Toast.makeText(this.context, 2131427435, 0).show();
                    return false;
                }
            }
            int j;
            for (i = 0; i < 36; i = j)
            {
                j = i + 1;
                if (!localCommandDAO.insert(new CommandModel(j, localObject2[i], arrayOfString1[i], arrayOfString2[i], this.action[i], this.editFlag[i], this.actionType[i])))
                {
                    Toast.makeText(this.context, 2131427435, 0).show();
                    return false;
                }
            }
            localObject1 = new VoiceDAO(this.context);
            localObject2 = ((VoiceDAO)localObject1).query(4, true);
            for (i = 0; i < ((List)localObject2).size(); i++) {
                if (!((VoiceDAO)localObject1).delete(((VoiceModel)((List)localObject2).get(i)).getId()))
                {
                    Toast.makeText(this.context, 2131427435, 0).show();
                    return false;
                }
            }
            Toast.makeText(this.context, 2131427436, 0).show();
            return true;
        }
        return false;
    }

    public static RestoreDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, String paramString1, String paramString2, boolean paramBoolean, OnRestoreDialogtClickListener paramOnRestoreDialogtClickListener)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296301, null);
        paramString1 = new RestoreDialog(localView, paramInt1, paramInt2, paramString1, paramString2, paramBoolean);
        paramString1.view = localView;
        paramString1.context = paramActivity;
        paramString1.setRestoreDialogClickListener(paramOnRestoreDialogtClickListener);
        paramString1.setOutsideTouchable(true);
        paramString1.setBackgroundDrawable(new ColorDrawable());
        paramString1.setAnimationStyle(2131493221);
        return paramString1;
    }

    private Resources getResourcesByLocale(Resources paramResources, String paramString1, String paramString2)
    {
        Configuration localConfiguration = new Configuration(paramResources.getConfiguration());
        if (paramString2 != null) {
            localConfiguration.locale = new Locale(paramString1, paramString2);
        } else {
            localConfiguration.locale = new Locale(paramString1);
        }
        return new Resources(paramResources.getAssets(), paramResources.getDisplayMetrics(), localConfiguration);
    }

    private void resetLocale(Resources paramResources)
    {
        Configuration localConfiguration = new Configuration(paramResources.getConfiguration());
        localConfiguration.locale = this.mCurLocale;
        new Resources(paramResources.getAssets(), paramResources.getDisplayMetrics(), localConfiguration);
    }

    private void setEditFlag()
    {
        int j;
        for (int i = 0;; i++)
        {
            j = 10;
            if (i >= 10) {
                break;
            }
            this.editFlag[i] = false;
        }
        while (j < 34)
        {
            this.editFlag[j] = true;
            j++;
        }
        this.editFlag[34] = false;
        this.editFlag[35] = false;
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i == 2131165317)
        {
            RestoreDataBase();
            this.onRestoreDialogClickListener.onRestoreDialogClick(true);
            dismiss();
        }
        else if (i == 2131165315)
        {
            this.onRestoreDialogClickListener.onRestoreDialogClick(false);
            dismiss();
        }
    }

    public void setRestoreDialogClickListener(OnRestoreDialogtClickListener paramOnRestoreDialogtClickListener)
    {
        this.onRestoreDialogClickListener = paramOnRestoreDialogtClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnRestoreDialogtClickListener
    {
        public abstract void onRestoreDialogClick(boolean paramBoolean);
    }
}
