package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.SendSmscLoginRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/19 14:33
 */
class SendSmscLoginViewModel(
    repository: SendSmscLoginRepository,
    guid: String?,
    phone: String?
) : ViewModel() {

    private var phone: String? = null
    private var type: String? = null


    fun setVerticodeParams(mobile: String, type: String) {
        this.phone = mobile
        this.type = type
    }

    val phoneVerticode = repository.postSendVertifycLogin(phone, guid)


}