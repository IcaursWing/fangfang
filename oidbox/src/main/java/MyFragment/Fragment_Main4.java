package MyFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fangfangtech.oidbox.R;
import com.fangfangtech.oidbox.activity.PersonInfoActivity;
import com.fangfangtech.oidbox.activity.SettingsActivity;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xpage.utils.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.app.ActivityUtils;

import butterknife.BindView;
import me.jessyan.autosize.internal.CancelAdapt;

@Page(name = "我的")
public class Fragment_Main4 extends XPageFragment implements CancelAdapt {


    @BindView(R.id.imageview_main4_title)
    ImageView imageviewMain4Title;
    @BindView(R.id.textView_username_main4)
    TextView tv_username;
    @BindView(R.id.textView_phone_main4)
    TextView tv_phone;
    @BindView(R.id.button_infodetail_main4)
    ImageButton bt_info;
    @BindView(R.id.supertextview_record_main4)
    SuperTextView tvsp_record;
    @BindView(R.id.linearlayout_info_main4)
    LinearLayout linearlayout_Info;
    @BindView(R.id.linearlayout_board_main4)
    LinearLayout xuilinearlayout_Board;
    @BindView(R.id.imagebutton_main4_setting)
    ImageButton bt_Setting;


    private SharedPreferences sharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main4;
    }

    @Override
    protected TitleBar initTitleBar() {
        return null;
    }

    @Override
    protected void initViews() {
        sharedPreferences = getContext().getSharedPreferences("account", Context.MODE_PRIVATE);
        tv_username.setText(sharedPreferences.getString("username", "默认昵称"));
        tv_phone.setText(sharedPreferences.getString("phone", "手机"));


        tvsp_record.getLeftIconIV().setAdjustViewBounds(true);
        tvsp_record.getLeftIconIV().setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @Override
    protected void initListeners() {


        bt_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(PersonInfoActivity.class);
            }
        });


        bt_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity(SettingsActivity.class);
            }
        });

    }


}
