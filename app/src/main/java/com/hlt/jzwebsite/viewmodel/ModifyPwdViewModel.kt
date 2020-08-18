package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.ModifyPwdRepository

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ModifyPwdViewModel(
    private val repository: ModifyPwdRepository,
    private val userToken: String?,
    private val oPwd: String?,
    private val nPwd: String?
) : ViewModel() {

    val modifyPwd = repository.postModifyPwd(userToken, oPwd, nPwd)

}