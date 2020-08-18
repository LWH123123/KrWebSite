package com.hlt.jzwebsite.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.RequestState
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.ModifyPwdBean
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ModifyPwdRepository(private val httpManager: HttpManager) {

    fun postModifyPwd(
        token: String?,
        oPwd: String?,
        nPwd: String?
    ): LiveData<RequestState<ModifyPwdBean>> {
        val liveData = MutableLiveData<RequestState<ModifyPwdBean>>()
        liveData.value = RequestState.loading()
        httpManager.api.postModifyPwd(token, oPwd, nPwd)
            .asyncSubscribe({
                //                liveData.postValue(RequestState.success(it.data))
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val modifyPhoBean: ModifyPwdBean =
                    Gson().fromJson<ModifyPwdBean>(resData, ModifyPwdBean::class.java)
                liveData.postValue(RequestState.success(modifyPhoBean!!))
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}