package com.hlt.jzwebsite.http

import com.hlt.jzwebsite.model.*
import com.hlt.jzwebsite.test.*
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
interface Api {

    /* 登录*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/logins")
    fun postLogin(
        @Field("openid") openid: String?,
        @Field("nickname") nickname: String?,
        @Field("gender") gender: Int?,
        @Field("avatar") avatar: String?,
        @Field("province") province: String?,
        @Field("city") city: String?,
        @Field("area") area: String?,
        @Field("unionid") unionid: String?
    ): Observable<HttpResponse<LoginBean>>


    /**
     * 商务合作
     */
//    @FormUrlEncoded
    @POST("index.php/mobile/app/getbusinessjson")
    fun postCoopData(/*@Field("uid") uid: String?*/): Observable<HttpResponse<CooperationBean>>

    /**
     * 企业规划
     */
//    @FormUrlEncoded
    @POST("index.php/mobile/app/getplanningjson")
    fun postDynamicsData(/*@Field("uid") uid: String?*/): Observable<HttpResponse<DynamicsBean>>

    /* 获取用户信息*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/getuserinfojson")
    fun postUserInfo(
        @Field("user_token") user_token: String?
    ): Observable<HttpResponse<UserInfo>>

    /**
     * 无参数请求 要去掉注解  @FormUrlEncoded
     * @return
     */
    /*首页*/
    @POST("index.php/mobile/app/index")
    fun postHomeData(/*@Field("os") os: String*/): Observable<HttpResponse<HomeBean>>

    /*上市项目*/
//    @POST("index.php/mobile/app/getlistedjson")
    @FormUrlEncoded
    @POST("index.php/mobile/app/housecat")
    fun postProjectData(@Field("title") title: String?): Observable<HttpResponse<ProjectResult>>

    /*课堂列表*/
    @POST("index.php/mobile/app/getclassroomjson")
    fun postClassListData(/*@Field("os") os: String*/): Observable<HttpResponse<ClassListBean>>

    /*我的收藏*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/getcollectjson")
    fun getCollecton(@Field("uid") uid: String?): Observable<HttpResponse<CollectonResult>>

    /* 添加收藏  / 取消收藏*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/checkcollect")
    fun postToCollect(@Field("uid") uid: String?,
                      @Field("id") aid: String?,
                      @Field("status") status: String?): Observable<HttpResponse<CollectionStatusBean>>

    /*修改密码*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/savepwd")
    fun postModifyPwd(@Field("user_token") user_token: String?,
                      @Field("opassword") oPwd: String?,
                      @Field("npassword") nPwd: String?): Observable<HttpResponse<ModifyPwdBean>>

    /*文章详情页*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/info")
    fun postWebDetail(@Field("id") id: String?,
                      @Field("catId") catId: String?,
                      @Field("uid") uid: String?,
                      @Field("title") title_detail: String?
    ): Observable<HttpResponse<WebDetailBean>>


    /*修改手机号*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/bindingmobile")
    fun postModifyPhone(@Field("user_token") user_token: String?,
        @Field("phone") oPwd: String?,
        @Field("phone_code") phone_code: String?
    ): Observable<HttpResponse<ModifyPhoBean>>

    /* 验证码登录*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/codelogin")
    fun postvierificaLogin(@Field("guid") guid: String?,
        @Field("mobile") mobile: String?,
        @Field("vcode") vcode: String?
    ): Observable<HttpResponse<VierificaLoginBean>>

    /*发送短信*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/sendcode")
    fun postSendVertifyCode(@Field("phone") mobile: String?, @Field("user_token") type: String?): Observable<HttpResponse<SmsCodeBean>>

    /*立即推广*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/tuiguang")
    fun postPromotion(@Field("user_token") type: String?): Observable<HttpResponse<PromotionBean>>

    /* app更新*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/version")
    fun postAppUpdate(@Field("version") version: String?): Observable<HttpResponse<AppUpdateBean>>
    /*发送短信  登录*/
    @FormUrlEncoded
    @POST("index.php/mobile/app/phonecode")
    fun postSendVertifycLogin(@Field("mobile") mobile: String?, @Field("guid") type: String?): Observable<HttpResponse<SmsCodeBean>>


    @POST("index.php/mobile/app/contacts")
    fun postContactway(): Observable<HttpResponse<ContactWayBean>>



}