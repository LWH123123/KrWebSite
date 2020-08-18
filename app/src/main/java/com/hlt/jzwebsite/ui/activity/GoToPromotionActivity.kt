package com.hlt.jzwebsite.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cn.bingoogolapple.bgabanner.BGABanner
import cn.sharesdk.framework.Platform
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback
import cn.sharesdk.wechat.friends.Wechat
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.base.BaseActivity
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.repository.GoToPromotionRepository
import com.hlt.jzwebsite.utils.ImageLoader
import com.hlt.jzwebsite.utils.StatusBarUtil
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.Utils
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.GoToPromotionViewModel
import com.orhanobut.logger.Logger
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_go_to_prommotion.*
import kotlinx.android.synthetic.main.ui_layout_mine_module_one.*
import kotlinx.android.synthetic.main.ui_layout_prommotion_constop.*
import kotlinx.android.synthetic.main.ui_layout_prommotion_constop.iv_avatar
import kotlinx.android.synthetic.main.ui_layout_prommotion_constop_old.*
import kotlinx.android.synthetic.main.ui_toolbar_common.*
import okhttp3.Call
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.MalformedURLException
import java.net.URL

/**
 * @author LXB
 * @description: 立即推广
 * @date :2020/3/17 14:41
 */
class GoToPromotionActivity : BaseActivity() {
    //是否下载海报
    private var isDownloadPoster = false
    private var posters: List<String>? = null
    private var imagePath: String? = null //要下载的图片url
    private var bitmapPic: Bitmap? = null
    private var file: File? = null
    val MSG_ONCLICK_SAVE_PIC_UI = 100
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_ONCLICK_SAVE_PIC_UI -> {
                    bitmapPic = msg.obj as Bitmap
                    ToastUtils.show("保存成功")
                    saveToSystemGallery(
                        mContext,
                        file,
                        file!!.name
                    ) //把文件插入到系统图库
                }
            }
        }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = GoToPromotionRepository(HttpManager.getInstance())
                return GoToPromotionViewModel(repository, userToken) as T
            }
        }).get(GoToPromotionViewModel::class.java)
    }

    private var userToken: String? = null

    override var layoutId: Int = R.layout.activity_go_to_prommotion

    override fun initStatusBar() {
        super.initStatusBar()
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)
    }

    override fun initData() {
        tv_title.text = getString(R.string.prommotion)
        userToken = SPUtils.getInstance().getString(Constants.SP_USER_TOKEN)
        Log.d(TAG, "userToken:===" + userToken)
        iv_close.apply {
            setOnClickListener { finish() }
        }

        initBanner()

    }

    private fun initBanner() {
        //头像
        val avatar = SPUtils.getInstance().getString(Constants.SP_AVATAR)
        val nickName = SPUtils.getInstance().getString(Constants.SP_NICK_NAME)
        tv_nick.text = nickName
        if (avatar.isNullOrEmpty()){
            iv_avatar.setBackgroundResource(R.mipmap.ic_default_avator)
        }else{
            mContext?.let {
                setImg(
                    it, iv_avatar,
                    avatar
                )
            }
        }
    }

    override fun subscribeUi() {
        handleData(viewModel.promotion) {
            Logger.d("获取立即推广数据---->>>")
            if (it.result.invit_id.toString().isNotEmpty()) {
//                tv_inviteCode.text = it.result.invit_id.toString()
                val spannableString = SpannableString(resources.getString(R.string.prommotion_txt_tip))
                val colorSpan = ForegroundColorSpan(Color.parseColor("#CC0000"))
                 spannableString.setSpan(colorSpan,3,8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tv_inviteCode.text = spannableString
            } else {
            }
            posters = it.result.poster
            val banner = xbanner as BGABanner
            banner.run {
                setAdapter { _, itemView, model, _ ->
                    ImageLoader.load(this.context, model as String?, itemView as ImageView)
                }
                //点击预览大图
//                setDelegate { _, _, _, position ->
//                    if (posters!!.isNotEmpty()) {
//                        val item = posters!![position]
//                        AppIdentityJumpUtils.previewLargePic(
//                            this@GoToPromotionActivity,
//                            posters,
//                            position
//                        )
//                    }
//                }

                val imageList = ArrayList<String>()
                val titleList = ArrayList<String>()
                Observable.fromIterable(posters)
                    .subscribe { list ->
                        imageList.add(list)
                        titleList.add("")
                    }
                setAutoPlayAble(imageList.size > 1)
                setData(imageList, titleList)
            }
            //保存海报
            val posters = it.result.poster
            tvSavePic.setOnClickListener {
                isDownloadPoster = true
                val currentItem = xbanner.currentItem
                if (posters != null) {
                    val imageUrl: String = posters!!.get(currentItem)
                    try {
                        imagePath = imageUrl
                        mDownloadPosterPic(imagePath!!)
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }
                }
            }
            //分享海报
            tvShareWeinXin.setOnClickListener {
                val currentItem = xbanner.currentItem
                if (posters != null) {
                    val imageUrl: String = posters!!.get(currentItem)
                    imagePath = imageUrl
                    try {
                        isDownloadPoster = false
                        mDownloadPosterPic(imagePath!!)
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }
                }
            }

        }

    }

    override fun initClickEvent() {
        super.initClickEvent()
        val text = tv_inviteCode.text
        btn_copy.setOnClickListener {
            if (text.isNotEmpty()){
                mContext?.let { it1 -> Utils.copyTextClipboard(it1, text as String?) }
                ToastUtils.show(getString(R.string.invite_code_msg_succ))
                return@setOnClickListener
            }else{
                ToastUtils.show(getString(R.string.invite_code_msg_tip))
                return@setOnClickListener
            }
        }
    }





    /**
     * 将获取到的网络图片下载下来，通过流转成Bitmap
     *
     * @param picturePath 网络图片文件路径
     * @return
     */
    @Throws(MalformedURLException::class)
    private fun mDownloadPosterPic(picturePath: String) {
        checkPermission()
    }


    private fun checkPermission() {
        val perms = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        EasyPermissions.requestPermissions(this, "保存图片需要以下权限，请允许", 0, *perms)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 0) {
            if (perms.isNotEmpty()) {
                if (perms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    downloadPoster()
                }
            }
        }
    }


    @Throws(MalformedURLException::class)
    private fun downloadPoster() {
        val url = URL(imagePath)
        OkHttpUtils
            .get()
            .url(url.toString())
            .build()
            .execute(object : FileCallBack(
                Environment.getExternalStorageDirectory().absolutePath,
                "app_poster"
            ) {
                override fun onResponse(response: File, id: Int) {
                    Log.e(TAG, "onResponse :" + response.absolutePath)
                    file = response
                    Log.d(TAG, "文件下载成功 路径为：" + file!!.getAbsolutePath())
                    if (isDownloadPoster) {
                        try {
                            val bitmap: Bitmap? = mToBitmap(file!!)
                            val message =
                                Message.obtain() //下面这是把图片携带在Message里面
                            message.what =
                                MSG_ONCLICK_SAVE_PIC_UI
                            message.obj = bitmap
                            mHandler.sendMessage(message)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                    } else {
                        try { //将图片文件转为bitmap
                            val bitmap: Bitmap? = mToBitmap(file!!)
                            bitmap?.let { sendToFriends(imagePath, it) } ?: Log.e(TAG, "下载推广海报失败!")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(
                    call: Call?,
                    e: Exception,
                    id: Int
                ) {
                    Log.e(TAG, "下载出错:" + e.message)
                    ToastUtils.show("下载出错啦，再试试呗...")
                    val pdfFilePath =
                        Environment.getExternalStorageDirectory().toString() + "/temp" + file?.getName()
                    val file = File(pdfFilePath)
                    if (!file.exists()) {
                        file.delete()
                    }
                }

                override fun inProgress(
                    progress: Float,
                    total: Long,
                    id: Int
                ) {
                    super.inProgress(progress, total, id)
                    Log.d(TAG, "inProgress:======" + 100 * progress)
                }
            })
    }


    /**
     * 将图片文件转为bitmap
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    private fun mToBitmap(file: File): Bitmap? {
        return if (file.exists()) {
            val options = BitmapFactory.Options()
            BitmapFactory.decodeStream(FileInputStream(file), null, options)
        } else {
            null
        }
    }

    /**
     * 发送给好友
     */
    private fun sendToFriends(imgUrl: String?, bitmap: Bitmap) {
        val oks = OnekeyShare() //关闭sso授权
        oks.disableSSOWhenAuthorize()
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImageUrl(imgUrl) //只分享图片
        oks.setShareContentCustomizeCallback(object : ShareContentCustomizeCallback {
            override fun onShare(platform: Platform, shareParams: Platform.ShareParams) {
                if (platform.getName().equals(Wechat.NAME)) { // 分享到微信朋友/朋友圈
                    shareParams.setShareType(Platform.SHARE_IMAGE)
                    shareParams.setUrl(imgUrl)
                    shareParams.setImageData(bitmap)
                }
            }
        })
        // 启动分享GUI
        oks.show(this)
    }


    /**
     * 保存图片到本地,其次把文件插入到系统图库
     *
     * @param context
     * @param file
     * @param fileName
     */
    fun saveToSystemGallery(
        context: Context?,
        file: File?,
        fileName: String?
    ) {
        try {
            MediaStore.Images.Media.insertImage(
                context?.contentResolver,
                file?.absolutePath, fileName, null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 最后通知图库更新
        context?.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse(file?.absolutePath)
            )
        )
    }

    /**
     *  设置顶部 banner 数据
     */
    fun setImg(context: Context, iv: AppCompatImageView, picUrl: String?) {
        if (picUrl?.isNotBlank()!!) {
            context.let { ImageLoader.load(it, picUrl, iv) }
        }
    }


}

