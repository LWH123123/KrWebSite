package com.hlt.jzwebsite.model

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 9:24
 *
 * -1：新密码不能与旧密码一致
-2：密码不能少于6位
-3：参数非法
-4：非法请求
-5：原密码错误
2：密码修改成功
 *
 */

/*data class ModifyPwdBean(
    val result: String
)*/

data class ModifyPwdBean(
    val result: PwdResult
)

data class PwdResult(
    val msg: String,
    val state: Int
)

