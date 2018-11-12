package com.library.chart;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * 图标工具类
 * Created by pingan on 2017/7/2.
 */

public class ChartUtils {

    private static Rect rect;
    public static int STEP = 9;
    public static int RIGHT_SPACE = 20;
    private static float EXPAND_VALUE = 1.3f;

    /**
     * 获取数据中的最大值
     *
     * @param chartDataList 图标显示的数据
     * @return 最大值
     */
    public static int GetMaxValue(@NonNull List<ChartData> chartDataList) {
        int max = 0;
        for (int i = 0; i < chartDataList.size(); i++) {
            if (chartDataList.get(i).getValueList() != null) {
                for (int j = 0; j < chartDataList.get(i).getValueList().size(); j++) {
                    if (max <= chartDataList.get(i).getValueList().get(j).getValue()) {
                        max = chartDataList.get(i).getValueList().get(j).getValue();
                    }
                }
            }
        }
        return max;
    }

    /**
     * 测量字体宽度
     *
     * @param str   文字
     * @param paint 画笔
     * @return 字体宽度
     */
    public static int GetTextWidthSize(@NonNull String str, @NonNull Paint paint) {
        if (rect == null) {
            rect = new Rect();
        }
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }

    /**
     * 测量字体高度
     *
     * @param str   文字
     * @param paint 画笔
     * @return 字体宽度
     */
    public static int GetTextHeightSize(@NonNull String str, @NonNull Paint paint) {
        if (rect == null) {
            rect = new Rect();
        }
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();
    }

    /**
     * 测量字体宽度
     *
     * @param chartDataList 数据
     * @param paint         画笔
     * @return 最大字体宽度
     */
    public static float GetMaxTextWidthSize(@NonNull List<ChartData> chartDataList, @NonNull Paint paint) {
        float max = 0;
        for (int i = 0; i < chartDataList.size(); i++) {
            if (max <= GetTextWidthSize(chartDataList.get(i).getUserName(), paint)) {
                max = GetTextWidthSize(chartDataList.get(i).getUserName(), paint);
            }
        }
        return max;
    }

    /**
     * 测量字体高度
     *
     * @param chartDataList 数据
     * @param paint         画笔
     * @return 最大字体宽度
     */
    public static float GetMaxTextHeightSize(@NonNull List<ChartData> chartDataList, @NonNull Paint paint) {
        float max = 0;
        for (int i = 0; i < chartDataList.size(); i++) {
            if (max <= GetTextHeightSize(chartDataList.get(i).getUserName(), paint)) {
                max = GetTextHeightSize(chartDataList.get(i).getUserName(), paint);
            }
        }
        return max;
    }

    public static int[] GetXText(@NonNull List<ChartData> chartDataList) {
        int[] texts = new int[STEP];
        float maxValue = GetMaxValue(chartDataList) * EXPAND_VALUE;
        int step = (int) Math.ceil(maxValue / STEP);
        if (step == 0) {
            step = 1;
        }
        for (int i = 0; i < STEP; i++) {
            texts[i] = i * step;
        }
        return texts;
    }

    public static int GetMaxXValue(@NonNull List<ChartData> chartDataList) {
        return Math.round(GetMaxValue(chartDataList) * EXPAND_VALUE);
    }

}
