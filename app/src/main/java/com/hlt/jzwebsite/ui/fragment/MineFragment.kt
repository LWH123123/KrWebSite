package com.hlt.jzwebsite.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alibaba.fastjson.JSON
import com.hlt.jzwebsite.App
import com.hlt.jzwebsite.Constants
import com.hlt.jzwebsite.R
import com.hlt.jzwebsite.adapter.PartnerListAdapter
import com.hlt.jzwebsite.base.BaseFragment
import com.hlt.jzwebsite.eventbus.LoginMessageEvent
import com.hlt.jzwebsite.http.HttpManager
import com.hlt.jzwebsite.model.UserInfo
import com.hlt.jzwebsite.model.UserInfo.mContext
import com.hlt.jzwebsite.repository.MineRepository
import com.hlt.jzwebsite.ui.activity.CollectionActivity
import com.hlt.jzwebsite.ui.activity.GoToPromotionActivity
import com.hlt.jzwebsite.ui.activity.SettingActivity
import com.hlt.jzwebsite.ui.activity.VerificaCodeLoginActivity
import com.hlt.jzwebsite.utils.ImageLoader
import com.hlt.jzwebsite.utils.NetUtils
import com.hlt.jzwebsite.utils.ToastUtils
import com.hlt.jzwebsite.utils.java.DisplayUtil
import com.hlt.jzwebsite.utils.java.SPUtils
import com.hlt.jzwebsite.viewmodel.MineViewModel
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_dynamics.multiple_status_view
import kotlinx.android.synthetic.main.fragment_dynamics.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.fragment_mine.scrollview
import kotlinx.android.synthetic.main.fragment_mine_new.*
import kotlinx.android.synthetic.main.ui_layout_mine_module_cardview_top.*
import kotlinx.android.synthetic.main.ui_layout_mine_module_end_old.*
import kotlinx.android.synthetic.main.ui_layout_mine_module_one_old.*
import kotlinx.android.synthetic.main.ui_layout_mine_module_two_old.*
import org.greenrobot.eventbus.EventBus


/**
 * Created by lxb on 2020/2/21.
 * 邮箱：2072301410@qq.com
 * TIP：
 */
