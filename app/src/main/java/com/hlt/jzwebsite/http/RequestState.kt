package com.hlt.jzwebsite.http

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */

enum class Status {
    LOADING,
    SUCCESS,
    ERROR,
}

data class RequestState<out T>(val status: Status, val data: T?, val message: String? = null) {
    companion object {
        fun <T> loading(data: T? = null) = RequestState(Status.LOADING, data)

        fun <T> success(data: T? = null) = RequestState(Status.SUCCESS, data)

        fun <T> error(msg: String? = null, data: T? = null) = RequestState(Status.ERROR, data, msg)
    }

    fun isLoading(): Boolean = status == Status.LOADING
    fun isSuccess(): Boolean = status == Status.SUCCESS
    fun isError(): Boolean = status == Status.ERROR
}