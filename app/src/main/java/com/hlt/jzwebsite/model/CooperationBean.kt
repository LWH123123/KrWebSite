package com.hlt.jzwebsite.model

/**
 * Created by lxb on 2020/2/24.
 * 邮箱：2072301410@qq.com
 * TIP： 商务合作 实体
 */
//data class CooperationBean(
//    var result: Results
//)
//
//data class Results(
//    var item: List<Item>
//)
//
//data class Item(
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

//data class CooperationBean(
//    val result: CoopResult
//)

/*
data class CoopResult(
    val business: List<Busines>
)

data class Busines(
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
)*/

data class CooperationBean(
    val result: List<Busines>
)

data class Busines(
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
