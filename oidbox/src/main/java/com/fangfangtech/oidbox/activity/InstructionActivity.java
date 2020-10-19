package com.fangfangtech.oidbox.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.fangfangtech.oidbox.R;
import com.xuexiang.xutil.app.IntentUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.autosize.internal.CustomAdapt;

public class InstructionActivity extends Activity implements CustomAdapt {

    @BindView(R.id.layout_instruction1)
    LinearLayout layout1;
    @BindView(R.id.layout_instruction2)
    LinearLayout layout2;
    @BindView(R.id.layout_instruction3)
    LinearLayout layout3;
    @BindView(R.id.layout_instruction4)
    LinearLayout layout4;
    @BindView(R.id.layout_instruction5)
    LinearLayout layout5;
    @BindView(R.id.layout_instruction6)
    LinearLayout layout6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(this);

        switch (IntentUtils.getIntExtra(getIntent(), "id", 0)) {
            case 1:
                layout1.setVisibility(View.VISIBLE);
                break;
            case 2:
                layout2.setVisibility(View.VISIBLE);
                break;
            case 3:
                layout3.setVisibility(View.VISIBLE);
                break;
            case 4:
                layout4.setVisibility(View.VISIBLE);
                break;
            case 5:
                layout5.setVisibility(View.VISIBLE);
                break;
            case 6:
                layout6.setVisibility(View.VISIBLE);
                break;

            default:
                break;
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
}
