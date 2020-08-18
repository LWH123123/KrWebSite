package com.hlt.jzwebsite.paging.factory

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Planning
import com.hlt.jzwebsite.paging.source.DynamicsDataSource

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class DynamicsFactory(private val httpManager: HttpManager, private val uid: String?): BaseDataSourceFactory<Planning>() {
    override fun createDataSource(): BaseItemKeyedDataSource<Planning> {
        return DynamicsDataSource(httpManager,uid)
    }
}