package com.hlt.jzwebsite.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.extensions.checkBlank
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.repository.ModifyPwdRepository
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.Utils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.ModifyPwdViewModel
import kotlinx.android.synthetic.main.activity_modify_pwd.*
import kotlinx.android.synthetic.main.ui_toolbar_common.*

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ModifyPwdActivity : BaseActivity() {
    private var userToken: String? = null
    private var oldePwd: String? = null
    private var newPwd: String? = null
    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = ModifyPwdRepository(HttpManager.getInstance())
                return ModifyPwdViewModel(repository, userToken, oldePwd, newPwd) as T
            }
        }).get(ModifyPwdViewModel::class.java)
    }

    override var layoutId: Int = R.layout.activity_modify_pwd

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

    }

    override fun initData() {
        tv_title.text = getString(R.string.title_modify_pwd)
        userToken = SPUtils.getInstance().getString(Constants.SP_USER_TOKEN)
        Log.d(TAG, "userToken:===" + userToken)
        iv_close.apply {
            setOnClickListener { finish() }
        }
    }

    override fun subscribeUi() {

    }

    override fun initClickEvent() {
        super.initClickEvent()
        tv_confirm.setOnClickListener {
            // 优雅地判空
            oldePwd = et_old_pwd.checkBlank(resources.getString(R.string.check_blank_old_pwd))
                ?: return@setOnClickListener
            if (!Utils.validatePhonePass(oldePwd!!)) {
                ToastUtils.show(getString(R.string.modify_pwd_tip))
                return@setOnClickListener
            }
            newPwd = et_new_pwd.checkBlank(resources.getString(R.string.check_blank_new_pwd))
                ?: return@setOnClickListener
            if (!Utils.validatePhonePass(newPwd!!)) {
                ToastUtils.show(getString(R.string.modify_pwd_tip))
                return@setOnClickListener
            }
            viewModel.run {
                //修改密码
                handleData(modifyPwd) {
                    val state = it.result.state
                    val msg = it.result.msg
                    if (state == 2) {
                        ToastUtils.show(msg)
                        finish()
                    } else {
                        ToastUtils.show(msg)
                        return@handleData
                    }
                }
            }
        }
    }
}