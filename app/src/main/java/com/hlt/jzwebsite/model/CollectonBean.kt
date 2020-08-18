package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/4 13:36
 */

data class CollectonResult(
    val result: List<Collec>
)

data class Collec(
    val cid: Int,
    val coid: Int,
    val collect: Int,
    val create_time: String,
    val enname: String,
    val hits: Int,
    val id: Int,
    val image: String,
    val sort: Int,
    val summary: String,
    val title: String,
    val uid: Int,
    val update_time: String,
    val url: String,
    val source: String
)