package com.hlt.jzwebsite.model

import java.io.Serializable

/**
 * Created by lxb  on 2020/2/24.
 * 邮箱：2072301410@qq.com
 * TIP： 首页实体
 */


//data class BannerBean(
//    var title: String,
//    var desc: String,
//    var imagePath: String,
//    var url: String
//)
//
//data class ProjectMenuBean(
//    var icon: String,
//    var title: String,
//    var url: String ="https://web.gtt20.com/app/index.php?i=1&c=entry&m=ewei_shopv2&do=mobile&r=article&aid=8"
//)
//
//data class IntroduceBean(
//    var icon: String,
//    var url: String ="https://web.gtt20.com/app/index.php?i=1&c=entry&m=ewei_shopv2&do=mobile&r=article&aid=8"
//)
//
//data class TrainingClassBean(
//    var icon: String,
//    var title: String,
//    var collection: String,
//    var views: String,
//    var url: String ="https://web.gtt20.com/app/index.php?i=1&c=entry&m=ewei_shopv2&do=mobile&r=article&aid=8"
//)
//data class MineNocie(
//    var content:String
//)

data class HomeBean(
    val result: HomeResult
) :Serializable

data class HomeResult(
    val classroom: List<Classroom>,
    val company: List<Company>,
    val notice: List<Notice>,
    val onlinerecord: String,
    val serve: List<Serve>,
    val junzi: Junzi,
    val slideshow: List<Slideshow>
)

data class Classroom(
    val cid: Int,
    val collect: Int,
    val hits: Int,
    val id: Int,
    val image: String,
    val title: String
)

data class Company(
    val cid: Int,
    val id: Int,
    val image: String,
    val title: String,
    val types: Int  //入口跳转详情 还是列表类型
)

data class Notice(
    val cid: Int,
    val id: Int,
    val title: String
)

data class Junzi(
    val imgurl: String,
    val returnurl: String
)

data class Serve(
    val cid: Int,
    val id: Int,
    val image: String,
    val title: String,
    val source: String,
    val types: Int
)

data class Slideshow(
    val image: String,
    val url: String,
    val title: String //自己手动添加字段
)

