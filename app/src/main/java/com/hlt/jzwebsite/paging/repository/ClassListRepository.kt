package com.hlt.jzwebsite.paging.repository

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BasePagingRepository
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Crlist
import com.hlt.jzwebsite.paging.factory.ClassListFactory

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 12:11
 */
class ClassListRepository(private val httpManager: HttpManager, private val uid: String?) :
    BasePagingRepository<Crlist>() {
    override fun createDataBaseFactory(): BaseDataSourceFactory<Crlist> {
        return ClassListFactory(httpManager, uid)
    }
}