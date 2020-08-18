package com.hlt.jzwebsite.repository

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.WebDetailBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 9:51
 */
class WebDetailRepository(private val httpManager: HttpManager) {

    fun postWebDetail(
        id: String?,
        catId: String?,
        uid: String?,
        title_detail: String?
    ): LiveData<RequestState<WebDetailBean>> {
        val liveData = MutableLiveData<RequestState<WebDetailBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postWebDetail(id, catId,uid,title_detail)
            .asyncSubscribe({
                //                liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val webDetailBean: WebDetailBean =
                    Gson().fromJson<WebDetailBean>(resData, WebDetailBean::class.java)
                liveData.postValue(RequestState.success(webDetailBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}