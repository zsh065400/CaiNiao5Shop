package cn.zhaoshuhao.cniaosshop.http;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.activity.LoginActivity;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import dmax.dialog.SpotsDialog;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zsh06
 * Created on 2016/10/13 16:08.
 */

public abstract class SpotsCallback<T> extends BaseCallback<T> {
    private SpotsDialog mDialog;
    private Context mContext;

    public SpotsCallback(Context context) {
        mContext = context;
        mDialog = new SpotsDialog(context, "正在加载");
    }

    public void showDialog() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public void setMessage(String s) {
        if (mDialog != null) {
            mDialog.setMessage(s);
        }
    }

    @Override
    public void onRequestBefor(Request request) {
        showDialog();
    }

    @Override
    public void onFailure(Request request, IOException e) {
        dismissDialog();
    }

    @Override
    public void onResponse(Response response) {
        dismissDialog();
    }

    @Override
    public void onTokenError(Response response, int code) {
        ToastUtils.show(mContext, R.string.token_error);
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
        CainiaoApplication.getInstance().clearUser();
    }
}
