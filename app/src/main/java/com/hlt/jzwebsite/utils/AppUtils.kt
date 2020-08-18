package com.hlt.jzwebsite.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.tencent.mm.opensdk.diffdev.a.e
import java.security.MessageDigest
import android.content.Intent
import android.content.ComponentName
import android.content.pm.ResolveInfo




/**
 * @author LXB
 * @description:
 * @date :2020/3/10 18:33
 */
class AppUtils private constructor() {


    init {
        throw Error("Do not need instantiate!")
    }

    companion object {

        private val DEBUG = true
        private val TAG = "AppUtils"


        /**
         * 得到软件版本号
         *
         * @param context 上下文
         * @return 当前版本Code
         */
        fun getVerCode(context: Context): Int {
            var verCode = -1
            try {
                val packageName = context.packageName
                verCode = context.packageManager
                    .getPackageInfo(packageName, 0).versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return verCode
        }


        /**
         * 获取应用运行的最大内存
         *
         * @return 最大内存
         */
        val maxMemory: Long
            get() = Runtime.getRuntime().maxMemory() / 1024


        /**
         * 得到软件显示版本信息
         *
         * @param context 上下文
         * @return 当前版本信息
         */
        fun getVerName(context: Context): String {
            var verName = ""
            try {
                val packageName = context.packageName
                verName = context.packageManager
                    .getPackageInfo(packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return verName
        }

        /**
         * 获取APP 版本号
         */
        fun getAppVersionCode(ctx: Context): Int {
            var versionCode = 0
            try {
                val pm = ctx.packageManager
                val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
                if (pi != null) {
                    versionCode = pi.versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Error while collect package info", e)
            }

            return versionCode
        }


        @SuppressLint("PackageManagerGetSignatures")
                /**
                 * 获取应用签名
                 *
                 * @param context 上下文
                 * @param pkgName 包名
                 * @return 返回应用的签名
                 */
        fun getSign(context: Context, pkgName: String): String? {
            return try {
                @SuppressLint("PackageManagerGetSignatures") val pis = context.packageManager
                    .getPackageInfo(
                        pkgName,
                        PackageManager.GET_SIGNATURES
                    )
                hexDigest(pis.signatures[0].toByteArray())
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                null
            }

        }

        /**
         * 将签名字符串转换成需要的32位签名
         *
         * @param paramArrayOfByte 签名byte数组
         * @return 32位签名字符串
         */
        private fun hexDigest(paramArrayOfByte: ByteArray): String {
            val hexDigits = charArrayOf(
                48.toChar(),
                49.toChar(),
                50.toChar(),
                51.toChar(),
                52.toChar(),
                53.toChar(),
                54.toChar(),
                55.toChar(),
                56.toChar(),
                57.toChar(),
                97.toChar(),
                98.toChar(),
                99.toChar(),
                100.toChar(),
                101.toChar(),
                102.toChar()
            )
            try {
                val localMessageDigest = MessageDigest.getInstance("MD5")
                localMessageDigest.update(paramArrayOfByte)
                val arrayOfByte = localMessageDigest.digest()
                val arrayOfChar = CharArray(32)
                var i = 0
                var j = 0
                while (true) {
                    if (i >= 16) {
                        return String(arrayOfChar)
                    }
                    val k = arrayOfByte[i].toInt()
                    arrayOfChar[j] = hexDigits[0xF and k.ushr(4)]
                    arrayOfChar[++j] = hexDigits[k and 0xF]
                    i++
                    j++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }


        /**
         * 获取设备的可用内存大小
         *
         * @param context 应用上下文对象context
         * @return 当前内存大小
         */
        fun getDeviceUsableMemory(context: Context): Int {
            val am = context.getSystemService(
                Context.ACTIVITY_SERVICE
            ) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            // 返回当前系统的可用内存
            return (mi.availMem / (1024 * 1024)).toInt()
        }


        fun getMobileModel(): String {
            var model: String? = Build.MODEL
            model = model?.trim { it <= ' ' } ?: ""
            return model
        }

        /**
         * 获取手机系统SDK版本
         *
         * @return 如API 17 则返回 17
         */
        val sdkVersion: Int
            get() = android.os.Build.VERSION.SDK_INT



        /**
         *  查阅链接：https://www.jianshu.com/p/33695ee91d18
         *  判断B应用是否安装
         * 判断某APP是否安装(检查包是否存在)
         */

        fun checkApkExist(context: Context, packageName: String): Boolean {
            if (TextUtils.isEmpty(packageName))
                return false
            try {
                val info = context.packageManager
                    .getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                return false
            }
        }


        //------------------------------Android APP打开另一个APP完整逻辑实现--------------start--------------------------

        /**
         *  查阅链接：https://www.jianshu.com/p/33695ee91d18
         *  判断B应用是否安装
         * @param packname
         * @return
         */
        fun checkPackInfo(context: Context,packname: String): Boolean {
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = context.packageManager.getPackageInfo(packname, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return packageInfo != null
        }




        /**
         * 判断B应用是否在后台运行并直接打开
         */
        @SuppressLint("WrongConstant")
        fun getAppOpenIntentByPackageName(context: Context, packageName: String): Intent? {
            //Activity完整名
            var mainAct: String? = null
            //根据包名寻找
            val pkgMag = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags =
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_NEW_TASK

            val list = pkgMag.queryIntentActivities(
                intent,
                PackageManager.GET_ACTIVITIES
            )
            for (i in list.indices) {
                val info = list[i]
                if (info.activityInfo.packageName == packageName) {
                    mainAct = info.activityInfo.name
                    break
                }
            }
            if (TextUtils.isEmpty(mainAct)) {
                return null
            }
            intent.component = ComponentName(packageName, mainAct!!)
            return intent
        }

        fun getPackageContext(context: Context, packageName: String): Context? {
            var pkgContext: Context? = null
            if (context.packageName == packageName) {
                pkgContext = context
            } else {
                // 创建第三方应用的上下文环境
                try {
                    pkgContext = context.createPackageContext(
                        packageName,
                        Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            }
            return pkgContext
        }

        fun openPackage(context: Context, packageName: String): Boolean {
            val pkgContext = getPackageContext(context, packageName)
            val intent = getAppOpenIntentByPackageName(context, packageName)
            if (pkgContext != null && intent != null) {
                pkgContext.startActivity(intent)
                return true
            }
            return false
        }
        //------------------------------Android APP打开另一个APP完整逻辑实现--------------end--------------------------

    }


}