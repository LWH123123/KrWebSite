package com.hlt.jzwebsite.paging.source

import com.google.gson.Gson
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.*
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CooperationDataSource(private val httpManager: HttpManager,
                            private val uid:String?) :
    BaseItemKeyedDataSource<Busines>() {

    var pageNo = 0

    override fun setKey(item: Busines): Int = item.id

    override fun onLoadAfter(params: LoadParams<Int>, callback: LoadCallback<Busines>) {
        loadMoreSuccess()
    }

    override fun onLoadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Busines>
    ) {

        httpManager.api.postCoopData(/*uid*/)
            .asyncSubscribe({
//                refreshSuccess(it.result!!.isEmpty())
//                val result = it.result as CoopResult
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val cooperationBean: CooperationBean = Gson().fromJson<CooperationBean>(resData, CooperationBean::class.java)
                if (cooperationBean.result ==null || cooperationBean.result.size ==0){
                    refreshSuccess(true)
                }else{
                    refreshSuccess(cooperationBean.result!!.isEmpty())
                    callback.onResult(cooperationBean.result!!)
                }
            }, {
                refreshFailed(it.message, params, callback)
            })
    }
}