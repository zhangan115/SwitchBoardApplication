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

public class ChartXYView extends View {

    private Context context;
    private Paint paint;//文字
    private Paint linePaint;//文字
    private int lineColor = Color.BLACK;
    private int textColor = Color.BLACK;
    private float lineSize = 1;
    private float textSize = 1;
    private List<ChartData> chartDataList = new ArrayList<>();

    public ChartXYView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public ChartXYView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

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
        float textHeight = DisplayUtil.dip2px(getContext(), 20);

        float leftSize = ChartUtils.GetMaxTextWidthSize(chartDataList, paint) + 10;
        paint.setColor(lineColor);
        canvas.drawLine(leftSize, height - textHeight, leftSize, 0, linePaint);
        canvas.drawLine(leftSize, height - textHeight, width, height - textHeight, linePaint);
        int[] texts = ChartUtils.GetXText(chartDataList);
        float startX = leftSize;
        float w = (width - DisplayUtil.dip2px(context, ChartUtils.RIGHT_SPACE)) / texts[texts.length - 1];
        float startY = height - textHeight + ChartUtils.GetMaxTextHeightSize(chartDataList, paint) + 5;
        for (int text : texts) {
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            canvas.drawText(String.valueOf(text), startX + w * text, startY, paint);
        }

    }

    public ChartXYView setLineColor(int lineColor) {
        this.lineColor = lineColor;
        linePaint.setColor(lineColor);
        return this;
    }

    public ChartXYView setTextColor(int textColor) {
        this.textColor = textColor;
        paint.setColor(textColor);
        return this;
    }


    public ChartXYView setLineSize(float lineSize) {
        this.lineSize = lineSize;
        linePaint.setStrokeWidth(lineSize);
        return this;
    }


    public ChartXYView setTextSize(float textSize) {
        this.textSize = textSize;
        paint.setTextSize(textSize);
        return this;
    }

    public ChartXYView setChartDataList(List<ChartData> chartDataList) {
        this.chartDataList = chartDataList;
        return this;
    }

    public void show() {
        invalidate();
    }
}
