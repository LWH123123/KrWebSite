package com.hlt.jzwebsite.http

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
object ExecutorUtils {
    val DISK_IO = Executors.newSingleThreadExecutor()

    val NETWORK_IO = Executors.newFixedThreadPool(5)

    fun main_thread(runnable: Runnable, delay: Long = 0) {
        Handler(Looper.getMainLooper())
            .postDelayed(runnable, delay)
    }
}