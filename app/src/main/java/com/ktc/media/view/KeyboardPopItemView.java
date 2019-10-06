package com.ktc.media.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;

public class KeyboardPopItemView extends RelativeLayout {

    private TextView mTextView;
    private OnItemClickListener mOnItemClickListener;

    public KeyboardPopItemView(Context context) {
        super(context);
        init(context);
    }

    public KeyboardPopItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mTextView = new TextView(context);
        mTextView.setTextSize(33.3f);//setTextSize以dp为单位
        mTextView.setTextColor(getResources().getColor(R.color.common_text_color));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        Paint.FontMetricsInt fontMetricsInt = mTextView.getPaint().getFontMetricsInt();
        int top = (int) (fontMetricsInt.bottom / 2.0 + fontMetricsInt.top / 2.0);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        layoutParams.topMargin = top;//设置文字居中
        mTextView.setLayoutParams(layoutParams);
        mTextView.setGravity(Gravity.CENTER);
        addView(mTextView);
        addListener();
    }

    private void addListener() {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundResource(R.drawable.keyboard_pop_item_focus);
                } else {
                    setBackgroundResource(0);
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v);
                }
            }
        });
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public String getTextString() {
        return mTextView.getText().toString();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
