package cn.zhaoshuhao.cniaosshop.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Campaign;
import cn.zhaoshuhao.cniaosshop.bean.HomeCampaign;

/**
 * Created by zsh06 on 2016/9/22.
 */

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.MyHolder> {
    private static int VIEW_TYPE_L = 0;
    private static int VIEW_TYPE_R = 1;

    private Context mContext;

    private List<HomeCampaign> mDatas;
    private LayoutInflater mInflater;

    private OnCampaignClickListener mListener;

    public void setOnCampaignClickListener(OnCampaignClickListener listener) {
        mListener = listener;
    }

    public HomeCategoryAdapter(List<HomeCampaign> datas, Context context) {
        mDatas = datas;
        mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_R) {
            return new MyHolder(mInflater.inflate(R.layout.template_home_cardview2, parent, false));
        } else {
            return new MyHolder(mInflater.inflate(R.layout.template_home_cardview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        HomeCampaign homeCampaign = mDatas.get(position);
        holder.tvTitle.setText(homeCampaign.getTitle());
        Picasso.with(mContext).load(homeCampaign.getCpOne().getImgUrl()).into(holder.ivBig);
        Picasso.with(mContext).load(homeCampaign.getCpTwo().getImgUrl()).into(holder.ivSmallTop);
        Picasso.with(mContext).load(homeCampaign.getCpThree().getImgUrl()).into(holder.ivSmallBottom);
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return VIEW_TYPE_R;
        } else {
            return VIEW_TYPE_L;
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        ImageView ivBig;
        ImageView ivSmallTop;
        ImageView ivSmallBottom;

        public MyHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.id_tv_title);
            ivBig = (ImageView) itemView.findViewById(R.id.id_iv_big);
            ivSmallTop = (ImageView) itemView.findViewById(R.id.id_iv_small_top);
            ivSmallBottom = (ImageView) itemView.findViewById(R.id.id_iv_small_bottom);

            ivBig.setOnClickListener(this);
            ivSmallBottom.setOnClickListener(this);
            ivSmallTop.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                anim(v);
        }

        private void anim(final View v) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationX", 0.0f, 360.f);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    HomeCampaign campaign = mDatas.get(getLayoutPosition());
                    switch (v.getId()) {
                        case R.id.id_iv_big:
                            mListener.onClick(v, campaign.getCpOne());
                            break;
                        case R.id.id_iv_small_top:
                            mListener.onClick(v, campaign.getCpTwo());
                            break;
                        case R.id.id_iv_small_bottom:
                            mListener.onClick(v, campaign.getCpThree());
                            break;
                    }
                }
            });
            animator.start();
        }
    }

    public interface OnCampaignClickListener {
        void onClick(View view, Campaign campaign);
    }


}
