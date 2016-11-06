package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.MainActivity;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.User;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.msg.LoginRespMsg;
import cn.zhaoshuhao.cniaosshop.utils.CountTimerView;
import cn.zhaoshuhao.cniaosshop.utils.DESUtil;
import cn.zhaoshuhao.cniaosshop.utils.ManifestUtil;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import cn.zhaoshuhao.cniaosshop.widget.ClearEditText;
import dmax.dialog.SpotsDialog;
import okhttp3.Response;

/**
 * Created by zsh06
 * Created on 2016/11/2 11:05.
 */

public class RegSecondActivity extends BaseActivity {

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolBar;
    @ViewInject(R.id.id_tv_tip)
    private TextView mTvTip;
    @ViewInject(R.id.id_btn_resend)
    private Button mBtnResend;
    @ViewInject(R.id.id_et_code)
    private ClearEditText mEtCode;

    private String mPhone;
    private String mPwd;
    private String mCountryCode;

    private CountTimerView mCountTimeView;
    private SpotsDialog mDialog;
    private OkHttpHelper mHelper = OkHttpHelper.getInstance();
    private SmsEventHandler mEventHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_second);
        x.view().inject(this);

        getIntentData();
        initToolbar();

        String formatPhone = "+" + mCountryCode + " " + splitPhoneNumber(mPhone);
        String text = getString(R.string.smssdk_send_mobile_detail) + formatPhone;
        mTvTip.setText(Html.fromHtml(text));

        mCountTimeView = new CountTimerView(mBtnResend);
        mCountTimeView.start();

        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSecrect"));
        mEventHandler = new SmsEventHandler();
        SMSSDK.registerEventHandler(mEventHandler);

        mDialog = new SpotsDialog(this, "正在校验");
    }

    @Event(R.id.id_btn_resend)
    private void resend(View v) {
        SMSSDK.getVerificationCode("+" + mCountryCode, mPhone);
        mCountTimeView.start();

        mDialog.setMessage("重新获取验证码");
        mDialog.show();
    }

    /**
     * 反序格式化，sample：151-3362-5934
     *
     * @param phone
     * @return
     */
    @NonNull
    private String splitPhoneNumber(String phone) {
        StringBuilder sb = new StringBuilder(phone);
        sb.reverse();
        for (int i = 4; i < sb.length(); i += 5) {
            sb.insert(i, '-');
        }
        sb.reverse();
        return sb.toString();
    }


    private void initToolbar() {
        mToolBar.setOnRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
            }
        });
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitCode() {
        String code = mEtCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.show(this, "请输入验证码");
            return;
        }
        SMSSDK.submitVerificationCode(mCountryCode, mPhone, code);
        mDialog.show();
    }

    public void getIntentData() {
        Intent intent = getIntent();
        mPhone = intent.getStringExtra("phone");
        mPwd = intent.getStringExtra("pwd");
        mCountryCode = intent.getStringExtra("countryCode");
    }


    class SmsEventHandler extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //回调完成
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            doReg();
                            mDialog.setMessage("正在提交信息");
                        }
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            dissmissDialog();
                            ToastUtils.show(RegSecondActivity.this, "验证码已发送");
                            mEtCode.setText("");
                        }
                    } else {
                        // 根据服务器返回的网络错误，给toast提示
                        dissmissDialog();
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;
                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                ToastUtils.show(RegSecondActivity.this, des);
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }
                }


            });
        }
    }

    private void dissmissDialog() {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    private void doReg() {
        Map<String, String> params = new HashMap<>(2);
        params.put("phone", mPhone);
        params.put("pwd", DESUtil.encode(Contants.DES_KEY, mPwd));
        mHelper.doPost(Contants.API.REGISTER, params, new SpotsCallback<LoginRespMsg<User>>(this) {

            @Override
            public void onSuccess(Response response, LoginRespMsg<User> userLoginRespMsg) {
                dissmissDialog();
                CainiaoApplication app = CainiaoApplication.getInstance();
                app.putUser(userLoginRespMsg.getData(), userLoginRespMsg.getToken());
                startActivity(new Intent(RegSecondActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {
                dismissDialog();
            }

            @Override
            public void onTokenError(Response response, int code) {
                dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEventHandler);
    }
}
