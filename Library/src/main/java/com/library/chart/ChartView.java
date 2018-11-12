package com.library.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.library.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表背景
 * Created by pingan on 2017/7/2.
 */

public class ChartView extends View {

    private Context context;
    private Paint paint;//文字
    private Paint linePaint;//文字
    private int textColor = Color.BLACK;
    private float lineSize = 1;
    private float textSize = 1;
    private List<ChartData> chartDataList = new ArrayList<>();
    private int maxValue;
    private int topMargin, bottomMargin;
    private int[] lineColors = new int[]{Color.BLACK};

    public ChartView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        topMargin = DisplayUtil.dip2px(context, 15);
        bottomMargin = DisplayUtil.dip2px(context, 25);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, 200);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, 200);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        float width = getWidth() - paddingLeft - paddingRight;
        float height = getHeight() - paddingTop - paddingBottom;
        float leftSize = ChartUtils.GetMaxTextWidthSize(chartDataList, paint) + 10;
        float startX = leftSize;
        float textStartX = 0;
        float textStartY = topMargin;
        maxValue = ChartUtils.GetXText(chartDataList)[ChartUtils.STEP - 1];
        float w = (width - DisplayUtil.dip2px(context, ChartUtils.RIGHT_SPACE)) / maxValue;
        for (int i = 0; i < chartDataList.size(); i++) {
            paint.setColor(textColor);
            canvas.drawText(chartDataList.get(i).getUserName(), textStartX, textStartY + ChartUtils.GetTextHeightSize(chartDataList.get(i).getUserName(), paint) / 2, paint);
            for (int j = 0; j < chartDataList.get(i).getValueList().size(); j++) {
                float v = chartDataList.get(i).getValueList().get(j).getValue();
                linePaint.setColor(lineColors[j]);
                linePaint.setStrokeWidth(lineSize);
                canvas.drawLine(startX, textStartY, leftSize + v * w, textStartY, linePaint);
                paint.setColor(lineColors[j]);
                String valueStr = String.valueOf(chartDataList.get(i).getValueList().get(j).getValue());
                canvas.drawText(valueStr, leftSize + v * w + DisplayUtil.dip2px(context, 2), textStartY + ChartUtils.GetTextHeightSize(valueStr, paint) / 2, paint);
                textStartY = textStartY + lineSize;
            }
            textStartY = textStartY + bottomMargin;
        }

    }

    public ChartView setTextColor(int lineColor) {
        this.textColor = lineColor;
        paint.setColor(textColor);
        return this;
    }


    public ChartView setLineSize(float lineSize) {
        this.lineSize = lineSize;
        return this;
    }


    public ChartView setTextSize(float textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
        return this;
    }

    public ChartView setChartDataList(List<ChartData> chartDataList) {
        this.chartDataList = chartDataList;
        return this;
    }

    public ChartView setLineColors(int[] lineColors) {
        this.lineColors = lineColors;
        return this;
    }

    public void show() {
        invalidate();
    }
}
