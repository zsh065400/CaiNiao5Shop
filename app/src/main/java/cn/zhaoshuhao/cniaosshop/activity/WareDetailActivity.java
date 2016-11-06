package cn.zhaoshuhao.cniaosshop.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Favorite;
import cn.zhaoshuhao.cniaosshop.bean.User;
import cn.zhaoshuhao.cniaosshop.bean.Wares;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import cn.zhaoshuhao.cniaosshop.utils.CartProvider;
import cn.zhaoshuhao.cniaosshop.utils.ToastUtils;
import cn.zhaoshuhao.cniaosshop.widget.CNToolbar;
import dmax.dialog.SpotsDialog;
import okhttp3.Response;

/**
 * Created by zsh06
 * Created on 2016/10/28 15:13.
 */

public class WareDetailActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.id_web_view)
    private WebView mWebView;

    @ViewInject(R.id.id_toolbar)
    private CNToolbar mToolbar;

    @ViewInject(R.id.id_refresh_layout)
    private MaterialRefreshLayout mRefreshLayout;
    private Wares mWare;
    private WebAppInterface mWebAppInterface;
    private CartProvider mCartProvider;

    private SpotsDialog mDialog;
    private boolean isCanRefresh;
    private OkHttpHelper mHelper = OkHttpHelper.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * 使用自动代码生成，防止低级错误（代码遗漏等）
        * */
        setContentView(R.layout.activity_ware_detail);
        x.view().inject(this);
        mWare = (Wares) getIntent().getSerializableExtra(Contants.API.WARE_ITEM);
        if (mWare == null) this.finish();
        mCartProvider = CartProvider.getInstance(this);
        mDialog = new SpotsDialog(this, "正在加载");
        mDialog.show();
        initToolbar();
        initWebView();
        initRefreshLayout();
        ShareSDK.initSDK(this);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                if (isCanRefresh) {
                    mDialog.show();
                    mWebView.loadUrl(Contants.API.WARE_DETAIL);
                }
            }
        });
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        /*使webview能够加载图片*/
        settings.setBlockNetworkImage(false);
        settings.setAppCacheEnabled(true);

        mWebView.loadUrl(Contants.API.WARE_DETAIL);
        mWebAppInterface = new WebAppInterface(this);
        mWebView.addJavascriptInterface(mWebAppInterface, "appInterface");
        mWebView.setWebViewClient(new WC());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK();
    }

    private void initToolbar() {
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.showRightBtn();
        mToolbar.getRightButton().setText("分享");
        mToolbar.setOnRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WareDetailActivity.this.showShare();
            }
        });
    }


    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    class WC extends WebViewClient {
        /*加载网页代码时，逐行回调该方法*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
            mWebAppInterface.showDetail();

            if (!isCanRefresh)
                isCanRefresh = true;
            else
                mRefreshLayout.finishRefresh();
        }
    }

    class WebAppInterface {
        private Context mContext;

        public WebAppInterface(Context context) {
            mContext = context;
        }

        /*
         * 若该方法调用js代码，则需要放入主线程中
         * 若是回调native代码，则不需要
         * */
        @JavascriptInterface
        public void showDetail() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:showDetail(" + mWare.getId() + ")");
                }
            });
        }

        @JavascriptInterface
        public void buy(int id) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addFavorite();
                }
            });
        }

        @JavascriptInterface
        public void addToCart(int id) {
            mCartProvider.put(mWare);
            Toast.makeText(mContext, "成功添加到购物车", Toast.LENGTH_SHORT).show();
        }
    }

    private void addFavorite() {
        User user = CainiaoApplication.getInstance().getUser();
        int userId = user.getId();

        Map<String, String> params = new HashMap<>();
        params.put("user_id", "" + userId);
        params.put("ware_id", "" + mWare.getId());

        mHelper.doPost(Contants.API.FAVORITE_CREATE, params, new SpotsCallback<List<Favorite>>
                (WareDetailActivity.this) {
            @Override
            public void onSuccess(Response response, List<Favorite> favorites) {
                ToastUtils.show(WareDetailActivity.this, "已添加到收藏夹");
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("菜鸟商城");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.cniao5.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mWare.getName());
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
        oks.setImageUrl(mWare.getImgUrl());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数,优先级大于setImageUrl
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(mWare.getDescription());
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(mWare.getName());
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.cniao5.com");
        // 启动分享GUI
        oks.show(this);
    }
}
