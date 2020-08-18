package com.hlt.jzwebsite.utils.java;

import android.app.Activity;
import android.graphics.Rect;

import com.previewlibrary.GPreviewActivity;
import com.previewlibrary.GPreviewBuilder;
import com.previewlibrary.enitity.ThumbViewInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LXB
 * @description: 查看预览大图
 * @date :2020/3/18 15:35
 */
public class AppIdentityJumpUtils {

    private static ArrayList<ThumbViewInfo> mThumbViewInfoList = new ArrayList<>(); // 组建大图预览数据基
    /**
     *  海报查看
     * @param activity
     * @param bannerList
     * @param position
     */
    public static void previewLargePic(Activity activity, List<String> bannerList, int position) {
        if (bannerList != null && bannerList.size() > 0) {
            ThumbViewInfo item;
            mThumbViewInfoList.clear();
            for (int i = 0; i < bannerList.size(); i++) {
                Rect bounds = new Rect();
                item = new ThumbViewInfo(bannerList.get(i));
                item.setBounds(bounds);
                mThumbViewInfoList.add(item);
            }
            //打开预览界面
            GPreviewBuilder.from(activity)
                    //是否使用自定义预览界面，当然8.0之后因为配置问题，必须要使用
                    .to(GPreviewActivity.class)
                    .setData(mThumbViewInfoList)
                    .setCurrentIndex(position)
                    .setSingleFling(true)
                    .setType(GPreviewBuilder.IndicatorType.Number)
                    // 小圆点
                    // .setType(GPreviewBuilder.IndicatorType.Dot)
                    .start();//启动
        }
    }
}
