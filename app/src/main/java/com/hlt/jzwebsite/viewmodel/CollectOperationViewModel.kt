package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel

/**
 * @author LXB
 * @description: 添加收藏/取消收藏
 * @date :2020/3/5 16:14
 */
class CollectOperationViewModel(
    repository: CollectOperationRepository,
    uid: String?,
    aid: String?,
    status: String?
) : ViewModel() {

    private var phone: String? = null
    private var code: String? = null

    fun setParams(phone: String, code: String) {
        this.phone = phone
        this.code = code
    }

    val collect = repository.postToCollect(uid, aid, status)


}