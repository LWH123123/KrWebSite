package com.hlt.jzwebsite.viewmodel

import com.hlt.jzwebsite.base.paging.BasePagingViewModel
import com.hlt.jzwebsite.model.Planning
import com.hlt.jzwebsite.paging.repository.DynamicsRepository

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class DynamicsViewModel(repository:DynamicsRepository): BasePagingViewModel<Planning>(repository) {
}