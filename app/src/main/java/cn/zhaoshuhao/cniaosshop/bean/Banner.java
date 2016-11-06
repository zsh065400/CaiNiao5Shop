package cn.zhaoshuhao.cniaosshop.bean;

/**
 * Created by zsh06
 * Created on 2016/10/12 14:48.
 */

public class Banner {

    /**
     * id : 1
     * name : 音箱狂欢
     * description : null
     * imgUrl : http://7mno4h.com2.z0.glb.qiniucdn.com/5608f3b5Nc8d90151.jpg
     * type : 1
     */

    private String name;
    private Object description;
    private String imgUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getDescription() {
        return description;
    }

    public void setDescription(Object description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
