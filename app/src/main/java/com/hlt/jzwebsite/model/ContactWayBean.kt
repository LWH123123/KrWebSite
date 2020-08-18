package com.hlt.jzwebsite.model

/**
 * @author lwh
 * @description :
 * @date 2020/3/27.
 */
data class ContactWayBean(
    val result : ContactWayBeanResult
)
data class ContactWayBeanResult(
    val id :Int,
    val country_hot :String,
    val customer_wx : List<String>,
    val business_consultation : String,
    val adress : String
)