package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.view.View
import android.widget.TextView
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.SettingAdapter
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.eventbus.LoginMessageEvent
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.utils.MessageDialog
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.java.AppManager
import com.hlt.jzwebsite.utils.java.SPUtils
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.ui_toolbar_common.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class SettingActivity : BaseActivity() {
    private lateinit var adapter: SettingAdapter
    private val titles = arrayOf(
       "更换手机号"
    )

    override var layoutId: Int = R.layout.activity_setting

    override fun initData() {
        tv_title.text = getString(R.string.title_my_setting)
        iv_close.apply {
            setOnClickListener { finish() }
        }
    }

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyItemChanged(1)
    }

    override fun subscribeUi() {
        val stringArray = resources.getStringArray(R.array.my_setting_txt)
        val arrayList = titles.toList() as List<String> //kotlin  Array 转 List
        adapter =
            SettingAdapter(this, arrayList, R.layout.item_recy_my_setting)
        recyclerview.adapter = adapter
//        val footer =
//            LayoutInflater.from(this).inflate(R.layout.ui_layout_setting_footer_view, null)
//        initHeaderData(footer)
//        adapter.addFooterView(footer)
        adapter.setOnItemClickListener { itemView, viewType, position ->
            when (position) {
//                0 ->
//                    Intent(this, ModifyPwdActivity::class.java).run {
//                        startActivity(this)
//                    }
                0 ->
                    Intent(this, ModifyPhoneActivity::class.java).run {
                        startActivity(this)
                    }
                else -> return@setOnItemClickListener
            }
        }
    }

    override fun initClickEvent() {
        super.initClickEvent()
        //退出登录
        tv_exit_app.setOnClickListener {
            MessageDialog().showConfirmAndCancelDialog(
                this,
                getString(R.string.dialog_tip_exit_app),
                "",
                object : MessageDialog.DialogClick {
                    override fun onCancelClickListener() {}
                    override fun onSureClickListener() {
                        mExitLoginOperation()
                        EventBus.getDefault().post(LoginMessageEvent(Constants.I_EVENT.EVENT_MESSAGE_LOGIN))
                        AppManager.getAppManager().finishAllActivity()
                        Intent(mContext,MainActivity::class.java).run {
                            startActivity(this)
                        }
                    }
                })
        }
    }

    /**
     * 退出登录,清空用户信息
     */
    private fun mExitLoginOperation() {
        SPUtils.getInstance().clear()
        SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, false)
        val user = UserInfo.getInstance()
        SPUtils.getInstance().clearShareData()
        user.clearUserData()
    }

    private fun initHeaderData(headerView: View) {
        val tv_exit_app = headerView.findViewById(R.id.tv_exit_app) as TextView
        tv_exit_app.setOnClickListener {
            finish()
        }
    }
}