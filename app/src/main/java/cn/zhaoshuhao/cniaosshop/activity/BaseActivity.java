package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.bean.User;

/**
 * Created by zsh06
 * Created on 2016/11/1 16:42.
 */

public class BaseActivity extends AppCompatActivity {

    public void startActivity(Intent intent, boolean isNeedLogin) {
        if (isNeedLogin) {
            User user = CainiaoApplication.getInstance().getUser();
            if (user != null) {
                super.startActivity(intent);
            } else {
                CainiaoApplication.getInstance().putIntent(intent);
                Intent i = new Intent(this, LoginActivity.class);
                super.startActivity(intent);
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
                Intent i = new Intent(this, LoginActivity.class);
                super.startActivity(intent);
            }
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }
}
