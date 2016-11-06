package cn.zhaoshuhao.cniaosshop.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Favorite;
import cn.zhaoshuhao.cniaosshop.bean.Wares;

/**
 * Created by zsh06
 * Created on 2016/11/6 11:13.
 */

public class FavoritesAdapter extends SimpleAdapter<Favorite> {

    private OnFavoriteRemovedListener mListener;

    public FavoritesAdapter(Context context, List<Favorite> datas, OnFavoriteRemovedListener listener) {
        super(context, datas, R.layout.template_favorite);
        this.mListener = listener;
    }

    @Override
    public void bindData(BaseViewHolder holder, Favorite favorite) {
        final Wares wares = favorite.getWares();
        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.id_sdv);
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        holder.getTextView(R.id.id_tv_title).setText(wares.getName());
        holder.getTextView(R.id.id_tv_price).setText("￥ " + wares.getPrice());

        Button buttonRemove = holder.getButton(R.id.id_btn_remove);
        Button buttonLike = holder.getButton(R.id.id_btn_like);

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRemove(wares);
                }
            }
        });
    }

    public interface OnFavoriteRemovedListener {
        void onRemove(Wares ware);
    }
}
