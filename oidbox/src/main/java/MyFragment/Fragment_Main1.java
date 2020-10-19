package MyFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.fangfangtech.oidbox.R;
import com.fangfangtech.oidbox.activity.GameMusicActivity;
import com.fangfangtech.oidbox.activity.GameQuestionActivity;
import com.fangfangtech.oidbox.activity.GameTableActivity;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.base.XPageFragment;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xutil.tip.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import Adapter.ListAdapter_Main1;
import butterknife.BindView;
import me.jessyan.autosize.internal.CancelAdapt;
import widget.MyListView;
import widget.MyScrollView;
import widget.RadiusImageBanner;


@Page(name = "听小方游乐天地")
public class Fragment_Main1 extends XPageFragment implements CancelAdapt {


    @BindView(R.id.titlebar_main1)
    TitleBar titleBar;
    @BindView(R.id.banner_main1)
    RadiusImageBanner banner;
    @BindView(R.id.listview_main1)
    MyListView listView;
    @BindView(R.id.ScrollView_main1)
    MyScrollView scrollView;

    private List<BannerItem> bannerItems;
    private XUISimplePopup mMenuPopup;
    private static AdapterItem[] menuItems;
    private ListAdapter_Main1 listAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main1;
    }

    @Override
    protected void initViews() {

        menuItems = getMenuItems();
        mMenuPopup = new XUISimplePopup(getContext(), menuItems).create(new XUISimplePopup.OnPopupItemClickListener() {
            @Override
            public void onItemClick(XUISimpleAdapter adapter, AdapterItem item, int position) {
                ToastUtils.toast("设备" + (position + 1));
            }
        });
        titleBar.disableLeftView().addAction(new TitleBar.ImageAction(R.drawable.icon_add) {
            @Override
            public void performAction(View view) {
                mMenuPopup.show(view);
            }
        });


        bannerItems = getBannerItemList();
        banner.setSource(bannerItems).startScroll();


        listAdapter = new ListAdapter_Main1(getContext());
        listView.setAdapter(listAdapter);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListeners() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                listView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0:
                        Intent intent = new Intent(getContext(), GameTableActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        Intent intent1 = new Intent(getContext(), GameQuestionActivity.class);
                        startActivity(intent1);
                        break;

                    case 2:
                        Intent intent2 = new Intent(getContext(), GameMusicActivity.class);
                        startActivity(intent2);
                        break;

                    default:
                        ToastUtils.toast("item" + position);
                        break;
                }
            }
        });

    }


    @Override
    protected com.xuexiang.xpage.utils.TitleBar initTitleBar() {
        return null;
    }

    private static List<BannerItem> getBannerItemList() {
        String[] urls = new String[]{//640*360 360/640=0.5625
                "http://www.fangfangtech.com/AppSource/banner_main1.jpg", //1
                "http://www.fangfangtech.com/AppSource/banner_main2.jpg", //2
                "http://www.fangfangtech.com/AppSource/banner_main3.jpg"//3
        };
        String[] titles = new String[]{"图1", "图2", "图3"};

        ArrayList<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = titles[i];
            list.add(item);
        }
        return list;
    }


    private static AdapterItem[] getMenuItems() {
        menuItems = new AdapterItem[]{new AdapterItem("设备1"), new AdapterItem("设备2"), new AdapterItem("设备3"),};
        return menuItems;
    }


}
