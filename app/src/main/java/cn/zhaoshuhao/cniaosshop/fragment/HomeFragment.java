package cn.zhaoshuhao.cniaosshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.io.IOException;
import java.util.List;

import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.activity.WareListActivity;
import cn.zhaoshuhao.cniaosshop.adapter.HomeCategoryAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.CardViewtemDecortion;
import cn.zhaoshuhao.cniaosshop.bean.Banner;
import cn.zhaoshuhao.cniaosshop.bean.Campaign;
import cn.zhaoshuhao.cniaosshop.bean.HomeCampaign;
import cn.zhaoshuhao.cniaosshop.http.BaseCallback;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zsh06 on 2016/9/20.
 */

public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";

    private SliderLayout mSliderLayout;
    //    private PagerIndicator mIndicator;
    private RecyclerView mRecyclerView;
    private HomeCategoryAdapter mAdapter;

    //    private Gson mGson = new Gson();
    private OkHttpHelper mHelper;
    private List<Banner> mBanners;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mSliderLayout = (SliderLayout) view.findViewById(R.id.id_slider);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recycle_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestImage();
        initRecycleView(view);
    }

    @Override
    public void init() {

    }

    private void requestImage() {
        mHelper = OkHttpHelper.getInstance();
        mHelper.doGet(Contants.API.BANNER_HOME, new SpotsCallback<List<Banner>>(getContext()) {
            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                Log.d(TAG, "onSuccess: " + banners.size());
                mBanners = banners;
                initSlider();
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {

            }
        });
    }

    private void initSlider() {
        if (mBanners != null) {
            for (Banner banner : mBanners) {
                TextSliderView tsv = new TextSliderView(this.getActivity());
                tsv.image(banner.getImgUrl());
                tsv.description(banner.getName());
                mSliderLayout.addSlider(tsv);
            }
        }

//        mSliderLayout.setCustomIndicator(mIndicator);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        mSliderLayout.setDuration(3000);

        mSliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: ");
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: ");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: ");
            }
        });
    }

    private void initRecycleView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.id_recycle_view);
        mHelper.doGet(Contants.API.CAMPAIGN_HOME, new BaseCallback<List<HomeCampaign>>() {
            @Override
            public void onRequestBefor(Request request) {

            }

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Response response, List<HomeCampaign> homeCampaigns) {
                initData(homeCampaigns);
            }


            @Override
            public void onError(Response response, int errCode, Exception e) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onTokenError(Response response, int code) {

            }
        });
    }

    public void initData(List<HomeCampaign> homeCampaigns) {
        mAdapter = new HomeCategoryAdapter(homeCampaigns, getContext());
        mAdapter.setOnCampaignClickListener(new HomeCategoryAdapter.OnCampaignClickListener() {
            @Override
            public void onClick(View v, Campaign campaign) {
                Intent intent = new Intent(getActivity(), WareListActivity.class);
                intent.putExtra(Contants.API.CAMPAIGN_ID, campaign.getId());
                startActivity(intent);
            }
        });
        mRecyclerView.addItemDecoration(new CardViewtemDecortion());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        if (mSliderLayout != null)
            mSliderLayout.stopAutoCycle();
        super.onStop();
    }
}
