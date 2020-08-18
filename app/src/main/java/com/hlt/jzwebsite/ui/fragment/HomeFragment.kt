package com.hlt.jzwebsite.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.bingoogolapple.bgabanner.BGABanner
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.HttpConstants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.IntroduceAdapter
import com.hlt.jzwebsite.adapter.ProjectMenuAdapter
import com.hlt.jzwebsite.adapter.TrainingClassAdapter
import com.hlt.jzwebsite.base.BaseFragment
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.*
import com.hlt.jzwebsite.paging.repository.HomeRepository
import com.hlt.jzwebsite.ui.activity.*
import com.hlt.jzwebsite.ui.activity.video.VideoPlayerActivity
import com.hlt.jzwebsite.utils.AppUtils
import com.hlt.jzwebsite.utils.AppUtils.Companion.openPackage
import com.hlt.jzwebsite.utils.ImageLoader
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.HomeViewModel
import com.orhanobut.logger.Logger
import com.sunfusheng.marqueeview.MarqueeView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_dynamics.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_home_new.*
import kotlinx.android.synthetic.main.ui_layout_home_end_module.*
import kotlinx.android.synthetic.main.ui_layout_home_noice_module_new.*
import kotlinx.android.synthetic.main.ui_layout_home_video_module.*


/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class HomeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private lateinit var projectMenuAdapter: ProjectMenuAdapter
    private lateinit var introduceAdapter: IntroduceAdapter
    private lateinit var classAdapter: TrainingClassAdapter
    private lateinit var homeBean: HomeBean

    companion object {
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = HomeRepository(HttpManager.getInstance())
                return HomeViewModel(repository) as T
            }
        })
            .get(HomeViewModel::class.java)
    }

    override var layoutId = R.layout.fragment_home_new

    override fun lazyLoad() {
        super.lazyLoad()
        if (!isPrepared || !isVisible) {
            return;
        }
//        addData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
        }
    }

    override fun initData() {
        multipleStatusView = multiple_status_view
        initSwipe()
        isPrepared = true;
        lazyLoad()
        //状态栏透明和间距处理
//        activity?.let { StatusBarUtil.darkMode(it) }
//        activity?.let { StatusBarUtil.immersive(it.window,R.color.transparent,0.0f) }

    }


    override fun subscribeUi() {
        handleData(viewModel.homeData) {
            Logger.d("首页数据获取成功---->>")
            val homeBean: HomeBean = it
            addData(it)
        }
    }

    private fun initSwipe() {
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swipeRefreshLayout.setOnRefreshListener(this)
    }


    private fun addData(it: HomeBean) {
        //轮播图
        val slideshow = it.result.slideshow
        bindTopBannerData(slideshow)
        //公告
        val messages = mutableListOf<String>()
        val notices = it.result.notice
        notices?.forEach {
            messages.add(it.title)
        }
        val notice = notice as MarqueeView<String>
        notice.startWithList(messages)
        notice.setOnItemClickListener(MarqueeView.OnItemClickListener { position, textView ->
            val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
            if (!isLogin) {
                Intent(activity, VerificaCodeLoginActivity::class.java).run {
                    activity?.startActivity(this)
                }
            } else {
                if (notices != null && notices.size > 0) {
                    val id = notices.get(position).id
                    val cid = notices.get(position).cid
                    mCommonFun(id, cid, position) //详情
                }
            }
        })
        //项目分类s
        val serve = it.result.serve
        projectMenuAdapter =
            ProjectMenuAdapter(
                mActivity,
                serve as MutableList<Serve>,
                R.layout.item_recy_home_project_menu
            )
        recyc_project.adapter = projectMenuAdapter
        projectMenuAdapter.setOnItemClickListener { itemView, viewType, position ->
            if (serve != null && serve.size > 0) {
                val title = serve.get(position).title
                val ids = serve.get(position).id
                val cid = serve.get(position).cid
                val types = serve.get(position).types
                if (types == 0) {// 详情
                    mCommonFunServe(ids, cid, position)
                } else {//房车万里行list
                    if (position == 0) {
                        mAppOpenOtherApp(it.result.junzi.returnurl, title)
                    } else {
                        SPUtils.getInstance().put(Constants.SP_LIST_ID, title)
                        Intent(context, ProjectListActivity::class.java).run {
                            putExtra(Constants.TITLE, title)
                            context?.startActivity(this)
                        }
                    }
                }
            } else {
            }
        }
        recyc_project.setNestedScrollingEnabled(false)
        //企业介绍
        val company = it.result.company
        introduceAdapter =
            IntroduceAdapter(
                mActivity,
                company as MutableList<Company>,
                R.layout.item_recy_home_introduce
            )
        recyc_introduce.adapter = introduceAdapter
        val headerView =
            LayoutInflater.from(context).inflate(R.layout.ui_layout_home_tip_view, null)
        initHeaderData(headerView, 1)
        introduceAdapter.addHeaderView(headerView)
        introduceAdapter.setOnItemClickListener { itemView, viewType, position ->
            if (company != null && company.size > 0) {
                val id = company.get(position - 1).id
                val cid = company.get(position - 1).cid
                var types: Int = company.get(position - 1).types
                val title = company.get(position - 1).title
                if (types == 0) {// 详情
                    mCommonFunCompany(id, cid, position)
                } else { //上市列表
                    SPUtils.getInstance().put(Constants.SP_LIST_ID, title)
                    Intent(context, ProjectListActivity::class.java).run {
                        putExtra(Constants.TITLE, title)
                        context?.startActivity(this)
                    }
                }
            } else {
            }
        }
        recyc_introduce.setNestedScrollingEnabled(false)
        //在线纪实
        val onlinerecord: String = it.result.onlinerecord
        if (onlinerecord.isNullOrEmpty()) return
        iv_play.setOnClickListener {
            Intent(context, VideoPlayerActivity::class.java).run {
                putExtra(Constants.WEB_TITLE, "")
                putExtra(Constants.WEB_URL, onlinerecord/*Constants.test_vidio*/)
                context?.startActivity(this)
            }
        }
        //课堂培训
        val classroom = it.result.classroom
        classAdapter =
            TrainingClassAdapter(
                mActivity,
                classroom as MutableList<Classroom>,
                R.layout.item_recy_home_class_new
            )
        recyc_classroom.adapter = classAdapter
        val header =
            LayoutInflater.from(context).inflate(R.layout.ui_layout_home_tip_view, null)
        initHeaderData(header, 3)
        classAdapter.addHeaderView(header)
        classAdapter.setOnItemClickListener { itemView, viewType, position ->
            val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
            if (!isLogin) {
                Intent(mActivity, VerificaCodeLoginActivity::class.java).run {
                    mActivity?.startActivity(this)
                }
            } else {
                val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
                if (tokenError == -1) { //wx token 失效
                    SPUtils.getInstance().clear()
                    ToastUtils.show(getString(R.string.relogin_tip))
                    return@setOnItemClickListener
                }
                if (classroom != null && classroom.size > 0) {
                    val id = classroom.get(position - 1).id
                    val cid = classroom.get(position - 1).cid
                    Intent(context, WebDetailActivity::class.java).run {
                        putExtra(Constants.BUNDLE_KEY_ID, id.toString())
                        putExtra(Constants.BUNDLE_KEY_CATID, cid.toString())
                        putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
                        context?.startActivity(this)
                    }
                } else {
                }
            }

        }
        recyc_classroom.setNestedScrollingEnabled(false)
        //bottom  banner
        val junzi = it.result.junzi
        iv_banner.setOnClickListener {
            mAppOpenOtherApp(junzi.returnurl, "title")
        }

    }

    /**
     * APP打开另一个APP
     * tip :包名拉起（这里就是进去启动页）
     *  A拉起B可实现的几种方法：
     *  1、包名，特定Activity名拉起；2、包名拉起 启动页 3、url拉起
     */
    private fun mAppOpenOtherApp(url: String, title: String) {
        if (AppUtils.checkPackInfo(mActivity, Constants.APPLICATIONID_JZSP)) {
            if (openPackage(mActivity, Constants.APPLICATIONID_JZSP)) {
            } else {
                val intent =
                    mActivity.packageManager.getLaunchIntentForPackage(Constants.APPLICATIONID_JZSP)
                if (intent != null) {
                    intent!!.putExtra("type", "110")
                    intent!!.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        } else {//下载操作
            Log.d(TAG, "没有安装")
            val junzi = url
            Intent(activity, WebActivity::class.java).run {
                putExtra(Constants.WEB_TITLE, title)
                putExtra(Constants.WEB_URL, junzi!!)
                activity?.startActivity(this)
            }
        }
    }

    private fun mCommonFun(
        id: Int,
        catId: Int,
        position: Int
    ) {
//        val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
//        if (tokenError == -1) { //wx token 失效
//            SPUtils.getInstance().clear()
//            ToastUtils.show(getString(R.string.relogin_tip))
//            return
//        }
        Intent(context, WebDetailCommActivity::class.java).run {
            putExtra(Constants.BUNDLE_KEY_ID, id.toString())
            putExtra(Constants.BUNDLE_KEY_CATID, catId.toString())
            putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
            context?.startActivity(this)
        }
    }

    private fun mCommonFunCompany(
        id: Int,
        catId: Int,
        position: Int
    ) {
//        val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
//        if (tokenError == -1) { //wx token 失效
//            SPUtils.getInstance().clear()
//            ToastUtils.show(getString(R.string.relogin_tip))
//            return
//        }
        Intent(context, WebDetailCommActivity::class.java).run {
            putExtra(Constants.BUNDLE_KEY_ID, id.toString())
            putExtra(Constants.BUNDLE_KEY_CATID, catId.toString())
            if (position == 1) {
                putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "企业简章")
            } else if (position == 2) {
                putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "为民服务体系")
            } else {
                putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
            }
            context?.startActivity(this)
        }
    }


    private fun mCommonFunServe(
        id: Int,
        catId: Int,
        position: Int
    ) {
//        val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
//        if (tokenError == -1) { //wx token 失效
//            SPUtils.getInstance().clear()
//            ToastUtils.show(getString(R.string.relogin_tip))
//            return
//        }

        val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
        if (!isLogin) {
            Intent(activity, VerificaCodeLoginActivity::class.java).run {
                activity?.startActivity(this)
            }
        } else {
            Intent(context, WebDetailCommActivity::class.java).run {
                putExtra(Constants.BUNDLE_KEY_ID, id.toString())
                putExtra(Constants.BUNDLE_KEY_CATID, catId.toString())
                if (position == 2) {
                    putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "跨融直播")
                } else {
                    putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "0")
                }
                context?.startActivity(this)
            }
        }
    }

    private fun initHeaderData(headerView: View, flag: Int) {
        val tv_tip = headerView.findViewById(R.id.tv_tip) as TextView
        val tv_see_more = headerView.findViewById(R.id.tv_see_more) as TextView
        if (flag == 1) {
            tv_tip.setText(R.string.introduce_tip)
            tv_see_more.visibility = View.GONE
        } else if (flag == 2) {
            tv_tip.setText(R.string.online_tip)
            tv_see_more.visibility = View.GONE
        } else {
            tv_tip.setText(R.string.class_tip)
//            cons_module_threetip.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_bg_default))
            tv_see_more.visibility = View.VISIBLE
            tv_see_more.setOnClickListener {
                Intent(context, ClassListActivity::class.java).run {
                    context?.startActivity(this)
                }
            }
            web_container.visibility = View.VISIBLE
        }
    }


    /**
     *  设置底部 banner 数据
     */
    fun setImg(context: Context, iv_banner: AppCompatImageView, picUrl: String?) {
        if (picUrl?.isNotBlank()!!) {
            context.let { ImageLoader.load(it, picUrl, iv_banner) }
        }
    }

    /**
     *  设置顶部 banner 数据
     */
    fun bindTopBannerData(bannerData: List<Slideshow>) {
        val banner = banner as BGABanner
        banner.run {
            setAdapter { _, itemView, model, _ ->
                ImageLoader.load(this.context, model as String?, itemView as ImageView)
            }
            setDelegate { _, _, _, position ->
                if (bannerData.isNotEmpty()) {
                    val item = bannerData[position]
                    if (item.url.isNullOrEmpty()) return@setDelegate
                    Intent(context, WebActivity::class.java).run {
                        putExtra(Constants.WEB_TITLE, item.title)
                        putExtra(Constants.WEB_URL, item.url)
                        context.startActivity(this)
                    }
                }
            }
            val imageList = ArrayList<String>()
            val titleList = ArrayList<String>()
            Observable.fromIterable(bannerData)
                .subscribe { list ->
                    imageList.add(HttpConstants.BASE_URL + list.image)
                    titleList.add(list.title)
                }
            setAutoPlayAble(imageList.size > 1)
            setData(imageList, titleList)
        }
    }

    override fun onRefresh() {
        if (!NetUtils.isConnected(App.instance)) {
            ToastUtils.show(getString(R.string.network_error))
            swipeRefreshLayout.isRefreshing = false
            return
        }
        swipeRefreshLayout.isRefreshing = false  //关闭下拉刷新进度条
        val vm = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: java.lang.Class<T>): T {
                val repository = HomeRepository(HttpManager.getInstance())
                return HomeViewModel(repository) as T
            }
        })
            .get(HomeViewModel::class.java)
        handleData(vm.homeData) {
            Logger.d("首页数据刷新成功---->>")
            addData(it)
//            projectMenuAdapter.notifyDataSetChanged()
//            introduceAdapter.notifyDataSetChanged()
//            classAdapter.notifyDataSetChanged()
        }
    }

    override fun onRetry() {
        super.onRetry()
        multipleStatusView?.showNoNetwork()
        multipleStatusView?.setOnRetryClickListener {

        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_noice ->
                Intent(context, WebActivity::class.java).run {
                    putExtra(Constants.WEB_TITLE, "title")
                    putExtra(
                        Constants.WEB_URL,
                        "https://web.gtt20.com/app/index.php?i=1&c=entry&m=ewei_shopv2&do=mobile&r=article&aid=8"
                    )
                    context?.startActivity(this)
                }
        }
    }
}