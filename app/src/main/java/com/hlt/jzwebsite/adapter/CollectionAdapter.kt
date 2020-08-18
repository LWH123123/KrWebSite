package com.hlt.jzwebsite.adapter

import android.content.Intent
import androidx.recyclerview.widget.DiffUtil
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.paging.BasePagingAdapter
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.ui.activity.WebActivity
import com.hlt.jzwebsite.ui.activity.WebDetailActivity
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CollectionAdapter (retryCallback: () -> Unit) : BasePagingAdapter<Collec>(diffCallback, retryCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Collec>() {
            override fun areContentsTheSame(oldItem: Collec, newItem: Collec): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Collec, newItem: Collec): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun getItemLayout() = R.layout.item_recy_my_collection

    override fun bind(holder: ViewHolder, item: Collec, position: Int) {
        holder.run {
            setText(R.id.title, item.title)
            setText(R.id.subtitle, item.summary)
            setText(R.id.time, item.create_time)
            setImg(context, R.id.iv_theme, HttpConstants.BASE_URL + item.image)
        }
        holder.itemView.setOnClickListener {
            val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
            if (tokenError == -1) { //wx token 失效
                SPUtils.getInstance().clear()
                ToastUtils.show(context.getString(R.string.relogin_tip))
                return@setOnClickListener
            }
            if (item.source.isNullOrEmpty()) {
                Intent(context, WebDetailActivity::class.java).run {
                    putExtra(Constants.BUNDLE_KEY_ID, item.id.toString())
                    putExtra(Constants.BUNDLE_KEY_CATID, item.cid.toString())
                    putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
                    context?.startActivity(this)
                }
            }else{
                Intent(context, WebActivity::class.java).run {
                    putExtra(Constants.WEB_TITLE, item.title)
                    putExtra(Constants.WEB_URL, item.source)
                    context.startActivity(this)
                }
            }
        }
    }
}