package com.hlt.jzwebsite.ui.activity

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.ContactWayAdapter
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.ContactListBean
import com.hlt.jzwebsite.model.ContactWayBean
import com.hlt.jzwebsite.repository.ContactWayRepository
import com.hlt.jzwebsite.viewmodel.ContactWayViewModel
import kotlinx.android.synthetic.main.activity_contact_way.*
import kotlinx.android.synthetic.main.ui_toolbar_common.*

/**
 * A simple [Fragment] subclass.
 */
class ContactWayActivity : BaseActivity() {
    private var cid: String? = null
    private lateinit var contactwayadapter: ContactWayAdapter



    override var layoutId: Int = R.layout.activity_contact_way
    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository =
                    ContactWayRepository(HttpManager.getInstance())
                return ContactWayViewModel(repository) as T
            }
        })
            .get(ContactWayViewModel::class.java)
    }



    override fun initData() {
        tv_title.text = getString(R.string.title_contactway);
        iv_close.apply {
            setOnClickListener { finish() }
        }


    }

    override fun subscribeUi() {
        handleData(viewModel.contactway) {
            initRecycleData(it)

        }
    }

    private fun initRecycleData(it: ContactWayBean) {
        var list = arrayListOf<ContactListBean>()

        list.apply {
            val result = it.result
            add(ContactListBean(R.mipmap.country_hot,result.country_hot))
            var data : String
            Log.e("数组中的数据",result.customer_wx[1])
          /*  result.customer_wx.forEach {
                data?.append(it+"/n")
            }*/
               /*data?.append("客服一 : ${result.customer_wx[0]} /n")
               data?.append("客服二 : ${result.customer_wx[1]} /n")*/
 data="""客服1 : ${result.customer_wx[0]}  
客服3 : ${result.customer_wx[1]}  
客服8 : ${result.customer_wx[2]}"""
//               data="客服三 : ${result.customer_wx[2]} \n"
            Log.e("我的数据",data)
            add(ContactListBean(R.mipmap.customer_wx,data))
            add(ContactListBean(R.mipmap.business_consultation,result.business_consultation))
            add(ContactListBean(R.mipmap.address,result.adress))
        }
        contactwayadapter=ContactWayAdapter(this,list,R.layout.item_contact_way)
        re_view.adapter=contactwayadapter
        re_view.setNestedScrollingEnabled(false)
        re_view.setFocusable(false)



    }
}

