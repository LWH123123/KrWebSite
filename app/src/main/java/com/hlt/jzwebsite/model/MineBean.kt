package com.hlt.jzwebsite.model

import java.io.Serializable

/**
 * Created by lxb on 2020/2/24.
 * 邮箱：2072301410@qq.com
 * TIP： 我的实体
 */
data class SettingBean(
    var title: String
)

data class LoginBean(
    val result: Result
)

data class Result(
    val user_token: String
)

//// 用户信息实体
//data class UserInfoBean(
//    val userInfo: UserInfo
//)
//
//data class UserInfo(
//    val avator: String,
//    val collaborate: List<Collaborate>,
//    val grade_name: String,
//    val member_id: String,
//    val notice: String,
//    val point: String,
//    val predepoit: String,
//    val tguser_count: Int,
//    val username: String,
//    val yesterday_yongjin: String,
//    val yongjin_count: String,
//    val urlss: String,
//    val token : String //手动添加字段
//) : Serializable
//
//
//
////战略合作实体bean
//data class Collaborate(
//    val atid: Int,
//    val atname: String,
//    val enname: String,
//    val id: Int,
//    val image: String,
//    val sort: Int,
//    val url: String
//)
//data class SmsCodeBean(
//    val result: SmsResult
//)
//
//data class SmsResult(
//    val state: Boolean
//)

data class SmsCodeBean(
    val result: SmsResult
)

data class SmsResult(
    val msg: String,
    val state: Int
)

