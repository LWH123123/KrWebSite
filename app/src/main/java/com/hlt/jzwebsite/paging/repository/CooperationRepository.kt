package com.hlt.jzwebsite.paging.repository

import com.hlt.jzwebsite.base.paging.BaseDataSourceFactory
import com.hlt.jzwebsite.base.paging.BasePagingRepository
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Busines
import com.hlt.jzwebsite.paging.factory.CooperationFactory

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CooperationRepository(private val httpManager: HttpManager,private val uid:String?) :
    BasePagingRepository<Busines>() {
    override fun createDataBaseFactory(): BaseDataSourceFactory<Busines> {
        return CooperationFactory(httpManager,uid)
    }
}