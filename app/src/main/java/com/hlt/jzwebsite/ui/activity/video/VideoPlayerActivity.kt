package com.hlt.jzwebsite.ui.activity.video

import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.orhanobut.logger.Logger
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import kotlinx.android.synthetic.main.activity_video_player.*

/**
 * @author LXB
 * @description: 视频播放页
 * @date :2020/4/3 9:59
 */
class VideoPlayerActivity : BaseActivity() {
    private lateinit var title: String
    private lateinit var videoUrl: String

    //video
    companion object {
        const val IMG_TRANSITION = "IMG_TRANSITION"
        const val TRANSITION = "TRANSITION"
    }

    private var orientationUtils: OrientationUtils? = null
    private var isPlay: Boolean = false
    private var isPause: Boolean = false
    private var isTransition: Boolean = false
    private var transition: Transition? = null

    override var layoutId: Int = R.layout.activity_video_player

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.immersive(window, R.color.transparent, 0.0f)
    }

    override fun initData() {
        intent.extras?.apply {
            title =
                getString(Constants.WEB_TITLE, getString(com.hlt.jzwebsite.R.string.title_err))
            videoUrl = getString(
                Constants.WEB_URL,
                "http://main.gtt20.com/template/mobile/default/img/video.mp4"
            )
        }
        initTransition()    //过渡动画
        initVideoViewConfig()
    }

    override fun subscribeUi() {

    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils)
        }
    }


    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (StandardGSYVideoPlayer.backFromWindowFull(this))
            return
        mVideoView.setStandardVideoAllCallBack(null)   //释放所有
        GSYVideoPlayer.releaseAllVideos()
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) run {
            super.onBackPressed()
        } else {
            finish()
//            overridePendingTransition(R.anim.anim_out, R.anim.anim_in)  //activity finish 转场动画
        }
    }

    override fun onResume() {
        super.onResume()
        getCurPlay().onVideoResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        getCurPlay().onVideoPause()
        isPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoPlayer.releaseAllVideos()
        orientationUtils?.releaseListener()
    }


    private fun getCurPlay(): GSYVideoPlayer {
        return if (mVideoView.fullWindowPlayer != null) {
            mVideoView.fullWindowPlayer
        } else mVideoView
    }


    private fun initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
            ViewCompat.setTransitionName(
                mVideoView,
                IMG_TRANSITION
            )
            addTransitionListener()
            startPostponedEnterTransition()
        } else {
            loadVideoInfo(videoUrl)
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener() {
        transition = window.sharedElementEnterTransition
        transition?.addListener(object : Transition.TransitionListener {
            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
            }

            override fun onTransitionEnd(p0: Transition?) {
                Logger.d("onTransitionEnd()------")
                loadVideoInfo(videoUrl)
                transition?.removeListener(this)
            }

        })
    }

    /**
     *  加载视频资源
     */
    private fun loadVideoInfo(url: String) {
        val netType = NetUtils.isWifi(App.instance)
        if (netType) { //wifi 高清
            setVideo(url)
        } else { //标清、普通
            setVideo(url)
        }
    }

    /**
     * 设置播放视频 URL
     */
    private fun setVideo(url: String) {
        Logger.d("playUrl:$url")
        mVideoView.setUp(url, false, "")
        //开始自动播放
        mVideoView.startPlayLogic()
    }


    /**
     * 初始化 VideoView 的配置
     */
    private fun initVideoViewConfig() {
        //设置旋转
        orientationUtils = OrientationUtils(this, mVideoView)
        //是否旋转
        mVideoView.isRotateViewAuto = false
        //是否可以滑动调整
        mVideoView.setIsTouchWiget(true)

        //增加封面
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setBackgroundColor(R.mipmap.ic_bg_video)
//        GlideApp.with(this)
//            .load(itemData.data?.cover?.feed)
//            .centerCrop()
//            .into(imageView)
        mVideoView.thumbImageView = imageView

        mVideoView.setStandardVideoAllCallBack(object : VideoListener {

            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                //开始播放了才能旋转和全屏
                orientationUtils?.isEnable = true
                isPlay = true
            }

            override fun onAutoComplete(url: String, vararg objects: Any) {
                super.onAutoComplete(url, *objects)
                Logger.d("***** onAutoPlayComplete **** ")
                Log.e(TAG, "onAutoComplete ---->>>" + url)
            }

            override fun onPlayError(url: String, vararg objects: Any) {
                super.onPlayError(url, *objects)
                Log.e(TAG, "onPlayError ---->>>" + "播放失败")
                ToastUtils.show(getString(R.string.video_player_error))
            }

            override fun onEnterFullscreen(url: String, vararg objects: Any) {
                super.onEnterFullscreen(url, *objects)
                Logger.d("***** onEnterFullscreen **** ")
            }

            override fun onQuitFullscreen(url: String, vararg objects: Any) {
                super.onQuitFullscreen(url, *objects)
                Logger.d("***** onQuitFullscreen **** ")
                //列表返回的样式判断
                orientationUtils?.backToProtVideo()
            }
        })
        //设置返回按键功能
        mVideoView.backButton.setOnClickListener({ onBackPressed() })
        //设置全屏按键功能
        mVideoView.fullscreenButton.setOnClickListener {
            //直接横屏
            orientationUtils?.resolveByClick()
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            mVideoView.startWindowFullscreen(this, true, true)
        }
        //锁屏事件
        mVideoView.setLockClickListener(object : LockClickListener {
            override fun onClick(view: View?, lock: Boolean) {
                //配合下方的onConfigurationChanged
                orientationUtils?.isEnable = !lock
            }

        })
    }
}