package com.hlt.jzwebsite.adapter

import android.content.Context
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.utils.java.SPUtils
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder


/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class SettingAdapter(context: Context, items: List<String>, layoutResId: Int) :
    SuperAdapter<String>(context, items, layoutResId) {

    override fun onBind(
        holder: SuperViewHolder?,
        viewType: Int,
        layoutPosition: Int,
        item: String?
    ) {
        val title = holder?.run { run({ setText(R.id.tv_txt, item) }) }
//        if (layoutPosition ==0){
//            val tv_txt_right = holder?.run { run({ setText(R.id.tv_txt_right,context.getString(R.string.setting_txt_modify) ) }) }
//        }else if (layoutPosition ==1){
//            val mobile = SPUtils.getInstance().getString(Constants.SP_USER_PHONE)
//            if (!mobile.isNullOrEmpty()){
//                val sb = StringBuilder(mobile)
//                val showPhone = sb.replace(3, 7, "****").toString()
//                val tv_txt_right = holder?.run { run({ setText(R.id.tv_txt_right,showPhone ) }) }
//            }else{
//                val tv_txt_right = holder?.run { run({ setText(R.id.tv_txt_right,context.getString(R.string.setting_txt_unbind_phone) ) }) }
//            }
//        }

        val mobile = SPUtils.getInstance().getString(Constants.SP_USER_PHONE)
        if (!mobile.isNullOrEmpty()) {
            val sb = StringBuilder(mobile)
            val showPhone = sb.replace(3, 7, "****").toString()
            val tv_txt_right = holder?.run { run({ setText(R.id.tv_txt_right, showPhone) }) }
        } else {
            val tv_txt_right = holder?.run {
                run({
                    setText(
                        R.id.tv_txt_right,
                        context.getString(R.string.setting_txt_unbind_phone)
                    )
                })
            }
        }

    }

}