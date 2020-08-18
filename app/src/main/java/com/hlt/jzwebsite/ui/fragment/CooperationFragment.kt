package com.hlt.jzwebsite.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.CooperationAdapter
import com.hlt.jzwebsite.base.BaseFragment
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Busines
import com.hlt.jzwebsite.paging.repository.CooperationRepository
import com.hlt.jzwebsite.ui.activity.ContactWayActivity
import com.hlt.jzwebsite.ui.activity.WebDetailCommActivity
import com.hlt.jzwebsite.utils.ImageLoader
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.CooperationViewModel
import kotlinx.android.synthetic.main.fragment_cooperation.*
import kotlinx.android.synthetic.main.fragment_dynamics.*
import kotlinx.android.synthetic.main.fragment_dynamics.recyclerView
import kotlinx.android.synthetic.main.fragment_dynamics.swipeRefreshLayout

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CooperationFragment : BaseFragment() {
    private var uid: String? = null
    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = CooperationRepository(HttpManager.getInstance(), uid)
                return CooperationViewModel(repository) as T
            }
        }).get(CooperationViewModel::class.java)
    }

    private val adapter by lazy {
        CooperationAdapter { viewModel.retry() }
    }

    override var layoutId = R.layout.fragment_cooperation

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            scrollview.scrollTo(0, 0)
        }
    }

    override fun initData() {
        multipleStatusView = multiple_status_view
        initSwipe()
        initRecyclerView()
        val isLogin = SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN)
        if (!isLogin) {
            uid = ""
        } else {
            uid = SPUtils.getInstance().getString(Constants.SP_USER_ID)
        }
        Log.d(TAG, "uid:===" + uid)
        cons_noice.setOnClickListener {
            val tokenError = SPUtils.getInstance().getInt(Constants.SP_USER_TOKEN_ERROR)
            if (tokenError == -1) { //wx token 失效
                SPUtils.getInstance().clear()
                ToastUtils.show("用户信息过期，请点击 我的 重新登录")
                return@setOnClickListener
            }
            /*Intent(context, WebDetailCommActivity::class.java).run {
                putExtra(Constants.BUNDLE_KEY_TITLE_DETAIL, "联系方式")
                putExtra(Constants.BUNDLE_KEY_ID, "")
                putExtra(Constants.BUNDLE_KEY_CATID, "")
                context?.startActivity(this)
            }*/
            Intent(context, ContactWayActivity::class.java).run {
                context?.startActivity(this)
            }
        }
//        activity?.let { StatusBarUtil.darkMode(it) }
//        activity?.let { StatusBarUtil.immersive(it.window,R.color.transparent,0.0f) }
    }


    override fun subscribeUi() {
        viewModel.run {
            pagedList.observe(viewLifecycleOwner, Observer {
                //                val list = it as PagedList<Busines>
//                list.forEach {
//                    removeIndexData(list,it)
//                }
                adapter.submitList(it)
            })
            refreshState.observe(
                viewLifecycleOwner,
                Observer {
                    swipeRefreshLayout.isRefreshing = false
                    when {
                        it.isLoading() ->
                            if (isRefreshFromPull) {
                                swipeRefreshLayout.isRefreshing = true
                                isRefreshFromPull = false
                            } else {
                                multipleStatusView?.showLoading()
                            }
                        it.isSuccess() ->
                            if (it.data!!) {
                                // refreshState 中的 data 表示数据是否为空
                                multipleStatusView?.showEmpty()
                            } else {
                                multipleStatusView?.showContent()
                            }
                        it.isError() -> multipleStatusView?.showError()
                    }
                })
            networkState.observe(viewLifecycleOwner, Observer {
                adapter.setRequestState(it)
            })
            initLoad(50)
        }
    }

    override fun onRetry() {
        viewModel.refresh()
    }


    /**
     *  移除第0 条数据，todo 需研究
     */
    fun removeIndexData(
        bleDeviceList: PagedList<Busines>,
        busines: Busines
    ) {
        var y: Int? = 0
        for (i in bleDeviceList.indices) {
            val device = bleDeviceList[i - y!!]
            if (busines.title == device!!.title) {
                bleDeviceList.removeAt(i - y)
                y++
            }
        }
    }


    private fun initSwipe() {
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swipeRefreshLayout.setOnRefreshListener {
            isRefreshFromPull = true
            viewModel.refresh()
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.setNestedScrollingEnabled(false)
        recyclerView.setFocusable(false);
    }

    /**
     *  设置顶部 banner 数据
     */
    fun setImg(context: Context, iv: AppCompatImageView, picUrl: String?) {
        if (picUrl?.isNotBlank()!!) {
            context.let { ImageLoader.load(it, picUrl, iv) }
        }
    }
}