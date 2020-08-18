package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.paging.repository.AppUpdateRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/12 11:56
 */
class AppUpdateViewModel(private val repository: AppUpdateRepository) : ViewModel() {
    val appUpdate = repository.postAppUpdate()

}