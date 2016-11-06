package cn.zhaoshuhao.cniaosshop.bean;

/**
 * Created by zsh06 on 2016/9/22.
 */

public class HomeCategory extends Category {

    private int imgBig;
    private int imgSmallTop;
    private int imgSmallBottom;

    public HomeCategory(int imgBig, int imgSmallTop, int imgSmallBottom) {
        this.imgBig = imgBig;
        this.imgSmallTop = imgSmallTop;
        this.imgSmallBottom = imgSmallBottom;
    }

    public HomeCategory(String name, int imgBig, int imgSmallTop, int imgSmallBottom) {
        super(name);
        this.imgBig = imgBig;
        this.imgSmallTop = imgSmallTop;
        this.imgSmallBottom = imgSmallBottom;
    }

    public HomeCategory(long id, String name, int imgBig, int imgSmallTop, int imgSmallBottom) {
        super(id, name);
        this.imgBig = imgBig;
        this.imgSmallTop = imgSmallTop;
        this.imgSmallBottom = imgSmallBottom;
    }

    public int getImgBig() {
        return imgBig;
    }

    public void setImgBig(int imgBig) {
        this.imgBig = imgBig;
    }

    public int getImgSmallTop() {
        return imgSmallTop;
    }

    public void setImgSmallTop(int imgSmallTop) {
        this.imgSmallTop = imgSmallTop;
    }

    public int getImgSmallBottom() {
        return imgSmallBottom;
    }

    public void setImgSmallBottom(int imgSmallBottom) {
        this.imgSmallBottom = imgSmallBottom;
    }
}
