package com.hlt.jzwebsite.viewmodel

import androidx.lifecycle.ViewModel
import com.hlt.jzwebsite.repository.MainRepository
import com.hlt.jzwebsite.wxapi.WXUserInfo

/**
 * Created by lxb on 2020/2/27.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class MainViewModel(
    private val repository: MainRepository,
    info: WXUserInfo
) : ViewModel() {

//    private var nickname: String? = null
//    private var gender: Int? = null
//    private var avatar: String? = null
//    private var province: String? = null
//    private var city: String? = null
//    private var area: String? = null
//    private var unionid: String? = null

//    fun setParams(openid: String, nickname: String,gender: Int,
//                  avatar: String,province: String, city: String
//                  ,area: String, unionid: String) {
//        this.openid = openid
//        this.nickname = nickname
//        this.gender = gender
//        this.avatar = avatar
//        this.province = province
//        this.city = city
//        this.area = area
//        this.unionid = unionid
//    }
    val login = repository.postLogin(info.openid,
        info.nickname,
        info.getSex(),
        info.headimgurl,
        info.province,
        info.city,
        info.country,
        info.unionid
)

}