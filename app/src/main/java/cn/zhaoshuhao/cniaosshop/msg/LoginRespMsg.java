package cn.zhaoshuhao.cniaosshop.msg;

/**
 * Created by zsh06
 * Created on 2016/11/1 8:39.
 */

public class LoginRespMsg<T> extends BaseRespMsg {
    private String token;
    private T data;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
