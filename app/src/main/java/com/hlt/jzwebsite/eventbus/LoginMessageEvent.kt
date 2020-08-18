package com.hlt.jzwebsite.eventbus

/**
 * @author LXB
 * @description: 登录消息事件
 * @date :2020/3/10 9:40
 *
 * tip : 参考博文：https://www.codercto.com/a/62039.html
 */
class LoginMessageEvent internal constructor(loginMessage:String){
    private var message:String? =null
    init {
        this.message = loginMessage
    }
    internal fun getMessage():String?{
        return message
    }
    internal fun sendMessage(loginMessage:String){
        this.message = loginMessage
    }
}