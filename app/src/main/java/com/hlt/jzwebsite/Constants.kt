package com.hlt.jzwebsite

/**
 * Created by lxb on 2020/2/20.
 * 邮箱：2072301410@qq.com
 * TIP： 全局常量
 */
object Constants {

    const val APPLICATIONID_JZSP = "com.slodonapp.ywj_release"
    const val PERMISSION_CODE = 100
    const val URL_EXTENSION = "url_extension" //推广中心


    //--------------------------------budle--------------start------------------
    const val KNOWLEDGE = "knowledge"
    const val WEB_TITLE = "web_title"
    const val WEB_URL = "web_url"
    const val BUNDLE_KEY_TITLE_DETAIL = "title_detail"
    const val BUNDLE_KEY_ID = "id"
    const val BUNDLE_KEY_CATID = "catId"
    const val TITLE = "title"
    const val BUNDLE_KEY_PHONE = "phone"
    //--------------------------------budle--------------start------------------


    //--------------------------------sp--------------start------------------
    const val SP_DEVICES_ID = "devices_id"
    const val SP_ISFIRST_LAUNCHED = "isFirstLaunched"
    const val SP_USER_TOKEN = "user_token"
    const val SP_USER_TOKEN_ERROR = "token_error"
    const val SP_USER_ISLOGIN = "isLogin"
    const val SP_USER_UERDATA = "userdata"
    const val SP_USER_ID = "sp_user_id"
    const val SP_NICK_NAME = "sp_nick_name"
    const val SP_AVATAR = "sp_avatar"
    const val SP_LIST_ID = "sp_list_id"
    const val SP_USER_PHONE = "sp_user_phone"
    const val SP_HOME_PAGE_DATA = "home_page_data"
    const val SP_CONTACT_WAY="sp_contact_way"

    //--------------------------------sp----------------end----------------


    //--------------------------------message event----------------start----------------
    interface I_EVENT {
        companion object {
            val EVENT_MESSAGE_LOGIN = "event_message_login"            //退出时 返回主页
        }
    }

    //--------------------------------message event----------------end----------------


    //----------------------test ----------------------
    const val  resData :String = "nfNc9XSAp/gI7XVEAYGyzB0L5eIRqW3+lIXurIVQ2CefcsDQZgvfHz8vjl23Nhpz+5yt04nIT0v6hhCrrtOGIIwh8DFiV5DsIGWJ0Cjy2q9Zxk1S6IsU1krm2NLlxQvLcbbU6B7/Yqvyxs4kL1LX4bm0MFKQdoOKtms/aslAMlVzPLEnsBiTefciUqKwFtFN/ducewGdvnqzjGNBBIBTSv24xijKe8KBeOsCI0fkB5tDl+XMW0B8hkcmFUcdHqeYfP4c2KwbPe9SfYOPCKvCKZfo6y/6uwbeAV3YAHO61+Kc0gS31EXo3U+BX91UqKA9wtmlGhNKyiEvv4Hq2d3Vdw=="
    const val test_vidio = "http://main.gtt20.com/template/mobile/default/img/video.mp4"

}