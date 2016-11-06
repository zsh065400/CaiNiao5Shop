package cn.zhaoshuhao.cniaosshop.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.MainActivity;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.activity.AddressListActivity;
import cn.zhaoshuhao.cniaosshop.activity.LoginActivity;
import cn.zhaoshuhao.cniaosshop.activity.MyFavoriteActivity;
import cn.zhaoshuhao.cniaosshop.activity.MyOrderActivity;
import cn.zhaoshuhao.cniaosshop.bean.User;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends BaseFragment {

    @ViewInject(R.id.id_img_header)
    private CircleImageView mCivHeader;
    @ViewInject(R.id.id_tv_username)
    private TextView mTvUserName;
    @ViewInject(R.id.id_btn_logout)
    private Button mBtnLogout;

    @ViewInject(R.id.id_tv_my_address)
    private TextView mTvMyAddress;

    @ViewInject(R.id.id_tv_my_order)
    private TextView mTvMyOrder;

    @ViewInject(R.id.id_tv_my_favorites)
    private TextView mTvMyFavorites;

    private CNToolbar mToolbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        resetToolbar(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    public void resetToolbar(Context context) {
        if (context instanceof MainActivity) {
            MainActivity mActivity = (MainActivity) context;
            mToolbar = (CNToolbar) mActivity.findViewById(R.id.id_toolbar);
            mToolbar.hideSearchView();
            mToolbar.hideRightBtn();
            mToolbar.setTitle(R.string.tab_mine);
        }
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        return view;
    }

    @Override
    public void init() {

    }

    private void initView() {
        User user = CainiaoApplication.getInstance().getUser();
        showUser(user);
    }

    @Event(type = View.OnClickListener.class, value = {R.id.id_tv_username, R.id.id_img_header})
    private void toLogin(View v) {
        if (CainiaoApplication.getInstance().getUser() == null) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, Contants.REQUEST_CODE, false);
        }
    }

    @Event(type = View.OnClickListener.class, value = R.id.id_btn_logout)
    private void logout(View v) {
        CainiaoApplication.getInstance().clearUser();
        logoutUI();
        ToastUtils.show(getActivity(), "您已退出，可重新登录");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        User user = CainiaoApplication.getInstance().getUser();
        showUser(user);
    }

    private static final String TAG = "MineFragment";

    private void showUser(User user) {
        if (user != null) {
            loginUI(user);
        } else {
            logoutUI();
        }
    }

    private void loginUI(User user) {
        mTvUserName.setText(user.getUsername());
        Picasso.with(getActivity()).load(user.getLogo_url()).into(mCivHeader);
        mBtnLogout.setVisibility(View.VISIBLE);
    }

    private void logoutUI() {
        mTvUserName.setText(getString(R.string.to_login));
        mCivHeader.setImageDrawable(getResources().getDrawable(R.drawable.default_head, null));
        mBtnLogout.setVisibility(View.GONE);
    }

    @Event(R.id.id_tv_my_address)
    private void toMyAddress(View v) {
        startActivity(new Intent(getActivity(), AddressListActivity.class));
    }

    @Event(R.id.id_tv_my_order)
    private void toMyOrder(View v) {
        startActivity(new Intent(getActivity(), MyOrderActivity.class));
    }

    @Event(R.id.id_tv_my_favorites)
    private void toMyFavorites(View v) {
        startActivity(new Intent(getActivity(), MyFavoriteActivity.class));
    }
}
