package com.hlt.jzwebsite.wxapi;


import com.hlt.jzwebsite.utils.java.SPUtils;

public class WechatInfoSpHelper {
    public static final String WECHAT_SP_NAME = "WECHAT_GJF";
    public static final String WECHAT_SP_USER_KEY = "user_key";
    public static final String WECHAT_OPENID = "WECHAT_OPENID";
    public static final String WECHAT_NICKNAME = "WECHAT_NICKNAME";
    public static final String WECHAT_SEX = "WECHAT_SEX";
    public static final String WECHAT_PROVINCE = "WECHAT_PROVINCE";
    public static final String WECHAT_CITY = "WECHAT_CITY";
    public static final String WECHAT_COUNTRY = "WECHAT_COUNTRY";
    public static final String WECHAT_HEADIMGURL = "WECHAT_HEADIMGURL";
    public static final String WECHAT_UNIONID = "WECHAT_UNIONID";
    public static final String WECHAT_SCOPE = "WECHAT_SCOPE";
    public static final String WECHAT_ACCESS_TOKEN = "WECHAT_ACCESS_TOKEN";
    public static final String WECHAT_REFRESH_TOKEN = "WECHAT_REFRESH_TOKEN";

    public static String getWechatAccessToken() {
        return getSharePreferences().getString(WECHAT_ACCESS_TOKEN, null);
    }

    public static String getWechatOpenid() {
        return getSharePreferences().getString(WECHAT_OPENID, null);
    }

    public static String getWechatRefreshToken() {
        return getSharePreferences().getString(WECHAT_REFRESH_TOKEN, null);
    }

    public static String getWechatUserNickname() {
        return getSharePreferences().getString(WECHAT_NICKNAME, "");
    }

    public static void saveWechatAccessInfoToSP(WXAccessTokenInfo tokenInfo) {
        // 防止传null
        if (tokenInfo.openid == null) {
            tokenInfo.openid = "";
        }

        if (tokenInfo.access_token == null) {
            tokenInfo.access_token = "";
        }
        if (tokenInfo.refresh_token == null) {
            tokenInfo.refresh_token = "1";
        }
        if (tokenInfo.scope == null) {
            tokenInfo.scope = "";
        }

//        SharedPreferences.Editor edit = getSharePreferences().edit();
        getSharePreferences().put(WECHAT_OPENID, tokenInfo.openid);
        getSharePreferences().put(WECHAT_ACCESS_TOKEN, tokenInfo.access_token);
        getSharePreferences().put(WECHAT_REFRESH_TOKEN, tokenInfo.refresh_token);
        getSharePreferences().put(WECHAT_SCOPE, tokenInfo.scope);
//        edit.commit();
    }

    public static void saveWechatUserInfoToSP(WXUserInfo info) {
//        SharedPreferences.Editor editor = getSharePreferences().edit();
        // 防止传null
        if (info.openid == null) {
            info.openid = "";
        }

        if (info.nickname == null) {
            info.nickname = "";
        }
        if (info.sex == null) {
            info.sex = "1";
        }
        if (info.province == null) {
            info.province = "";
        }
        if (info.city == null) {
            info.city = "";
        }
        if (info.country == null) {
            info.country = "";
        }
        if (info.headimgurl == null) {
            info.headimgurl = "";
        }
        if (info.unionid == null) {
            info.unionid = "";
        }

        getSharePreferences().put(WECHAT_OPENID, info.openid);
        getSharePreferences().put(WECHAT_NICKNAME, info.nickname);
        getSharePreferences().put(WECHAT_SEX, info.sex);
        getSharePreferences().put(WECHAT_PROVINCE, info.province);
        getSharePreferences().put(WECHAT_CITY, info.city);
        getSharePreferences().put(WECHAT_COUNTRY, info.country);
        getSharePreferences().put(WECHAT_HEADIMGURL, info.headimgurl);
        getSharePreferences().put(WECHAT_UNIONID, info.unionid);
//        editor.commit();
    }

    private static SPUtils getSharePreferences() {
        return SPUtils.getInstance(WECHAT_SP_NAME);
//        return AppApplication.getInstance().getSharedPreferences("WECHAT_GJF", Context.MODE_PRIVATE);
    }
}
