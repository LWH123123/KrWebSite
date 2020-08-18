package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/12 11:50
 */


data class AppUpdateBean(
    val result: UpdateResult
)

data class UpdateResult(
    val desc: String,
    val size: String,
    val status: Int,
    val url: String,
    val version: String
)