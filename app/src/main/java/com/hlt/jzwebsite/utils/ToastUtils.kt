package com.hlt.jzwebsite.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.R

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class ToastUtils {
    companion object {
        private var toast: Toast? = null

        fun show(content: String) {
            val inflater = LayoutInflater.from(App.instance)
            val view = inflater.inflate(R.layout.toast_layout, null)
            val text = view.findViewById(R.id.toast_text) as TextView
            text.text = content
            toast = Toast(App.instance)
            toast!!.setGravity(Gravity.CENTER, 0, 0)
            toast!!.duration = Toast.LENGTH_SHORT
            toast!!.view = view
            toast!!.show()
        }
    }
}