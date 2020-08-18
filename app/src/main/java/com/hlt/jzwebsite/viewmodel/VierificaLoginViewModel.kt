package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.VierificaLoginRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/19 15:12
 */
class VierificaLoginViewModel(
    repository: VierificaLoginRepository,
    guid: String?,
    phone: String?,
    code: String?
) : ViewModel() {

    val vierificaLogin = repository.postvierificaLogin(guid, phone, code)


}