package com.hlt.jzwebsite.adapter

import android.content.Context
import android.widget.ImageView
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.model.Classroom
import com.hlt.jzwebsite.utils.ImageLoader
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder

/**
 * Created by lxb on 2020/2/25.
 * 邮箱：2072301410@qq.com
 * TIP：培训课堂
 */
class TrainingClassAdapter(context: Context, items: MutableList<Classroom>, layoutResId: Int) :
    SuperAdapter<Classroom>(context, items, layoutResId) {

    override fun onBind(
        holder: SuperViewHolder?,
        viewType: Int,
        layoutPosition: Int,
        item: Classroom?
    ) {
        val title = holder?.run { run({ setText(R.id.tv_title, item?.title) }) }
        val collection = holder?.run { run({ setText(R.id.tv_collection, item?.collect.toString()+"收藏") }) }
        val views = holder?.run { run({ setText(R.id.tv_views, item?.hits.toString()+"次浏览") }) }
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