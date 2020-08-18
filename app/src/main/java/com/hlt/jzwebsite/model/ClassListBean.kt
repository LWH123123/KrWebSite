package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 12:02
 */

//data class ClassListResult(
//    val result: List<Class>
//)
//
//data class Class(
//    val cid: Int,
//    val collect: Int,
//    val hits: Int,
//    val id: Int,
//    val image: String,
//    val title: String
//)
data class ClassListBean(
    val result: ClassListResult
)

data class ClassListResult(
    val classroomCount: Int,
    val crlist: List<Crlist>
)

data class Crlist(
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