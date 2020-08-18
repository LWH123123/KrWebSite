package com.hlt.jzwebsite.paging.repository

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BasePagingRepository
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Project
import com.hlt.jzwebsite.paging.factory.ProjectFactory

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:21
 */
class ProjectRepository(private val httpManager: HttpManager, private val cid: String?) :
    BasePagingRepository<Project>() {
    override fun createDataBaseFactory(): BaseDataSourceFactory<Project> {
        return ProjectFactory(httpManager, cid)
    }
}