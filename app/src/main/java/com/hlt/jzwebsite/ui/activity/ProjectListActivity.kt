package com.hlt.jzwebsite.ui.activity

import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.StatusBarUtil
import kotlinx.android.synthetic.main.ui_toolbar_common.*

/**
 * @author LXB
 * @description: 上市项目 list
 * @date :2020/3/5 11:14
 */
class ProjectListActivity:BaseActivity() {

    private lateinit var title: String
    override var layoutId: Int= R.layout.project_list


    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun initData() {
        getIntentData()
        tv_title.text = title
        iv_close.apply {
            setOnClickListener { finish() }
        }
    }

    private fun getIntentData() {
        intent.extras?.apply {
            title = getString(Constants.TITLE, getString(R.string.app_name))
        }
    }

    override fun subscribeUi() {
    }
}