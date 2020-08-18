package com.hlt.jzwebsite.paging.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.HomeBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class HomeRepository(private val httpManager: HttpManager) {

    fun postHomeData(): LiveData<RequestState<HomeBean>> {
        val liveData = MutableLiveData<RequestState<HomeBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postHomeData()
            .asyncSubscribe({
                val result = it as? HttpResponse<*>
                val res = String(RSAUtils2.decode(result?.resData))//分段
                val homeBean: HomeBean = Gson().fromJson(res, HomeBean::class.java)
                liveData.postValue(RequestState.success(homeBean))
                if (homeBean.result ==null){
                    liveData.postValue(RequestState.loading(homeBean))
                }else{
                    liveData.postValue(RequestState.success(homeBean))
                }

            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}