package com.hlt.jzwebsite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hlt.jzwebsite.R;

/**
 * Created by ${lixuebin} on 2018/5/24.
 * 邮箱：2072301410@qq.com
 * TIP：  强制更新  弹窗
 */

public class DialogAppForceUpdate extends Dialog {
    private Context mContext;
    private TextView tvBtnOk;
    private TextView tvTitle;
    private TextView tvContent;

    /**
     *  打开对话框 参数
     * @param context
     * @param title  对话框的标题
     * @param content  对话框的 内容
     */
    public DialogAppForceUpdate(Context context, String title, String content) {
        super(context, R.style.WinDialog);
        setContentView(R.layout.dialog_app_force_update);
        this.mContext = context;
        tvTitle=(TextView) this.findViewById(R.id.dialog_title);
        tvContent=(TextView) this.findViewById(R.id.dialog_content);
        tvTitle.setText(title);// 设置标题
        tvContent.setText(content);// 设置内容
        tvBtnOk=(TextView) this.findViewById(R.id.btn_ok);

        tvBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClickSure();
//                    DialogAppForceUpdate.this.dismiss();
                }
            }
        });
    }
    @Override
    public void show() {
        super.show();

    }
    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            return false;
        }
        return super.onTouchEvent(event);
    }

    public void setCancelEnable(boolean enable){
        setCanceledOnTouchOutside(enable);
        setCancelable(enable);
    }

    private DialogAppForceUpdate.onClickSureBtnListener listener;


    public void setListener(DialogAppForceUpdate.onClickSureBtnListener listener) {
        this.listener = listener;
    }


    public interface onClickSureBtnListener {
        void onClickSure();
    }
}
