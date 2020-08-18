package com.hlt.jzwebsite.adapter

import android.content.Intent
import androidx.recyclerview.widget.DiffUtil
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.paging.BasePagingAdapter
import com.hlt.jzwebsite.model.Project
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.ui.activity.VerificaCodeLoginActivity
import com.hlt.jzwebsite.ui.activity.WebActivity
import com.hlt.jzwebsite.ui.activity.WebDetailCommActivity
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.Utils
import com.hlt.jzwebsite.utils.java.SPUtils

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:31
 */
class ProjectListAdapter(retryCallback: () -> Unit) :
    BasePagingAdapter<Project>(diffCallback, retryCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Project>() {
            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun getItemLayout() = R.layout.item_recy_home_project

    override fun bind(holder: ViewHolder, item: Project, position: Int) {
        holder.run {
            setText(R.id.title, item?.title)
            setText(R.id.subtitle, item?.summary)
//            setText(R.id.time, Utils.transToString(item?.create_time))
            setImg(
                context,
                R.id.iv_theme,
                HttpConstants.BASE_URL + item?.image
            )
        }
        holder.itemView.setOnClickListener {
            val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
            if (!isLogin) {
                Intent(context, VerificaCodeLoginActivity::class.java).run {
                    context?.startActivity(this)
                }
            } else {
                val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
                if (tokenError == -1) { //wx token 失效
                    SPUtils.getInstance().clear()
                    ToastUtils.show(context.getString(R.string.relogin_tip))
                    return@setOnClickListener
                }
                if (item.source.isNullOrEmpty()) {
                    Intent(context, WebDetailCommActivity::class.java).run {
                        putExtra(Constants.BUNDLE_KEY_ID, item.id.toString())
                        putExtra(Constants.BUNDLE_KEY_CATID, item.catid.toString())
                        putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
                        context?.startActivity(this)
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
}