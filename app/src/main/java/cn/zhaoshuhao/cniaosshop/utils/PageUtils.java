package cn.zhaoshuhao.cniaosshop.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zhaoshuhao.cniaosshop.R;
import cn.zhaoshuhao.cniaosshop.bean.Page;
import cn.zhaoshuhao.cniaosshop.http.OkHttpHelper;
import cn.zhaoshuhao.cniaosshop.http.SpotsCallback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zsh06
 * Created on 2016/10/25 8:38.
 */

public class PageUtils {
    //    private static volatile Builder sBuilder;
    private static volatile Builder sBuilder;
    private OkHttpHelper mHelper;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFRESH = 1;
    private static final int STATE_MORE = 2;

    private int state = STATE_NORMAL;

    private PageUtils() {
        mHelper = OkHttpHelper.getInstance();
        initRefreshLayout();
    }

    public static PageUtils.Builder newBuilder(Context context) {
//        if (sBuilder == null) {
//            synchronized (Builder.class) {
//                if (sBuilder == null) {
//                    sBuilder = new Builder(context);
//                } else {
//                    return sBuilder;
//                }
//            }
//        }
        sBuilder = new Builder(context);
        return sBuilder;
    }

    public void request() {
        if (sBuilder.requestType == RequestType.GET) {
            doGetReq();
        } else if (sBuilder.requestType == RequestType.POST) {
            doPostReq();
        }
    }

