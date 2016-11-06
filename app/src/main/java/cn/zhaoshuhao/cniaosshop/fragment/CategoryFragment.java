package cn.zhaoshuhao.cniaosshop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.List;

import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.adapter.BaseAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.CategoryAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.WaresAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerGridItemDecoration;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerItemDecoration;
import cn.zhaoshuhao.cniaosshop.adapter.layoutmanager.NewGridLayoutManager;
import cn.zhaoshuhao.cniaosshop.bean.Banner;
import cn.zhaoshuhao.cniaosshop.bean.Category;
import cn.zhaoshuhao.cniaosshop.bean.Page;
import cn.zhaoshuhao.cniaosshop.bean.Wares;
import cn.zhaoshuhao.cniaosshop.http.BaseCallback;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import okhttp3.Request;
import okhttp3.Response;


public class CategoryFragment extends BaseFragment {
    @ViewInject(R.id.id_recycle_view)
    private RecyclerView mCategoryRecycleView;
    @ViewInject(R.id.id_recycle_wares)
    private RecyclerView mWaresRecycleView;
    @ViewInject(R.id.id_slider)
    private SliderLayout mSliderLayout;
    @ViewInject(R.id.id_refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;

    private OkHttpHelper mHelper = OkHttpHelper.getInstance();
    private CategoryAdapter mCategoryAdapter;
    private WaresAdapter mWaresAdapter;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestCategoryData();
        requestBannerData();
        initRefreshLayout();
    }

    @Override
    public void init() {

    }

    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage < totalPage) {
                    loadMoreData();
                } else {
                    Toast.makeText(getActivity(), "已经没有了哦!", Toast.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private long categoryId;

    private void refreshData() {
        /*
        * 早期的刷新数据为获取最新的数据到内容顶端
        *
        * 现在的下拉刷新为刷新数据集，上拉加载为获取新数据，方向相反
        * */
        curPage = 1;
        state = STATE_REFRESH;
        requestWares(categoryId);
    }

    private void loadMoreData() {
        curPage++;
        state = STATE_MORE;
        requestWares(categoryId);
    }

    private void requestCategoryData() {
        mHelper.doGet(Contants.API.CATEGORY_LIST, new SpotsCallback<List<Category>>(getContext()) {
            @Override
            public void onSuccess(Response response, List<Category> categories) {
                showCategoryData(categories);
                if (categories != null && categories.size() > 0) {
                    categoryId = categories.get(0).getId();
                    requestWares(categoryId);
                }
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {

            }
        });
    }

    private void showCategoryData(List<Category> datas) {
        mCategoryAdapter = new CategoryAdapter(getContext(), datas);
        mCategoryRecycleView.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Category category = mCategoryAdapter.getItem(position);
                if (categoryId == category.getId()) {
                    return;
                }
                categoryId = category.getId();
                curPage = 1;
                totalPage = 1;
                state = STATE_NORMAL;
                requestWares(categoryId);
            }
        });
        mCategoryRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCategoryRecycleView.setItemAnimator(new DefaultItemAnimator());
        mCategoryRecycleView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void requestBannerData() {
        mHelper.doGet(Contants.API.BANNER_HOME, new SpotsCallback<List<Banner>>(getContext()) {

            @Override
            public void onSuccess(Response response, List<Banner> banners) {
                showSliderView(banners);
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {

            }
        });
    }

    private void showSliderView(List<Banner> banners) {
        if (banners != null) {
            for (Banner banner : banners) {
                DefaultSliderView tsv = new DefaultSliderView(this.getActivity());
                tsv.image(banner.getImgUrl());
//                tsv.description(banner.getName());
                mSliderLayout.addSlider(tsv);
            }
        }
//        mSliderLayout.setCustomIndicator(mIndicator);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateDown);
        mSliderLayout.setDuration(3000);

//        mSliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d(TAG, "onPageScrolled: ");
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                Log.d(TAG, "onPageSelected: ");
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.d(TAG, "onPageScrollStateChanged: ");
//            }
//        });
    }

    private int curPage = 1;
    private int pageSize = 10;
    private int totalPage = 1;
    private int state = STATE_NORMAL;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_MORE = 2;

    private void requestWares(long categoryId) {
        String url = Contants.API.WARES_LIST + "?"
                + "categoryId=" + categoryId + "&"
                + "curPage=" + curPage + "&"
                + "pageSize=" + pageSize;
        mHelper.doGet(url, new BaseCallback<Page<Wares>>() {
            @Override
            public void onRequestBefor(Request request) {

            }

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {
                curPage = waresPage.getCurrentPage();
                pageSize = waresPage.getPageSize();
                totalPage = waresPage.getTotalPage();
                showWaresData(waresPage.getList());
            }


            @Override
            public void onError(Response response, int errCode, Exception e) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onTokenError(Response response, int code) {
                /*token*/
            }
        });
    }

    private void showWaresData(List<Wares> wares) {
        switch (state) {
            case STATE_NORMAL:
                if (mWaresAdapter == null) {
                    mWaresAdapter = new WaresAdapter(getContext(), wares);
                    mWaresRecycleView.setAdapter(mWaresAdapter);
                    mWaresRecycleView.setLayoutManager(new NewGridLayoutManager(getContext(), 2));
                    mWaresRecycleView.setItemAnimator(new DefaultItemAnimator());
                    mWaresRecycleView.addItemDecoration(new DividerGridItemDecoration(getContext()));
                } else {
                    /*
                    * 加载新的item内容
                    * */
                    mWaresAdapter.refreshData(wares);
                    mWaresRecycleView.scrollToPosition(0);
                }

                break;
            case STATE_REFRESH:
                mWaresAdapter.refreshData(wares);
                mWaresRecycleView.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;
            case STATE_MORE:
                mWaresAdapter.addData(wares);
//                mWaresRecycleView.scrollToPosition(mCategoryAdapter.getDataSize());
                mRefreshLayout.finishRefreshLoadMore();
                break;
        }
    }

    @Override
    public void onStop() {
        if (mSliderLayout != null)
            mSliderLayout.stopAutoCycle();
        super.onStop();
    }
}



