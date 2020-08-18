package com.hlt.jzwebsite.utils.java;

/**
 * Created by lxb on 2020/2/22.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
public class ResponseThrowable extends Exception {
    public int code;
    public String message;

    public ResponseThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }
}
