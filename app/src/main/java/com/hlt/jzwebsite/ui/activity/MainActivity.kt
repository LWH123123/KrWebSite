package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.eventbus.LoginMessageEvent
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.paging.repository.AppUpdateRepository
import com.hlt.jzwebsite.repository.MainRepository
import com.hlt.jzwebsite.ui.fragment.CooperationFragment
import com.hlt.jzwebsite.ui.fragment.DynamicsFragment
import com.hlt.jzwebsite.ui.fragment.HomeFragment
import com.hlt.jzwebsite.ui.fragment.MineFragment
import com.hlt.jzwebsite.utils.AppUtils
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.AppUpdateViewModel
import com.hlt.jzwebsite.viewmodel.MainViewModel
import com.hlt.jzwebsite.widget.dialog.Dialog4AppUpdate
import com.hlt.jzwebsite.widget.dialog.DialogAppForceUpdate
import com.hlt.jzwebsite.wxapi.WXEntryActivity
import com.hlt.jzwebsite.wxapi.WXUserInfo
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException


class MainActivity : BaseActivity(){
    private var lastExitTime: Long = 0
    private var mCurrentFragment: Fragment? = null
    private var index = 0 //默认选中角标
    private var prePos: Int = 0//默认选中角标
    private val PRE = "PREPOS"
    private var fragmentTag: String? = null
    private val fragmentNames = arrayOf(
        HomeFragment::class.java.name, DynamicsFragment::class.java.name,
        CooperationFragment::class.java.name, MineFragment::class.java.name
    )
    private var verName: String? = null

    private val bottomTitles = arrayOf(
        R.string.home,
        R.string.dynamics,
        R.string.cooperation,
        R.string.mine
    )

