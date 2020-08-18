package com.hlt.jzwebsite.paging.factory

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.paging.source.CollectionDataSource

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CollectionFactory(
    private val httpManager: HttpManager,
    private val uid: String?
) : BaseDataSourceFactory<Collec>() {
    override fun createDataSource(): BaseItemKeyedDataSource<Collec> {
        return CollectionDataSource(httpManager,uid)
    }
}