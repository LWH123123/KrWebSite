package com.hlt.jzwebsite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.PromotionBean
import com.hlt.jzwebsite.model.SmsCodeBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/17 15:14
 */
class GoToPromotionRepository(private val httpManager: HttpManager) {

    fun postPromotion(token: String?): LiveData<RequestState<PromotionBean>> {
        val liveData = MutableLiveData<RequestState<PromotionBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postPromotion(token)
            .asyncSubscribe({
                // liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val promotionBean: PromotionBean =
                    Gson().fromJson<PromotionBean>(resData, PromotionBean::class.java)
                liveData.postValue(RequestState.success(promotionBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}