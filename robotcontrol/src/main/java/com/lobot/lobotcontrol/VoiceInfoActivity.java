package com.lobot.lobotcontrol;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lobot.lobotcontrol.db.VoiceDAO;
import com.lobot.lobotcontrol.model.VoiceModel;
import java.util.List;

public class VoiceInfoActivity
        extends AppCompatActivity
{
    public static final int MSG_UPDATE_LIST = 1;
    private VoiceInfoAdapter adapter;
    private VoiceDAO dao;
    private Handler mHandler;

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(2131296286);
        paramBundle = (ListView)findViewById(2131165394);
        ImageButton localImageButton = (ImageButton)findViewById(2131165209);
        this.dao = new VoiceDAO(this);
        this.adapter = new VoiceInfoAdapter(this, this.dao.query(true));
        paramBundle.setAdapter(this.adapter);
        paramBundle.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
            {
                VoiceInfoActivity.this.adapter.getItem(paramAnonymousInt).isCanEdit();
                return false;
            }
        });
        this.mHandler = new Handler(new UICallback());
        localImageButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                AddVoiceDialog.createDialog(VoiceInfoActivity.this, NormalModeActivity.screenHigh * 2 / 3, NormalModeActivity.screenWidth / 2, null, VoiceInfoActivity.this.mHandler).showDialog();
            }
        });
    }

    class UICallback
            implements Handler.Callback
    {
        UICallback() {}

        public boolean handleMessage(Message paramMessage)
        {
            if (paramMessage.what == 1) {
                VoiceInfoActivity.this.adapter.updateData(VoiceInfoActivity.this.dao.query(true));
            }
            return true;
        }
    }

    class VoiceInfoAdapter
            extends BaseAdapter
            implements View.OnClickListener
    {
        Context context;
        List<VoiceModel> modelList;

        public VoiceInfoAdapter(List<VoiceModel> paramList)
        {
            this.context = paramList;
            List localList;
            this.modelList = localList;
        }

        public int getCount()
        {
            return this.modelList.size();
        }

        public VoiceModel getItem(int paramInt)
        {
            return (VoiceModel)this.modelList.get(paramInt);
        }

        public long getItemId(int paramInt)
        {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
        {
            Object localObject = new ViewHolder();
            if (paramView == null)
            {
                paramView = LayoutInflater.from(this.context).inflate(2131296292, paramViewGroup, false);
                ((ViewHolder)localObject).nameTv = ((TextView)paramView.findViewById(2131165395));
                ((ViewHolder)localObject).actionTv = ((TextView)paramView.findViewById(2131165384));
                ((ViewHolder)localObject).acceptTv = ((TextView)paramView.findViewById(2131165383));
                ((ViewHolder)localObject).deleteBtn = ((ImageButton)paramView.findViewById(2131165392));
                paramView.setTag(localObject);
                paramViewGroup = (ViewGroup)localObject;
            }
            else
            {
                paramViewGroup = (ViewHolder)paramView.getTag();
            }
            localObject = (VoiceModel)this.modelList.get(paramInt);
            paramViewGroup.nameTv.setText(((VoiceModel)localObject).getName());
            paramViewGroup.actionTv.setText(VoiceInfoActivity.this.getString(2131427479, new Object[] { Integer.valueOf(((VoiceModel)localObject).getAction()) }));
            paramViewGroup.acceptTv.setText(VoiceInfoActivity.this.getString(2131427478, new Object[] { ((VoiceModel)localObject).getAcceptVoice().replace(";", "��") }));
            if (((VoiceModel)localObject).isCanEdit())
            {
                paramViewGroup.deleteBtn.setVisibility(0);
                paramViewGroup.deleteBtn.setTag(Integer.valueOf(paramInt));
                paramViewGroup.deleteBtn.setOnClickListener(this);
            }
            else
            {
                paramViewGroup.deleteBtn.setVisibility(4);
                paramViewGroup.deleteBtn.setTag(null);
            }
            return paramView;
        }

        public void onClick(View paramView)
        {
            if ((paramView instanceof ImageButton))
            {
                Object localObject = paramView.getTag();
                if (localObject != null)
                {
                    paramView = this.modelList;
                    localObject = (Integer)localObject;
                    paramView = (VoiceModel)paramView.get(((Integer)localObject).intValue());
                    if (VoiceInfoActivity.this.dao.delete(paramView.getId()))
                    {
                        this.modelList.remove(((Integer)localObject).intValue());
                        notifyDataSetChanged();
                        Toast.makeText(this.context, 2131427396, 0).show();
                    }
                    else
                    {
                        Toast.makeText(this.context, 2131427395, 0).show();
                    }
                }
            }
        }

        public void updateData(List<VoiceModel> paramList)
        {
            this.modelList = paramList;
            notifyDataSetChanged();
        }

        class ViewHolder
        {
            TextView acceptTv;
            TextView actionTv;
            ImageButton deleteBtn;
            TextView nameTv;

            ViewHolder() {}
        }
    }
}
