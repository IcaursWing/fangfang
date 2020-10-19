package MyFragment;

import android.view.View;
import android.widget.ImageView;

import com.fangfangtech.oidbox.R;
import com.fangfangtech.oidbox.activity.GameTableRankActivity;
import com.fangfangtech.oidbox.activity.InstructionActivity;
import com.fangfangtech.oidbox.activity.RecordsActivity;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.app.ActivityUtils;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.internal.CancelAdapt;

@Page(name = "发现精彩")
public class Fragment_Main3 extends XPageFragment implements CancelAdapt {
    @BindView(R.id.supertextview_tablerank_main3)
    SuperTextView tvsp_tablerank;
    @BindView(R.id.supertextview_gamerecord_main3)
    SuperTextView tvsp_gamerecord;
    @BindView(R.id.imageview_instruction1_main3)
    RadiusImageView iv_Instruction1;
    @BindView(R.id.imageview_instruction2_main3)
    RadiusImageView iv_Instruction2;
    @BindView(R.id.imageview_instruction3_main3)
    RadiusImageView iv_Instruction3;
    @BindView(R.id.imageview_instruction4_main3)
    RadiusImageView iv_Instruction4;
    @BindView(R.id.imageview_instruction5_main3)
    RadiusImageView iv_Instruction5;
    @BindView(R.id.imageview_instruction6_main3)
    RadiusImageView iv_Instruction6;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main3;
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @Override
    protected void initViews() {
        tvsp_tablerank.getLeftIconIV().setAdjustViewBounds(true);
        tvsp_tablerank.getLeftIconIV().setScaleType(ImageView.ScaleType.FIT_CENTER);
        tvsp_gamerecord.getLeftIconIV().setAdjustViewBounds(true);
        tvsp_gamerecord.getLeftIconIV().setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    protected void initListeners() {

        tvsp_tablerank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(GameTableRankActivity.class);
            }
        });

        tvsp_gamerecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(RecordsActivity.class);
            }
        });

    }

    @OnClick({R.id.supertextview_tablerank_main3, R.id.supertextview_gamerecord_main3, R.id.imageview_instruction1_main3, R.id.imageview_instruction2_main3, R.id.imageview_instruction3_main3,
            R.id.imageview_instruction4_main3, R.id.imageview_instruction5_main3, R.id.imageview_instruction6_main3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.supertextview_tablerank_main3:
                break;
            case R.id.supertextview_gamerecord_main3:
                break;
            case R.id.imageview_instruction1_main3:
                ActivityUtils.startActivity(InstructionActivity.class, "id", 1);
                break;
            case R.id.imageview_instruction2_main3:
                ActivityUtils.startActivity(InstructionActivity.class, "id", 2);
                break;
            case R.id.imageview_instruction3_main3:
                ActivityUtils.startActivity(InstructionActivity.class, "id", 3);
                break;
            case R.id.imageview_instruction4_main3:
                ActivityUtils.startActivity(InstructionActivity.class, "id", 4);
                break;
            case R.id.imageview_instruction5_main3:
                ActivityUtils.startActivity(InstructionActivity.class, "id", 5);
                break;
            case R.id.imageview_instruction6_main3:
                ActivityUtils.startActivity(InstructionActivity.class, "id", 6);
                break;
        }
    }

}
