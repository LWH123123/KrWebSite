package com.hlt.jzwebsite.paging.factory

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Busines
import com.hlt.jzwebsite.paging.source.CooperationDataSource

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CooperationFactory(private val httpManager: HttpManager,private val uid: String?) : BaseDataSourceFactory<Busines>() {
    override fun createDataSource(): BaseItemKeyedDataSource<Busines> {
        return CooperationDataSource(httpManager,uid)
    }
}