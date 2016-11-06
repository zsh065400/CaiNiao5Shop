package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.ShoppingCart;

/**
 * Created by zsh06
 * Created on 2016/11/3 10:51.
 */

public class WaresOrderAdapter extends SimpleAdapter<ShoppingCart> {
    public WaresOrderAdapter(Context context, List<ShoppingCart> datas) {
        super(context, datas, R.layout.template_order_wares);
    }

    @Override
    public void bindData(BaseViewHolder holder, ShoppingCart cart) {
        SimpleDraweeView sdv = (SimpleDraweeView) holder.getView(R.id.id_drawee_view);
        sdv.setImageURI(cart.getImgUrl());
        TextView tvTitle = holder.getTextView(R.id.id_tv_title);
        TextView tvPrice = holder.getTextView(R.id.id_tv_price);
        TextView tvTotal = holder.getTextView(R.id.id_tv_total);
        tvTitle.setText(cart.getName());
        tvPrice.setText("单价：" + cart.getPrice());
        tvTotal.setText("数量：" + cart.getCount());
    }

    public float getTotalPrice() {
        float sum = 0;
        if (isNull())
            return sum;
        for (ShoppingCart cart : mDatas) {
            sum += cart.getCount() * cart.getPrice();
        }
        return sum;
    }

    private boolean isNull() {
        return isNull(mDatas);
    }
}
