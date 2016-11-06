package cn.zhaoshuhao.cniaosshop.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.zhaoshuhao.cniaosshop.CainiaoApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zsh06
 * Created on 2016/10/12 16:33.
 */

public class OkHttpHelper {
    public static final int TOKEN_MISSING = 401;// token 丢失
    public static final int TOKEN_ERROR = 402; // token 错误
    public static final int TOKEN_EXPIRE = 403; // token 过期

    private static OkHttpClient mClient;
    private static Gson mGson;
    private static Handler mHandler;
    private static final OkHttpHelper sInstance = new OkHttpHelper();

    private OkHttpHelper() {
        mClient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS).build();
        mGson = new Gson();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpHelper getInstance() {
        return sInstance;
    }

    public void doGet(String url, BaseCallback callback) {
        doGet(url, null, callback);
    }

    public void doGet(String url, Map<String, String> params, BaseCallback callback) {
        Request request = buildGetRequest(url, params);
        doRequest(request, callback);
    }

    public void doPost(String url, Map<String, String> params, BaseCallback callback) {
        Request request = buildPostRequest(url, params);
        doRequest(request, callback);

    }

    private static final String TAG = "OkHttpHelper";

    public void doRequest(final Request request, final BaseCallback callback) {
        Log.d(TAG, "doRequest: " + request.url());
//        callback.onRequestBefor(request);
        callbackBeforeRequest(callback, request);
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callbackFailure(callback, request, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                callback.onResponse(response);
                callbackResponse(callback, response);
                if (response.isSuccessful()) {
                    String resultString = response.body().string();
                    if (callback.mType == String.class) {
                        callbackSuccess(callback, response, resultString);
                    } else {
                        try {
                            Object obj = mGson.fromJson(resultString, callback.mType);
                            callbackSuccess(callback, response, obj);
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            callbackError(callback, response, response.code(), e);
                        }
                    }
                } else if (response.code() == TOKEN_ERROR || response.code() == TOKEN_EXPIRE || response.code() == TOKEN_MISSING) {
                    callbackTokenError(callback, response);
                } else {
                    callbackError(callback, response, response.code(), null);
                }
            }
        });
    }

    private Request buildGetRequest(String url, Map<String, String> params) {
        return buildRequest(url, params, HttpMethodType.GET);
    }

    private Request buildPostRequest(String url, Map<String, String> params) {
        return buildRequest(url, params, HttpMethodType.POST);
    }

    private Request buildRequest(String url, Map<String, String> params, HttpMethodType methodType) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (methodType == HttpMethodType.GET) {
            url = buildUrlParams(url, params);
            builder.url(url);
            builder.get();
        } else if (methodType == HttpMethodType.POST) {
            RequestBody body = buildFormData(params);
            builder.post(body);
        }
        return builder.build();
    }

    private String buildUrlParams(String url, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>(1);
        }
        String token = CainiaoApplication.getInstance().getToken();
        if (!TextUtils.isEmpty(token))
            params.put("token", token);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        if (url.indexOf("?") > 0) {
            url = url + "&" + s;
        } else {
            url = url + "?" + s;
        }
        return url;
    }

    private RequestBody buildFormData(Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
            String token = CainiaoApplication.getInstance().getToken();
            if (!TextUtils.isEmpty(token)) {
                builder.add("token", token);
            }
        }
        return builder.build();
    }

    private void callbackResponse(final BaseCallback callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }

    private void callbackTokenError(final BaseCallback callback, final Response response) {
        if (callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onTokenError(response, response.code());
                }
            });
        }
    }

    private void callbackSuccess(final BaseCallback callback, final Response response, final Object object) {
        if (callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(response, object);
                }
            });
        }
    }

    private void callbackError(final BaseCallback callback, final Response response, final int errCode,
                               final Exception e) {
        if (callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(response, errCode, e);
                }
            });
        }
    }

    private void callbackBeforeRequest(final BaseCallback callback, final Request request) {
        if (callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onRequestBefor(request);
                }
            });
        }
    }

    private void callbackFailure(final BaseCallback callback, final Request request,
                                 final Exception e) {
        if (callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(request, (IOException) e);
                }
            });
        }
    }

    enum HttpMethodType {
        GET, POST
    }
}
