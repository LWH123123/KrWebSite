package com.hlt.jzwebsite.paging.source

import com.google.gson.Gson
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.*
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lx on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class DynamicsDataSource(
    private val httpManager: HttpManager,
    private val uid: String?
) : BaseItemKeyedDataSource<Planning>() {

    override fun setKey(item: Planning): Int = item.id

    override fun onLoadAfter(params: LoadParams<Int>, callback: LoadCallback<Planning>) {
        loadMoreSuccess()
}

    override fun onLoadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Planning>) {
        httpManager.api.postDynamicsData(/*uid*/)
            .asyncSubscribe({
//                refreshSuccess(it.data!!.isEmpty())
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val dynamicsBean: DynamicsBean = Gson().fromJson<DynamicsBean>(resData, DynamicsBean::class.java)
                if (dynamicsBean.result ==null || dynamicsBean.result.size ==0){
                    refreshSuccess(true)
                }else{
                    refreshSuccess(dynamicsBean.result!!.isEmpty())
                    callback.onResult(dynamicsBean.result!!)
                }
            }, {
                refreshFailed(it.message, params, callback)
            })
    }
}