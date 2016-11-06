package cn.zhaoshuhao.cniaosshop;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.zhaoshuhao.cniaosshop.activity.BaseActivity;
import cn.zhaoshuhao.cniaosshop.bean.Tab;
import cn.zhaoshuhao.cniaosshop.fragment.CartFragment;
import cn.zhaoshuhao.cniaosshop.fragment.CategoryFragment;
import cn.zhaoshuhao.cniaosshop.fragment.HomeFragment;
import cn.zhaoshuhao.cniaosshop.fragment.HotFragment;
import cn.zhaoshuhao.cniaosshop.fragment.MineFragment;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
//import cn.zhaoshuhao.cniaosshop.widget.FragmentTabHost;

public class MainActivity extends BaseActivity {

    private List<Tab> mTabs = new ArrayList<>(5);

    private LayoutInflater mInflater;

    private FragmentTabHost mTabHost;
    private CNToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (CNToolbar) findViewById(R.id.id_toolbar);
        initTab();
    }

    private CartFragment mCartFragment;
    private MineFragment mMineFragment;

    private void initTab() {
        Tab home = new Tab(R.string.tab_home, R.drawable.selector_icon_home, HomeFragment.class);
        Tab hot = new Tab(R.string.tab_hot, R.drawable.selector_icon_hot, HotFragment.class);
        Tab category = new Tab(R.string.tab_category, R.drawable.selector_icon_category, CategoryFragment.class);
        Tab cart = new Tab(R.string.tab_cart, R.drawable.selector_icon_cart, CartFragment.class);
        Tab mine = new Tab(R.string.tab_mine, R.drawable.selector_icon_mine, MineFragment.class);

        mTabs.add(home);
        mTabs.add(hot);
        mTabs.add(category);
        mTabs.add(cart);
        mTabs.add(mine);

        mInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.id_fla_tab_content);

        for (Tab tab : mTabs) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(buildIndicator(tab));
            mTabHost.addTab(tabSpec, tab.getFragment(), null);
        }

        mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabHost.setCurrentTab(0);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                String cart_fragment = MainActivity.this.getString(R.string.tab_cart);
                String mine_fragment = MainActivity.this.getString(R.string.tab_mine);
                if (tabId == cart_fragment) {
                    MainActivity.this.setCartFragmentUI(cart_fragment);
                } else if (tabId == mine_fragment) {
                    MainActivity.this.setMineFragmentUi(mine_fragment);
                } else {
                    MainActivity.this.restoreToolbar();
                }
            }
        });
    }

    private void setMineFragmentUi(String s) {
        if (mMineFragment == null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(s);
            if (fragment != null) {
                mMineFragment = (MineFragment) fragment;
                mMineFragment.resetToolbar(MainActivity.this);
            }
        } else {
            mMineFragment.resetToolbar(MainActivity.this);
        }
    }

    private void restoreToolbar() {
        mToolbar.showSearchView();
        mToolbar.hideRightBtn();
        mToolbar.setTitle("");
    }

    private void setCartFragmentUI(String s) {
        if (mCartFragment == null) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(s);
            if (fragment != null) {
                mCartFragment = (CartFragment) fragment;
                mCartFragment.refreshData();
                mCartFragment.resetToolbar(MainActivity.this);
            }
        } else {
            mCartFragment.refreshData();
            mCartFragment.resetToolbar(MainActivity.this);
        }
    }

    private View buildIndicator(Tab tab) {
        View view = mInflater.inflate(R.layout.tab_indicator, null);
        ImageView tabIv = (ImageView) view.findViewById(R.id.id_tab_iv);
        TextView tabTv = (TextView) view.findViewById(R.id.id_tab_tv);
        tabIv.setBackgroundResource(tab.getIcon());
        tabTv.setText(tab.getTitle());
        return view;
    }

//    public void requestFocus(View v) {
//        v.requestFocus();
//    }
}
