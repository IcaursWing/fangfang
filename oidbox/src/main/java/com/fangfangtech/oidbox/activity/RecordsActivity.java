package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fangfangtech.oidbox.R;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;
import com.yanzhenjie.nohttp.rest.SyncRequestExecutor;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;

import Adapter.FangRecords;
import Adapter.SimpleRecyclerAdapter_Records;
import ExtraUtil.XToastUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CustomAdapt;

public class RecordsActivity extends Activity implements CustomAdapt {

    @BindView(R.id.titlebar_records)
    TitleBar titlebar;
    @BindView(R.id.bt_Records_Game)
    ButtonView bt_Game;
    @BindView(R.id.bt_Records_More)
    ButtonView bt_More;
    @BindView(R.id.recyclerview_records)
    SwipeRecyclerView recyclerview;
    @BindView(R.id.refreshlayout_records)
    SwipeRefreshLayout refreshlayout;

    SharedPreferences sharedPreferences;
    XUISimplePopup popup_game;
    XUISimplePopup popup_more;
    int mode, game;
    ShowRecordsThread showRecordsThread;
    private SimpleRecyclerAdapter_Records mAdapter;
    ArrayList<FangRecords> fangRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        ButterKnife.bind(this);

        NoHttp.initialize(this);


        Init();
        InitListener();
    }


    private void Init() {
        mode = 0;
        game = 5;
        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
        fangRecords = new ArrayList<>();

        WidgetUtils.initRecyclerView(recyclerview, 50, getResources().getColor(R.color.xui_config_color_light_blue));
        recyclerview.setAdapter(mAdapter = new SimpleRecyclerAdapter_Records());

    }

    private void InitListener() {
        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String[] strings1 = getResources().getStringArray(R.array.TableTime2);
        popup_game = new XUISimplePopup(this, strings1).create(800, new XUISimplePopup.OnPopupItemClickListener() {
            @Override
            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {
                game = position + 1;
                bt_Game.setText("舒尔特竞赛" + strings1[position]);

                refreshlayout.setRefreshing(true);
                showRecordsThread = new ShowRecordsThread();
                showRecordsThread.start();
            }
        });

        String[] strings2 = new String[]{"舒尔特竞赛", "趣味知识竞赛"};
        popup_more = new XUISimplePopup(this, strings2).create(800, new XUISimplePopup.OnPopupItemClickListener() {
            @Override
            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {
                mode = position;
                bt_Game.setText(strings2[position]);

                refreshlayout.setRefreshing(true);
                showRecordsThread = new ShowRecordsThread();
                showRecordsThread.start();
            }
        });


        bt_Game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_game.showDown(bt_Game);


            }
        });


        bt_More.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_more.showDown(bt_More);
            }
        });

        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRecordsThread = new ShowRecordsThread();
                showRecordsThread.start();
            }
        });
    }


    @Override
    public boolean isBaseOnWidth() {
        return false;
    }

    @Override
    public float getSizeInDp() {
        return (1920f / 2.875f);
    }

    private class ShowRecordsThread extends Thread {

        @Override
        public void run() {
            super.run();


            StringRequest request = new StringRequest("http://121.36.30.71:8888/OidBox/apprecord", RequestMethod.POST);
            request.set("game", String.valueOf(game));
            request.set("phone", sharedPreferences.getString("phone", "phone"));
            switch (mode) {
                case 0:
                    request.set("mode", "table");
                    break;

                case 1:
                    request.set("mode", "question");
                    break;

                default:
                    break;
            }

            Response<String> response = SyncRequestExecutor.INSTANCE.execute(request);
            if (response.isSucceed()) {

                String s = response.get();

                JSONObject jsonObject = JSONObject.parseObject(s);

                JSONArray results_json = jsonObject.getJSONArray("results");
                String[] results = new String[results_json.size()];
                results = results_json.toArray(results);

                JSONArray time_json = jsonObject.getJSONArray("times");
                String[] times = new String[time_json.size()];
                times = time_json.toArray(times);


                fangRecords = new ArrayList<>();
                for (int i = 0; i < results.length; i++) {
                    int j = results.length - i - 1;
                    fangRecords.add(new FangRecords((i + 1) + ".", times[j], results[j]));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mAdapter.refresh(fangRecords);
                        refreshlayout.setRefreshing(false);

                    }
                });

            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        XToastUtils.error("数据获取失败");
                    }
                });

                Exception e = response.getException();
            }


        }
    }

}
