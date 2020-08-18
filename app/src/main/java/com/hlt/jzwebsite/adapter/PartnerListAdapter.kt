package com.hlt.jzwebsite.adapter

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.utils.ImageLoader
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder

/**
 * @author LXB
 * @description:
 * @date :2020/4/3 14:07
 */
class PartnerListAdapter(
    context: Context?,
    items: MutableList<UserInfo.ResultBean.CollaborateBean>,
    layoutResId: Int
) :
    SuperAdapter<UserInfo.ResultBean.CollaborateBean>(context, items, layoutResId) {

    override fun onBind(
        holder: SuperViewHolder?,
        viewType: Int,
        layoutPosition: Int,
        item: UserInfo.ResultBean.CollaborateBean?
    ) {
        val iv_menu = holder?.findViewById<ImageView>(R.id.iv_partner) as AppCompatImageView
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