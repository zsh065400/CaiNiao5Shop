package cn.zhaoshuhao.cniaosshop.temp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.adapter.decoration.DividerItemDecoration;

/**
 * Created by zsh06
 * Created on 2016/10/14 11:21.
 */

public class TestActivity extends AppCompatActivity {

    private final String URL_1 = "http://ww2.sinaimg.cn/orj480/ecc0b9dbjw1f1qy8upjolj22e836ou0y.jpg";
    private final String URL_2 = "http://bizhi.4493.com/uploads/151109/1-151109150HX21.jpg";

    private static final String TAG = "TestActivity";

    //    @ViewInject(R.id.id_image_view)
    private SimpleDraweeView mDraweeView;

    //    @ViewInject(R.id.id_rv_test)
    private RecyclerView mRecyclerView;

    //    @ViewInject(R.id.id_refresh)
    private SwipeRefreshLayout mRefreshLayout;
    //
//
    private TestAdapter mAdapter;

    @ViewInject(R.id.id_web_view)
    private WebView mWebView;
    @ViewInject(R.id.id_button)
    private Button mButton;

    private WebAppInterface mWebAppInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        x.view().inject(this);

        mWebView.getSettings().setJavaScriptEnabled(true);
        /*
        * google留下的漏洞坑
        * 还有recycle的坑，edittext的坑等
        * */
        mWebAppInterface = new WebAppInterface(this);
        mWebView.addJavascriptInterface(mWebAppInterface, "app");
        mWebView.loadUrl("file:///android_asset/index.html");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebAppInterface.showName("测试");
            }
        });
//        moreImage();
//        requestImage();
//        mDraweeView.setImageURI(uri);
//        refreshRecycleView();
//        initRefreshLayout();
    }


    class WebAppInterface {
        private Context mContext;

        public WebAppInterface(Context context) {
            mContext = context;
        }

        @JavascriptInterface
        public void sayHello(String name) {
            Toast.makeText(mContext, "name=" + name, Toast.LENGTH_SHORT).show();
        }

        public void showName(final String name) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:showName('" + name + "')");
                }
            });
        }
    }


    private void refreshRecycleView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        List<String> strs = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            strs.add("第" + (i + 1) + "条数据");
        }
        mAdapter = new TestAdapter(this, strs);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initRefreshLayout() {
        mRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mRefreshLayout.setDistanceToTriggerSync(100);
        mRefreshLayout.setProgressBackgroundColorSchemeColor(Color.BLACK);
        mRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 11; i++) {
                            mAdapter.addData(i, "新数据：" + System.currentTimeMillis());
                            mAdapter.notifyItemRangeChanged(0, 10);
                            mRecyclerView.scrollToPosition(0);
//                          刷新后调用停止刷新
                            mRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 3000);
            }
        });
    }

    private void requestImage() {
        Uri uri = Uri.parse("http://ww2.sinaimg.cn/large/7651812fgw1erzdpja3ekj21kw2dc4qp.jpg");
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setImageRequest(request)
//                .setOldController(mDraweeView.getController())
//                .build();
//        mDraweeView.setController(controller);
    }

    private void moreImage() {
        Uri uriLow = Uri.parse(URL_1);
        Uri uriHeight = Uri.parse(URL_2);

        ControllerListener<ImageInfo> listener = new BaseControllerListener<ImageInfo>() {
            /**
             * 加载完成回掉
             *
             * @param id
             * @param imageInfo
             * @param anim
             */
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                FLog.d("Final image received! " +
                                "Size %d x %d",
                        "Quality level %d, good enough: %s, full quality: %s",
                        imageInfo.getWidth(),
                        imageInfo.getHeight(),
                        qualityInfo.getQuality(),
                        qualityInfo.isOfGoodEnoughQuality(),
                        qualityInfo.isOfFullQuality());
                Log.d(TAG, "onFinalImageSet: ");
            }

            /**
             * 渐进加载回掉
             *
             * @param id
             * @param imageInfo
             */
            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                FLog.e(getClass(), "onIntermediateImageSet %s", id);
                Log.d(TAG, "onIntermediateImageSet: ");
            }

            /**
             * 加载错误回掉
             *
             * @param id
             * @param throwable
             */
            @Override
            public void onFailure(String id, Throwable throwable) {
                FLog.e(getClass(), throwable, "Error loading %s", id);
                Log.e(TAG, "onFailure: ");
            }
        };

//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setLowResImageRequest(ImageRequest.fromUri(uriLow))
//                .setImageRequest(ImageRequest.fromUri(uriHeight))
//                .setOldController(mDraweeView.getController())
//                .setControllerListener(listener)
//                .build();
//        mDraweeView.setController(controller);
    }
}
