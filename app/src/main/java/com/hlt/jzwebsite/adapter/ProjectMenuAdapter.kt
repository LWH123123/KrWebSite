package com.hlt.jzwebsite.adapter

import android.content.Context
import android.widget.ImageView
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.model.Serve
import com.hlt.jzwebsite.utils.ImageLoader
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder

/**
 * Created by lxb on 2020/2/25.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ProjectMenuAdapter(context: Context, items: MutableList<Serve>, layoutResId: Int) :
    SuperAdapter<Serve>(context, items, layoutResId) {

    override fun onBind(
        holder: SuperViewHolder?,
        viewType: Int,
        layoutPosition: Int,
        item: Serve?
    ) {
        val text = holder?.run { run({ setText(R.id.tv_menu, item?.title) }) }
        val iv_menu: ImageView = holder?.findViewById<ImageView>(R.id.iv_menu) as ImageView
        setImg(context, iv_menu, HttpConstants.BASE_URL + item?.image)


    }

    fun setImg(
        context: Context,
        imageView: ImageView,
        picUrl: String?
    ) {
        if (picUrl?.isNotBlank()!!) {
            context.let { ImageLoader.loadNoPlaceHolder(it, picUrl, imageView) }
        }
    }
}