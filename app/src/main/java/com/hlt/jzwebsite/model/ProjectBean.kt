package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:01
 */

//data class ProjectResult(
//    val result: List<Project>
//)
//
//data class Project(
//    val cid: Int,
//    val collect: Int,
//    val create_time: String,
//    val enname: String,
//    val hits: Int,
//    val id: Int,
//    val image: String,
//    val sort: Int,
//    val source: String,
//    val summary: String,
//    val title: String,
//    val url: String
//)
data class ProjectResult(
    val result: List<Project>
)

data class Project(
    val author: String,
    val catid: Int,
    val collect: Int,
    val content: String,
    val create_time: Long,
    val description: String,
    val download: String,
    val hits: Int,
    val id: Int,
    val image: String,
    val images: String,
    val keywords: String,
    val sort: Int,
    val source: String,
    val status: Int,
    val summary: String,
    val template: String,
    val thumb: String,
    val title: String,
    val title_style: String,
    val update_time: Int
)