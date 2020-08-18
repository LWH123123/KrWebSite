package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 9:55
 */

data class WebDetailBean(
    val result: WebDetailResult
)

data class WebDetailResult(
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
    val collectstatus: Int,
    val summary: String,
    val template: String,
    val thumb: String,
    val title: String,
    val title_style: String,
    val update_time: Int,
    val url: String
)