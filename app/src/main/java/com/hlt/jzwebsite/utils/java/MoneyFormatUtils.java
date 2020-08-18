package com.hlt.jzwebsite.utils.java;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author LXB
 * @description: 金额 格式化工具类
 * @date :2020/1/20 12:22
 */
public class MoneyFormatUtils {
    private static final String TAG = "MoneyFormatUtils";

    /**
     * 金额显示小数点后两位
     * #.00
     * #.## 表示有0就显示0,没有0就不显示  例如 1.20 就会变成1.2
     * 0.00 表示,有没有0都会显示    例如 1.20 依然是 1.20
     * @param price
     * @return
     */
    public static String keepTwoDecimal(double price) {
//        NumberFormat nf = new DecimalFormat("##.##");
        NumberFormat nf = new DecimalFormat("0.00");
        Double d = price;
        return nf.format(d);
    }

    /**
     *  不显示0
     * @param price
     * @return
     */
    public static String keepTwoDecimalNoDisplay0(double price) {
        NumberFormat nf = new DecimalFormat("##.##");
        Double d = price;
        return nf.format(d);
    }

    /**
     * 金钱数字保留4位小数且以“￥”开头
     *
     * @param price
     * @return
     */
    public static String keepFourDecimal(double price) {
        NumberFormat nf = new DecimalFormat("$##.####");
        Double d = price;
        return nf.format(d);
    }

    /**
     * 金钱数字保留4位小数且三位三位的隔开
     *
     * @param price
     * @return
     */
    public static String splitDisplayThreeDecimal(double price) {
        NumberFormat nf = new DecimalFormat("#,###.####");
        Double d = price;
        return nf.format(d);
    }
}
