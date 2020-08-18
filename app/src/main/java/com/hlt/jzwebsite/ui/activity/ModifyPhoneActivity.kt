package com.hlt.jzwebsite.ui.activity

import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.extensions.checkBlank
import com.hlt.jzwebsite.extensions.regexPhone
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.SmsResult
import com.hlt.jzwebsite.repository.ModifyPhoneRepository
import com.hlt.jzwebsite.repository.SendSmsCodeRepository
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.ModifyPhoneViewModel
import com.hlt.jzwebsite.viewmodel.SendSmsCodeViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_modify_phone.*
import kotlinx.android.synthetic.main.activity_modify_pwd.tv_confirm
import kotlinx.android.synthetic.main.ui_toolbar_common.*

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ModifyPhoneActivity : BaseActivity() {
    private var phone: String? = null
    private var userToken: String? = null
    private var stateCode: Boolean = false

    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                100 -> btn_verify_code.text = "剩余时间(${time})秒"
                200 -> {
                    btn_verify_code.isEnabled = true
                    btn_verify_code.text = "点击重发"
                    time = 60
                }
            }
        }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = SendSmsCodeRepository(HttpManager.getInstance())
                return SendSmsCodeViewModel(repository, userToken, phone) as T
            }
        }).get(SendSmsCodeViewModel::class.java)
    }

    override var layoutId: Int = R.layout.activity_modify_phone

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun initData() {
        tv_title.text = getString(R.string.title_modify_phone)
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
        btn_verify_code.setOnClickListener {
            //获取验证码
            val mobile = et_phone.checkBlank(resources.getString(R.string.check_blank_phone))
                ?: return@setOnClickListener
            if (!regexPhone(mobile)) {
                ToastUtils.show(resources.getString(R.string.check_phone_invalid))
                return@setOnClickListener
            }
            phone = mobile
            viewModel.run {
                //发短信验证码
                handleData(sendVerticode) {
                    Logger.d("发送验证码成功---->>>")
                    val smsResult = it.result as SmsResult
                    val state: Int = smsResult?.state
                    if (state == 200) {
                        Logger.e("获取验证码成功")
                        stateCode = true
                        //发送验证码
                        btn_verify_code.isEnabled = false
                        Thread(cutTask()).start()
                    } else {
                        stateCode = false
                        Logger.e("获取验证码失败")
                        ToastUtils.show(smsResult.msg)
                        return@handleData
                    }
                }
            }

        }
        tv_confirm.setOnClickListener {
            //绑定手机号
            val mobile = et_phone.checkBlank(resources.getString(R.string.check_blank_phone))
                ?: return@setOnClickListener
            if (!regexPhone(mobile)) {
                ToastUtils.show(resources.getString(R.string.check_phone_invalid))
                return@setOnClickListener
            }
            val code =
                et_verticode.checkBlank(resources.getString(R.string.check_blank_verti_code))
                    ?: return@setOnClickListener

            val modifyPhoneViewModel: ModifyPhoneViewModel =
                ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        val repository = ModifyPhoneRepository(HttpManager.getInstance())
                        return ModifyPhoneViewModel(repository, userToken, phone, code) as T
                    }
                }).get(ModifyPhoneViewModel::class.java)
            //修改密码
            handleData(modifyPhoneViewModel.modifyPhone) {
                Logger.d("绑定手机号成功---->>>")
                phone?.let { it1 -> SPUtils.getInstance().put(Constants.SP_USER_PHONE, it1) }
                val state: Int = it.result?.state
                if (state == 0) {
                    ToastUtils.show(getString(R.string.bind_phone_tip_0))
                    return@handleData
                } else if (state == 1) {
                    ToastUtils.show(getString(R.string.bind_phone_tip_1))
                    return@handleData
                } else if (state == 2) {
                    ToastUtils.show(getString(R.string.bind_phone_tip_2))
                    return@handleData
                } else if (state == 3) {
                    ToastUtils.show(getString(R.string.bind_phone_tip_3))
                    finish()
                } else {
                    ToastUtils.show(getString(R.string.bind_phone_tip_4))
                    return@handleData
                }

            }
        }
    }

    var time = 60

    inner class cutTask() : Runnable {
        override fun run() {
            while (time > 0) {
                mHandler.sendEmptyMessage(100)
                SystemClock.sleep(999)
                time--
            }
            mHandler.sendEmptyMessage(200)
        }

    }

}