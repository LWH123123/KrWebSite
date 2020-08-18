package com.hlt.jzwebsite.paging.repository

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BasePagingRepository
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.paging.factory.CollectionFactory

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CollectionRepository(private val httpManager: HttpManager, private val uid: String? ) : BasePagingRepository<Collec>() {
    override fun createDataBaseFactory(): BaseDataSourceFactory<Collec> {
        return CollectionFactory(httpManager,uid)
    }
}