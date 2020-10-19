package Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fangfangtech.oidbox.R;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.ArrayList;

public class ListAdapter_Main1 extends BaseAdapter {
    private Context context;
    ArrayList<GameInfo> gameInfos;


    public ListAdapter_Main1(Context context) {
        this.context = context;
        gameInfos = new ArrayList<>();

        gameInfos.add(new GameInfo(context.getResources().getDrawable(R.drawable.gametable), new String[]{//
                "舒尔特竞赛", "培养孩子注意力的指向性、集中性和抗干扰性，相对延长注意力集中的时间。", "记忆力", "专注力", "4-12岁"//
        }));
        gameInfos.add(new GameInfo(context.getResources().getDrawable(R.drawable.question_competition), new String[]{//
                "趣味知识竞赛", "培养孩子的记忆力，锻炼孩子的反应速度，拓展孩子的知识面。", "记忆力", "趣味学习", "4-12岁"//
        }));
        gameInfos.add(new GameInfo(context.getResources().getDrawable(R.drawable.aimusic), new String[]{//
                "AI音乐", "培养孩子的乐感，体验人工智能技术，开发孩子的创造能力。", "音乐创作", "兴趣培养", "4-12岁"//
        }));

    }

    public class ViewHolder {
        RadiusImageView radiusImageView;
        TextView textView_title;
        TextView textView_content;
        SuperTextView superTextView1, superTextView2, superTextView3;

    }

    private class GameInfo {
        Drawable drawable;
        String[] gametext = new String[]{"游戏名称", "游戏介绍", "标签1", "标签2", "标签3"};

        GameInfo(Drawable drawable, String[] gametext) {
            this.drawable = drawable;
            this.gametext = gametext;
        }

    }

    @Override
    public int getCount() {
        return gameInfos.size();
    }

    @Override
    public Object getItem(int position) {
        if (position > gameInfos.size())
            return null;

        return gameInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.listitem_main1, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);

            viewHolder.radiusImageView = convertView.findViewById(R.id.imagevie_main);
            viewHolder.textView_title = convertView.findViewById(R.id.textView_main_title);
            viewHolder.textView_content = convertView.findViewById(R.id.textView_main_content);
            viewHolder.superTextView1 = convertView.findViewById(R.id.textView_main_tag1);
            viewHolder.superTextView2 = convertView.findViewById(R.id.textView_main_tag2);
            viewHolder.superTextView3 = convertView.findViewById(R.id.textView_main_tag3);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.radiusImageView.setImageDrawable(gameInfos.get(position).drawable);
        viewHolder.textView_title.setText(gameInfos.get(position).gametext[0]);
        viewHolder.textView_content.setText(gameInfos.get(position).gametext[1]);
        viewHolder.superTextView1.setCenterString(gameInfos.get(position).gametext[2]);
        viewHolder.superTextView2.setCenterString(gameInfos.get(position).gametext[3]);
        viewHolder.superTextView3.setCenterString(gameInfos.get(position).gametext[4]);

        viewHolder.superTextView1.getLayoutParams().width = viewHolder.superTextView1.getCenterString().length() * 30;
        viewHolder.superTextView2.getLayoutParams().width = viewHolder.superTextView2.getCenterString().length() * 30;
        viewHolder.superTextView3.getLayoutParams().width = viewHolder.superTextView3.getCenterString().length() * 30;

        return convertView;
    }


}
