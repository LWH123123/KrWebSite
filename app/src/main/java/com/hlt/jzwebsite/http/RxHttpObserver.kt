package com.hlt.jzwebsite.http

import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.RSASignature
import com.hlt.jzwebsite.utils.java.RSAUtils2
import com.hlt.jzwebsite.utils.java.StringUtils
import com.orhanobut.logger.Logger
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.ParameterizedType

/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
abstract class RxHttpObserver<T> : Observer<T> {

    /**
     * 必须要添加泛型才能正确解析
     * @param t
     */
//    abstract fun onResult(t: T)
//
//    abstract fun onError(errarMessage: String)


    override fun onSubscribe(d: Disposable) {
        if (!NetUtils.isConnected(App.instance)) {
            onError(RuntimeException(App.instance.getString(R.string.network_error)))
        }
    }


    override fun onError(e: Throwable) {
        e.message?.let {
            ExecutorUtils.main_thread(Runnable { ToastUtils.show(it) })
        }
    }

    override fun onNext(it: T) {
        //业务失败
        try {
            val result = it as? HttpResponse<*>
            if (result?.errorCode != 0) {
                onError(
                    RuntimeException(
                        if (result?.errorMsg.isNullOrBlank())
                            App.instance.getString(R.string.business_error)
                        else {
                            result?.errorMsg
                        }
                    )
                )
            }
            try {
                val res = String(RSAUtils2.decode(result?.resData))//分段
                if (StringUtils.isEmpty(res)) {
                    val message: String = "解密异常"
                    Logger.e("error:$message")
                    onError(
                        RuntimeException(
                            if (result?.errorMsg.isNullOrBlank())
                                App.instance.getString(R.string.business_error)
                            else {
                                result?.errorMsg
                            }
                        )
                    )
                    return
                }
                val sign = RSASignature.doCheck(res, result?.sign)
                if (!sign) {
                    val message: String = "签名异常"
                    Logger.e("error:$message")
                    onError(
                        RuntimeException(
                            if (result?.errorMsg.isNullOrBlank())
                                App.instance.getString(R.string.business_error)
                            else {
                                result?.errorMsg
                            }
                        )
                    )
                    return
                }
                val jsonObject = JSONObject(res)
                Logger.json(jsonObject.toString(1)) //输出解密内容 方便调试

                /**
                 *  todo 基类中统一对返回json 串处理，泛型转换有问题,以后再研究，先在每个接口处解析json 数据
                 *  报错：sun.reflect.generics.reflectiveObjects.TypeVariableImpl cannot be cast to java.lang.Class异常解决方法
                 */
//                val tClass = getTClass()
//                if (tClass == null) {
//                onNext(jsonObject as T )
//                } else {
//                    val message: String = "一个字段返回多个类型  解析不过！"
//                    Logger.e("error:$message")
//                onNext(jsonObject as T)
//                }

            } catch (e: JSONException) {
                e.printStackTrace()
                val message: String = "数据解析错误"
                Logger.e("error:$message")
            } catch (e: Exception) {
                e.printStackTrace()
                val message: String = "未知错误！"
                Logger.e("error:$message")
            }
        } catch (e: ClassCastException) {
            e.printStackTrace();
        }


    }

    override fun onComplete() {
    }

    /**
     * 获取泛型参数
     * Java获取泛型T的类型 T.class - hellozhxy的博客 - CSDN博客
     * https://blog.csdn.net/hellozhxy/article/details/82024712
     * @return
     */
    open fun getTClass(): Class<T>? {
        try {
            return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


}