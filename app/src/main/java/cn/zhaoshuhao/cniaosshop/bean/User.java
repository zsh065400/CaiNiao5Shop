package cn.zhaoshuhao.cniaosshop.bean;

import java.io.Serializable;

/**
 * Created by zsh06
 * Created on 2016/11/1 8:40.
 * <p>
 * 登陆后获得的用户信息
 */

public class User implements Serializable {

    /**
     * id : 269782
     * email : 3104517435@qq.com
     * logo_url : http://cniao5-imgs.qiniudn.com/FtH1rRWX01vfMEkR_mHfpOQlb_7z?imageMogr2/thumbnail/300x300/crop/!300x300a0a0
     * username : zsh065400
     * mobi : 15133625934
     */

    private int id;
    private String email;
    private String logo_url;
    private String username;
    private String mobi;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobi() {
        return mobi;
    }

    public void setMobi(String mobi) {
        this.mobi = mobi;
    }
}
