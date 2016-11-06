package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.Iterator;
import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.ShoppingCart;
import cn.zhaoshuhao.cniaosshop.utils.CartProvider;
import cn.zhaoshuhao.cniaosshop.widget.NumberAddSubView;

/**
 * Created by zsh06
 * Created on 2016/10/21 10:24.
 */

public class CartAdapter extends SimpleAdapter<ShoppingCart> implements BaseAdapter.OnItemClickListener {
    private CartProvider mCartProvider;
    private OnDataUpdateListener mCallback;

    public CartAdapter(Context context, List<ShoppingCart> datas, OnDataUpdateListener callback) {
        super(context, datas, R.layout.template_cart);
        mCartProvider = CartProvider.getInstance(context);
        mCallback = callback;
        setOnItemClickListener(this);
    }

    @Override
    public void bindData(BaseViewHolder holder, final ShoppingCart cart) {
        holder.getTextView(R.id.id_tv_title).setText(cart.getName());
        holder.getTextView(R.id.id_tv_price).setText("￥" + cart.getPrice());
        SimpleDraweeView sdw = (SimpleDraweeView) holder.getView(R.id.id_sdw_image);
        sdw.setImageURI(cart.getImgUrl());

        CheckBox cb = (CheckBox) holder.getView(R.id.id_check_box);
        cb.setChecked(cart.isChecked());

        NumberAddSubView view = (NumberAddSubView) holder.getView(R.id.id_num_add_sub);
        view.setValue(cart.getCount());
        view.setMaxValue(50);
        view.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void OnButtonAddListener(View v, int value) {
                changeValue(value, cart);
            }

            @Override
            public void OnButtonSubListener(View v, int value) {
                changeValue(value, cart);
            }
        });
    }

    private void changeValue(int value, ShoppingCart cart) {
        cart.setCount(value);
        commit(cart);
        if (mCallback != null)
            mCallback.onUpdate();
    }

    private void commit(ShoppingCart cart) {
        mCartProvider.update(cart);
    }

    public float getTotalPrice() {
        float num = 0f;
        if (!isDataNull()) {
            for (ShoppingCart cart : mDatas) {
                if (cart.isChecked())
                    num += cart.getCount() * cart.getPrice();
            }
        }
        return num;
    }

    private boolean isDataNull() {
        return (mDatas == null && mDatas.size() == 0);
    }

    @Override
    public void onClick(View v, int position) {
        ShoppingCart cart = getItem(position);
        cart.setChecked(!cart.isChecked());
        notifyItemChanged(position);
        commit(cart);
        checkListen();
        mCallback.onUpdate();
    }

    private void checkListen() {
        int count = 0;
        int checkNum = 0;
        if (!isDataNull()) {
            count = getItemCount();
            for (ShoppingCart cart : mDatas) {
                if (!cart.isChecked()) {
                    mCallback.onChecked(false);
                    return;
                }
                checkNum += 1;
            }
            if (count == checkNum)
                mCallback.onChecked(true);
        }
    }

    public void changeItemCheckedStatus(boolean b) {
        if (!isDataNull()) {
            int i = 0;
            for (ShoppingCart cart : mDatas) {
                cart.setChecked(b);
                notifyItemChanged(i);
                commit(cart);
                i++;
            }
            mCallback.onUpdate();
        }
    }

    public void deleteCart() {
        if (!isDataNull()) {
            for (Iterator iterator = mDatas.iterator(); iterator.hasNext(); ) {
                ShoppingCart cart = (ShoppingCart) iterator.next();
                if (cart.isChecked()) {
                    int position = mDatas.indexOf(cart);
                    mCartProvider.delete(cart);
                    iterator.remove();
                    notifyItemRemoved(position);
                }
            }
        }
    }

    public interface OnDataUpdateListener {
        void onUpdate();

        void onChecked(boolean isChecked);
    }
}