class MineFragment : BaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private var userToken: String? = null
    private var urlss: String? = null
    private lateinit var mAdapter: PartnerListAdapter
    private var titlHeight: Int = 0
    private var windowHeight: Int = 0
    private var scrollHeight: Int = 0

    private val viewModel by lazy {
        //        var userToken: String by PreferenceUtils(Constants.SP_USER_TOKEN, "")
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = MineRepository(HttpManager.getInstance())
                return MineViewModel(repository, userToken) as T
            }
        }).get(MineViewModel::class.java)
    }
    override var layoutId = R.layout.fragment_mine

    override fun initData() {
        multipleStatusView = multiple_status_view
        userToken = SPUtils.getInstance().getString(Constants.SP_USER_TOKEN)
        Log.d(TAG, "userToken:===" + userToken)
        mIsLoginUi(userToken)
        setClickEvent()
//        activity?.let { StatusBarUtil.darkMode(it) }
//        alphaTitleBar()   //渐变状态栏
    }

    private fun mIsLoginUi(userToken: String?) {
        if (!userToken.isNullOrBlank()) {//已登录
            cons_logined.visibility = View.VISIBLE
            cons_on_login.visibility = View.GONE
            initSwipe()
            swipeRefreshLayout.isEnabled = true
        } else {
            cons_logined.visibility = View.GONE
            cons_on_login.visibility = View.VISIBLE
            swipeRefreshLayout.isEnabled = false
        }
    }

    private fun alphaTitleBar() {
        titlHeight = DisplayUtil.dip2px(context, 55f)
        scrollview.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //            var t = scrollY
//            if (t > titlHeight) {
//                t = titlHeight
//            }
//            cons_title.setBackgroundColor(
//                Color.argb(
//                    Math.round(t * 256 * 0.7f / titlHeight),
//                    204,
//                    0,
//                    0
//                )
//            )

            //如果向上滑动的距离>=iv_img.height - tv_title_text.height,隐藏的标题导航栏设置显示
            var distanceScrollY = titlHeight
            if (scrollY >= distanceScrollY) {
                cons_title.visibility = View.VISIBLE
            } else {
                cons_title.visibility = View.VISIBLE
            }
            //设置标题栏渐变
            if (scrollY <= 0) {
                //初始位置：未滑动时，设置标题背景透明
                tv_title.setBackgroundColor(Color.TRANSPARENT)
                tv_title.setTextColor(Color.WHITE)
            } else if (scrollY > 0 && scrollY <= distanceScrollY) {
                var scale: Float = (scrollY.toFloat()) / distanceScrollY
                var alpha: Float = 255 * scale
                tv_title.setBackgroundColor(Color.argb(alpha.toInt(), 204, 0, 0))
                tv_title.setTextColor(Color.argb(alpha.toInt(), 255, 255, 255))
            } else {
                tv_title.setBackgroundColor(Color.argb(255, 204, 0, 0))
                tv_title.setTextColor(Color.argb(255, 255, 255, 255))
            }

        })

    }

    private fun initSwipe() {
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE)
        swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) { //自动刷新
            onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        userToken = SPUtils.getInstance().getString(Constants.SP_USER_TOKEN)
        if (!userToken.isNullOrBlank()) {//登录后，才获取用户信息
            val mineVM = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repository = MineRepository(HttpManager.getInstance())
                    return MineViewModel(repository, userToken) as T
                }
            }).get(MineViewModel::class.java)
            handleData(mineVM.userInfo) {
                Logger.d("刷新我的数据成功---->>")
                mSaveUserInfo(it)
                setList(it.result)
                mIsLoginUi(userToken)
            }
        } else { //未登录，设置默认数据

        }

    }

    override fun subscribeUi() {
        if (!userToken.isNullOrBlank()) {//登录后，才获取用户信息
            handleData(viewModel.userInfo) {
                // adapter.setData(it)
                Logger.d("我的数据请求成功---->>")
                if (it.result.tokenError == -1) { //wx token 失效
                    mExitLoginOperation()
                    EventBus.getDefault()
                        .post(LoginMessageEvent(Constants.I_EVENT.EVENT_MESSAGE_LOGIN))
                    ToastUtils.show("用户信息过期")
                    return@handleData
                } else {
                    mSaveUserInfo(it)
                    setList(it.result)
                }
            }
        } else { //未登录，设置默认数据

        }
    }

    /**
     * 清空用户信息
     */
    private fun mExitLoginOperation() {
        SPUtils.getInstance().clear()
        SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, false)
        val user = UserInfo.getInstance()
        SPUtils.getInstance().clearShareData()
        user.clearUserData()
    }

    /**
     *  保存用户信息
     */
    private fun mSaveUserInfo(it: UserInfo) {
        val resultJson = JSON.toJSONString(it)   //将注册后的实体 转为json 串，便于保存到sp
        SPUtils.getInstance().put(Constants.SP_USER_UERDATA, resultJson)
        SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN, true)
        SPUtils.getInstance().put(Constants.SP_USER_ID, it.result.member_id)
        SPUtils.getInstance().put(Constants.SP_USER_PHONE, it.result.member_mobile)
        SPUtils.getInstance().put(Constants.SP_NICK_NAME, it.result.username)
        SPUtils.getInstance().put(Constants.SP_AVATAR, it.result.avator)
        UserInfo.parseUserFromJsonInSP(mContext)
    }


    fun setList(userInfo: UserInfo.ResultBean) {
        if (userInfo == null) return
        urlss = userInfo.urlss
//        tv_extension.text = Html.fromHtml("<u>" + "立即推广" + "</u>")
//        var message: String = userInfo.notice
//        tv_notice_tip.setTypeface(context?.let { ResourcesCompat.getFont(it, R.font.huawenxinwei) })
//        tvNotice.setTypeface(context?.let { ResourcesCompat.getFont(it, R.font.huawenxinwei) })
//        tvNotice.startWithText(message)
//        tvNotice.setOnItemClickListener { position, textView ->
//        }
        //头像
        if (userInfo?.avator.isNullOrEmpty()) {
            iv_avatar.setBackgroundResource(R.mipmap.ic_default_avator)
        } else {
            context?.let {
                setImg(
                    it, iv_avatar,
                    userInfo?.avator
                )
            }
        }
        tv_nickName.text = userInfo.username
        tv_reference.text = "ID:" + userInfo.member_id
        tv_level.text = userInfo.grade_name

        tv_balance.text = userInfo.predepoit
        tv_integral.text = userInfo.point
        tv_extensionAmount.text = userInfo.yongjin_count
        tv_yesterday.text = userInfo.yesterday_yongjin

        val tguserCount = userInfo.tguser_count
        tv_team_num.text = tguserCount.toString()
        //战略合作伙伴 广告位
//        context?.let {
//            setImg(
//                it, iv_partner,
//                "https://www.wanandroid.com/blogimgs/fceb1aac-68be-44b9-bcbb-8512e333acc6.jpeg"
//            )
//        }
//        setPartnerData(userInfo)
    }

    /**
     *  招商加盟
     */
    private fun setPartnerData(userInfo: UserInfo.ResultBean) {
        val collaborate = userInfo.collaborate
        mAdapter =
            PartnerListAdapter(
                context,
                collaborate,
                R.layout.item_recy_home_mine_partner
            )
        recyc_partner.adapter = mAdapter

    }

    fun setClickEvent() {
        tv_login.setOnClickListener(this)    //登录
        iv_collection.setOnClickListener(this)    //收藏
        iv_setting.setOnClickListener(this) //设置
        iv_extension.setOnClickListener(this) //立即推广

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_login ->
                Intent(context, VerificaCodeLoginActivity::class.java).run {
                    context?.startActivity(this)
                }
            R.id.iv_collection ->
                if (!userToken.isNullOrBlank()) {//已登录
                    Intent(context, CollectionActivity::class.java).run {
                        context?.startActivity(this)
                    }
                } else {
                    return
                }
            R.id.iv_setting ->
                if (!userToken.isNullOrBlank()) {//已登录
                    Intent(context, SettingActivity::class.java).run {
                        context?.startActivity(this)
                    }
                } else {
                    return
                }
            R.id.iv_extension ->
                if (!userToken.isNullOrBlank()) {//已登录
                    Intent(context, GoToPromotionActivity::class.java).run {
                        context?.startActivity(this)
                    }
                } else {
                    return
                }
        }
    }

    /**
     *  设置顶部 banner 数据
     */
    fun setImg(context: Context, iv: AppCompatImageView, picUrl: String?) {
        if (picUrl?.isNotBlank()!!) {
            context.let { ImageLoader.load(it, picUrl, iv) }
        }
    }

    override fun onRefresh() {
        if (!NetUtils.isConnected(App.instance)) {
            ToastUtils.show(getString(R.string.network_error))
            swipeRefreshLayout.isRefreshing = false //关闭下拉刷新进度条
            return
        }
        swipeRefreshLayout.isRefreshing = false  //关闭下拉刷新进度条
        val mineVM = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repository = MineRepository(HttpManager.getInstance())
                return MineViewModel(repository, userToken) as T
            }
        }).get(MineViewModel::class.java)

        handleData(mineVM.userInfo) {
            Logger.d("我的数据刷新成功---->>")
            mSaveUserInfo(it)
            setList(it.result)
        }
    }


}