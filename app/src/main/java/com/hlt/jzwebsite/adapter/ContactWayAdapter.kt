package com.hlt.jzwebsite.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.paging.BasePagingAdapter
import com.hlt.jzwebsite.model.Collec
import com.hlt.jzwebsite.model.Company
import com.hlt.jzwebsite.model.ContactListBean
import kotlinx.android.synthetic.main.item_contact_way.view.*
import org.byteam.superadapter.SuperAdapter
import org.byteam.superadapter.SuperViewHolder

/**
 * @author lwh
 * @description :
 * @date 2020/3/27.
 */
class ContactWayAdapter (context: Context,var items: MutableList<ContactListBean>, layoutResId: Int) :
    SuperAdapter<ContactListBean>(context, items, layoutResId){

    override fun onBind(
        holder: SuperViewHolder?,
        viewType: Int,
        layoutPosition: Int,
        item: ContactListBean?
    ) {
        val title = holder?.run { run({ item?.icon?.let { setImageResource(R.id.iv_avatar, it) } }) }
        val data =holder?.run { kotlin.run { setText(R.id.tv_data,item?.mesage) } }


        if(layoutPosition==0){
            val data =holder?.run { kotlin.run { setVisibility(R.id.view_line, View.GONE) } }
        }

        if(layoutPosition==items.size-1){
            val iv_menu: TextView = holder?.findViewById<TextView>(R.id.tv_data) as TextView
            iv_menu.maxEms=8

        }

    }


}