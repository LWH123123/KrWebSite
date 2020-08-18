package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.ModifyPhoneRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/4 17:44
 */
class ModifyPhoneViewModel(
    repository: ModifyPhoneRepository,
    userToken: String?,
    phone: String?,
    code: String?
) : ViewModel() {

    private var phone: String? = null
    private var code: String? = null

    fun setParams(phone: String, code: String) {
        this.phone = phone
        this.code = code
    }

    val modifyPhone = repository.postModifyPhone(userToken,phone,code)


}