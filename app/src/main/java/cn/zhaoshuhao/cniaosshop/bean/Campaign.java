package cn.zhaoshuhao.cniaosshop.bean;

import java.io.Serializable;

/**
 * Created by zsh06
 * Created on 2016/10/13 17:15.
 */

public class Campaign implements Serializable {

    /**
     * id : 17
     * title : 手机专享
     * imgUrl : http://7mno4h.com2.z0.glb.qiniucdn.com/555c6c90Ncb4fe515.jpg
     */
    private int id;
    private String title;
    private String imgUrl;

    public Campaign(int id, String title, String imgUrl) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
