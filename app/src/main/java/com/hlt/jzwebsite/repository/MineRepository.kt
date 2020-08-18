package com.hlt.jzwebsite.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.http.*
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.RSAUtils2
import com.hlt.jzwebsite.utils.java.SPUtils


/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class MineRepository(private val httpManager: HttpManager) {

    fun postUserInfo(userToken:String?): LiveData<RequestState<UserInfo>> {
        val liveData = MutableLiveData<RequestState<UserInfo>>()
        liveData.value = RequestState.loading()
        httpManager.api.postUserInfo(userToken)
            .asyncSubscribe({
                val result = it as? HttpResponse<*>
                if (result?.code == -1){
                    ToastUtils.show("用户信息过期")
                    Log.d("WX token 失效====","用户信息过期")
//                    val tokenerror: Int = -1
                    val resData = String(RSAUtils2.decode(result?.resData))//分段
                    val userInfo: UserInfo = Gson().fromJson(resData, UserInfo::class.java)
                    userInfo.result.tokenError = result?.code
                    SPUtils.getInstance().put(Constants.SP_USER_TOKEN_ERROR, result?.code)
                    liveData.postValue(RequestState.success(userInfo!!))
                    return@asyncSubscribe
                }else{
                    val resData = String(RSAUtils2.decode(result?.resData))//分段
                    val userInfo: UserInfo = Gson().fromJson(resData, UserInfo::class.java)
                    userInfo.result.tokenError = 0
                    SPUtils.getInstance().put(Constants.SP_USER_TOKEN_ERROR, 0)
                    liveData.postValue(RequestState.success(userInfo!!))
                }
            }, {
                liveData.postValue(RequestState.error(it.message))
            })
        return liveData
    }
}