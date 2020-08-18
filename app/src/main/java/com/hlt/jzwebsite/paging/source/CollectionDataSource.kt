package com.hlt.jzwebsite.paging.source

import com.google.gson.Gson
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.model.CollectonResult
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CollectionDataSource(
    private val httpManager: HttpManager,
    private val uid: String?
) :
    BaseItemKeyedDataSource<Collec>() {

    override fun setKey(item: Collec): Int = item.id

    override fun onLoadAfter(params: LoadParams<Int>, callback: LoadCallback<Collec>) {
        loadMoreSuccess()
    }

    override fun onLoadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Collec>
    ) {
        httpManager.api.getCollecton(uid)
            .asyncSubscribe({
                //refreshSuccess(it.data!!.isEmpty())
                //callback.onResult(it.data!!)
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val collectonResult: CollectonResult =
                    Gson().fromJson<CollectonResult>(resData, CollectonResult::class.java)
                if (collectonResult.result ==null || collectonResult.result.size ==0){
                    refreshSuccess(true)
                }else{
                    refreshSuccess(collectonResult.result!!.isEmpty())
                    callback.onResult(collectonResult.result!!)
                }
            }, {
                refreshFailed(it.message, params, callback)
            })
    }
}