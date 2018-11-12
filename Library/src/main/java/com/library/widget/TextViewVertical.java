package com.library.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * 竖排显示文字的TextView
 * Created by zhangan on 2016-07-19.
 */
public class TextViewVertical extends android.support.v7.widget.AppCompatTextView {

    public TextViewVertical(Context context) {
        this(context, null);
    }

    public TextViewVertical(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewVertical(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text == null || TextUtils.isEmpty(text) || text.length() == 0) {
            return;
        }
        int length = text.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            CharSequence index = text.toString().subSequence(i, i + 1);
            if (i == length - 1) {
                sb.append(index);
            } else {
                sb.append(index).append("\n");
            }

        }
        super.setText(sb, type);
    }
}
