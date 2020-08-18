package com.hlt.jzwebsite.model;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.hlt.jzwebsite.Constants;
import com.hlt.jzwebsite.utils.java.SPUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author LXB
 * @description:
 * @date :2020/3/4 10:04
 */
public class UserInfo implements Serializable {
    public static Context mContext;
    private static UserInfo user;

    public UserInfo(ResultBean result) {
        this.result = result;
    }

    /**
     * 私有化构造方法，可以在内部控制创建实例的数目
     */
    public UserInfo() {

    }

    public static UserInfo getInstance() {
        return getInstance(null);
    }

    public static UserInfo getInstance(Context context) {
        if (context != null && context instanceof Activity) {
            mContext = context.getApplicationContext();
        } else {
            mContext = context;
        }
        if (user == null) {
            user = new UserInfo();
        }
        return user;
    }


    public static void setUser(UserInfo user) {
        UserInfo.user = user;
    }

    public void clearUserData() {
        SPUtils.getInstance().put(Constants.SP_USER_ISLOGIN,false);
        UserInfo user = UserInfo.getInstance();
        SPUtils.getInstance().clearShareData();
        UserInfo.getInstance().remove();
    }

    public void remove() {
//        user.is_student = user.birthday = user.phone = user.major = user.create_time = user.id = user.identity = user.last_login_ip = user.last_login_time = user.score = user.sex = user.user_activation_key = user.user_login = user.user_email = user.user_nicename = user.user_pass = user.user_status = user.user_type = user.user_url = null;
    }

    public static void parseUserFromJsonInSP(Context context) {
        String userJson = SPUtils.getInstance().getString(Constants.SP_USER_UERDATA,  "");
        if (!TextUtils.isEmpty(userJson)) {
            UserInfo userBean = JSON.parseObject(userJson, UserInfo.class);
            if (userBean != null) {
                try {
                    UserInfo.setUser(userBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean hasLogin(Context context) {
        return SPUtils.getInstance().getBoolean(Constants.SP_USER_ISLOGIN);
    }

    //---------------------------------以上为 本地保存用户信息，添加方法，以下为服务器返回用户登录信息----------------------------------------






    /**
     * result : {"collaborate":[{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":11,"image":"/uploads/20190816/f7980e81a4fb61455598e1733a666c64.png","sort":11,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":12,"image":"/uploads/20190816/df21833f23d4e79cb1f23af1bf3d5bbe.png","sort":12,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":13,"image":"/uploads/20190816/8d567ef29db741d7dbd9433c1686b0c8.png","sort":13,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":14,"image":"/uploads/20190816/26b7c275aee3e40db3d0afb3adbc4581.png","sort":14,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":15,"image":"/uploads/20190816/e6dde2a9d62b34e7983b09d28aa2f6e9.png","sort":15,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":16,"image":"/uploads/20190816/84e7aa1be68aa46a013adfbe7c2d740d.png","sort":16,"url":""}],"notice":"尊敬的商城会员，请您登录账号后尽快完善个人信息。","member_id":"21","username":"向阳而生","avator":"http://test.gtt20.com/attachment/images/2/2020/02/VQxfQx7e3MANkPq58t58w75XtMfPyA.jpg","predepoit":"945307.04","point":"999562.00","yongjin_count":"0.00","yesterday_yongjin":"0.00","tguser_count":0,"grade_name":"普通会员"}
     */



    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * collaborate : [{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":11,"image":"/uploads/20190816/f7980e81a4fb61455598e1733a666c64.png","sort":11,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":12,"image":"/uploads/20190816/df21833f23d4e79cb1f23af1bf3d5bbe.png","sort":12,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":13,"image":"/uploads/20190816/8d567ef29db741d7dbd9433c1686b0c8.png","sort":13,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":14,"image":"/uploads/20190816/26b7c275aee3e40db3d0afb3adbc4581.png","sort":14,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":15,"image":"/uploads/20190816/e6dde2a9d62b34e7983b09d28aa2f6e9.png","sort":15,"url":""},{"atid":7,"atname":"战略合作伙伴","enname":"collaborate","id":16,"image":"/uploads/20190816/84e7aa1be68aa46a013adfbe7c2d740d.png","sort":16,"url":""}]
         * notice : 尊敬的商城会员，请您登录账号后尽快完善个人信息。
         * member_id : 21
         * username : 向阳而生
         * avator : http://test.gtt20.com/attachment/images/2/2020/02/VQxfQx7e3MANkPq58t58w75XtMfPyA.jpg
         * predepoit : 945307.04
         * point : 999562.00
         * yongjin_count : 0.00
         * yesterday_yongjin : 0.00
         * tguser_count : 0
         * grade_name : 普通会员
         */

        private String notice;
        private String member_id;
        private String member_mobile;
        private String username;
        private String avator;
        private String predepoit;
        private String point;
        private String yongjin_count;
        private String yesterday_yongjin;
        private int tguser_count;
        private String grade_name;
        private String urlss;
        private List<CollaborateBean> collaborate;
        //手动添加字段
        private String token;
        private int tokenError; //登录失效标识

        public int getTokenError() {
            return tokenError;
        }

        public void setTokenError(int tokenError) {
            this.tokenError = tokenError;
        }




        public String getMember_mobile() {
            return member_mobile;
        }

        public void setMember_mobile(String member_mobile) {
            this.member_mobile = member_mobile;
        }


        public String getUrlss() {
            return urlss;
        }

        public void setUrlss(String urlss) {
            this.urlss = urlss;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getMember_id() {
            return member_id;
        }

        public void setMember_id(String member_id) {
            this.member_id = member_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAvator() {
            return avator;
        }

        public void setAvator(String avator) {
            this.avator = avator;
        }

        public String getPredepoit() {
            return predepoit;
        }

        public void setPredepoit(String predepoit) {
            this.predepoit = predepoit;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public String getYongjin_count() {
            return yongjin_count;
        }

        public void setYongjin_count(String yongjin_count) {
            this.yongjin_count = yongjin_count;
        }

        public String getYesterday_yongjin() {
            return yesterday_yongjin;
        }

        public void setYesterday_yongjin(String yesterday_yongjin) {
            this.yesterday_yongjin = yesterday_yongjin;
        }

        public int getTguser_count() {
            return tguser_count;
        }

        public void setTguser_count(int tguser_count) {
            this.tguser_count = tguser_count;
        }

        public String getGrade_name() {
            return grade_name;
        }

        public void setGrade_name(String grade_name) {
            this.grade_name = grade_name;
        }

        public List<CollaborateBean> getCollaborate() {
            return collaborate;
        }

        public void setCollaborate(List<CollaborateBean> collaborate) {
            this.collaborate = collaborate;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public static class CollaborateBean {
            /**
             * atid : 7
             * atname : 战略合作伙伴
             * enname : collaborate
             * id : 11
             * image : /uploads/20190816/f7980e81a4fb61455598e1733a666c64.png
             * sort : 11
             * url :
             */

            private int atid;
            private String atname;
            private String enname;
            private int id;
            private String image;
            private int sort;
            private String url;

            public int getAtid() {
                return atid;
            }

            public void setAtid(int atid) {
                this.atid = atid;
            }

            public String getAtname() {
                return atname;
            }

            public void setAtname(String atname) {
                this.atname = atname;
            }

            public String getEnname() {
                return enname;
            }

            public void setEnname(String enname) {
                this.enname = enname;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public int getSort() {
                return sort;
            }

            public void setSort(int sort) {
                this.sort = sort;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
