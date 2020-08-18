package com.hlt.jzwebsite.ui.activity

import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.StatusBarUtil
import kotlinx.android.synthetic.main.ui_toolbar_common.*

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP： 收藏
 * 框架疑问： 如何在 activity  中，使用 BasePagingAdapter 进行列表分页加载，后期研究，暂时将此页面改为 fragment 写
 */
class CollectionActivity : BaseActivity() {

    override var layoutId: Int = R.layout.activity_collection

    override fun initData() {
        tv_title.text = getString(R.string.title_my_collection)
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

    override fun onBackPressed() {
        super.onBackPressed()
    }
}