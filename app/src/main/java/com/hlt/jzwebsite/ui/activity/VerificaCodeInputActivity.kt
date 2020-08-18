package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.eventbus.LoginMessageEvent
import com.hlt.jzwebsite.extensions.checkBlank
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.SmsResult
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.repository.SendSmsCodeRepository
import com.hlt.jzwebsite.repository.SendSmscLoginRepository
import com.hlt.jzwebsite.repository.VierificaLoginRepository
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.AppManager
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.SendSmsCodeViewModel
import com.hlt.jzwebsite.viewmodel.SendSmscLoginViewModel
import com.hlt.jzwebsite.viewmodel.VierificaLoginViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_verificac_input.*
import kotlinx.android.synthetic.main.ui_toolbar_common.*
import org.greenrobot.eventbus.EventBus

/**
 * @author LXB
 * @description: 验证码输入
 * @date :2020/3/19 12:28
 */
class VerificaCodeInputActivity : BaseActivity() {
    private var phone: String? = null
    private var guid: String? = null
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
                val repository = SendSmscLoginRepository(HttpManager.getInstance())
                return SendSmscLoginViewModel(repository, guid, phone) as T
            }
        }).get(SendSmscLoginViewModel::class.java)
    }

    override fun doBeforeSetContent() {
        super.doBeforeSetContent()
        //将window扩展至全屏，也就是全屏显示，并且不会覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        //为了避免在状态栏的显示状态发生变化时重新布局，从而避免界面卡顿
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override var layoutId: Int = R.layout.activity_verificac_input

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.immersive(window, R.color.transparent, 0.0f)
    }

    override fun initData() {
        toolbar.apply {
            visibility = View.GONE
            setBackgroundColor(resources.getColor(R.color.transparent))
        }
        iv_close.apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
        iv_back.apply {
            setOnClickListener { finish() }
        }
        tv_title.apply {
            visibility = View.VISIBLE
            text = getString(R.string.title_verifica_login)
            isSelected = true
        }
        guid = App.myDeviceID
        Log.d(TAG, "guid:===" + guid)
        iv_close.apply {
            setOnClickListener { finish() }
        }
        getIntentData()
    }

    private fun getIntentData() {
        intent.extras?.apply {
            phone = getString(Constants.BUNDLE_KEY_PHONE,"")
        }
        val sb = StringBuilder(phone)
        val showPhone = sb.replace(3, 7, "****").toString()
        tv_tip.text = "已发送到：+86 " + showPhone

    }

    override fun subscribeUi() {
        //一进页面就，获取验证码
        viewModel.run {
            //发短信验证码
            handleData(phoneVerticode) {
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
                    tv_error.text = smsResult.msg
                    ToastUtils.show(smsResult.msg)
                    return@handleData
                }
            }
        }
        //验证码输入完成
        et_verticode.setInputListener {
            Log.d(TAG, "验证码输入完成----->>>" + it)
            mLogin(it)
        }

    }

    private fun mLogin(vcode: String) {
        val code = et_verticode.checkBlank(vcode) ?: return
        val loginViewModel: VierificaLoginViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repository = VierificaLoginRepository(HttpManager.getInstance())
                    return VierificaLoginViewModel(repository, guid, phone, code) as T
                }
            }).get(VierificaLoginViewModel::class.java)

        handleData(loginViewModel.vierificaLogin) {
            Logger.d("手机验证码登录成功---->>>")
            if (it.result.state ==200){
                val userInfo: UserInfo.ResultBean? = UserInfo().result
                userInfo?.setToken(it.result?.user_token)

//            var userToken: String by PreferenceUtils(Constants.SP_USER_TOKEN, it.user_token)
                SPUtils.getInstance().put(Constants.SP_USER_TOKEN, it.result?.user_token)
                SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, true)
                ToastUtils.show("登录成功")
                //登录成功后 ，根据不同入口处理业务逻辑
//                EventBus.getDefault().post(LoginMessageEvent(Constants.I_EVENT.EVENT_MESSAGE_LOGIN))
//                AppManager.getAppManager().finishAllActivity()
//                Intent(mContext,MainActivity::class.java).run {
//                    startActivity(this)
//                }
                finish()
            }else{
               ToastUtils.show(it.result.msg)
            }

        }

    }

    override fun initClickEvent() {
        super.initClickEvent()
        //获取验证码
        btn_verify_code.setOnClickListener {
//            viewModel.run {
//                //发短信验证码
//                handleData(phoneVerticode) {
//                    Logger.d("发送验证码成功---->>>")
//                    val smsResult = it.result as SmsResult
//                    val state: Int = smsResult?.state
//                    if (state == 200) {
//                        Logger.e("获取验证码成功")
//                        stateCode = true
//                        //发送验证码
//                        btn_verify_code.isEnabled = false
//                        Thread(cutTask()).start()
//                    } else {
//                        stateCode = false
//                        Logger.e("获取验证码失败")
//                        tv_error.text = smsResult.msg
//                        ToastUtils.show(smsResult.msg)
//                        return@handleData
//                    }
//                }
//            }
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