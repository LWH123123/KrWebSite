package com.hlt.jzwebsite.paging.source

import com.google.gson.Gson
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.ClassListBean
import com.hlt.jzwebsite.model.Crlist
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 12:13
 */
class ClassListDataSource(
    private val httpManager: HttpManager,
    private val uid: String?
) :
    BaseItemKeyedDataSource<Crlist>() {

    override fun setKey(item: Crlist): Int = item.id

    override fun onLoadAfter(params: LoadParams<Int>, callback: LoadCallback<Crlist>) {
        loadMoreSuccess()
    }

    override fun onLoadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Crlist>
    ) {
        httpManager.api.postClassListData()
            .asyncSubscribe({
                //refreshSuccess(it.data!!.isEmpty())
                //callback.onResult(it.data!!)
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val classListBean: ClassListBean =
                    Gson().fromJson<ClassListBean>(resData, ClassListBean::class.java)
                if (classListBean.result.crlist == null || classListBean.result.crlist.size == 0) {
                    refreshSuccess(true)
                } else {
                    refreshSuccess(classListBean.result.crlist!!.isEmpty())
                    callback.onResult(classListBean.result.crlist!!)
                }
            }, {
                refreshFailed(it.message, params, callback)
            })
    }
}