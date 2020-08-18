package com.hlt.jzwebsite.base.paging

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP： 针对 列表使用的viewmodel 基类
 */
open class BasePagingViewModel<T>(repository: BasePagingRepository<T>) : ViewModel(){

    private val pageSize = MutableLiveData<Int>()
    private val repoResult = Transformations.map(pageSize) {
        repository.getData(it)
    }
    val pagedList = Transformations.switchMap(repoResult) { it.pagedList }
    val networkState = Transformations.switchMap(repoResult) { it.networkState }
    val refreshState = Transformations.switchMap(repoResult) { it.refreshState }

    fun refresh() {
        repoResult.value!!.refresh?.invoke()
    }

    fun initLoad(newSize: Int = 10): Boolean {
        if (pageSize?.value == newSize)
            return false
        pageSize?.value = newSize
        return true
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }
}