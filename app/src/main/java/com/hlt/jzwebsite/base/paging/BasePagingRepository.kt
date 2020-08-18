package com.hlt.jzwebsite.base.paging

import androidx.lifecycle.Transformations
import androidx.paging.Config
import androidx.paging.toLiveData

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
abstract class BasePagingRepository<T> {

    fun getData(pageSize: Int): Listing<T> {

        val sourceFactory = createDataBaseFactory()
        val pagedList = sourceFactory.toLiveData(
            config = Config(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSizeHint = pageSize * 2
            )
        )
        val refreshState = Transformations.switchMap(sourceFactory.sourceLivaData) { it.refreshStatus }
        val networkStatus = Transformations.switchMap(sourceFactory.sourceLivaData) { it.loadMoreStatus }

        return Listing(
            pagedList,
            networkStatus,
            refreshState,
            refresh = {
                sourceFactory.sourceLivaData.value?.invalidate()
            },
            retry = {
                sourceFactory.sourceLivaData.value?.retryFailed()
            }
        )
    }

    abstract fun createDataBaseFactory(): BaseDataSourceFactory<T>
}