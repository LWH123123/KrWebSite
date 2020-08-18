package com.hlt.jzwebsite.ui.fragment

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.ProjectListAdapter
import com.hlt.jzwebsite.base.BaseFragment
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.paging.repository.ProjectRepository
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.ProjectViewModel
import kotlinx.android.synthetic.main.fragment_my_collection.*

/**
 * @author LXB
 * @description:
 * @date :2020/3/5 11:18
 */
class ProjectListFragment : BaseFragment() {
    private var cid: String? = null

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = ProjectRepository(HttpManager.getInstance(), cid)
                return ProjectViewModel(repository) as T
            }
        })
            .get(ProjectViewModel::class.java)
    }

    override var layoutId: Int = R.layout.fragment_project_list


    private val adapter by lazy {
        ProjectListAdapter { viewModel.retry() }
    }

    override fun initData() {
        multipleStatusView = multiple_status_view
        cid = SPUtils.getInstance().getString(Constants.SP_LIST_ID)
        Log.d(TAG, "SP_LIST_ID:===" + cid)
        initSwipe()
        initRecyclerView()
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
