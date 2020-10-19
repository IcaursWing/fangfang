package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangfangtech.oidbox.R;
import com.hb.dialog.dialog.LoadingDialog;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.ArrayList;

import Adapter.FangRecord;
import ExtraUtil.XToastUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CustomAdapt;

public class GameTableRankActivity extends Activity implements CustomAdapt {

    @BindView(R.id.titlebar_tablerank)
    TitleBar titlebar;
    @BindView(R.id.niceSpinner_tablerank)
    NiceSpinner niceSpinner;
    @BindView(R.id.imageview_tablerank_myimage)
    RadiusImageView iv_Myimage;
    @BindView(R.id.supertextview_tablerank_myrecord)
    SuperTextView tvsp_Myrecord;
    @BindView(R.id.imageview_tablerank_2image)
    RadiusImageView iv_2image;
    @BindView(R.id.imageview_tablerank_1image)
    RadiusImageView iv_1image;
    @BindView(R.id.imageview_tablerank_3image)
    RadiusImageView iv_3image;
    @BindView(R.id.textview_tablerank_2record)
    TextView tv_2record;
    @BindView(R.id.textview_tablerank_1record)
    TextView tv_1record;
    @BindView(R.id.textview_tablerank_3record)
    TextView tv_3record;
    @BindView(R.id.recyclerView_tablerank)
    RecyclerView recyclerView;

    MyRecyclerViewAdapter myRecyclerViewAdapter;
    ArrayList<FangRecord> fangRecords;
    SharedPreferences sharedPreferences;
    String myresult, myrank;
    RankRequestThread rankRequestThread;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablerank);
        ButterKnife.bind(this);
        NoHttp.initialize(this);


        Init();
        InitListener();

        niceSpinner.setSelectedIndex(4);

        loadingDialog = new LoadingDialog(GameTableRankActivity.this);
        loadingDialog.setMessage("排名中...");
        loadingDialog.setCancelable(false);

        myRecyclerViewAdapter = new MyRecyclerViewAdapter();

        rankRequestThread = new RankRequestThread(5);
        rankRequestThread.start();
        loadingDialog.show();

    }


    private void Init() {
        fangRecords = new ArrayList<>();
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);

        WidgetUtils.initRecyclerView(recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter();


    }

    private void InitListener() {
        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {

                loadingDialog = new LoadingDialog(GameTableRankActivity.this);
                loadingDialog.setMessage("排名中...");
                loadingDialog.setCancelable(false);

                myRecyclerViewAdapter = new MyRecyclerViewAdapter();

                rankRequestThread = new RankRequestThread(position + 1);
                rankRequestThread.start();
                loadingDialog.show();

            }
        });


    }

    private class RankRequestThread extends Thread {
        int game;
        String[] names;
        Integer[] results;

        RankRequestThread(int game) {
            this.game = game;
        }


        @Override
        public void run() {
            super.run();

            fangRecords = new ArrayList<>();
            StringRequest request = new StringRequest("http://121.36.30.71:8888/OidBox/apptablerank", RequestMethod.POST);
            request.set("game", String.valueOf(game));
            request.set("phone", sharedPreferences.getString("phone", "phone"));
            Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
            if (response.isSucceed()) {
                String s = response.get();
                JSONObject jsonObject = JSONObject.parseObject(s);

                JSONArray names_json = jsonObject.getJSONArray("name");
                names = new String[names_json.size()];
                names = names_json.toArray(names);


                JSONArray result_json = jsonObject.getJSONArray("result");
                results = new Integer[result_json.size()];
                results = result_json.toArray(results);

                myresult = jsonObject.getString("myresult");
                myrank = jsonObject.getString("myrank");

                for (int i = 0; i < names.length; i++) {
                    fangRecords.add(new FangRecord(i + 1, names[i], (float) results[i]));
                }

                for (int i = 3; i < fangRecords.size(); i++) {
                    myRecyclerViewAdapter.add(fangRecords.get(i));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();

                        tvsp_Myrecord.setCenterString(myrank);
                        tvsp_Myrecord.setCenterBottomString(Float.valueOf(myresult) / 10 + "");

                        if (fangRecords.size() > 0) {
                            tv_1record.setText(fangRecords.get(0).name + "\n" + fangRecords.get(0).record / 10 + "秒");
                        } else {
                            tv_1record.setText("虚位以待\n无");
                        }
                        if (fangRecords.size() > 1) {
                            tv_2record.setText(fangRecords.get(1).name + "\n" + fangRecords.get(1).record / 10 + "秒");
                        } else {
                            tv_2record.setText("虚位以待\n无");
                        }
                        if (fangRecords.size() > 2) {
                            tv_3record.setText(fangRecords.get(2).name + "\n" + fangRecords.get(2).record / 10 + "秒");
                        } else {
                            tv_3record.setText("虚位以待\n无");
                        }

                        recyclerView.setAdapter(myRecyclerViewAdapter);
                    }
                });

            } else {
                loadingDialog.dismiss();
                XToastUtils.error("数据获取失败！");
                Exception e = response.getException();
            }

        }
    }


    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return (1920f / 2.875f);
    }

    class MyRecyclerViewAdapter extends BaseRecyclerAdapter<FangRecord> {


        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.listitem_main3_tablerank;
        }

        @Override
        protected void bindData(@NonNull RecyclerViewHolder holder, int position, FangRecord item) {

            SuperTextView textView;
            textView = holder.findViewById(R.id.listitem_main3_tablerank);

            textView.setLeftString(item.rank + "");
            textView.setCenterString(item.name);
            textView.setRightString((item.record / 10) + "秒");
            textView.getLeftIconIV().setAdjustViewBounds(true);
        }


    }

}
