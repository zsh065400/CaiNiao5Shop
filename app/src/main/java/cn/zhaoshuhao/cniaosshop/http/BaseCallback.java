package cn.zhaoshuhao.cniaosshop.http;

import com.google.gson.internal.$Gson$Types;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zsh06
 * Created on 2016/10/12 16:36.
 */

public abstract class BaseCallback<T> {

    public Type mType;

    static Type getSuperClassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterizedType.getActualTypeArguments()[0]);
    }

    public BaseCallback() {
        mType = getSuperClassTypeParameter(getClass());
    }

    public abstract void onRequestBefor(Request request);

    public abstract void onFailure(Request request, IOException e);

    /**
     * 状态码大于200小于300时调用
     *
     * @param response
     * @param t
     */
    public abstract void onSuccess(Response response, T t);

    /**
     * 状态码400，403，404，500时调用此方法
     *
     * @param response
     * @param errCode
     * @param e
     */
    public abstract void onError(Response response, int errCode, Exception e);

    /**
     * 请求调用成功
     *
     * @param response
     */
    public abstract void onResponse(Response response);


    /**
     * 验证失败，状态码401，402，403时调用
     *
     * @param response
     * @param code
     */
    public abstract void onTokenError(Response response, int code);

}
