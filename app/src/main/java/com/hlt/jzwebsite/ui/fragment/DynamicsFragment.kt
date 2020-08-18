package com.hlt.jzwebsite.ui.fragment

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.DynamicsAdapter
import com.hlt.jzwebsite.base.BaseFragment
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.paging.repository.DynamicsRepository
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.DynamicsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dynamics.*

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：企业动态
 */
class DynamicsFragment : BaseFragment() {
    private var uid: String? = null
    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = DynamicsRepository(HttpManager.getInstance(),uid)
                return DynamicsViewModel(repository) as T
            }
        })
            .get(DynamicsViewModel::class.java)
    }

    private val adapter by lazy {
        DynamicsAdapter { viewModel.retry() }
    }


    override var layoutId = R.layout.fragment_dynamics


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
//        activity?.let { StatusBarUtil.darkMode(it) }
//        activity?.let { StatusBarUtil.immersive(it.window,R.color.transparent,0.0f) }
    }

    override fun subscribeUi() {
        viewModel.run {
            pagedList.observe(viewLifecycleOwner, Observer {
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

    private fun initSwipe() {
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swipeRefreshLayout.setOnRefreshListener {
            isRefreshFromPull = true
            viewModel.refresh()
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = adapter
    }


}