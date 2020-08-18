package com.hlt.jzwebsite.base.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.hlt.jzwebsite.http.RequestState

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
data class Listing<T>(
    //数据
    val pagedList: LiveData<PagedList<T>>,
    //上拉加载更多状态
    val networkState: LiveData<RequestState<Boolean>>,
    //下拉刷新状态
    val refreshState: LiveData<RequestState<Boolean>>,
    //刷新逻辑
    val refresh: () -> Unit,
    //重试逻辑，刷新或加载更多
    val retry: () -> Unit

)
