package com.hlt.jzwebsite.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface

/**
 * @author LXB
 * @description: 公用弹窗基类
 * @date :2020/3/9 17:40
 */
class BaseDialog : Dialog {

    private var res: Int = 0

    constructor(context: Context) : super(context) {
    }

    constructor(
        context: Context?,
        themeResId: Int,
        layoutResId: Int
    ) : super(context, themeResId) {
        this.res = layoutResId
        setContentView(this.res)
        setCanceledOnTouchOutside(false)
    }

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener) {

    }

    init {
        this.res = res
    }

}