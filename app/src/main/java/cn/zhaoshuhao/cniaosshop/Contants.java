package cn.zhaoshuhao.cniaosshop;

/**
 * Created by zsh06
 * Created on 2016/10/13 17:19.
 */

public class Contants {

    public static final String USER_JSON = "user_json";
    public static final String TOKEN = "token";

    public static final String DES_KEY = "Cniao5_123456";

    public static final int REQUEST_CODE = 0;

    public static class API {
        public static final String CAMPAIGN_ID = "campaign_id";
        public static final String WARE_ITEM = "ware_item";

        public static final String BASE_URL = "http://112.124.22.238:8081/course_api/";
        public static final String CAMPAIGN_HOME = BASE_URL + "campaign/recommend";
        public static final String BANNER_HOME = BASE_URL + "banner/query?type=1";
        //      需要参数curPage和pageSize
        public static final String HOT_WARES = BASE_URL + "wares/hot";
        public static final String CATEGORY_LIST = BASE_URL + "category/list";
        public static final String WARES_LIST = BASE_URL + "wares/list";
        public static final String WARE_CAMPAIN_LIST = BASE_URL + "wares/campaign/list";
        public static final String WARE_DETAIL = BASE_URL + "wares/detail.html";
        public static final String LOGIN = BASE_URL + "auth/login";
        public static final String USER_DETAIL = BASE_URL + "user/get?id=1";
        public static final String REGISTER = BASE_URL + "auth/reg";
        public static final String CREATE_ORDER = BASE_URL + "order/create";
        public static final String CHANGE_ORDER = BASE_URL + "order/complete";
        public static final String ADDRESS_LIST = BASE_URL + "addr/list";
        public static final String ADDRESS_UPDATE = BASE_URL + "addr/update";
        public static final String ADDRESS_DELETE = BASE_URL + "addr/del";
        public static final String ADDRESS_CREATE = BASE_URL + "addr/create";

        public static final String ORDER_CREATE=BASE_URL +"/order/create";
        public static final String ORDER_COMPLEPE=BASE_URL +"/order/complete";
        public static final String ORDER_LIST=BASE_URL +"order/list";

        public static final String FAVORITE_LIST=BASE_URL +"favorite/list";
        public static final String FAVORITE_CREATE=BASE_URL +"favorite/create";
        public static final String FAVORITE_DELETE=BASE_URL +"favorite/del";
    }
}
