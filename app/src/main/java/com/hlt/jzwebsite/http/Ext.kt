package com.hlt.jzwebsite.http

import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.utils.java.RSASignature
import com.hlt.jzwebsite.utils.java.RSAUtils2
import com.hlt.jzwebsite.utils.java.StringUtils
import com.orhanobut.logger.Logger
import com.tencent.mm.opensdk.diffdev.a.e
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
fun <T> Observable<T>.async(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.asyncSubscribe(onNext: (T) -> Unit, onError: (Throwable) -> Unit) {
    this.async()
        .subscribe(object : RxHttpObserver<T>() {
            override fun onNext(it: T) {
                super.onNext(it)
                onNext(it)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                onError(e)
            }

        })

}