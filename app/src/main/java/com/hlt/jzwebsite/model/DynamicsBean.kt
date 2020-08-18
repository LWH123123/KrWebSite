package com.hlt.jzwebsite.model

/**
 * Created by lxb on 2020/2/24.
 * 邮箱：2072301410@qq.com
 * TIP： 动态  实体
 */

//data class DynamicsBean(
//    var result: DynamicsResult
//)
//
//data class DynamicsResult(
//    var planning: List<Planning>
//)
//
//data class Planning(
//    var cid: Int,
//    var collect: Int,
//    var create_time: String,
//    var enname: String,
//    var hits: Int,
//    var id: Int,
//    var image: String,
//    var sort: Int,
//    var source: String,
//    var summary: String,
//    var title: String,
//    var url: String
//)
data class DynamicsBean(
    val result: List<Planning>
)

data class Planning(
    val cid: Int,
    val collect: Int,
    val create_time: String,
    val enname: String,
    val hits: Int,
    val id: Int,
    val image: String,
    val sort: Int,
    val source: String,
    val summary: String,
    val title: String,
    val url: String
)
