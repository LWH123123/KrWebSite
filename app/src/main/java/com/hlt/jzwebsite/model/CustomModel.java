package com.hlt.jzwebsite.model;

import com.sunfusheng.marqueeview.IMarqueeItem;

/**
 * Created by lxb on 2020/2/26.
 * 邮箱：2072301410@qq.com
 * TIP： 公告 自定义model
 */
public class CustomModel implements IMarqueeItem {

    public int id;
    public String title;
    public String content;

    public CustomModel(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    @Override
    public CharSequence marqueeMessage() {
        return title + "\n" + content;
    }
}
