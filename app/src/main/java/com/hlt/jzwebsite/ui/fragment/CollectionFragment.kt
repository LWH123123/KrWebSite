package com.hlt.jzwebsite.ui.fragment

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.CollectionAdapter
import com.hlt.jzwebsite.base.BaseFragment
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.paging.repository.CollectionRepository
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.CollectionViewModel
import kotlinx.android.synthetic.main.fragment_my_collection.*

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class CollectionFragment : BaseFragment() {
    private var uid: String? = null
    private var collecs: PagedList<Collec>? = null

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = CollectionRepository(HttpManager.getInstance(), uid)
                return CollectionViewModel(repository) as T
            }
        })
            .get(CollectionViewModel::class.java)
    }

    override var layoutId: Int = R.layout.fragment_my_collection


    private val adapter by lazy {
        CollectionAdapter { viewModel.retry() }
    }

    override fun initData() {
        multipleStatusView = multiple_status_view
        uid = SPUtils.getInstance().getString(Constants.SP_USER_ID)
        Log.d(TAG, "SP_USER_ID:===" + uid)
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