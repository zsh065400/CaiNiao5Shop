package cn.zhaoshuhao.cniaosshop.utils;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.zhaoshuhao.cniaosshop.bean.ShoppingCart;
import cn.zhaoshuhao.cniaosshop.bean.Wares;

/**
 * 购物车数据集
 * <p>
 * Created by zsh06
 * Created on 2016/10/20 16:40.
 */

// TODO: 2016/10/28 单例的使用
public class CartProvider {
    public static final String CART_JSON = "cart_json";

    private SparseArray<ShoppingCart> mDatas = null;
    private Context mContext;
    private static CartProvider sInstance;


    /*
    * 对包含读写操作功能（数据、网络、图片等等）的类，应该保证其是线程安全的
    * 并且保证其在内存中的实例为1
    *
    * 为了保证操作过程中数据的一致性
    * */
    public static CartProvider getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CartProvider.class) {
                if (sInstance == null) {
                    sInstance = new CartProvider(context);
                }
                return sInstance;
            }
        }
        return sInstance;
    }

    private CartProvider(Context context) {
        mDatas = new SparseArray<>();
        mContext = context;
        listToSparse();
    }

    public void put(ShoppingCart cart) {
        ShoppingCart t = mDatas.get(cart.getId().intValue());
        if (t != null) {
            t.setCount(t.getCount() + 1);
        } else {
            t = cart;
            t.setCount(1);
        }
        update(t);
    }

    public void put(Wares ware) {
        ShoppingCart cart = convertData(ware);
        put(cart);
    }

    public void update(ShoppingCart cart) {
        mDatas.put(cart.getId().intValue(), cart);
        commit();
    }

    public void delete(ShoppingCart cart) {
        mDatas.delete(cart.getId().intValue());
        commit();
    }

    public List<ShoppingCart> getAll() {
        return getDataFromLocal();
    }

    private void commit() {
        List<ShoppingCart> carts = sparseToList();
        PreferencesUtils.putString(mContext, CART_JSON, JSONUtil.toJSON(carts));
    }

    private List<ShoppingCart> sparseToList() {
        int size = mDatas.size();
        List<ShoppingCart> carts = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            carts.add(mDatas.valueAt(i));
        }
        return carts;
    }

    private void listToSparse() {
        List<ShoppingCart> carts = getDataFromLocal();
        if (carts != null && carts.size() > 0) {
            for (ShoppingCart cart : carts) {
                mDatas.put(cart.getId().intValue(), cart);
            }
        }
    }

    private List<ShoppingCart> getDataFromLocal() {
        String json = PreferencesUtils.getString(mContext, CART_JSON);
        List<ShoppingCart> carts = null;
        if (json != null) {
            /*
            * gson用法
            * */
            carts = JSONUtil.fromJson(json, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        } else {
            carts = new ArrayList<>();
        }
        return carts;
    }

    public List<ShoppingCart> getCheckedWare() {
        List<ShoppingCart> all = getDataFromLocal();
        if (all != null && all.size() == 0)
            return all;
        List<ShoppingCart> postOrder = new ArrayList<>();
        for (ShoppingCart cart : all) {
            if (cart.isChecked()) {
                postOrder.add(cart);
            }
        }
        return postOrder;
    }

    private ShoppingCart convertData(Wares wares) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(wares.getId());
        cart.setName(wares.getName());
        cart.setDescription(wares.getDescription());
        cart.setImgUrl(wares.getImgUrl());
        cart.setPrice(wares.getPrice());
        return cart;
    }

    public void clearAll() {
        mDatas.clear();
        /*
        * 一种直接清空，另一种覆盖
        * */
//        return PreferencesUtils.removeAll(mContext);
        commit();
    }

    // TODO: 2016/10/28 可实现功能
    /*
    * 设计数据缓存，内存---->磁盘
    * 读取与存储，优先内存
    * */

}
