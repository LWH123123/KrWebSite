package com.hlt.jzwebsite.paging.source

import com.google.gson.Gson
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.http.HttpResponse
import com.hlt.jzwebsite.http.asyncSubscribe
import com.hlt.jzwebsite.model.*
import com.hlt.jzwebsite.utils.java.RSAUtils2

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:24
 */
class ProjectDataSource(
    private val httpManager: HttpManager,
    private val cid: String?
) :
    BaseItemKeyedDataSource<Project>() {

    override fun setKey(item: Project): Int = item.id

    override fun onLoadAfter(params: LoadParams<Int>, callback: LoadCallback<Project>) {
        loadMoreSuccess()
    }

    override fun onLoadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Project>
    ) {
        httpManager.api.postProjectData(cid)
            .asyncSubscribe({
                //refreshSuccess(it.data!!.isEmpty())
                //callback.onResult(it.data!!)
                val result = it as? HttpResponse<*>
                val resData = String(RSAUtils2.decode(result?.resData))//分段
                val projectResult: ProjectResult =
                    Gson().fromJson<ProjectResult>(resData, ProjectResult::class.java)
                if (projectResult.result == null || projectResult.result.size == 0) {
                    refreshSuccess(true)
                } else {
                    refreshSuccess(projectResult.result!!.isEmpty())
                    callback.onResult(projectResult.result!!)
                }
            }, {
                refreshFailed(it.message, params, callback)
            })
    }
}