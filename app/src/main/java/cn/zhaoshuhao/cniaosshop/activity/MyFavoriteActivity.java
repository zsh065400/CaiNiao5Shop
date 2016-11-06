package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.adapter.BaseAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.FavoritesAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerItemDecoration;
import cn.zhaoshuhao.cniaosshop.bean.Favorite;
import cn.zhaoshuhao.cniaosshop.bean.User;
import cn.zhaoshuhao.cniaosshop.bean.Wares;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.msg.BaseRespMsg;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import okhttp3.Response;

public class MyFavoriteActivity extends AppCompatActivity implements FavoritesAdapter.OnFavoriteRemovedListener {

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolbar;

    @ViewInject(R.id.id_recycle_view)
    private RecyclerView mRecyclerview;

    private FavoritesAdapter mAdapter;

    private OkHttpHelper mHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);
        x.view().inject(this);

        init();
        initToolbar();
    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init() {
        int userId = CainiaoApplication.getInstance().getUser().getId();
        Map<String, String> params = new HashMap<>();
        params.put("user_id", "" + userId);

        mHelper.doGet(Contants.API.FAVORITE_LIST, params, new SpotsCallback<List<Favorite>>(this) {
            @Override
            public void onSuccess(Response response, List<Favorite> data) {
                showData(data);
            }

            @Override
            public void onError(Response response, int errCode, Exception e) {

            }
        });
    }

    private void showData(final List<Favorite> datas) {
        if (mAdapter == null) {
            mAdapter = new FavoritesAdapter(this, datas, this);
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onClick(View v, int position) {
                    Intent intent = new Intent(MyFavoriteActivity.this, WareDetailActivity.class);
                    intent.putExtra(Contants.API.WARE_ITEM, datas.get(position).getWares());
                    startActivity(intent);
                }
            });
        } else {
            mAdapter.refreshData(datas);
        }
    }

    @Override
    public void onRemove(Wares ware) {
//      调用删除api并重新加载页面
        deleteFavorite(ware);
    }

    private void deleteFavorite(Wares wares) {
        User user = CainiaoApplication.getInstance().getUser();
        int userId = user.getId();

        Map<String, String> params = new HashMap<>();
        params.put("user_id", "" + userId);
        params.put("ware_id", "" + wares.getId());

        mHelper.doPost(Contants.API.FAVORITE_DELETE, params, new SpotsCallback<BaseRespMsg>
                (this) {
            @Override
            public void onSuccess(Response response, BaseRespMsg favorites) {
                ToastUtils.show(MyFavoriteActivity.this, "删除成功");
                init();
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }
}
