package com.meiduohui.groupbuying.UI.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

public class SmartHintTextView extends android.support.v7.widget.AppCompatTextView {
    private CharSequence mSmartHint;

    public SmartHintTextView(Context context) {
        super(context);
    }

    public SmartHintTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartHintTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(getHint()) && !TextUtils.isEmpty(mSmartHint)) {
            setHint(mSmartHint);
        } else if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(getHint())) {
            mSmartHint = getHint();
            setHint(null);
        }
    }
}

