package com.hlt.jzwebsite.utils

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseDialog

/**
 * @author LXB
 * @description:
 * @date :2020/3/9 17:37
 */
class MessageDialog {
    /**
     * 显示错误信息、提示信息的dialog（不带标题）
     *
     * @param mContext
     * @param message
     */
    fun showMessageDialog(
        mContext: Context,
        message: String,
        messageDialogClick: MessageDialogClick
    ) {
        val builder = AlertDialog.Builder(mContext)
        val view = View.inflate(mContext, R.layout.dialog_prompt, null)
        builder.setView(view)
        builder.setCancelable(false)
        val tvPrompt = view.findViewById(R.id.tv_prompt) as TextView
        val tvConfirm = view.findViewById(R.id.tv_confirm) as TextView

        val dialog = builder.create()
        tvPrompt.text = message
        tvConfirm.setOnClickListener {
            dialog.cancel()
            messageDialogClick.onSureClickListener()
        }
        dialog.show()
    }


    fun showConfirmAndCancelDialog(
        context: Context,
        title: String,
        content: String,
        dialogClick: DialogClick
    ) {
        showCancelAndSureDialog(context, title, content, 0, 0, dialogClick)
    }

    /**
     * 取消、确定的dialog
     *
     * @param context
     * @param title
     * @param content
     * @param cancelColor
     * @param sureColor
     * @param dialogClick
     */
    fun showCancelAndSureDialog(
        context: Context,
        title: String,
        content: String,
        cancelColor: Int,
        sureColor: Int,
        dialogClick: DialogClick
    ) {
        val dialog:BaseDialog = BaseDialog(context, R.style.BaseDialog, R.layout.dialog_confirm_cancel)as BaseDialog
        dialog.setCancelable(false)
        val dialogTitle :TextView = dialog.findViewById(R.id.tv_dialog_title) as TextView
        val dialogContent:TextView = dialog.findViewById(R.id.tv_dialog_content) as TextView
        val dialogCancel:TextView = dialog.findViewById(R.id.tv_dialog_cancel) as TextView
        val dialogSure:TextView = dialog.findViewById(R.id.tv_dialog_sure)  as TextView


        if (TextUtils.isEmpty(title)) {
            dialogTitle.setVisibility(View.GONE)
        } else {
            dialogTitle.setVisibility(View.VISIBLE)
            dialogTitle.setText(title)
        }

        if (TextUtils.isEmpty(content)) {
            dialogContent.setVisibility(View.GONE)
        } else {
            dialogContent.setVisibility(View.VISIBLE)
            dialogContent.setText(content)
        }
        dialogCancel.setText("取消")
        //        dialogCancel.setTypeface(Typeface.DEFAULT);
        if (cancelColor == 0) {
            dialogCancel.setTextColor(context.resources.getColor(R.color.divider))
        } else {
            dialogCancel.setTextColor(context.resources.getColor(R.color.divider))
        }
        dialogSure.setText("确认")
        //        dialogSure.setTypeface(Typeface.DEFAULT);
        if (sureColor == 0) {
            dialogSure.setTextColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            dialogSure.setTextColor(context.resources.getColor(R.color.colorPrimary))
        }

        dialogCancel.setOnClickListener(View.OnClickListener {
            dialog.cancel()
            dialogClick.onCancelClickListener()
        })

        dialogSure.setOnClickListener(View.OnClickListener {
            dialog.cancel()
            dialogClick.onSureClickListener()
        })
        dialog.show()
    }

    /**
     * dialog取消、确定的点击事件监听
     */
    interface DialogClick {
        fun onCancelClickListener()

        fun onSureClickListener()
    }

    interface MessageDialogClick {
        fun onSureClickListener()
    }
}