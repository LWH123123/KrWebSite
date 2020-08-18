package com.hlt.jzwebsite.paging.factory

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Crlist
import com.hlt.jzwebsite.paging.source.ClassListDataSource

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 12:12
 */
class ClassListFactory(
    private val httpManager: HttpManager,
    private val uid: String?
) : BaseDataSourceFactory<Crlist>() {
    override fun createDataSource(): BaseItemKeyedDataSource<Crlist> {
        return ClassListDataSource(httpManager, uid)
    }
}