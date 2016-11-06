package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.User;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.msg.LoginRespMsg;
import cn.zhaoshuhao.cniaosshop.utils.DESUtil;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import cn.zhaoshuhao.cniaosshop.widget.ClearEditText;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.id_et_phone)
    private ClearEditText mEtPhone;
    @ViewInject(R.id.id_et_password)
    private ClearEditText mEtPassWord;
    @ViewInject(R.id.id_btn_login)
    private Button mBtnLogin;
    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolbar;
    @ViewInject(R.id.id_tv_register)
    private TextView mTvRegister;

    private OkHttpHelper mHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        x.view().inject(this);
        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.onBackPressed();
            }
        });
    }

    @Event(type = View.OnClickListener.class, value = R.id.id_btn_login)
    private void login(View v) {
        String phone = mEtPhone.getText().toString();
        String pwd = mEtPassWord.getText().toString();
        Log.d(TAG, "login: " + phone + ":" + pwd);
        if (TextUtils.isEmpty(phone) && TextUtils.isEmpty(pwd)) {
            ToastUtils.show(this, "请填写内容");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show(this, "请输入密码");
        }
        Map<String, String> params = new HashMap<>(2);
        params.put("phone", phone);
        params.put("password", DESUtil.encode(Contants.DES_KEY, pwd));
        mHelper.doPost(Contants.API.LOGIN, params, new SpotsCallback<LoginRespMsg<User>>(this) {

            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {
                if (userLoginRespMsg == null) {
                    ToastUtils.show(LoginActivity.this, "用户名或密码错误，请重试");
                    return;
                }
                if (userLoginRespMsg.getData() == null) {
                    ToastUtils.show(LoginActivity.this, "用户名或密码错误，请重试");
                    Log.d(TAG, "onSuccess: " + userLoginRespMsg.getMessage());
                    return;
                }
                CainiaoApplication application = CainiaoApplication.getInstance();
                application.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());
                if (application.getIntent() == null) {
                    setResult(RESULT_OK);
                } else {
                    application.jumpToTargetActivity(LoginActivity.this);
                }
                finish();
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {
                ToastUtils.show(LoginActivity.this, "无网络访问，请检查");
            }
        });
    }

    @Event(R.id.id_tv_register)
    private void toRegister(View v) {
        startActivity(new Intent(this, RegActivity.class));
    }

    private static final String TAG = "LoginActivity";
}

