package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.SendSmsCodeRepository

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class SendSmsCodeViewModel(
    repository: SendSmsCodeRepository,
    userToken: String?,
    phone: String?
) : ViewModel() {

    private var phone: String? = null
    private var type: String? = null


    fun setVerticodeParams(mobile: String, type: String) {
        this.phone = mobile
        this.type = type
    }

    val sendVerticode = repository.postSendVertifyCode(phone, userToken)


}