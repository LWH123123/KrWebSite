package com.hlt.jzwebsite.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.ContactWayBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author lwh
 * @description :
 * @date 2020/3/27.
 */
class ContactWayRepository(private val httpManager: HttpManager)  {

    fun postContactWay(): LiveData<RequestState<ContactWayBean>> {
        val liveData = MutableLiveData<RequestState<ContactWayBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postContactway()
            .asyncSubscribe({
                // liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                Log.e("联系方式====>>",resData)
                val contactWayBean: ContactWayBean =
                    Gson().fromJson<ContactWayBean>(resData, ContactWayBean::class.java)
                liveData.postValue(RequestState.success(contactWayBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }




}