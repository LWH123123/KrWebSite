package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.MineRepository

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class MineViewModel(
    private val repository: MineRepository,
    userToken: String?
): ViewModel() {

    val userInfo = repository.postUserInfo(userToken)

}