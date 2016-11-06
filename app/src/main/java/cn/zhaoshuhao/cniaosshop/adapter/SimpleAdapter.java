package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by zsh06
 * Created on 2016/10/19 11:02.
 */

public abstract class SimpleAdapter<T> extends BaseAdapter<T, BaseViewHolder> {
    public SimpleAdapter(Context context, List<T> datas, int layoutResId) {
        super(context, datas, layoutResId);
    }
}
