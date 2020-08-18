package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.CollectionStatusBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 16:15
 */
class CollectOperationRepository(private val httpManager: HttpManager) {

    fun postToCollect(
        uid: String?,
        aid: String?,
        status: String?
    ): LiveData<RequestState<CollectionStatusBean>> {
        val liveData = MutableLiveData<RequestState<CollectionStatusBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postToCollect(uid, aid, status)
            .asyncSubscribe({
                //                liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val collectionstatusbean: CollectionStatusBean =
                    Gson().fromJson<CollectionStatusBean>(resData, CollectionStatusBean::class.java)
                liveData.postValue(RequestState.success(collectionstatusbean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }

}