package com.fangfangtech.oidbox.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.fangfangtech.oidbox.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.adapter.FragmentAdapter;
import com.xuexiang.xutil.common.ClickUtils;

import MyFragment.Fragment_Main1;
import MyFragment.Fragment_Main2;
import MyFragment.Fragment_Main3;
import MyFragment.Fragment_Main4;
import me.jessyan.autosize.internal.CancelAdapt;

public class MainActivity extends FragmentActivity implements CancelAdapt, ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener {
    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;

    String[] titles = new String[]{"听小方游乐天地", "设备列表", "发现精彩", "我的"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        //StatusBarUtils.translucent(MainActivity.this);
        setContentView(R.layout.activity_main);

        Init();
        InitViews();


    }

    private void Init() {
        viewPager = findViewById(R.id.ViewPager_main);
        viewPager.addOnPageChangeListener(this);
        bottomNavigationView = findViewById(R.id.BottomNavigation_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void InitViews() {
        FragmentAdapter<Fragment> adapter = new FragmentAdapter<>(getSupportFragmentManager());

        adapter.addFragment(new Fragment_Main1(), titles[0]);
        adapter.addFragment(new Fragment_Main2(), titles[1]);
        adapter.addFragment(new Fragment_Main3(), titles[2]);
        adapter.addFragment(new Fragment_Main4(), titles[3]);

        viewPager.setOffscreenPageLimit(titles.length - 1);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        MenuItem item = bottomNavigationView.getMenu().getItem(position);
        item.setChecked(true);
    }

    @Override
    public void onPageSelected(int position) {
//        MenuItem item = bottomNavigationView.getMenu().getItem(position);
//        item.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.item_main1:
                viewPager.setCurrentItem(0, false);
                return true;
            case R.id.item_main2:
                viewPager.setCurrentItem(1, false);
                return true;
            case R.id.item_main3:
                viewPager.setCurrentItem(2, false);
                return true;
            case R.id.item_main4:
                viewPager.setCurrentItem(3, false);
                return true;

            default:
                break;
        }


//        int index = CollectionUtils.arrayIndexOf(titles, menuItem.getTitle());
//        if (index != -1) {
//            viewPager.setCurrentItem(index, true);
//            return true;
//        }

        return false;
    }

    @Override
    public void onBackPressed() {
        ClickUtils.exitBy2Click();
    }
}
