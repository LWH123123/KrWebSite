package com.hlt.jzwebsite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.ModifyPhoBean
import com.hlt.jzwebsite.model.VierificaLoginBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/19 15:13
 */
class VierificaLoginRepository(private val httpManager: HttpManager) {

    fun postvierificaLogin(token: String?,phone: String?, code: String?): LiveData<RequestState<VierificaLoginBean>> {
        val liveData = MutableLiveData<RequestState<VierificaLoginBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postvierificaLogin(token,phone,code)
            .asyncSubscribe({
                //                liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val vierificaLoginBean: VierificaLoginBean =
                    Gson().fromJson<VierificaLoginBean>(resData, VierificaLoginBean::class.java)
                liveData.postValue(RequestState.success(vierificaLoginBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }

}