package com.hlt.jzwebsite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.ModifyPhoBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ModifyPhoneRepository(private val httpManager: HttpManager) {

    fun postModifyPhone(token: String?,phone: String?, code: String?): LiveData<RequestState<ModifyPhoBean>> {
        val liveData = MutableLiveData<RequestState<ModifyPhoBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postModifyPhone(token,phone,code)
            .asyncSubscribe({
//                liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val modifyPhoBean: ModifyPhoBean =
                    Gson().fromJson<ModifyPhoBean>(resData, ModifyPhoBean::class.java)
                liveData.postValue(RequestState.success(modifyPhoBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }

}