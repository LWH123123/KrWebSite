package com.hlt.jzwebsite

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.multidex.MultiDexApplication
import com.hlt.jzwebsite.utils.*
import com.hlt.jzwebsite.utils.java.AppManager
import com.hlt.jzwebsite.utils.java.GetDeviceId
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.utils.java.StringUtils
import com.hlt.jzwebsite.wxapi.WXEntryActivity
import com.hlt.jzwebsite.wxapi.WxConst
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.tencent.mm.opensdk.openapi.IWXAPI

/**
 * Created by lxb on 2020/2/20.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class App : MultiDexApplication() {

    companion object {
        lateinit var instance: Application
        lateinit var myDeviceID: String
        lateinit var sApi: IWXAPI
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Utils.init(instance) //初始化工具类
        initLogger()
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        getDeviceUniqueId()
        initWeiXin()
    }

    private fun initWeiXin() {
        sApi = WXEntryActivity.initWeiXin(this, WxConst.WEIXIN_APP_ID)
    }

    private fun initLogger() {
        val lever = if (BuildConfig.DEBUG) LogLevel.FULL else LogLevel.NONE
        Logger.init("jzwebsite")
            .methodCount(0)
            .hideThreadInfo()
            .logLevel(lever)
    }

    private val mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Logger.d(activity.componentName.className + " onCreated")
            AppManager.getAppManager().addActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            Logger.d(activity.componentName.className + " onResumed")
        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            Logger.d(activity.componentName.className + " onDestroyed")
            AppManager.getAppManager().removeActivity(activity)
        }
    }


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
                myDeviceID = readDeviceID
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()

    }
}