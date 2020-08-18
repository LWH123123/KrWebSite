package com.hlt.jzwebsite.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hlt.jzwebsite.App;
import com.hlt.jzwebsite.R;
import com.hlt.jzwebsite.utils.Utils;
import com.hlt.jzwebsite.utils.java.SPUtils;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tech.jianyue.auth.AuthActivity;

public class WXEntryActivity extends AuthActivity implements IWXAPIEventHandler {
    private static final String TAG = WXEntryActivity.class.getSimpleName();
    private static final String WEIXIN_ACCESS_TOKEN_KEY = "wx_access_token_key";
    private static final String WEIXIN_OPENID_KEY = "wx_openid_key";
    private static final String WEIXIN_REFRESH_TOKEN_KEY = "wx_refresh_token_key";

    private Gson mGson;
    private static ApiCallback<WXUserInfo> loginCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 微信事件回调接口注册
        App.sApi.handleIntent(getIntent(), this);
        mGson = new Gson();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGson = null;
    }

    /**
     * 微信组件注册初始化
     *
     * @param context       上下文
     * @param weixin_app_id appid
     * @return 微信组件api对象
     */
    public static IWXAPI initWeiXin(Context context, @NonNull String weixin_app_id) {
        if (TextUtils.isEmpty(weixin_app_id)) {
            Toast.makeText(context.getApplicationContext(), "app_id 不能为空", Toast.LENGTH_SHORT).show();
        }
        IWXAPI api = WXAPIFactory.createWXAPI(context, weixin_app_id, true);
        api.registerApp(weixin_app_id);
        return api;
    }

    /**
     * 登录微信
     *
     * @param api 微信服务api
     */
    public static void loginWeixin(Context context, IWXAPI api) {
        loginWeixin(context, api, null);
    }

    public static void loginWeixin(Context context, IWXAPI api, ApiCallback<WXUserInfo> callback) {
        Logger.d("==WX==");
        // 判断是否安装了微信客户端
        if (!api.isWXAppInstalled()) {
            Toast.makeText(context.getApplicationContext(), "您还未安装微信客户端！", Toast.LENGTH_SHORT).show();
            return;
        }
        //登录页面回调
        loginCallback = callback;
        // 发送授权登录信息，来获取code
        SendAuth.Req req = new SendAuth.Req();
        // 应用的作用域，获取个人信息
        req.scope = "snsapi_userinfo";
        /**
         * 用于保持请求和回调的状态，授权请求后原样带回给第三方
         * 为了防止csrf攻击（跨站请求伪造攻击），后期改为随机数加session来校验
         */
        req.state = "app_wechat";
        boolean b = api.sendReq(req);

        Logger.d("微信登陆发送的请求结果:", "===>>>>>" + b);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        Logger.d("=======>>>>>>-------", req.getType() + "===");
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        String result = "@=";
        Log.d(TAG, "BaseResp：errCode－－－》》》" + String.valueOf(resp.errCode));
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:    // 用户同意
                switch (resp.getType()) {
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX: // 分享给好友、朋友圈
                        //现在取消分享也会返回成功
                        result = "分享成功";
                        showMessage(result);
                    case ConstantsAPI.COMMAND_PAY_BY_WX:
                        result = "支付成功";
                        finish();
                        break;
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        // 获取code
                        String code = ((SendAuth.Resp) resp).code;
                        // 从手机本地获取存储的授权口令信息，判断是否存在access_token，不存在请求获取，存在就判断是否过期
                        String accessToken = WechatInfoSpHelper.getWechatAccessToken();
                        String openid = WechatInfoSpHelper.getWechatOpenid();
                        if (!TextUtils.isEmpty(accessToken)) {
                            // 有access_token，判断是否过期有效
                            isExpireAccessToken(accessToken, openid);
                        } else {
                            // 没有access_token 通过code获取授权口令access_token
                            getAccessToken(code);
                        }
                        result = "登录成功";
                        Logger.d("微信授权登录 onResp --->>" + result);
//                        showMessage(result);
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "用户取消";
                showMessage(result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "用户被拒绝";
                showMessage(result);
                finish();
                break;
            default:
                result = "分享返回";
                showMessage(result);
                finish();
                break;
        }
    }

    /**
     * 获取授权口令
     */
    private void getAccessToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + WxConst.WEIXIN_APP_ID +
                "&secret=" + WxConst.WEIXIN_APP_SECRET +
                "&code=" + code +
                "&grant_type=authorization_code";
        // 网络请求获取access_token
        httpRequest(url, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Logger.d("微信响应的数据=========>>>>>>>>>" + response);
                // 判断是否获取成功，成功则去获取用户信息，否则提示失败
                processGetAccessTokenResult(response);
            }

            @Override
            public void onError(int errorCode, final String errorMsg) {
                showMessage(errorCode, "wx错误信息: " + errorMsg);
            }

            @Override
            public void onFailure(IOException e) {
                showMessage(e, "wx登录失败");
            }

            @Override
            public void onPayError(String res) {

            }
        });
    }


    /**
     * 处理获取的授权信息结果
     *
     * @param response 授权信息结果
     */
    private void processGetAccessTokenResult(String response) {
        // 验证获取授权口令返回的信息是否成功
        if (validateSuccess(response)) {
            // 使用Gson解析返回的授权口令信息
            WXAccessTokenInfo tokenInfo = mGson.fromJson(response, WXAccessTokenInfo.class);
            // 保存信息到手机本地
            WechatInfoSpHelper.saveWechatAccessInfoToSP(tokenInfo);
            // 获取用户信息
            getUserInfo(tokenInfo.getAccess_token(), tokenInfo.getOpenid());
        } else {
            // 授权口令获取失败，解析返回错误信息
            WXErrorInfo wxErrorInfo = mGson.fromJson(response, WXErrorInfo.class);
            // 提示错误信息
            showMessage(0, "wx错误信息: " + wxErrorInfo.errmsg);
        }
    }

    private boolean validateSuccess(String response) {
        Log.i("TAG", "validateSuccess: " + response);
        if (response.contains("errcode") && response.contains("errmsg")) {
            int code = 66666;
            try {
                JSONObject obj = new JSONObject(response);
                code = obj.getInt("errcode");

                if (code != 66666) {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return true;
            }
        }

        return true;
    }

    /**
     * 判断accesstoken是过期
     *
     * @param accessToken token
     * @param openid      授权用户唯一标识
     */
    private void isExpireAccessToken(final String accessToken, final String openid) {
        String url = "https://api.weixin.qq.com/sns/auth?" +
                "access_token=" + accessToken +
                "&openid=" + openid;
        httpRequest(url, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Logger.d("微信授权登录 返回的数据:=======>>>>>>  " + response);
                if (validateSuccess(response)) {
                    // accessToken没有过期，获取用户信息
                    getUserInfo(accessToken, openid);
                } else {
                    // 过期了，使用refresh_token来刷新accesstoken
                    refreshAccessToken();
                }
            }

            @Override
            public void onError(int errorCode, final String errorMsg) {
                showMessage(errorCode, "wx错误信息: " + errorMsg);
            }

            @Override
            public void onFailure(IOException e) {
                showMessage(e, "wx登录失败");
            }

            @Override
            public void onPayError(String res) {
                Logger.d(res);
            }
        });
    }

    /**
     * 刷新获取新的access_token
     */
    private void refreshAccessToken() {
        // 从本地获取以存储的refresh_token
//        final String refreshToken = (String) ShareUtils.getValue(this, WEIXIN_REFRESH_TOKEN_KEY, "");
        final String refreshToken = WechatInfoSpHelper.getWechatRefreshToken();
        if (TextUtils.isEmpty(refreshToken)) {
            return;
        }
        // 拼装刷新access_token的url请求地址
        String url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                "appid=" + WxConst.WEIXIN_APP_ID +
                "&grant_type=refresh_token" +
                "&refresh_token=" + refreshToken;
        // 请求执行
        httpRequest(url, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                Logger.e("refreshAccessToken: " + response);
                // 判断是否获取成功，成功则去获取用户信息，否则提示失败
                processGetAccessTokenResult(response);
            }

            @Override
            public void onError(int errorCode, final String errorMsg) {
                showMessage(errorCode, "re wx错误信息: " + errorMsg);
                // 重新请求授权
                loginWeixin(WXEntryActivity.this.getApplicationContext(), App.sApi);
            }

            @Override
            public void onFailure(IOException e) {
                showMessage(e, "re wx登录失败");
                // 重新请求授权
                loginWeixin(WXEntryActivity.this.getApplicationContext(), App.sApi);
            }


            @Override
            public void onPayError(String res) {
                Logger.d(res);
            }
        });
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(String access_token, String openid) {
        String url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=" + access_token +
                "&openid=" + openid;
        httpRequest(url, new ApiCallback<String>() {
            @Override
            public void onSuccess(String response) {
                WechatInfoSpHelper.saveWechatAccessInfoToSP(new WXAccessTokenInfo());
                Logger.d("我保存的微信的信息", "=======>>>>>>>   " + response);
                // 解析获取的用户信息
                WXUserInfo userInfo = mGson.fromJson(response, WXUserInfo.class);
                Logger.e("wx用户信息获取结果：" + response);
                SPUtils.getInstance(WechatInfoSpHelper.WECHAT_SP_NAME).put(WechatInfoSpHelper.WECHAT_SP_USER_KEY,response);
                // TODO: 2020/2/22
//                Injection.provideDemoRepository().saveWXUserInfo(response);
                if (loginCallback != null)
                    loginCallback.onSuccess(userInfo);
                loginCallback = null;
                finish();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                showMessage(errorCode, "错误信息: " + errorMsg);
            }

            @Override
            public void onFailure(IOException e) {
                showMessage(e, "获取用户信息失败");
            }


            @Override
            public void onPayError(String res) {
                Logger.d("微信支付撤销");
            }
        });
    }

    private OkHttpClient mHttpClient = new OkHttpClient.Builder().build();
    private Handler mCallbackHandler = new Handler(Looper.getMainLooper());

    /**
     * 通过Okhttp与微信通信
     * * @param url 请求地址
     *
     * @throws Exception
     */
    public void httpRequest(String url, final ApiCallback<String> callback) {
        Logger.e("wx url: %s", url);
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                if (callback != null) {
                    mCallbackHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 请求失败，主线程回调
                            callback.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (callback != null) {
                    if (!response.isSuccessful()) {
                        mCallbackHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // 请求出错，主线程回调
                                callback.onError(response.code(), response.message());
                            }
                        });
                    } else {
                        mCallbackHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // 请求成功，主线程返回请求结果
                                    callback.onSuccess(response.body().string());
                                } catch (final IOException e) {
                                    // 异常出错，主线程回调
                                    mCallbackHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            callback.onFailure(e);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    public interface ApiCallback<T> {
        /**
         * 请求成功
         *
         * @param response 返回结果
         */
        void onSuccess(T response);

        /**
         * 请求出错
         *
         * @param errorCode 错误码
         * @param errorMsg  错误信息
         */
        void onError(int errorCode, String errorMsg);

        /**
         * 请求失败
         */
        void onFailure(IOException e);


        void onPayError(String res);

    }


    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Logger.e(msg);

    }

    private void showMessage(IOException e, String msg) {
        Toast.makeText(this, e.getMessage() + " " + msg, Toast.LENGTH_SHORT).show();
        Logger.d(e.getMessage() + " " + msg);
        if (loginCallback != null)
            loginCallback.onFailure(e);
    }

    private void showMessage(int errorCode, String msg) {
        Toast.makeText(this, errorCode + " " + msg, Toast.LENGTH_SHORT).show();
        Logger.d(errorCode + " " + msg);
        if (loginCallback != null)
            loginCallback.onError(errorCode, msg);
    }


    public static void ShareURL(String url, String title, String description) {

//        分享到对话:
//        SendMessageToWX.Req.WXSceneSession
//        分享到朋友圈:
//        SendMessageToWX.Req.WXSceneTimeline
//        分享到收藏:
//        SendMessageToWX.Req.WXSceneFavorite
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

//用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        Bitmap thumbBmp = BitmapFactory.decodeResource(Utils.Companion.getContext().getResources(), R.mipmap.ic_launcher);
        msg.thumbData = parseBitmapToBytes(thumbBmp);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "text";
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        //调用api接口，发送数据到微信
        App.sApi.sendReq(req);
    }

    public static byte[] parseBitmapToBytes(Bitmap bitmap) {
        byte[] bytes = null;

        if (bitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bytes = bos.toByteArray();
        }
        return bytes;
    }

}
