package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.WebDetailRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 9:49
 */
class WebDetailViewModel(
    private val repository: WebDetailRepository,
    private val id: String?,
    private val catId: String?,
    private val uid: String?,
    private val title_detail: String?
) : ViewModel() {

    val detailData = repository.postWebDetail(id, catId,uid,title_detail)

}