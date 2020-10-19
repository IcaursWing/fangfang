package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fangfangtech.oidbox.R;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CancelAdapt;

public class PersonInfoActivity extends Activity implements CancelAdapt {

    @BindView(R.id.titlebar_personinfo)
    TitleBar titlebar;
    @BindView(R.id.supertextview_photo_info)
    SuperTextView tvsp_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personinfo);
        ButterKnife.bind(this);
        Init();

    }

    private void Init() {

        titlebar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvsp_photo.getRightIconIV().setAdjustViewBounds(true);
        tvsp_photo.getRightIconIV().setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
}
