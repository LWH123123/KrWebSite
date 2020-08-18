package com.hlt.jzwebsite.extensions

import android.text.TextUtils
import android.widget.TextView
import com.hlt.jzwebsite.utils.ToastUtils
import java.util.regex.Pattern

/**
 * Created by lxb on 2020/2/27.
 * 邮箱：2072301410@qq.com
 * TIP：自定义扩展方法 优雅地判空
 */
/**
 *  判断EditText输入的数据是否为空
 */
fun TextView.checkBlank(message: String): String? {
    val text = this.text.toString()
    if (text.isBlank()) {
        ToastUtils.show(message)
        return null
    }
    return text
}

/**
 *  手机号正则验证
 */
fun regexPhone(phone: String): Boolean {
    var mainRegex =
        "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$"
    var p = Pattern.compile(mainRegex)
    val m = p.matcher(phone)
    return m.matches()

}

/**
 *  身份证号校验
 */
fun validateIdCard(cardNo: String?): Boolean {
    cardNo ?: return false
    if (cardNo.length != 18 || !cardNo.substring(0, 17).matches(Regex("\\d{17}"))) {
        return false
    }
    // 身份证号前17位分别和下面的array中对应的值相乘之后求和
    // 然后模11之后的结果作为数组下标,去validCodes中取正确的尾号和参数中的尾号做校验接即可
    val intArr = arrayOf(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    var sum = 0
    for (idx in intArr.indices) {
        sum += Character.digit(cardNo[idx], 10) * intArr[idx]
    }
    val mod = sum % 11
    val validCodes = arrayOf("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2")
    return validCodes[mod] == cardNo.substring(cardNo.length - 1)
}


/**
 * 密码验证的正则表达式 (6-16位字母和数字组合)
 * ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$
 *  -------------相关正则------------------
 * --必须包含 数字,字母,符号 3项组合的 正则表达式
 * ^(?:(?=.*[0-9].*)(?=.*[A-Za-z].*)(?=.*[,\.#%'\+\*\-:;^_`].*))[,\.#%'\+\*\-:;^_`0-9A-Za-z]{8,10}$
 *--不为纯数字或字母的正在表达式 用于密码验证
 * ^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$
 *--密码为6~16位数字,英文,符号至少两种组合的字符
 * String passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?!([^(0-9a-zA-Z)]|[\\(\\)])+$)([^(0-9a-zA-Z)]|[\\(\\)]|[a-zA-Z]|[0-9]){6,16}$";
 *  -------------相关正则------------------
 */
fun validatePhonePass(pass: String): Boolean {
    val passRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$"
    return !TextUtils.isEmpty(pass) && pass.matches(passRegex.toRegex())
}

