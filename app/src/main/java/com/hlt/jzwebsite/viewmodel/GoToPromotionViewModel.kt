package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.GoToPromotionRepository

/**
 * @author LXB
 * @description:
 * @date :2020/3/17 15:12
 */
class GoToPromotionViewModel(
    repository: GoToPromotionRepository,
    userToken: String?
) : ViewModel() {

    val promotion = repository.postPromotion(userToken)
}