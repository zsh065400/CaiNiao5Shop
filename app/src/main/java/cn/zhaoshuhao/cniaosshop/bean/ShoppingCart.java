package cn.zhaoshuhao.cniaosshop.bean;

import java.io.Serializable;

/**
 * Created by zsh06
 * Created on 2016/10/20 16:41.
 */

public class ShoppingCart extends Wares implements Serializable {
    private int count;
    private boolean isChecked = true;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
