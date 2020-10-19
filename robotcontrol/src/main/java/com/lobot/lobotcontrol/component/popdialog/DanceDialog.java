package com.lobot.lobotcontrol.component.popdialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.lobot.lobotcontrol.NormalModeActivity;
import com.lobot.lobotcontrol.db.CommandDAO;
import com.lobot.lobotcontrol.model.CommandModel;
import java.util.List;

public class DanceDialog
        extends PopupWindow
        implements OnClickListener, OnItemClickListener, OnItemLongClickListener
{
    private Button cancel;
    private Activity context;
    private List<CommandModel> danceList;
    private ListView danceListView;
    private int height;
    private Button ok;
    private OnDanceDialogtClickListener onDanceDialogClickListener;
    private int type;
    private View view;
    private int width;

    public DanceDialog(Activity paramActivity, int paramInt1, View paramView, int paramInt2, int paramInt3)
    {
        super(paramView, paramInt2, paramInt3, false);
        this.context = paramActivity;
        this.type = paramInt1;
        this.width = paramInt2;
        this.height = paramInt3;
        this.ok = ((Button)paramView.findViewById(2131165240));
        this.cancel = ((Button)paramView.findViewById(2131165244));
        this.danceListView = ((ListView)paramView.findViewById(2131165245));
        this.ok.setOnClickListener(this);
        this.cancel.setOnClickListener(this);
        showDanceList();
    }

    public static DanceDialog createDialog(Activity paramActivity, int paramInt1, int paramInt2, int paramInt3, OnDanceDialogtClickListener paramOnDanceDialogtClickListener)
    {
        View localView = paramActivity.getLayoutInflater().inflate(2131296299, null);
        paramActivity = new DanceDialog(paramActivity, paramInt1, localView, paramInt2, paramInt3);
        paramActivity.view = localView;
        paramActivity.setDanceDialogClickListener(paramOnDanceDialogtClickListener);
        paramActivity.setOutsideTouchable(true);
        paramActivity.setBackgroundDrawable(new ColorDrawable());
        paramActivity.setAnimationStyle(2131493221);
        return paramActivity;
    }

    private void showDanceList()
    {
        this.danceList = new CommandDAO(this.context).query(this.type);
        Object localObject = new String[this.danceList.size()];
        for (int i = 0; i < this.danceList.size(); i++) {
            if (NormalModeActivity.languageType == 0) {
                localObject[i] = ((CommandModel)this.danceList.get(i)).getTitle();
            } else if (NormalModeActivity.languageType == 1) {
                localObject[i] = ((CommandModel)this.danceList.get(i)).getTitleTw();
            } else if (NormalModeActivity.languageType == 2) {
                localObject[i] = ((CommandModel)this.danceList.get(i)).getTitleEn();
            }
        }
        localObject = new ArrayAdapter(this.context, 2131296287, (Object[])localObject);
        this.danceListView.setAdapter((ListAdapter)localObject);
        this.danceListView.setOnItemClickListener(this);
        this.danceListView.setOnItemLongClickListener(this);
    }

    public void onClick(View paramView)
    {
        int i = paramView.getId();
        if (i == 2131165240)
        {
            AddDanceDialog.createDialog(this.context, this.width, this.height * 2 / 3, this.type, 0, null, null, new AddDanceDialog.OnAddDanceDialogClickListener()
            {
                public void onAddDanceDialogClick(boolean paramAnonymousBoolean) {}
            }).showDialog();
            dismiss();
        }
        else if (i == 2131165244)
        {
            dismiss();
        }
    }

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        if (this.onDanceDialogClickListener != null) {
            this.onDanceDialogClickListener.onDanceDialogClick(((CommandModel)this.danceList.get(paramInt)).getAction());
        }
        dismiss();
    }

    public boolean onItemLongClick(final AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
        paramAdapterView = (CommandModel)this.danceList.get(paramInt);
        if (!paramAdapterView.isCanEdit())
        {
            Toast.makeText(this.context, 2131427385, 0).show();
            return false;
        }
        ChooseDialog.createDialog(this.context, this.width, this.height * 2 / 3, new ChooseDialog.OnChooseDialogClickListener()
        {
            public void onChooseDialogClick(boolean paramAnonymousBoolean)
            {
                if (paramAnonymousBoolean)
                {
                    String str = "";
                    if (NormalModeActivity.languageType == 0) {
                        str = paramAdapterView.getTitle();
                    }
                    for (;;)
                    {
                        break;
                        if (NormalModeActivity.languageType == 1) {
                            str = paramAdapterView.getTitleTw();
                        } else if (NormalModeActivity.languageType == 2) {
                            str = paramAdapterView.getTitleEn();
                        }
                    }
                    AddDanceDialog.createDialog(DanceDialog.this.context, DanceDialog.this.width, DanceDialog.this.height * 2 / 3, DanceDialog.this.type, paramAdapterView.getId(), str, String.valueOf(paramAdapterView.getAction()), new AddDanceDialog.OnAddDanceDialogClickListener()
                    {
                        public void onAddDanceDialogClick(boolean paramAnonymous2Boolean) {}
                    }).showDialog();
                }
                else if (!new CommandDAO(DanceDialog.this.context).delete(paramAdapterView.getId()))
                {
                    Toast.makeText(DanceDialog.this.context, 2131427395, 0).show();
                }
                else
                {
                    Toast.makeText(DanceDialog.this.context, 2131427396, 0).show();
                }
            }
        }).showDialog();
        dismiss();
        return false;
    }

    public void setDanceDialogClickListener(OnDanceDialogtClickListener paramOnDanceDialogtClickListener)
    {
        this.onDanceDialogClickListener = paramOnDanceDialogtClickListener;
    }

    public void showDialog()
    {
        update();
        showAtLocation(this.view, 17, 0, 0);
    }

    public static abstract interface OnDanceDialogtClickListener
    {
        public abstract void onDanceDialogClick(int paramInt);
    }
}
