package com.hlt.jzwebsite.ui.activity

import android.Manifest
import android.content.Intent
import android.graphics.Typeface
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.AppUtils
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.java.GetDeviceId
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.utils.java.StringUtils
import kotlinx.android.synthetic.main.activity_splash.*
import pub.devrel.easypermissions.EasyPermissions

/**
 * @author LXB
 * @description: 启动页
 * @date :2020/3/10 18:18
 */
class SplashActivity:BaseActivity() {
    var isFirstLaunched:Boolean = false // true: 已经登录 ; false:未登录;

    companion object{
        val IS_APP_OPEN = "is_app_open" //应用是否打开过
    }

    private var textTypeface: Typeface?=null
    private var descTypeFace: Typeface?=null
    private var alphaAnimation: AlphaAnimation?=null

    init {
        textTypeface = Typeface.createFromAsset(App.instance.assets, "fonts/Lobster-1.4.otf")
        descTypeFace = Typeface.createFromAsset(App.instance.assets, "fonts/FZLanTingHeiS-L-GB-Regular.TTF")
    }

    override var layoutId: Int = R.layout.activity_splash
    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.immersive(window,R.color.transparent,0.0f)
    }

    override fun initData() {
        tv_app_name.typeface = textTypeface
        tv_splash_desc.typeface = descTypeFace
        tv_version_name.text = "v${AppUtils.getVerName(App.instance)}"

        //渐变展示启动屏
        alphaAnimation= AlphaAnimation(0.3f, 1.0f)
        alphaAnimation?.duration = 2000
        alphaAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(arg0: Animation) {
                enterNextPage()
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationStart(animation: Animation) {}
        })
        checkPermission()
    }

    override fun subscribeUi() {

    }

     fun enterNextPage() {
         isFirstLaunched = SPUtils.getInstance().getBoolean(Constants.SP_ISFIRST_LAUNCHED,true)
        redirectTo()
    }

    fun redirectTo() {
        if (!isFirstLaunched) {
            Intent(mContext, MainActivity::class.java).run {
                mContext?.startActivity(this)
            }
            finish()
        } else { //引导页
            Intent(mContext, GuideActivity::class.java).run {
                mContext?.startActivity(this)
            }
            finish()
        }
    }

    /**
     * 6.0以下版本(系统自动申请) 不会弹框
     * 有些厂商修改了6.0系统申请机制，他们修改成系统自动申请权限了
     */
    private fun checkPermission(){
        val perms = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        EasyPermissions.requestPermissions(this, "君子道应用需要以下权限，请允许", 0, *perms)
    }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 0) {
            if (perms.isNotEmpty()) {
                if (perms.contains(Manifest.permission.READ_PHONE_STATE)
                    && perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    getDeviceUniqueId()
                    if (alphaAnimation != null) {
                        iv_web_icon.startAnimation(alphaAnimation)
                    }
                }
            }
        }
    }

    /**
     *  获取设备id
     */
    private fun getDeviceUniqueId() {
        Thread(Runnable {
            try {
                //获取保存在sd中的 设备唯一标识符
                var readDeviceID = GetDeviceId.readDeviceID(applicationContext)
                //获取缓存在  sharepreference 里面的 设备唯一标识
                val string = SPUtils.getInstance().getString(Constants.SP_DEVICES_ID, readDeviceID)
                //判断 app 内部是否已经缓存,  若已经缓存则使用app 缓存的 设备id
                if (string != null) {
                    //app 缓存的和SD卡中保存的不相同 以app 保存的为准, 同时更新SD卡中保存的 唯一标识符
                    if (StringUtils.isEmpty(readDeviceID) && string != readDeviceID) {
                        // 取有效地 app缓存 进行更新操作
                        if (StringUtils.isEmpty(readDeviceID) && !StringUtils.isEmpty(string)) {
                            readDeviceID = string
                            GetDeviceId.saveDeviceID(readDeviceID, applicationContext)
                        }
                    }
                }
                // app 没有缓存 (这种情况只会发生在第一次启动的时候)
                if (StringUtils.isEmpty(readDeviceID)) {
                    //保存设备id
                    readDeviceID = GetDeviceId.getDeviceId(applicationContext)
                }
                //最后再次更新app 的缓存
                SPUtils.getInstance().put(Constants.SP_DEVICES_ID, readDeviceID)
                App.myDeviceID = readDeviceID
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()

    }
}