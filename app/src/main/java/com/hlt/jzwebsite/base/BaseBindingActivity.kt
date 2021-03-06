package com.hlt.jzwebsite.base

import androidx.databinding.ViewDataBinding

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
abstract class BaseBindingActivity<T : ViewDataBinding>: BaseActivity() {

    override var layoutId = 0

    protected abstract var binding: T
}