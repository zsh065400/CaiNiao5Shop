package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Category;

/**
 * Created by zsh06
 * Created on 2016/10/19 15:36.
 */

public class CategoryAdapter extends SimpleAdapter<Category> {
    public CategoryAdapter(Context context, List<Category> datas) {
        super(context, datas, R.layout.template_signle_text);
    }

    @Override
    public void bindData(BaseViewHolder holder, Category category) {
        holder.getTextView(R.id.id_text_view).setText(category.getName());
    }
}
