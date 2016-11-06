/*
*User.java
*Created on 2015/11/18 下午3:44 by Ivan
*Copyright(c)2014 Guangzhou Onion Information Technology Co., Ltd.
*http://www.cniao5.com
*/
package cn.zhaoshuhao.cniaosshop.bean;

import java.io.Serializable;


public class OrderItem implements Serializable {

    private Long id;
    private Float amount;
    private Wares wares;

    public Wares getWares() {
        return wares;
    }

    public void setWares(Wares wares) {
        this.wares = wares;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

}
