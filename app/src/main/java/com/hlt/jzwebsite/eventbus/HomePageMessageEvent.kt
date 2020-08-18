package com.hlt.jzwebsite.eventbus

import com.hlt.jzwebsite.model.HomeBean

/**
 * @author LXB
 * @description:
 * @date :2020/3/10 10:46
 */
class HomePageMessageEvent internal constructor(homepagemessage: HomeBean) {
    private var data:HomeBean? =null
    init {
        this.data = homepagemessage
    }
    internal fun getMessage():HomeBean?{
        return data
    }
    internal fun sendMessage(homepagemessage:HomeBean){
        this.data = homepagemessage
    }
}