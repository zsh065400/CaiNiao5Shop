package cn.zhaoshuhao.cniaosshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.wallet.core.utils.JsonUtils;
import com.pingplusplus.android.Pingpp;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.adapter.WaresOrderAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.layoutmanager.FullyLinearLayoutManager;
import cn.zhaoshuhao.cniaosshop.bean.Charge;
import cn.zhaoshuhao.cniaosshop.bean.ShoppingCart;
import cn.zhaoshuhao.cniaosshop.bean.WareItem;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.msg.BaseRespMsg;
import cn.zhaoshuhao.cniaosshop.msg.CreateOrderRespMsg;
import cn.zhaoshuhao.cniaosshop.utils.CartProvider;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import okhttp3.Response;

public class CreateOrderActivity extends AppCompatActivity {
    private static final String TAG = "CreateOrderActivity";
    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

    private RelativeLayout mRlAddr;
    private TextView mTvContact;
    private TextView mTvAddr;

    @ViewInject(R.id.id_recycle_view)
    private RecyclerView mRvOrderList;

    @ViewInject(R.id.id_rl_alipay)
    private RelativeLayout mRlAlipay;
    @ViewInject(R.id.id_rl_wechat)
    private RelativeLayout mRlWechat;
    @ViewInject(R.id.id_rl_bd)
    private RelativeLayout mRlBd;

    @ViewInject(R.id.id_rb_alipay)
    private RadioButton mRbAlipay;
    @ViewInject(R.id.id_rb_webchat)
    private RadioButton mRbWechat;
    @ViewInject(R.id.id_rb_bd)
    private RadioButton mRbBd;

    @ViewInject(R.id.id_tv_total)
    private TextView mTvTotal;
    @ViewInject(R.id.id_btn_create_order)
    private Button mBtnCreateOrder;

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolbar;

    private CartProvider mCartProvider;
    private WaresOrderAdapter mAdapter;
    private OkHttpHelper mHelper = OkHttpHelper.getInstance();
    private String mOrderNum;
    private float mAmount;
    private String mPayChannel = CHANNEL_ALIPAY;

    private HashMap<String, RadioButton> mChannels = new HashMap<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        x.view().inject(this);
        showData();
        init();
        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init() {
        mChannels.put(CHANNEL_ALIPAY, mRbAlipay);
        mChannels.put(CHANNEL_WECHAT, mRbWechat);
        mChannels.put(CHANNEL_BFB, mRbBd);

        mAmount = mAdapter.getTotalPrice();
        mTvTotal.setText("应付款：" + mAmount);
    }

    private void showData() {
        mCartProvider = CartProvider.getInstance(this);
        List<ShoppingCart> all = mCartProvider.getCheckedWare();
        mAdapter = new WaresOrderAdapter(this, all);
        FullyLinearLayoutManager fullyLinearLayoutManager = new FullyLinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRvOrderList.setAdapter(mAdapter);
        mRvOrderList.setLayoutManager(fullyLinearLayoutManager);
    }

    @Event(R.id.id_btn_create_order)
    private void createNewOrder(View v) {
        postNewOrder();
    }

    private void postNewOrder() {
        List<ShoppingCart> datas = mAdapter.getDatas();
        ArrayList<WareItem> list = new ArrayList<>(datas.size());
        for (ShoppingCart cart : datas) {
            WareItem wareItem = new WareItem(cart.getId(), cart.getPrice().intValue());
            list.add(wareItem);
        }
        String item_json = JsonUtils.toJson(list);
        Map<String, String> params = new HashMap<>(5);
        params.put("user_id", CainiaoApplication.getInstance().getUser().getId() + "");
        params.put("item_json", item_json);
        params.put("pay_channel", mPayChannel);
        params.put("ammount", (int) mAmount + "");
        params.put("addr_id", 1 + "");

        mBtnCreateOrder.setEnabled(false);

        mHelper.doPost(Contants.API.CREATE_ORDER, params, new SpotsCallback<CreateOrderRespMsg>(this) {

            @Override
            public void onSuccess(Response response, CreateOrderRespMsg createOrderRespMsg) {
                mOrderNum = createOrderRespMsg.getData().getOrderNum();
                Charge charge = createOrderRespMsg.getData().getCharge();
                Log.d(TAG, "onSuccess: " + mOrderNum + "\n" + charge.toString());
                try {
                    Pingpp.createPayment(CreateOrderActivity.this, response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {
                mBtnCreateOrder.setEnabled(true);
                Log.d(TAG, "onError: " + errCode + ":" + response.message());
            }

            @Override
            public void onTokenError(Response response, int code) {
                mBtnCreateOrder.setEnabled(true);
            }
        });
    }

    private void changeOrderStatus(final int status) {
        Map<String, String> params = new HashMap<>(2);
        params.put("order_num", mOrderNum);
        params.put("status", status + "");
        mHelper.doPost(Contants.API.CHANGE_ORDER, params, new SpotsCallback<BaseRespMsg>(this) {

            @Override
            public void onSuccess(Response response, BaseRespMsg baseRespMsg) {
                mCartProvider.clearAll();
                toPayResultActivity(status);
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {
                toPayResultActivity(-1);
            }
        });
    }

    private void toPayResultActivity(int status) {
        Intent intent = new Intent(CreateOrderActivity.this, PayResultActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mBtnCreateOrder.setEnabled(true);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
                    changeOrderStatus(1);
                } else if (result.equals("fail")) {
                    changeOrderStatus(-1);
                } else if (result.equals("cancel")) {
                    changeOrderStatus(-2);
                } else {
                    changeOrderStatus(0);
                }
                // 处理返回值
                // "success" - 支付成功
                // "fail"    - 支付失败
                // "cancel"  - 取消支付
                // "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
            }
        }
    }

    public void selectPayChannel(String s) {
        mPayChannel = s;
        for (Map.Entry<String, RadioButton> entry : mChannels.entrySet()) {
            RadioButton rb = entry.getValue();
            if (entry.getKey().equals(s)) {
//                boolean checked = rb.isChecked();
                rb.setChecked(true);
            } else {
                rb.setChecked(false);
            }
        }
    }

    @Event(type = View.OnClickListener.class, value = {R.id.id_rl_alipay, R.id.id_rl_wechat, R.id.id_rl_bd
            , R.id.id_rb_alipay, R.id.id_rb_bd, R.id.id_rb_webchat})
    private void changePayChannel(View v) {
        selectPayChannel(v.getTag().toString());
    }
}
