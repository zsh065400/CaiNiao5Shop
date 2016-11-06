package cn.zhaoshuhao.cniaosshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;

import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.activity.WareDetailActivity;
import cn.zhaoshuhao.cniaosshop.adapter.BaseAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.HotWaresAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerItemDecoration;
import cn.zhaoshuhao.cniaosshop.bean.Page;
import cn.zhaoshuhao.cniaosshop.bean.Wares;
import cn.zhaoshuhao.cniaosshop.utils.PageUtils;


public class HotFragment extends BaseFragment {
    private HotWaresAdapter mAdapter;
    @ViewInject(R.id.id_recycle_view)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.id_refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;

    private List<Wares> mDatas;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot, container, false);
        return view;
    }

    @Override
    public void init() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAndShowData();
    }

    private void getAndShowData() {
        PageUtils pageUtils = PageUtils.newBuilder(getContext())
                .setUrl(Contants.API.HOT_WARES)
                .setLoadMore(true)
                .setOnPageChangeListener(new PageUtils.OnPageListener() {
                    @Override
                    public void load(final List data, int totalPage, int totalCount) {
                        mDatas = data;
                        loadFinished();
                    }

                    @Override
                    public void refresh(final List data, int totalPage, int totalCount) {
                        mDatas = data;
                        refreshFinished();

                    }

                    @Override
                    public void loadMore(final List data, int totalPage, int totalCount) {
                        mDatas = data;
                        loadMoreFinished();
                    }
                })
                .setPageSize(10)
                .setRefreshLayout(mRefreshLayout)
                .setType(new TypeToken<Page<Wares>>() {
                }.getType())
                .build();
        pageUtils.request();
    }

    private void loadFinished() {
        mAdapter = new HotWaresAdapter(getContext(), mDatas);
        mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Wares item = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), WareDetailActivity.class);
                intent.putExtra(Contants.API.WARE_ITEM, item);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void refreshFinished() {
        mAdapter.refreshData(mDatas);
        mRecyclerView.scrollToPosition(0);
    }

    private void loadMoreFinished() {
        mAdapter.loadMoreData(mDatas);
        mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
    }
}
