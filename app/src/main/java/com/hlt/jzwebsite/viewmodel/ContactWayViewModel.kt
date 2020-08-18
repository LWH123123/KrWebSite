package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.ContactWayRepository

/**
 * @author lwh
 * @description :
 * @date 2020/3/27.
 */
class ContactWayViewModel(repository: ContactWayRepository): ViewModel()  {
    var contactway=repository.postContactWay()
}