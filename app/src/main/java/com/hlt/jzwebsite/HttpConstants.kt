package com.hlt.jzwebsite

import com.hlt.jzwebsite.utils.PreferenceUtils

/**
 * Created by lxb on 2020/2/20.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
object HttpConstants {

//    const val BASE_URL = "http://test.gtt20.com/"    // app测试环境
    const val BASE_URL = "http://main.gtt20.com/"    // app测试环境
    const val NETWORK_TIME = 60
    const val CACHE_NAME = "jzd_website"
    const val MAX_CACHE_SIZE = 50
    const val COOKIE_HEADER_RESPONSE = "set-cookie"
    const val COOKIE_HEADER_REQUEST = "Cookie"

    const val SAVE_USER_LOGIN_KEY = "user/login"
    const val SAVE_USER_REGISTER_KEY = "user/register"

    val LOGIN_REQUIRED_URLS = arrayOf("lg/collect", "lg/uncollect", "lg/todo")


    fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set = HashSet<String>()
        cookies
            .map { cookie ->
                cookie.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            }
            .forEach {
                it.filterNot { set.contains(it) }.forEach { set.add(it) }
            }
        val ite = set.iterator()
        while (ite.hasNext()) {
            val cookie = ite.next()
            sb.append(cookie).append(";")
        }
        val last = sb.lastIndexOf(";")
        if (sb.length - 1 == last) {
            sb.deleteCharAt(last)
        }
        return sb.toString()
    }

    fun saveCookie(url: String?, host: String?, cookies: String) {
        url ?: return
        PreferenceUtils(url, cookies)
        host ?: return
        PreferenceUtils(host, cookies)
    }

}