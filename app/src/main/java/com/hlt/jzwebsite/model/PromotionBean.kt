package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/17 15:17
 */

data class PromotionBean(
    val result: PromotionResult
)

data class PromotionResult(
    val invit_id: Int,
    val poster: List<String>
)