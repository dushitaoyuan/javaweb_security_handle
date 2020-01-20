package com.taoyuanx.securitydemo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author dushitaoyuan
 * @date 2020/1/20
 */
public class PercentUtil {
    public static String percent(double num, double total, int scale) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(scale);
        format.setRoundingMode(RoundingMode.HALF_UP);
        double percent = num / total;
        return format.format(percent);
    }
}
