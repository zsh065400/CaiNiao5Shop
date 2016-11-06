package cn.zhaoshuhao.cniaosshop.utils;

import android.content.Context;
import android.text.TextUtils;

import cn.zhaoshuhao.cniaosshop.Contants;
import cn.zhaoshuhao.cniaosshop.bean.User;


/**
 * ProjectName:YayaShop
 * Autor： <a href="http://www.cniao5.com">菜鸟窝</a>
 * Description：
 * <p/>
 * 菜鸟窝是一个只专注做Android开发技能的在线学习平台，课程以实战项目为主，对课程与服务”吹毛求疵”般的要求，打造极致课程，是菜鸟窝不变的承诺
 */
public class UserLocalData {

    public static void putUser(Context context, User user) {
        String user_json = JSONUtil.toJSON(user);
        PreferencesUtils.putString(context, Contants.USER_JSON, user_json);
    }

    public static void putToken(Context context, String token) {
        PreferencesUtils.putString(context, Contants.TOKEN, token);
    }


    public static User getUser(Context context) {
        String user_json = PreferencesUtils.getString(context, Contants.USER_JSON);
        if (!TextUtils.isEmpty(user_json)) {
            return JSONUtil.fromJson(user_json, User.class);
        }
        return null;
    }

    public static String getToken(Context context) {
        return PreferencesUtils.getString(context, Contants.TOKEN);
    }

    public static void clearUser(Context context) {
        PreferencesUtils.remove(context, Contants.USER_JSON);
    }
    public static void clearToken(Context context) {
        PreferencesUtils.remove(context, Contants.TOKEN);
    }


}
