package com.hlt.jzwebsite.adapter

import android.content.Context
import android.widget.ImageView
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.model.Company
import com.hlt.jzwebsite.utils.ImageLoader
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder

/**
 * Created by lxb on 2020/2/25.
 * 邮箱：2072301410@qq.com
 * TIP： 企业介绍
 */
class IntroduceAdapter(context: Context, items: MutableList<Company>, layoutResId: Int) :
    SuperAdapter<Company>(context, items, layoutResId) {

    override fun onBind(
        holder: SuperViewHolder?,
        viewType: Int,
        layoutPosition: Int,
        item: Company?
    ) {
        val iv_menu: ImageView = holder?.findViewById<ImageView>(R.id.iv_menu) as ImageView
        setImg(context, iv_menu, HttpConstants.BASE_URL + item?.image)


    }

    fun setImg(
        context: Context,
        imageView: ImageView,
        picUrl: String?
    ) {
        if (picUrl?.isNotBlank()!!) {
            context.let { ImageLoader.load(it, picUrl, imageView) }
        }
    }
}