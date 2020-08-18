package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/19 15:16
 */

//data class VierificaLoginBean(
//    val result: VierificaLoginResult
//)
//
//data class VierificaLoginResult(
//    val state: Int
//)
data class VierificaLoginBean(
    val result: VierificaLoginResult
)

data class VierificaLoginResult(
    val msg: String,
    val state: Int,
    val user_token: String
)