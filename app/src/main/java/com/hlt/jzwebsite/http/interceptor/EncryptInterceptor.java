package com.hlt.jzwebsite.http.interceptor;

import com.google.gson.JsonObject;
import com.hlt.jzwebsite.App;
import com.hlt.jzwebsite.BuildConfig;
import com.hlt.jzwebsite.utils.java.RSASignature;
import com.hlt.jzwebsite.utils.java.RSAUtils2;
import com.hlt.jzwebsite.utils.java.StringUtils;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created by lxb on 2020/2/22.
 * 邮箱：2072301410@qq.com
 * TIP： 项目需求  前后台约定的  接口数据进行RSA 加密传输
 */
public class EncryptInterceptor implements Interceptor {
    public static final String KEY_REQ_DATA = "reqData";
    public static final String KEY_SIGN = "sign";

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Request.Builder newRequestBuild = oldRequest.newBuilder();
        String method = oldRequest.method();
        String postBodyString = "";
        String versionName = BuildConfig.VERSION_NAME;
        if ("POST".equals(method)) {
            RequestBody body = oldRequest.body();
            postBodyString = bodyToString(body);
            if (!StringUtils.isEmpty(postBodyString)) {

                postBodyString += "&os=android&guid=" + App.myDeviceID +"&version="+ versionName;
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                /**==============对参数json 加密   start  ============*/
                String jsonStr = createJsonString(postBodyString);
                String reqData = "";
                try {
                    reqData = RSAUtils2.encode(jsonStr.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String sign = RSASignature.sign(jsonStr);
                Logger.d("reqdata=" + reqData);
                Logger.d("sign=" + sign);
                /**=============对参数json 加密  end =============*/
                formBodyBuilder.add(KEY_REQ_DATA, reqData);
                formBodyBuilder.add(KEY_SIGN, sign);
                newRequestBuild = oldRequest.newBuilder();

                RequestBody formBody = formBodyBuilder.build();
//                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                postBodyString = ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
                newRequestBuild.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
            }
        } else {  // 添加新的参数
            HttpUrl.Builder commonParamsUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host());
//                    .addaddQueryParameter(KEY_REQ_DATA,"test_reqdata")
//                    .addQueryParameter(KEY_SIGN, "test_sign");
            newRequestBuild = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(commonParamsUrlBuilder.build());
        }

        /****====打印请求头===***/
//        if(BuildConfig.DEBUG){
//            String[] params = postBodyString.split("&");
//            for (String param : params) {
//                String[] temp = param.split("=");
//                if (temp == null || temp.length < 2)
//                    continue;
//                if(temp[0].equals(KEY_REQ_DATA)||temp[0].equals(KEY_SIGN))
//                    continue;
//                StringBuffer block = new StringBuffer();
//                for (int i = 1; i < temp.length; i++) {
//                    block.append(temp[i]);
//                }
//                newRequestBuild.addHeader(temp[0], block.toString());
//            }
//        }
        /****=======***/
        Request newRequest = newRequestBuild
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "zh")
                .build();

        okhttp3.Response response = chain.proceed(newRequest);
        MediaType mediaType = response.body().contentType();
        String content = response.body().string();
//        KLog.d("-------start:"+method+"|");
//        KLog.d(newRequest.toString()+"\n|");
//        KLog.d(method.equalsIgnoreCase("POST")?"post参数{"+ postBodyString +"}\n|":"");
        return response.newBuilder()
                .body(okhttp3.ResponseBody.create(mediaType, content))
                .build();
    }

    /**
     * 把参数封装成json
     *
     * @param postBodyString
     * @return
     */
    private static String createJsonString(String postBodyString) {
        JsonObject jsonObject = new JsonObject();
        try {
//            postBodyString = (URLDecoder.decode(postBodyString, "UTF-8"));
            postBodyString = (URLDecoder.decode(postBodyString.replaceAll("%(?![0-9a-fA-F]{2})", "%25"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.d("=======>>>>>>>>>>>"+postBodyString);
        String[] params = postBodyString.split("&");
        for (String param : params) {
            String[] temp = param.split("=");
            if (temp == null || temp.length < 2)
                continue;
            StringBuffer block = new StringBuffer();
            for (int i = 1; i < temp.length; i++) {
                block.append(temp[i]);
            }
            jsonObject.addProperty(temp[0], block.toString());
//            jsonObject.addProperty(temp[0],temp[1]);
        }
        return jsonObject.toString();
    }

    /**
     * @param request
     * @return
     */
    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

}
