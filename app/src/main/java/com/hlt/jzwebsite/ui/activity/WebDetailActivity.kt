package com.hlt.jzwebsite.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.WebDetailResult
import com.hlt.jzwebsite.repository.WebDetailRepository
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.Utils
import com.hlt.jzwebsite.utils.java.BaseWebviewUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.CollectOperationRepository
import com.hlt.jzwebsite.viewmodel.CollectOperationViewModel
import com.hlt.jzwebsite.viewmodel.WebDetailViewModel
import com.just.agentweb.NestedScrollAgentWebView
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_web_detail.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlin.properties.Delegates

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 9:42
 */
class WebDetailActivity : BaseActivity() {
    private lateinit var webTitle: String
    private lateinit var webUrl: String

    private var id: String? = null
    private var catId: String? = null
    private var title_detail: String? = null

    private var isCollect: Boolean = false
    private var uid: String? = ""
    private var status_flag: Int = 0
    private var statusF by Delegates.notNull<Int>()
    private var htmlContent: String? = null

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = WebDetailRepository(HttpManager.getInstance())
                return WebDetailViewModel(repository, id, catId, uid, title_detail) as T
            }
        }).get(WebDetailViewModel::class.java)
    }


    private val mWebView: NestedScrollAgentWebView by lazy {
        NestedScrollAgentWebView(this)
    }

    override var layoutId: Int = R.layout.activity_web_detail

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun initData() {
        toolbar.apply {
            visibility = View.GONE
            setBackgroundColor(resources.getColor(R.color.transparent))
        }
        iv_close.apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
        iv_back.apply {
            setOnClickListener { finish() }
        }
        tv_title.apply {
            visibility = View.VISIBLE
            text = getString(R.string.loading)
            isSelected = true
        }
        var uidSp: String = SPUtils.getInstance().getString(Constants.SP_USER_ID)
        uid = uidSp
        Log.d(TAG, "uid:===" + uid)
        intent.extras?.apply {
            webTitle = getString(Constants.WEB_TITLE, getString(R.string.title_err))
            webUrl = getString(Constants.WEB_URL, "")
            title_detail = getString(Constants.BUNDLE_KEY_TITLE_DETAIL, "")
            id = getString(Constants.BUNDLE_KEY_ID, "")
            catId = getString(Constants.BUNDLE_KEY_CATID, "")
        }
        initWebView()

    }

    override fun subscribeUi() {
        viewModel.run {
            handleData(detailData) {
                val webDetailResult: WebDetailResult = it.result
                if (webDetailResult.image.isNullOrEmpty()) {// 空设置默认图
                    iv_topBg.setBackgroundResource(R.mipmap.ic_detail_web_top_default)
                    iv_topBg.invalidate()
                } else {
                    Utils.setImg(mContext, iv_topBg, HttpConstants.BASE_URL + webDetailResult.image)
                }
                titles.text = webDetailResult.title
                time.text = "时间：" + Utils.transToString(webDetailResult.create_time)
                subtitle.text = "发布方：" + webDetailResult.author
                htmlContent = webDetailResult.content
                //加载H5 片段
                if (htmlContent!!.isNotEmpty()) {
                    val htmlData = BaseWebviewUtils.getHtmlData(htmlContent)
                    web_view.loadDataWithBaseURL(
                        null,
                        htmlData,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
                statusF = webDetailResult.collectstatus //当前是否收藏
                val status = webDetailResult.collectstatus //收藏标志
                if (status == 1) {
                    iv_collect.setBackgroundResource(R.mipmap.ic_collected)
                } else {
                    iv_collect.setBackgroundResource(R.mipmap.ic_uncollect)
                }
            }
        }
    }

    override fun initClickEvent() {
        super.initClickEvent()
        iv_collect.setOnClickListener {
            val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
            if (!isLogin) {
                Intent(mContext, VerificaCodeLoginActivity::class.java).run {
                    mContext?.startActivity(this)
                }
            } else {
                if (statusF == 1) {//已收藏
                    cancelCollect(statusF)
                } else {
                    toCollect(statusF)
                }
            }
        }
    }

    /**
     * 添加收藏
     */
    private fun toCollect(status: Int) {
        val modifyPhoneViewModel: CollectOperationViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repository = CollectOperationRepository(HttpManager.getInstance())
                    return CollectOperationViewModel(repository, uid, id, "1") as T
                }
            }).get(CollectOperationViewModel::class.java)

        modifyPhoneViewModel.run {
            handleData(collect) {
                Logger.d("收藏成功---->>>")
                iv_collect.setBackgroundResource(R.mipmap.ic_collected)
                iv_collect.invalidate()
                ToastUtils.show(getString(R.string.collect_succ))
                //刷新收藏标识
                statusF = 1

            }
        }
    }

    /**
     *  取消收藏
     */
    private fun cancelCollect(status: Int) {
        val modifyPhoneViewModel: CollectOperationViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repository = CollectOperationRepository(HttpManager.getInstance())
                    return CollectOperationViewModel(repository, uid, id, "0") as T
                }
            }).get(CollectOperationViewModel::class.java)

        modifyPhoneViewModel.run {
            //                setParams(mobile, code)
            handleData(collect) {
                Logger.d("取消收藏成功---->>>")
                iv_collect.setBackgroundResource(R.mipmap.ic_uncollect)
                iv_collect.invalidate()
                ToastUtils.show(getString(R.string.collection_cancel))
                //刷新收藏标识
                statusF = 0
            }
        }
    }

    private fun initWebView() {
        mConfigWebView()
        web_view?.run {
            settings.domStorageEnabled = true
            webViewClient = mWebViewClient
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            // 在回退的时候,做处理(涉及 重定向 问题)
            if (canGoBack()) {
                getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
                goBack()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                webChromeClient = webChromeClient
            }
//            web_view.loadUrl(webUrl)
        }

    }


    private fun mConfigWebView() {
        val webSettings = web_view.getSettings()
        //权限配置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
        }
        //打开页面时,自适应屏幕
        webSettings.setUseWideViewPort(true)//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true)//缩放至屏幕的大小
        //使页面支持缩放
        webSettings.setJavaScriptEnabled(true) // 设置支持javascript脚本
        webSettings.setBuiltInZoomControls(false) // 设置显示缩放按钮
        webSettings.setSupportZoom(true) // 支持缩放
        //自适应屏幕（支持内容重新布局）
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)
        webSettings.setDisplayZoomControls(false)
        webSettings.setAllowFileAccess(true) // 允许访问文件
        webSettings.setDomStorageEnabled(true)
        webSettings.setDatabaseEnabled(true)
        webSettings.setPluginState(WebSettings.PluginState.ON)
        webSettings.setAppCacheEnabled(false)//启用缓存
    }


    override fun onResume() {
        web_view.onResume()
        super.onResume()
    }

    override fun onPause() {
        web_view.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        clearWebViewResource()
        super.onDestroy()
    }

    private fun clearWebViewResource() {
        if (web_view != null) {
            web_view.removeAllViews()
            //在5.1上如果不加上这句话就会出现内存泄露。这是5.1的bug
            // mComponentCallbacks导致的内存泄漏
            (web_view.getParent() as ViewGroup).removeView(web_view)
            web_view.setTag(null)
            web_view.clearHistory()
            web_view.destroy()
        }
    }


    private val webChromeClient = object : com.just.agentweb.WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            tv_title.text = title
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

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

