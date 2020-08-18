package com.hlt.jzwebsite.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hlt.jzwebsite.R;


public class Dialog4AppUpdate extends Dialog {

    private TextView tvBtnCancel;
    private TextView tvBtnSure;
    private TextView tvTitle;
    private TextView tvContent;

    /**
     * 打开对话框 参数
     *
     * @param context
     */
    public Dialog4AppUpdate(Context context, String title, String desc) {
        super(context, R.style.WinDialog);
        setContentView(R.layout.dialog_app_update_layout);
        tvTitle = (TextView) this.findViewById(R.id.title);
        tvContent = (TextView) this.findViewById(R.id.dialog_content);
        tvTitle.setText(title);
        tvContent.setText(desc);
        tvBtnSure = (TextView) this.findViewById(R.id.btn_ok);
        tvBtnCancel = (TextView) this.findViewById(R.id.btn_cancel);
        tvBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClickSure();
                    Dialog4AppUpdate.this.dismiss();
                }
            }
        });

        tvBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClickCancel();
                }
                Dialog4AppUpdate.this.dismiss();
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    public void setCancelEnable(boolean enable) {
        setCanceledOnTouchOutside(enable);
        setCancelable(enable);
    }

    private onClickSureBtnListener listener;


    public void setListener(onClickSureBtnListener listener) {
        this.listener = listener;
    }


    public interface onClickSureBtnListener {
        void onClickSure();
        void onClickCancel();
    }


}
