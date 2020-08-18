package com.hlt.jzwebsite.paging.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.AppUpdateBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/12 11:57
 */
class AppUpdateRepository(private val httpManager: HttpManager, private val version: String?) {

    fun postAppUpdate(): LiveData<RequestState<AppUpdateBean>> {
        val liveData = MutableLiveData<RequestState<AppUpdateBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postAppUpdate(version)
            .asyncSubscribe({
                val result = it as? HttpResponse<*>
                val res = String(RSAUtils2.decode(result?.resData))//分段
                val appUpdateBeanBean: AppUpdateBean =
                    Gson().fromJson(res, AppUpdateBean::class.java)
                liveData.postValue(RequestState.success(appUpdateBeanBean))
                if (appUpdateBeanBean.result == null) {
                    liveData.postValue(RequestState.loading(appUpdateBeanBean))
                } else {
                    liveData.postValue(RequestState.success(appUpdateBeanBean))
                }

            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}