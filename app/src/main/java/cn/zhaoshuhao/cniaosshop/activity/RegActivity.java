package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.utils.ManifestUtil;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import cn.zhaoshuhao.cniaosshop.widget.ClearEditText;
import dmax.dialog.SpotsDialog;

public class RegActivity extends BaseActivity {

    private static final String TAG = "RegActivity";
    // 默认使用中国区号
    private static final String DEFAULT_COUNTRY_ID = "42";

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolBar;
    @ViewInject(R.id.id_tv_country)
    private TextView mTvCountry;
    @ViewInject(R.id.id_tv_country_code)
    private TextView mTvCountryCode;
    @ViewInject(R.id.id_et_phone)
    private ClearEditText mEtPhone;
    @ViewInject(R.id.id_et_pwd)
    private ClearEditText mEtPwd;

    private SpotsDialog mDialog;

    private SmsEventHandler mEventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        x.view().inject(this);

        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSecrect"));

//        SMSSDK.initSDK(this, "b3bef732496f",
//                "adc829e311c60c0ca04913333cc8e5b4");

        mEventHandler = new SmsEventHandler();
        SMSSDK.registerEventHandler(mEventHandler);

        mDialog = new SpotsDialog(this, "正在发送验证码");

        initToolbar();

        /*
        * 根据国家码获得国家和区号
        * */
        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if (country != null) {
            mTvCountryCode.setText("+" + country[1]);
            mTvCountry.setText(country[0]);
        }
    }

    class SmsEventHandler extends EventHandler {
        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //回调完成
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        dissmissDialog();
                        //返回支持发送验证码的国家列表
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
//                            SMSSDK.getSupportedCountries();
                            onCountryListGot((ArrayList<HashMap<String, Object>>) data);
                            //获取验证码成功
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            // 请求验证码后，跳转到验证码填写页面
                            afterVerificationCodeRequested((Boolean) data);
                            //提交验证码成功
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        }
                    } else {
                        dissmissDialog();
                        // 根据服务器返回的网络错误，给toast提示
                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;
                            JSONObject object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                ToastUtils.show(RegActivity.this, des);
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
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void initToolbar() {
        mToolBar.setOnRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        String phone = mEtPhone.getText().toString().trim().replace("\\s*", "");
        String countryCode = mTvCountryCode.getText().toString().trim();

        checkPhoneNum(phone, countryCode);
        /*请求获得验证码*/
        SMSSDK.getVerificationCode(countryCode, phone);
        mDialog.show();
    }

    /**
     * 检查手机号格式
     *
     * @param phone
     * @param countryCode
     */
    private void checkPhoneNum(String phone, String countryCode) {
        if (countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1);
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            dissmissDialog();
            return;
        }
        if (countryCode.equals("86")) {
            if (phone.length() != 11) {
                ToastUtils.show(this, "手机号长度不正确");
                dissmissDialog();
                return;
            }
        }
        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern compile = Pattern.compile(rule);
        Matcher matcher = compile.matcher(phone);
        if (!matcher.matches()) {
            ToastUtils.show(this, "您输入的手机号码格式不正确");
            dissmissDialog();
            return;
        }
    }

    /**
     * 请求验证码成功后跳转
     *
     * @param data
     */
    private void afterVerificationCodeRequested(Boolean data) {
        String phone = mEtPhone.getText().toString().trim().replace("\\s*", "");
        String pwd = mEtPwd.getText().toString().trim();
        String countryCode = mTvCountryCode.getText().toString().trim();

        if (countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1);
        }
        Intent intent = new Intent(this, RegSecondActivity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pwd);
        intent.putExtra("countryCode", countryCode);
        startActivity(intent);
    }

    /**
     * 获得支持的国家列表
     *
     * @param data
     */
    private void onCountryListGot(ArrayList<HashMap<String, Object>> data) {
        for (HashMap<String, Object> country : data) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }
            Log.d(TAG, "onCountryListGot: " + code + ":" + rule);
        }
    }

    private String[] getCurrentCountry() {
        String mcc = getMCC();
        String[] country = null;
        if (!TextUtils.isEmpty(mcc)) {
            country = SMSSDK.getCountryByMCC(mcc);
        }

        if (country == null) {
            Log.w("SMSSDK", "no country found by MCC: " + mcc);
            country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        }
        return country;
    }

    private String getMCC() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 返回当前手机注册的 网络运营商 所在国家的MCC+MNC. 如果没注册到网络就为空.
        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            return networkOperator;
        }

        // 返回 SIM卡运营商 所在国家的MCC+MNC. 5位或6位. 如果没有SIM卡返回空
        return tm.getSimOperator();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEventHandler);
    }
}
