package cn.zhaoshuhao.cniaosshop.bean;

import java.io.Serializable;

/**
 * Created by zsh06
 * Created on 2016/11/6 11:13.
 */
public class Favorite implements Serializable {
    private Long id;
    private Long createTime;
    private Wares wares;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Wares getWares() {
        return wares;
    }

    public void setWares(Wares wares) {
        this.wares = wares;
    }

}
