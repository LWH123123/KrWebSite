package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.alibaba.fastjson.JSON
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.eventbus.LoginMessageEvent
import com.hlt.jzwebsite.extensions.checkBlank
import com.hlt.jzwebsite.extensions.regexPhone
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.repository.MainRepository
import com.hlt.jzwebsite.repository.MineRepository
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.MainViewModel
import com.hlt.jzwebsite.viewmodel.MineViewModel
import com.hlt.jzwebsite.wxapi.WXEntryActivity
import com.hlt.jzwebsite.wxapi.WXUserInfo
import com.kongzue.dialog.v3.CustomDialog
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_verifica_code_login.*
import kotlinx.android.synthetic.main.ui_toolbar_common.*
import org.greenrobot.eventbus.EventBus
import java.io.IOException


/**
 * @author LXB
 * @description: 验证码登录
 * @date :2020/3/19 11:20
 */
class VerificaCodeLoginActivity : BaseActivity(), View.OnClickListener {
    private var phone: String? = null
    private var userToken: String? = null

    override fun doBeforeSetContent() {
        super.doBeforeSetContent()
        //将window扩展至全屏，也就是全屏显示，并且不会覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        //为了避免在状态栏的显示状态发生变化时重新布局，从而避免界面卡顿
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override var layoutId: Int = R.layout.activity_verifica_code_login

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
        userToken = SPUtils.getInstance().getString(Constants.SP_USER_TOKEN)
        Log.d(TAG, "userToken:===" + userToken)
        iv_close.apply {
            setOnClickListener { finish() }
        }
        val spannableString = SpannableString(getString(R.string.login_verifica_tip_agree))
        val colorSpan = ForegroundColorSpan(Color.parseColor("#CC0000"))
        spannableString.setSpan(colorSpan, 9, 18, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_userAgree.text = spannableString
    }

    override fun initClickEvent() {
        super.initClickEvent()
        btn_verify_code.setOnClickListener(this)
        iv_loginWx.setOnClickListener(this)
        tv_userAgree.setOnClickListener(this)
    }

    override fun subscribeUi() {
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_verify_code -> {//获取验证码
                //校验参数合法性
                val mobile = et_phone.checkBlank(resources.getString(R.string.check_blank_phone))
                    ?: return
                if (!regexPhone(mobile)) {
                    ToastUtils.show(resources.getString(R.string.check_phone_invalid))
//                btn_verify_code.background = getDrawable(R.drawable.shape_btn_sms_radius)
                    tv_tip.text = getString(R.string.et_phone_tip_error)
                    return
                }
                phone = mobile
//            btn_verify_code.background = getDrawable(R.drawable.shape_btn_radius_boder)
                Intent(mContext, VerificaCodeInputActivity::class.java).run {
                    putExtra(Constants.BUNDLE_KEY_PHONE, phone)
                    mContext?.startActivity(this)
                }
            }
            R.id.iv_loginWx -> { //微信授权登录
                toWxAuthorizedLogin()
            }
            R.id.tv_userAgree -> { //用户协议
                showDialog()
            }

        }
    }

    /**
     * 微信授权登录
     */
    private fun toWxAuthorizedLogin() {
        Log.d(TAG, "微信授权登录---->>>")
        WXEntryActivity.loginWeixin(
            App.instance,
            App.sApi,
            object : WXEntryActivity.ApiCallback<WXUserInfo> {
                override fun onSuccess(info: WXUserInfo) {
                    Log.d("onSuccess()--->>", info.toString())
                    postLogin(info)
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    Log.d("onError()--->>", errorMsg)
                    //判断 WX token 是否失效
                    UserInfo.getInstance().result.tokenError = -1
                    ToastUtils.show("用户信息过期")
                }

                override fun onFailure(e: IOException) {
                    Log.d("onFailure()--->>", e.message)
                    UserInfo.getInstance().result.tokenError = -1
                    ToastUtils.show("用户信息过期")
                }

                override fun onPayError(res: String) {

                }
            })
    }

    /**
     * 将微信授权成功后，返回的数据，上传自己的服务器
     */
    private fun postLogin(
        info: WXUserInfo
    ) {
        info.guid = App.myDeviceID
        var viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = MainRepository(HttpManager.getInstance())
                return MainViewModel(repository, info) as T
            }
        }).get(MainViewModel::class.java)

        //登录
        handleData(viewModel.login) {
            Logger.d(TAG, "======>>>>登录成功")
//            val userInfo: UserInfo.ResultBean? = UserInfo().result
//            userInfo?.setToken(it.result?.user_token)
            //============获取用户信息接口====================
            if (!it.result?.user_token.isNullOrBlank()) {//登录后，才获取用户信息
                val mineVM = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                        val repository = MineRepository(HttpManager.getInstance())
                        return MineViewModel(repository, it.result?.user_token) as T
                    }
                }).get(MineViewModel::class.java)
                handleData(mineVM.userInfo) {
                    Logger.d("获取我的数据成功---->>")
                    mSaveUserInfo(it)
                    ToastUtils.show("登录成功")
                    finish()
                }
            } else { //未登录，设置默认数据
            }
            //============获取用户信息接口====================
//            var userToken: String by PreferenceUtils(Constants.SP_USER_TOKEN, it.user_token)
            SPUtils.getInstance().put(Constants.SP_USER_TOKEN, it.result?.user_token)
            SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, true)
//            ToastUtils.show("登录成功")
            //登录成功后 ，根据不同入口处理业务逻辑
//            EventBus.getDefault().post(LoginMessageEvent(Constants.I_EVENT.EVENT_MESSAGE_LOGIN))
//            AppManager.getAppManager().finishAllActivity()
////            Intent(mContext, MainActivity::class.java).run {
////                startActivity(this)
////            }
//            finish()
        }

    }


    /**
     *  保存用户信息
     */
    private fun mSaveUserInfo(it: UserInfo) {
        val resultJson = JSON.toJSONString(it)   //将注册后的实体 转为json 串，便于保存到sp
        SPUtils.getInstance().put(Constants.SP_USER_UERDATA, resultJson)
        SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, true)
        SPUtils.getInstance().put(Constants.SP_USER_ID, it.result.member_id)
        SPUtils.getInstance().put(Constants.SP_USER_PHONE, it.result.member_mobile)
        SPUtils.getInstance().put(Constants.SP_NICK_NAME, it.result.username)
        SPUtils.getInstance().put(Constants.SP_AVATAR, it.result.avator)
        UserInfo.parseUserFromJsonInSP(UserInfo.mContext)
    }


    private fun showDialog() {
        CustomDialog.show(
            this@VerificaCodeLoginActivity,
            R.layout.dialog_full_user_agree,
            CustomDialog.OnBindView { customDialog: CustomDialog, rootView: View ->

                val tv_Agree = rootView.findViewById<View>(R.id.tv_Agree) as TextView
                val spannableString = SpannableString(getString(R.string.dialog_txt_content))
                val colorSpan = ForegroundColorSpan(Color.parseColor("#CC0000"))
                spannableString.setSpan(colorSpan, 60, 79, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                tv_Agree.text = spannableString
                val iv_close = rootView.findViewById<View>(R.id.iv_close) as AppCompatImageView
                iv_close.setOnClickListener {
                    customDialog.doDismiss();
                }
            })
//            .setFullScreen(true) //全屏幕宽高
            .setAlign(CustomDialog.ALIGN.DEFAULT)     //从屏幕中部出现;
    }

}