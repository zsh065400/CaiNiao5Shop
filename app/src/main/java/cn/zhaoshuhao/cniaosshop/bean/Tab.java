package cn.zhaoshuhao.cniaosshop.bean;

/**
 * Created by zsh06 on 2016/9/20.
 */

public class Tab {
    private int mTitle;
    private int icon;
    private Class fragment;

    public Tab(int title, int icon, Class fragment) {
        mTitle = title;
        this.icon = icon;
        this.fragment = fragment;
    }

    public int getTitle() {
        return mTitle;
    }

    public void setTitle(int title) {
        mTitle = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Class getFragment() {
        return fragment;
    }

    public void setFragment(Class fragment) {
        this.fragment = fragment;
    }
}

