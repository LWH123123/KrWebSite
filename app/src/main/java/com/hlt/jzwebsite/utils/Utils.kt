package com.hlt.jzwebsite.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by lxb on 2020/2/22.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class Utils {
    companion object {
        private var context: Context? = null

        /**
         * 初始化工具类
         *
         * @param context 上下文
         */
        fun init(@NonNull context: Context) {
            Utils.context = context.applicationContext
        }

        /**
         * 获取ApplicationContext
         *
         * @return ApplicationContext
         */
        fun getContext(): Context {
            if (context != null) {
                return context as Context
            }
            throw NullPointerException("should be initialized in application")
        }

        /**
         *  设置网络rul图片
         */
        fun setImg(context: Context?, iv: AppCompatImageView, picUrl: String?) {
            if (picUrl?.isNotBlank()!!) {
                context.let { ImageLoader.load(it, picUrl, iv) }
            }
        }

        /**
         * 时间戳 转 日期
         * Timestamp to String
         * @param Timestamp
         * @return String
         * yyyy-MM-dd HH:mm:ss
         * yyyy-MM-dd
         * hHH:mm:ss
         */
        fun transToString(time: Long): String {
            return SimpleDateFormat("yyyy-MM-dd").format(Date(time * 1000L))

        }

        /**
         *  日期 转 时间戳
         * String to Timestamp
         * @param String
         * @return Timestamp
         */

        fun transToTimeStamp(date: String): Long {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date, ParsePosition(0)).time
        }

        /**
         *  返回当前日期，格式：2017-12-19 12:13:55
         */
        fun getNow(): String {
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
            } else {
                var tms = Calendar.getInstance()
                return tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH).toString() + "-" + tms.get(
                    Calendar.DAY_OF_MONTH
                ).toString() + " " + tms.get(Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(
                    Calendar.MINUTE
                ).toString() + ":" + tms.get(Calendar.SECOND).toString() + "." + tms.get(Calendar.MILLISECOND).toString()
            }
        }


        /**
         *
         * 密码验证的正则表达式 (6-16位字母和数字组合)
         *
         * 正则匹配： 由字母和数字组成，但不能为纯数字， 不能为纯字母
         * 密码由6位以上的字母和数字组成， 至少包含一个字母和数字， 不能由纯数字或字母组成。
         * 密码验证："^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$"
         *
         *  参数说明：
         * ^ 匹配一行的开头位置
         * (?![0-9]+$) 预测该位置后面不全是数字
         * (?![a-zA-Z]+$) 预测该位置后面不全是字母
         * [0-9A-Za-z] {6,10} 由6-10位数字或这字母组成
         * $ 匹配行结尾位置
         */
        val model: String = "^(?![0-9]+\$)(?![a-zA-Z]+\$)[0-9A-Za-z]{6,16}\$"

        fun patternCheckPwd(inputPwd: String): Boolean {
            val pattern = Pattern.compile(model)
            Log.d(
                "patternCheckPwd====>>>",
                String.format("%s \t match %b", inputPwd, pattern.matcher(inputPwd).find())
            )
            return pattern.matcher(inputPwd).find()
        }


        /**
         * 密码验证的正则表达式 (6-16位数字,英文,符号至少两种组合的字符)
         *
         * --必须包含 数字,字母,符号 3项组合的 正则表达式
         * ^(?:(?=.*[0-9].*)(?=.*[A-Za-z].*)(?=.*[,\.#%'\+\*\-:;^_`].*))[,\.#%'\+\*\-:;^_`0-9A-Za-z]{8,10}$
         *--不为纯数字或字母的正在表达式 用于密码验证
         * ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$
         *--密码为6~16位数字,英文,符号至少两种组合的字符
         * String passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\\(\\)])+$)([^(0-9a-zA-Z)]|[\\(\\)]|[a-zA-Z]|[0-9]){6,16}$";
         */
        fun validatePhonePass(pass: String): Boolean {
//          val passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$"
            val passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\\(\\)])+$)([^(0-9a-zA-Z)]|[\\(\\)]|[a-zA-Z]|[0-9]){6,16}$";
            return !TextUtils.isEmpty(pass) && pass.matches(passRegex.toRegex())
        }

        /**
         * 复制文本  到粘贴板
         * @param context
         * @param content
         */
        fun copyTextClipboard(
            context: Context,
            content: String?
        ) {
            val clipboardmanager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // Creates a new text clip to put on the
            val clip = ClipData.newPlainText("Label", content)
            clipboardmanager.primaryClip = clip
        }


        /**
         * 从粘贴板  粘贴文本
         * @param context
         */
        fun pasteTextClipboard(context: Context): String? {
            val mClipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (!mClipboardManager.hasPrimaryClip()) return ""
            //获取 ClipDescription
            val clipDescription = mClipboardManager.primaryClipDescription
            //获取 lable
            val lable = clipDescription.label.toString()
            val clipData = mClipboardManager.primaryClip
            val item = clipData.getItemAt(0)
            //获取text
            return item.text.toString()
        }

    }
}