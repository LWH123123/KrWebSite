package com.hlt.jzwebsite.utils

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：权限回调
 */
object Listeners {
    interface PermissionListener {
        fun onGranted()

        fun onDenied(permissions: List<String>)

        fun onShowReason()
    }
}