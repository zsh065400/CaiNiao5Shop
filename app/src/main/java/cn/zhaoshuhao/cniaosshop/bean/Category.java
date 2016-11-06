package cn.zhaoshuhao.cniaosshop.bean;

/**
 * Created by zsh06 on 2016/9/22.
 */

public class Category extends BaseBean {
    private String name;

    public Category() {

    }

    public Category(String name) {
        this.name = name;
    }

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
