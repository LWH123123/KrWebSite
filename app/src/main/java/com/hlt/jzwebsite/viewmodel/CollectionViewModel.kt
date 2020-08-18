package com.hlt.jzwebsite.viewmodel

import com.hlt.jzwebsite.base.paging.BasePagingViewModel
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.paging.repository.CollectionRepository

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CollectionViewModel(
    repository: CollectionRepository
): BasePagingViewModel<Collec>(repository){

}