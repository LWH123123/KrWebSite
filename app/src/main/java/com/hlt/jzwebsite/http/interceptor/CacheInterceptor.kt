package com.hlt.jzwebsite.http.interceptor

import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.ToastUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!NetUtils.isConnected(App.instance)) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE) // 阻值response使用网络
                .cacheControl(CacheControl.FORCE_NETWORK) //阻值response使用缓存
                .build()
        }
        val response = chain.proceed(request)
        if (NetUtils.isConnected(App.instance)) {
            val maxAge = 60 * 3
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$maxAge")
//                .header("Cache-Control","no-cache")
                .build()
        } else {
            val maxStale = 60 * 60 * 24 * 7
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                .build()
        }
        return response
    }
}