    private void initRefreshLayout() {
        sBuilder.refreshLayout.setLoadMore(sBuilder.canLoadMore);
        sBuilder.refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (sBuilder.pageIndex < sBuilder.pageSize) {
                    loadMoreData();
                } else {
                    Toast.makeText(sBuilder.context, R.string.no_have, Toast.LENGTH_SHORT).show();
                    sBuilder.refreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private static final String TAG = "PageUtils";

    private void doGetReq() {
        String url = buildUrl();
        Log.d(TAG, "doGetReq: " + url);
        mHelper.doGet(url, new RequestCallback(sBuilder.context, sBuilder.type));
    }

    private void doPostReq() {

    }

    private void refreshData() {
        /*
        * 早期的刷新数据为获取最新的数据到内容顶端
        *
        * 现在的下拉刷新为刷新数据集，上拉加载为获取新数据，方向相反
        * */
        sBuilder.pageIndex = 1;
        state = STATE_REFRESH;
        doGetReq();
    }

    private void loadMoreData() {
        sBuilder.pageIndex += 1;
        state = STATE_MORE;
        doGetReq();
    }

    private <T> void showData(List<T> data, int totalPage, int totalCount) {
        switch (state) {
            case STATE_NORMAL:
                if (sBuilder.onPageChangeListener != null) {
                    sBuilder.onPageChangeListener.load(data, totalPage, totalCount);
                }
                break;
            case STATE_REFRESH:
                if (sBuilder.onPageChangeListener != null) {
                    sBuilder.onPageChangeListener.refresh(data, totalPage, totalCount);
                }
                sBuilder.refreshLayout.finishRefresh();
                break;
            case STATE_MORE:
                if (sBuilder.onPageChangeListener != null) {
                    sBuilder.onPageChangeListener.loadMore(data, totalPage, totalCount);
                }
                sBuilder.refreshLayout.finishRefreshLoadMore();
                break;
        }
    }

    private String buildUrl() {
        return sBuilder.url + buildParams();
    }

    private String buildParams() {
        HashMap<String, Object> params = sBuilder.params;

        params.put("curPage", sBuilder.pageIndex);
        params.put("pageSize", sBuilder.pageSize);

        StringBuilder sb = new StringBuilder();
        sb.append("?");
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }

        String paramStr = sb.toString();
        if (paramStr.endsWith("&")) {
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }

        return paramStr;
    }

    public void setUrl(@NonNull String url) {
        sBuilder.url = url;
    }

    public void putParam(@NonNull String key, @NonNull Object value) {
        sBuilder.params.put(key, value);
    }

    public void setPageSize(@NonNull int pageSize) {
        sBuilder.pageSize = pageSize;
    }

    public void setPageIndex(@NonNull int pageIndex) {
        sBuilder.pageIndex = pageIndex;
    }

    public void setRefreshLayout(@NonNull MaterialRefreshLayout refreshLayout) {
        sBuilder.refreshLayout = refreshLayout;
    }

    public void setLoadMore(boolean canLoadMore) {
        sBuilder.canLoadMore = canLoadMore;
    }

    public void setOnPageChangeListener(OnPageListener onPageChangeListener) {
        sBuilder.onPageChangeListener = onPageChangeListener;
    }

    public void setType(Type type) {
        sBuilder.type = type;
    }

    public void setRequestType(RequestType requestType) {
        sBuilder.requestType = requestType;
    }

    public static class Builder {
        private static String url;
        private static HashMap<String, Object> params = new HashMap<>(5);
        private static MaterialRefreshLayout refreshLayout;

        private static OnPageListener onPageChangeListener;

        private static int pageSize = 10;
        private static int pageIndex = 1;
        private static int totalPage = 1;

        private static boolean canLoadMore = false;

        private static Context context;
        private static Type type;
        private static RequestType requestType = RequestType.GET;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setUrl(@NonNull String url) {
            this.url = url;
            return this;
        }

        public Builder putParam(@NonNull String key, @NonNull Object value) {
            params.put(key, value);
            return this;
        }

        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public Builder setRefreshLayout(@NonNull MaterialRefreshLayout refreshLayout) {
            this.refreshLayout = refreshLayout;
            return this;
        }

        public Builder setLoadMore(@NonNull boolean canLoadMore) {
            this.canLoadMore = canLoadMore;
            return this;
        }

        public Builder setOnPageChangeListener(OnPageListener onPageChangeListener) {
            this.onPageChangeListener = onPageChangeListener;
            return this;
        }

        public Builder setType(@NonNull Type type) {
            this.type = type;
            return this;
        }

        public Builder setRequestType(RequestType requestType) {
            this.requestType = requestType;
            return this;
        }

        public PageUtils build() {
            isInvalaid();
            return new PageUtils();
        }

        private void isInvalaid() {
            if (this.context == null)
                throw new RuntimeException("content can't be null");

            if (TextUtils.isEmpty(this.url))
                throw new RuntimeException("url can't be  null");

            if (this.refreshLayout == null)
                throw new RuntimeException("MaterialRefreshLayout can't be  null");
        }
    }

    public enum RequestType {
        GET, POST
    }

    public interface OnPageListener<T> {
        void load(List<T> data, int totalPage, int totalCount);

        void refresh(List<T> data, int totalPage, int totalCount);

        void loadMore(List<T> data, int totalPage, int totalCount);
    }

    class RequestCallback<T> extends SpotsCallback<Page<T>> {

        public RequestCallback(Context context, Type type) {
            super(context);
            super.mType = type;
        }

        @Override
        public void onSuccess(Response response, Page<T> page) {
            sBuilder.pageIndex = page.getCurrentPage();
            sBuilder.totalPage = page.getTotalPage();
            sBuilder.pageSize = page.getPageSize();
            showData(page.getList(), sBuilder.totalPage, page.getTotalCount());
        }

        @Override
        public void onError(Response response, int errCode, Exception e) {
            Toast.makeText(sBuilder.context, R.string.get_data_error, Toast.LENGTH_SHORT).show();
            finishStatus();
        }

        @Override
        public void onFailure(Request request, IOException e) {
            super.onFailure(request, e);
            finishStatus();
        }

        private void finishStatus() {
            if (STATE_REFRESH == state) {
                sBuilder.refreshLayout.finishRefresh();
            } else if (STATE_MORE == state) {
                sBuilder.refreshLayout.finishRefreshLoadMore();
            }
        }
    }
}
