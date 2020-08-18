package com.hlt.jzwebsite.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * @author LXB
 * @description:
 * @date :2020/3/19 9:40
 */
class GuideAdapter(views: List<View>?) : PagerAdapter() {

    private var views: List<View>? = null

    init {
        this.views = views
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return views!!.size
    }

    /**
     * 添加viwe
     *
     * @param container
     * @param position
     * @return
     */
    override fun instantiateItem(
        container: ViewGroup,
        position: Int
    ): Any { //        container.removeAllViews();
        container.addView(views!![position])
        return views!![position]
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        var `object` = `object`
        container.removeView(`object` as View?)
    }

}