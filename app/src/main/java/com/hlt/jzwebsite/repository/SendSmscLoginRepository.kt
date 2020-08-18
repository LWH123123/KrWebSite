package com.hlt.jzwebsite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.SmsCodeBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/19 14:34
 */
class SendSmscLoginRepository (private val httpManager: HttpManager) {

    fun postSendVertifycLogin(phone: String?, guid: String?): LiveData<RequestState<SmsCodeBean>> {
        val liveData = MutableLiveData<RequestState<SmsCodeBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postSendVertifycLogin(phone, guid)
            .asyncSubscribe({
                // liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val smsCodeBean: SmsCodeBean =
                    Gson().fromJson<SmsCodeBean>(resData, SmsCodeBean::class.java)
                liveData.postValue(RequestState.success(smsCodeBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}