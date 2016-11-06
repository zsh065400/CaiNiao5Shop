package cn.zhaoshuhao.cniaosshop.bean;

import java.io.Serializable;

/**
 * Created by zsh06 on 2016/9/22.
 */

public class BaseBean implements Serializable {
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
