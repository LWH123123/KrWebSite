package com.hlt.jzwebsite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.LoginBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lxb on 2020/2/27.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class MainRepository(private val httpManager: HttpManager) {

    fun postLogin(
        openid: String?,
        nickname: String?,
        gender: Int?,
        avatar: String?,
        province: String?,
        city: String?,
        area: String?,
        unionid: String?
    )
            : LiveData<RequestState<LoginBean>> {
        val liveData = MutableLiveData<RequestState<LoginBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postLogin(
            openid,
            nickname,
            gender,
            avatar,
            province,
            city,
            area,
            unionid
        )
            .asyncSubscribe({
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val loginBean: LoginBean = Gson().fromJson(resData, LoginBean::class.java)
                liveData.postValue(RequestState.success(loginBean))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }


}