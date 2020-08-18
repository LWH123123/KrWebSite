package com.hlt.jzwebsite.base

import androidx.fragment.app.Fragment

/**
 * Created by lxb on 2020/2/28.
 * 邮箱：2072301410@qq.com
 * TIP： Fragment数据的缓加载
 */
abstract class LazyBaseFragment : Fragment() {

    protected var isPrepared: Boolean = false    // 标志位，在子类中需要用
    protected var isVisibles: Boolean = false

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (getUserVisibleHint()) {
            isVisibles = true;
            onVisible();
        } else {
            isVisibles = false;
            onInvisible();
        }
    }

    protected fun onVisible() {
        lazyLoad()
    }

    protected abstract fun lazyLoad()

    protected fun onInvisible() {}
}