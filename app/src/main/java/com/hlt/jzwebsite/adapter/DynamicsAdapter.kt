package com.hlt.jzwebsite.adapter

import android.content.Intent
import androidx.recyclerview.widget.DiffUtil
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.paging.BasePagingAdapter
import com.hlt.jzwebsite.model.Planning
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.ui.activity.VerificaCodeLoginActivity
import com.hlt.jzwebsite.ui.activity.WebActivity
import com.hlt.jzwebsite.ui.activity.WebDetailCommActivity
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.widget.java.ShowAllTextView

/**
 * Created by lxb on 2020/2/23.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class DynamicsAdapter(retryCallback: () -> Unit) :
    BasePagingAdapter<Planning>(DynamicsAdapter.diffCallback, retryCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Planning>() {
            override fun areContentsTheSame(oldItem: Planning, newItem: Planning): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Planning, newItem: Planning): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun getItemLayout() = R.layout.item_dynamics

    override fun bind(holder: ViewHolder, item: Planning, position: Int) {
        val title = holder.getView(R.id.title) as ShowAllTextView
        title.maxShowLines = 1
        holder.run {
            setText(R.id.title, item.title)
            setText(R.id.subtitle, item.summary)
            setText(R.id.time, item.create_time)
            setImg(context, R.id.iv_theme, HttpConstants.BASE_URL + item.image)
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
                if (item.source.isNullOrEmpty()){
                    Intent(context, WebDetailCommActivity::class.java).run {
                        putExtra(Constants.BUNDLE_KEY_ID, item.id.toString())
                        putExtra(Constants.BUNDLE_KEY_CATID, item.cid.toString())
                        putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
                        context?.startActivity(this)
                    }
                }else{
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