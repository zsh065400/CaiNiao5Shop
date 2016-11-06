package cn.zhaoshuhao.cniaosshop.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.zhaoshuhao.cniaosshop.R;

public class PayResultActivity extends BaseActivity {

    @ViewInject(R.id.id_tv_status)
    private TextView mTvStatus;
    @ViewInject(R.id.id_iv_status)
    private ImageView mIvStatus;
    @ViewInject(R.id.id_btn_return)
    private Button mBtnReturn;

    private int mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        x.view().inject(this);

        mStatus = getIntent().getIntExtra("status", -1);
        showStatus();
    }

    private void showStatus() {
        if (mStatus == 1) {
//           默认状态
        } else if (mStatus == -1) {
            mIvStatus.setImageDrawable(getDrawable(R.mipmap.icon_cancel_128));
            mTvStatus.setText("支付失败");
        } else if (mStatus == -2) {
            mIvStatus.setImageDrawable(getDrawable(R.mipmap.icon_cancel_128));
            mTvStatus.setText("支付取消");
        }
    }

    @Event(R.id.id_btn_return)
    private void returnHome(View v) {
        onBackPressed();
    }
}
