package cn.zhaoshuhao.cniaosshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import cn.zhaoshuhao.cniaosshop.MainActivity;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.activity.CreateOrderActivity;
import cn.zhaoshuhao.cniaosshop.adapter.CartAdapter;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerGridItemDecoration;
import cn.zhaoshuhao.cniaosshop.bean.ShoppingCart;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.utils.CartProvider;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;

import static android.view.View.GONE;


public class CartFragment extends BaseFragment implements CartAdapter.OnDataUpdateListener,
        View.OnClickListener {
    private static final int ACTION_EDIT = 1;
    private static final int ACTION_COMPLETE = 2;
    @ViewInject(R.id.id_recycle_view)
    private RecyclerView mRecyclerView;
    @ViewInject(R.id.id_cb_all)
    private CheckBox mCheckBox;
    @ViewInject(R.id.id_tv_total)
    private TextView mTvTotal;
    @ViewInject(R.id.id_btn_order)
    private Button mBtnOrder;
    @ViewInject(R.id.id_btn_delete)
    private Button mBtnDelete;

    private CartAdapter mAdapter;
    private CartProvider mProvider;

    private CNToolbar mToolbar;
    private OkHttpHelper mHelper = OkHttpHelper.getInstance();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        resetToolbar(context);
    }

    public void resetToolbar(final Context context) {
        if (context instanceof MainActivity) {
            MainActivity activity = (MainActivity) context;
            mToolbar = (CNToolbar) activity.findViewById(R.id.id_toolbar);
            mToolbar.hideSearchView();
            mToolbar.setTitle(R.string.tab_cart);
            mToolbar.setRightButtonText("编辑");
            mToolbar.showRightBtn();
            mToolbar.setOnRightButtonOnClickListener(this);
            mToolbar.getRightButton().setTag(ACTION_EDIT);
        }
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        mProvider = CartProvider.getInstance(getActivity());
        return view;
    }

    @Override
    public void init() {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showData();
    }

    @Event(R.id.id_btn_delete)
    private void deleteCart(View v) {
        mAdapter.deleteCart();
    }

    private static final String TAG = "CartFragment";

    @Event(R.id.id_btn_order)
    private void toOrder(View v) {
        if (checkCart()) return;
        Intent intent = new Intent(getActivity(), CreateOrderActivity.class);
        startActivity(intent, true);
//        mHelper.doGet(Contants.API.USER_DETAIL, new SpotsCallback<User>(getActivity()) {
//
//            @Override
//            public void onSuccess(Response response, User user) {
//                Log.d(TAG, "onSuccess: " + response.code());
//            }
//
//            @Override
//            public void onError(Response response, int errCode, Exception e) {
//                Log.d(TAG, "onError: " + response.code());
//            }
//        });
    }

    private boolean checkCart() {
        if (mAdapter.getDataSize() == 0) {
            ToastUtils.show(getActivity(), "先去购买点物品吧");
            return true;
        }
        if (mProvider.getCheckedWare().size() == 0) {
            ToastUtils.show(getActivity(), "先选择要结算的商品哦");
            return true;
        }
        return false;
    }

    @Event(type = View.OnClickListener.class, value = R.id.id_cb_all)
    private void changeAllStatus(View v) {
        if (mCheckBox.isChecked()) {
            mAdapter.changeItemCheckedStatus(true);
        } else {
            mAdapter.changeItemCheckedStatus(false);
        }
    }

    private void showData() {
        List<ShoppingCart> carts = mProvider.getAll();
        mAdapter = new CartAdapter(getContext(), carts, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(getContext()));
        setTotalPrice();
    }

    public void refreshData() {
        mAdapter.clearAll();
        List<ShoppingCart> carts = mProvider.getAll();
        mAdapter.addData(carts);
        setTotalPrice();
    }

    private void setTotalPrice() {
        mTvTotal.setText("合计 ￥" + mAdapter.getTotalPrice());
    }

    @Override
    public void onUpdate() {
        setTotalPrice();
    }

    @Override
    public void onChecked(boolean isChecked) {
        mCheckBox.setChecked(isChecked);
    }

    @Override
    public void onClick(View v) {
        int action = (int) v.getTag();
        if (ACTION_EDIT == action) {
            if (mAdapter.getDataSize() == 0) {
                ToastUtils.show(getActivity(), "先去购买点物品吧");
                return;
            }
            showDelControl();
        } else if (ACTION_COMPLETE == action) {
            hideDelControl();
        }
    }

    private void hideDelControl() {
        mToolbar.setRightButtonText("编辑");
        mTvTotal.setVisibility(View.VISIBLE);
        mBtnOrder.setVisibility(View.VISIBLE);
        mBtnDelete.setVisibility(GONE);
        mToolbar.getRightButton().setTag(ACTION_EDIT);
        mAdapter.changeItemCheckedStatus(true);
        mCheckBox.setChecked(true);
    }

    private void showDelControl() {
        mToolbar.setRightButtonText("完成");
        mTvTotal.setVisibility(GONE);
        mBtnOrder.setVisibility(GONE);
        mBtnDelete.setVisibility(View.VISIBLE);
        mToolbar.getRightButton().setTag(ACTION_COMPLETE);
        mAdapter.changeItemCheckedStatus(false);
        mCheckBox.setChecked(false);
    }
}
