package com.hlt.jzwebsite.http

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：该类仅供参考，实际根据 业务返回的固定字段, 根据需求来定义，
 */
data class HttpResponse<T>(
    var data: T?,
    var errorCode: Int,
    var errorMsg: String?,

    // 定制
    var resData: String?,
    var sign: String?,
    var result: T?,
    var res: String?,
    var code: Int,
    var msg: String?
)