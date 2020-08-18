package com.hlt.jzwebsite.viewmodel

import com.hlt.jzwebsite.base.paging.BasePagingViewModel
import com.hlt.jzwebsite.model.Crlist
import com.hlt.jzwebsite.paging.repository.ClassListRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 12:10
 */
class ClassListViewModel(
    repository: ClassListRepository
) : BasePagingViewModel<Crlist>(repository) {

}