package com.hlt.jzwebsite.base.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
abstract class BaseDataSourceFactory<T> : DataSource.Factory<Int,T>()  {
    val sourceLivaData = MutableLiveData<BaseItemKeyedDataSource<T>>()

    override fun create(): BaseItemKeyedDataSource<T> {
        val dataSource: BaseItemKeyedDataSource<T> = createDataSource()
        sourceLivaData.postValue(dataSource)
        return dataSource
    }

    abstract fun createDataSource(): BaseItemKeyedDataSource<T>
}