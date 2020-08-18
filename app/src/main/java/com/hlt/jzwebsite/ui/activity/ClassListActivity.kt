package com.hlt.jzwebsite.ui.activity

import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.StatusBarUtil
import kotlinx.android.synthetic.main.ui_toolbar_common.*

/**
 * @author LXB
 * @description: 课堂培训 list
 * @date :2020/3/5 12:06
 */
class ClassListActivity: BaseActivity() {
    override var layoutId: Int = R.layout.activity_class_list

    override fun initData() {
        tv_title.text = getString(R.string.title_class)
        iv_close.apply {
            setOnClickListener { finish() }
        }
    }

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun subscribeUi() {

    }
}