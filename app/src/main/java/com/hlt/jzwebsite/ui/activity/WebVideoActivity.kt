package com.hlt.jzwebsite.ui.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.CustomViewCallback
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.base.BaseActivity
import kotlinx.android.synthetic.main.activity_webview_video.*
import kotlinx.android.synthetic.main.toolbar.*


/**
 * Created by lxb on 2020/2/29.
 * 邮箱：2072301410@qq.com
 * TIP：  webview  使用HTML5  播放视频及全屏方案
 *  查阅链接 ：
 *  https://www.cnblogs.com/renhui/p/5893593.html
 *  https://www.jianshu.com/p/a398018f34d6
 *
 */
class WebVideoActivity : BaseActivity() {

    /** 视频全屏参数  */
    protected val COVER_SCREEN_PARAMS = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    private var customView: View? = null
    private var fullscreenContainer: FrameLayout? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null

    private lateinit var webTitle: String
    private lateinit var webUrl: String


    override var layoutId: Int = com.hlt.jzwebsite.R.layout.activity_webview_video

    override fun initData() {
        //开启硬件加速
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        );
        tv_title.text = getString(com.hlt.jzwebsite.R.string.loading)
        iv_close.apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
        intent.extras?.apply {
            webTitle =
                getString(Constants.WEB_TITLE, getString(com.hlt.jzwebsite.R.string.title_err))
            webUrl = getString(
                Constants.WEB_URL,
                "http://main.gtt20.com/template/mobile/default/img/video.mp4"
            )
        }
        initWebView()
        webview.loadUrl(webUrl);  // 加载Web地址

    }

    override fun subscribeUi() {

    }

    private fun initWebView() {
        val webSettings = webview.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webSettings.setUseWideViewPort(true) // 关键点
        webSettings.setAllowFileAccess(true) // 允许访问文件
        webSettings.setSupportZoom(true) // 支持缩放
        webSettings.setLoadWithOverviewMode(true)
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE) // 不加载缓存内容

        webview.setWebChromeClient(WebChromeClient())
        webview.webChromeClient = object : WebChromeClient() {
            /*** 视频播放相关的方法 **/
            override fun getVideoLoadingProgressView(): View? {
                val frameLayout = FrameLayout(this@WebVideoActivity)
                frameLayout.layoutParams =
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                return frameLayout
            }

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                view?.let { callback?.let { it1 -> showCustomView(it, it1) } }
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                hideCustomView()
            }
        }
        val webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                url: String?
            ): Boolean {
                webview.loadUrl(url);
                return true
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webview.webViewClient = webViewClient
        }

    }


    /** 视频播放全屏 **/
    private fun showCustomView(view: View, callback: CustomViewCallback) {
        // if a view already exists then immediately terminate the new one
        if (customView != null) {
            callback.onCustomViewHidden()
            return
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        webview.setVisibility(View.GONE);

        val decor = window.getDecorView() as FrameLayout
        val fullscreenContainer = FullscreenHolder(this@WebVideoActivity)
        fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
        customView = view;
        setStatusBarVisibility(false);
        customViewCallback = callback;
    }

    /** 隐藏视频全屏 **/
    private fun hideCustomView() {
        if (customView == null) {
            return
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        customView?.setVisibility(View.GONE);

        setStatusBarVisibility(true)
        val decor = window.decorView as FrameLayout
        decor.removeView(fullscreenContainer)
        fullscreenContainer = null
        customView = null
        this.customViewCallback?.onCustomViewHidden()
        webview.setVisibility(View.VISIBLE)
    }


    /**
     * 全屏容器界面
     */
    class FullscreenHolder(webVideoActivity: WebVideoActivity) : FrameLayout(webVideoActivity) {

        init {
            setBackgroundColor(webVideoActivity.getResources().getColor(android.R.color.black));
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return true
        }
    }


    private fun setStatusBarVisibility(visible: Boolean) {
        val flag = if (visible) 0 else WindowManager.LayoutParams.FLAG_FULLSCREEN
        getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面  */
                if (customView != null) {
                    hideCustomView()
                } else if (webview.canGoBack()) {
                    webview.goBack()
                } else {
                    finish()
                }
                return true
            }
            else -> return super.onKeyUp(keyCode, event)
        }
    }

/*    override fun onBackPressed() {
        webview?.run {
            if (webview.canGoBack()) {
                super.onBackPressed()
                return
            }
        }
        super.onBackPressed()
    }*/

    override fun onResume() {
        super.onResume()
        webview.onResume()
    }

    override fun onPause() {
        super.onPause()
        webview.onPause()
    }

    override fun onStop() {
        super.onStop()
        webview.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        webview.destroy()
    }


}

