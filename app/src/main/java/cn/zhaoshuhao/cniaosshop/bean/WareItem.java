package cn.zhaoshuhao.cniaosshop.bean;

/**
 * Created by zsh06
 * Created on 2016/11/3 15:59.
 */

public class WareItem {
    private Long ware_id;
    private int amount;

    public WareItem(Long ware_id, int amount) {
        this.ware_id = ware_id;
        this.amount = amount;
    }

    public Long getWare_id() {
        return ware_id;
    }

    public void setWare_id(Long ware_id) {
        this.ware_id = ware_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
