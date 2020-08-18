package com.hlt.jzwebsite.adapter

import android.content.Intent
import androidx.recyclerview.widget.DiffUtil
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.paging.BasePagingAdapter
import com.hlt.jzwebsite.model.Busines
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.ui.activity.VerificaCodeLoginActivity
import com.hlt.jzwebsite.ui.activity.WebActivity
import com.hlt.jzwebsite.ui.activity.WebDetailCommActivity
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils

/**
 * Created by lxb on 2020/2/24.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CooperationAdapter(retryCallback: () -> Unit) : BasePagingAdapter<Busines>(CooperationAdapter.diffCallback, retryCallback){
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Busines>() {
            override fun areContentsTheSame(oldItem: Busines, newItem: Busines): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Busines, newItem: Busines): Boolean =
                oldItem.id == newItem.id
        }
    }

    override fun getItemLayout() = R.layout.item_recy_cooperation

    override fun bind(holder: ViewHolder, item: Busines, position: Int) {
        holder.run {
            setText(R.id.tv_title, item.title)
            setText(R.id.tv_desc,item.summary)
            setText(R.id.tv_time,item.create_time)
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
                        putExtra(Constants.BUNDLE_KEY_CATID,item.cid.toString())
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