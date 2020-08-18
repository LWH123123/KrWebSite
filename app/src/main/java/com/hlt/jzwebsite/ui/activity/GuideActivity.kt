package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.GuideAdapter
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_guide.*
import java.util.*

/**
 * @author LXB
 * @description: 引导页
 * @date :2020/3/19 9:27
 */
class GuideActivity : BaseActivity(), View.OnClickListener {

    override fun doBeforeSetContent() {
        super.doBeforeSetContent()
        //将window扩展至全屏，也就是全屏显示，并且不会覆盖状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        //为了避免在状态栏的显示状态发生变化时重新布局，从而避免界面卡顿
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override var layoutId: Int = R.layout.activity_guide

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.immersive(window, R.color.transparent, 0.0f)
    }

    override fun initData() {
        val guides: MutableList<View> =
            ArrayList()
        val imageView1 = ImageView(this)
        imageView1.scaleType = ImageView.ScaleType.FIT_XY
        imageView1.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView1.setImageResource(R.mipmap.ic_guide1)
        guides.add(imageView1)

        val imageView2 = ImageView(this)
        imageView2.scaleType = ImageView.ScaleType.FIT_XY
        imageView2.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView2.setImageResource(R.mipmap.ic_guide2)
        guides.add(imageView2)

        val imageView3 = ImageView(this)
        imageView3.scaleType = ImageView.ScaleType.FIT_XY
        imageView3.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView3.setImageResource(R.mipmap.ic_guide3)
        guides.add(imageView3)

//        ImageView imageView4 = new ImageView(this);
//        imageView4.setScaleType(ImageView.ScaleType.FIT_XY);
//        imageView4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
//        imageView4.setImageResource(R.drawable.guide4);
//        guides.add(imageView4);
        val imageView4 = ImageView(this)
        imageView4.scaleType = ImageView.ScaleType.FIT_XY
        imageView4.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView4.setImageResource(R.mipmap.ic_guide4)
        guides.add(imageView4)

        guide_viewpage.setAdapter(GuideAdapter(guides))
    }

    override fun initClickEvent() {
        super.initClickEvent()
        guide_button.setOnClickListener(this)
        //监听最后一张图片，显示立即体验按钮
        guide_viewpage.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) { //当position == 2,表示是第三张图片
                if (position == 3) {
                    guide_button.setVisibility(View.VISIBLE)
                } else {
                    guide_button.setVisibility(View.GONE)
                }
                if (position == 0) {
                    iv_point1.setImageResource(R.drawable.point_focused)
                    iv_point2.setImageResource(R.drawable.point_normal)
                    iv_point3.setImageResource(R.drawable.point_normal)
                    iv_point4.setImageResource(R.drawable.point_normal);
                } else if (position == 1) {
                    iv_point1.setImageResource(R.drawable.point_normal)
                    iv_point2.setImageResource(R.drawable.point_focused)
                    iv_point3.setImageResource(R.drawable.point_normal)
                    iv_point4.setImageResource(R.drawable.point_normal);
                } else if (position == 2) {
                    iv_point1.setImageResource(R.drawable.point_normal)
                    iv_point2.setImageResource(R.drawable.point_normal)
                    iv_point3.setImageResource(R.drawable.point_focused)
                    iv_point4.setImageResource(R.drawable.point_normal);
                } else if (position == 3) {
                    iv_point1.setImageResource(R.drawable.point_normal);
                    iv_point2.setImageResource(R.drawable.point_normal);
                    iv_point3.setImageResource(R.drawable.point_normal);
                    iv_point4.setImageResource(R.drawable.point_focused);
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun subscribeUi() {
    }

    override fun onClick(p0: View?) {
        Intent(this, MainActivity::class.java).run {
            startActivity(this)
        }
        finish()
    }
}