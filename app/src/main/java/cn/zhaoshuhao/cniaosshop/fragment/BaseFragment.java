package cn.zhaoshuhao.cniaosshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.activity.LoginActivity;
import cn.zhaoshuhao.cniaosshop.bean.User;


/**
 * Created by zsh06
 * Created on 2016/11/1 16:37.
 */

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = createView(inflater, container, savedInstanceState);
        x.view().inject(this, view);
        initToolBar();
        init();
        return view;
    }

    public void initToolBar() {

    }

    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void init();

    public void startActivity(Intent intent, boolean isNeedLogin) {
        if (isNeedLogin) {
            User user = CainiaoApplication.getInstance().getUser();
            if (user != null) {
                super.startActivity(intent);
            } else {
                CainiaoApplication.getInstance().putIntent(intent);
                Intent i = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(i);
            }
        } else {
            super.startActivity(intent);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean isNeedLogin) {
        if (isNeedLogin) {
            User user = CainiaoApplication.getInstance().getUser();
            if (user != null) {
                super.startActivityForResult(intent, requestCode);
            } else {
                CainiaoApplication.getInstance().putIntent(intent);
                Intent i = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(intent);
            }
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }
}