    //appUpdate
    private val appUpdateVM by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = AppUpdateRepository(HttpManager.getInstance(), verName)
                return AppUpdateViewModel(repository) as T
            }
        })
            .get(AppUpdateViewModel::class.java)
    }

    override var layoutId = R.layout.activity_main


    override fun initData() {
        SPUtils.getInstance().put(Constants.SP_ISFIRST_LAUNCHED, false) //设置不是初次安装app
        verName = AppUtils.getVerName(App.instance)
        initNavBottom(index)
        registEventBus() //注册消息事件
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initStatusBar() {
        super.initStatusBar()
//        StatusBarUtil.darkMode(this)
//        StatusBarUtil.setPaddingSmart(this, toolbar)
//        StatusBarUtil.immersive(window)
    }

    private fun registEventBus() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        } else {
            ToastUtils.show(getString(R.string.event_message_tip))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleEvent(loginMessage: LoginMessageEvent) {//事件订阅者处理事件
        index = 0
        val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
        if (!isLogin) {
            navigation_bottom.selectedItemId = navigation_bottom.menu.getItem(index).itemId
//            navigation_bottom.getChildAt(index).setSelected(true)
        } else {
            initNavBottom(index)
        }
    }

    override fun subscribeUi() {
        //版本更新
        handleData(appUpdateVM.appUpdate) {
            Log.d(TAG, "App 更新---->>")
            val status = it.result.status //是否强更
            if (it.result.version.isNotEmpty() && it.result.desc.isNotEmpty() && it.result.size.isNotEmpty()) {
                val newVersion = it.result.version
                val depict = it.result.desc
                val size = it.result.size
                val url = it.result.url
                if (status == 0) {// 0 无需更新

                } else if (status == 1) {//不强制更新
                    showNormalUpdateDialog(newVersion, size, depict, verName, url)
                } else if (status == 2) {  //2 强制更新
                    val dialog = DialogAppForceUpdate(
                        mContext,
                        "当前版本V" + verName
                                + ",是否升级到版本V" + newVersion + "?新版本大小:" + size,
                        "新版本功能 \n" + depict.replace("\\n", "\n")
                    )
                    dialog.setCancelEnable(false)
                    dialog.setListener(object : DialogAppForceUpdate.onClickSureBtnListener {
                        override fun onClickSure() {
                        }
                    })
                    if (!(mContext as MainActivity).isFinishing()) {
                        dialog.show()
                    }
                }

            }
        }
    }


    private fun initNavBottom(index: Int) {
        this.index = index
        navigation_bottom.setItemIconTintList(null);//去掉不显示图片默认颜色
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> this.index = 0
                    R.id.dynamics -> this.index = 1
                    R.id.cooperation -> this.index = 2
                    R.id.mine -> this.index = 3
                }
                bottomNav(this.index)
                true
            }
        navigation_bottom.run {
            labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }
        bottomNav(this.index)//设置默认页面
    }


    private fun bottomNav(index: Int) {
        this.index = index
        tv_title.text = getString(
            bottomTitles[this.index]
        )
        if (this.index == 0 || this.index == 3) {
            toolbar.visibility = View.GONE
        } else {
            toolbar.visibility = View.VISIBLE
        }
        fragmentTag = fragmentNames[this.index]
        val fragment = getFragmentByTag(fragmentTag!!)
//        mLoginToEntryOrNot(fragment)
        showFragment(mCurrentFragment, fragment, fragmentTag!!)

    }

    /**
     *  在点击 我的判断是否登录 逻辑
     */
    private fun mLoginToEntryOrNot(fragment: Fragment) {
        if (this.index == 3) {//我的
            /**
             *  1，首先判断是否授权登录
             *  2， 已授权 正常打开我的，未授权 去微信授权
             */
            var userToken = SPUtils.getInstance().getString(Constants.SP_USER_TOKEN)
            Log.d(TAG, "userToken:===" + userToken)
            if (!userToken.isNullOrBlank()) {
                showFragment(mCurrentFragment, fragment, fragmentTag!!)
            } else {
    //                toWxAuthorizedLogin(fragment)  微信授权登录入口
                Intent(mContext, VerificaCodeLoginActivity::class.java).run {
                    mContext?.startActivity(this)
                }
            }

        } else {
            showFragment(mCurrentFragment, fragment, fragmentTag!!)
        }
    }

    private fun getFragmentByTag(name: String): Fragment {
        var fragment = supportFragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            return fragment
        } else {
            try {
                fragment = Class.forName(name).newInstance() as Fragment
            } catch (e: Exception) {
                fragment = HomeFragment()
            }
        }
        return fragment!!
    }

    private fun showFragment(from: Fragment?, to: Fragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        if (from == null) {
            if (to.isAdded) {
                transaction.show(to)
            } else {
                transaction.add(R.id.container, to, tag)
            }
        } else {
            if (to.isAdded) {
                transaction.hide(from).show(to)
            } else {
                transaction.hide(from).add(R.id.container, to, tag)
            }
        }
        transaction.commit()
        mCurrentFragment = to
    }

    /**
     *  信授权登录
     */
    private fun toWxAuthorizedLogin(fragment: Fragment) {
        Log.d(TAG, "微信授权登录---->>>")
        WXEntryActivity.loginWeixin(
            App.instance,
            App.sApi,
            object : WXEntryActivity.ApiCallback<WXUserInfo> {
                override fun onSuccess(info: WXUserInfo) {
                    Log.d("onSuccess()--->>", info.toString())
                    postLogin(info, fragment)
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    Log.d("onError()--->>", errorMsg)
                    //判断 WX token 是否失效
                    UserInfo.getInstance().result.tokenError = -1
                    ToastUtils.show("用户信息过期")
//                    navigation_bottom.selectedItemId = navigation_bottom.menu.getItem(index).itemId
//                    initNavBottom(0)
                }

                override fun onFailure(e: IOException) {
                    Log.d("onFailure()--->>", e.message)
                    UserInfo.getInstance().result.tokenError = -1
                    ToastUtils.show("用户信息过期")
//                    navigation_bottom.selectedItemId = navigation_bottom.menu.getItem(index).itemId
//                    initNavBottom(0)
                }

                override fun onPayError(res: String) {

                }
            })

    }

    /**
     * 将微信授权成功后，返回的数据，上传自己的服务器
     */
    private fun postLogin(
        info: WXUserInfo,
        fragment: Fragment
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
            val userInfo: UserInfo.ResultBean? = UserInfo().result
            userInfo?.setToken(it.result?.user_token)

//            var userToken: String by PreferenceUtils(Constants.SP_USER_TOKEN, it.user_token)
            SPUtils.getInstance().put(Constants.SP_USER_TOKEN, it.result?.user_token)
            SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, true)
            ToastUtils.show("登录成功")
            showFragment(mCurrentFragment, fragment, fragmentTag!!)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastExitTime > 1500) {
                ToastUtils.show(getString(R.string.exit_hint))
                lastExitTime = currentTime
                return true
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)    //取消注册事件
    }


    private fun showNormalUpdateDialog(
        newVersion: String,
        size: String,
        depict: String,
        version: String?,
        url: String
    ) {
        val dialog = Dialog4AppUpdate(
            mContext,
            "当前版本V" + version
                    + ",是否升级到版本V" + newVersion + "?新版本大小:" + size,
            "新版本功能 \n" + depict.replace("\\n", "\n")
        )
        dialog.setCancelEnable(false)
        dialog.setListener(object : Dialog4AppUpdate.onClickSureBtnListener {
            override fun onClickSure() {
                if (url.isNotEmpty()) {
                    mCommonMethod(url)
                }
                dialog.dismiss()
            }

            override fun onClickCancel() {
                if (!(mContext as MainActivity).isFinishing()) {
                    dialog.dismiss()
                }
            }
        })
        //防止activity 已销毁，show 报错
        if (!(mContext as MainActivity).isFinishing()) {
            dialog.show()
        }
    }

    private fun mCommonMethod(url: String) {
        try {
            val intent = Intent()    //调web浏览器
            intent.action = "android.intent.action.VIEW"
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
//        super.onSaveInstanceState(outState, outPersistentState)
//        outState?.putInt(PRE, prePos)//保存上一个位置
    }

}
