package com.hlt.jzwebsite.paging.factory

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BaseItemKeyedDataSource
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Project
import com.hlt.jzwebsite.paging.source.ProjectDataSource

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:23
 */
class ProjectFactory(
    private val httpManager: HttpManager,
    private val cid: String?
) : BaseDataSourceFactory<Project>() {
    override fun createDataSource(): BaseItemKeyedDataSource<Project> {
        return ProjectDataSource(httpManager, cid)
    }
}