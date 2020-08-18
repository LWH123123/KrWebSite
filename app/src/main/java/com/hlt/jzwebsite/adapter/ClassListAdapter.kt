package com.hlt.jzwebsite.adapter

import android.content.Intent
import androidx.recyclerview.widget.DiffUtil
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.paging.BasePagingAdapter
import com.hlt.jzwebsite.model.Crlist
import com.hlt.jzwebsite.ui.activity.VerificaCodeLoginActivity
import com.hlt.jzwebsite.ui.activity.WebActivity
import com.hlt.jzwebsite.ui.activity.WebDetailActivity
import com.hlt.jzwebsite.utils.java.SPUtils

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 12:17
 */
class ClassListAdapter(retryCallback: () -> Unit) :
    BasePagingAdapter<Crlist>(diffCallback, retryCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Crlist>() {
            override fun areContentsTheSame(oldItem: Crlist, newItem: Crlist): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Crlist, newItem: Crlist): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun getItemLayout() = R.layout.item_recy_home_class

    override fun bind(holder: ViewHolder, item: Crlist, position: Int) {
        holder.run {
            setText(R.id.tv_title, item?.title)
            setText(R.id.tv_collection, item?.collect.toString() + "收藏")
            setText(R.id.tv_views, item?.hits.toString() + "次浏览")
            setImg(
                context,
                R.id.iv_menu,
                HttpConstants.BASE_URL + item?.image
            )
        }
        holder.itemView.setOnClickListener {
            if (item.source.isNullOrEmpty()) {
//                val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
//                if (tokenError == -1) { //wx token 失效
//                    SPUtils.getInstance().clear()
//                    ToastUtils.show(context.getString(R.string.relogin_tip))
//                    return@setOnClickListener
//                }
                val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
                if (!isLogin) {
                    Intent(context, VerificaCodeLoginActivity::class.java).run {
                        context?.startActivity(this)
                    }
                } else {
                    Intent(context, WebDetailActivity::class.java).run {
                        putExtra(Constants.BUNDLE_KEY_ID, item.id.toString())
                        putExtra(Constants.BUNDLE_KEY_CATID, item.cid.toString())
                        putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
                        context?.startActivity(this)
                    }
                }
            } else {
                Intent(context, WebActivity::class.java).run {
                    putExtra(Constants.WEB_TITLE, item.title)
                    putExtra(Constants.WEB_URL, item.source)
                    context?.startActivity(this)
                }
            }
        }
    }
}