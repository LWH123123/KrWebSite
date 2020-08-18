package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.NestedScrollAgentWebView
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.toolbar.iv_close
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar.tv_title

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：  Android webViewj简单处理apk的下载链接  参考链接：
 *        https://www.cnblogs.com/riasky/p/3483378.html
 *
 */
class WebActivity : BaseActivity() {
    private var agentWeb: AgentWeb? = null
    private lateinit var webTitle: String
    private lateinit var webUrl: String
    private lateinit var errorMsg: TextView
    private val mWebView: NestedScrollAgentWebView by lazy {
        NestedScrollAgentWebView(this)
    }

    override var layoutId = R.layout.activity_web

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun initData() {
        toolbar.apply {
            visibility = View.VISIBLE
        }
        iv_close.apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
        tv_title.apply {
            visibility = View.VISIBLE
            text = getString(R.string.loading)
            isSelected = true
        }
            intent.extras?.apply {
            webTitle = getString(Constants.WEB_TITLE, getString(R.string.title_err))
            webUrl = getString(Constants.WEB_URL, "")
        }
        initWebView()
    }

    override fun subscribeUi() {
    }

    private fun initWebView() {
        val layoutParams =
            CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
        val errorView = layoutInflater.inflate(R.layout.web_error_page, null)
        errorMsg = errorView.findViewById(R.id.error_msg)
        errorMsg.text = getString(R.string.web_page_err, webUrl)

        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(web_container, 1, layoutParams)
            .useDefaultIndicator(Color.parseColor("#CC0000"))
            .setWebView(mWebView)
            .setWebChromeClient(webChromeClient)
            .setMainFrameErrorView(errorView)
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)
            .createAgentWeb()
            .ready()
            .go(webUrl)

        agentWeb?.webCreator?.webView?.run {
            settings.domStorageEnabled = true
            webViewClient = mWebViewClient
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
        }
    }


    override fun onBackPressed() {
        agentWeb?.run {
            if (!back()) {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (agentWeb?.handleKeyEvent(keyCode, event)!!) {
            true
        } else {
            finish()
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onResume() {
        agentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onPause() {
        agentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        agentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }


    private val webChromeClient = object : com.just.agentweb.WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            tv_title.text = title
        }
    }

    private val mWebViewClient = object : com.just.agentweb.WebViewClient() {

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed() // 接受证书
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            // view.loadUrl(url);
            //处理h5 片段 中包含重定向问题
            val hitTestResult = view?.getHitTestResult()
            //hitTestResult==null解决重定向问题
            if (!TextUtils.isEmpty(url) && hitTestResult == null) {
                view?.loadUrl(url)
                return true
            }
            //Android webViewj简单处理apk的下载链接
            Log.d("shouldOverrideUrlLoading", "Apk 下载链接->$url")
            if (/*url?.endsWith(".apk")!!*/url?.contains("apk")!!) {
                val uri = Uri.parse(url)
                val viewIntent = Intent(Intent.ACTION_VIEW, uri)
                this@WebActivity.startActivity(viewIntent)
                return true
            }
            return super.shouldOverrideUrlLoading(view, url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.e(TAG, "onPageStarted---开始加载页面")
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.e(TAG, "onPageFinished---页面加载结束")
            view?.getSettings()?.setBlockNetworkImage(false)
        }

    }
}