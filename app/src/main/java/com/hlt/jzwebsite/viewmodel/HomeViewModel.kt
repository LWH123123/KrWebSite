package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.paging.repository.HomeRepository

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class HomeViewModel(private val repository: HomeRepository) : ViewModel() {
    val homeData = repository.postHomeData()

}