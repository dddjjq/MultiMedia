package com.ktc.media.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;

public class DialogButton extends RelativeLayout {

    private TextView mTextView;
    private OnButtonClickListener mOnButtonClickListener;

    public DialogButton(Context context) {
        super(context);
        init(context, null);
    }

    public DialogButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_dialog_button, this, true);
        mTextView = (TextView) findViewById(R.id.dialog_button_text);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DialogButton);
            String text = ta.getString(R.styleable.DialogButton_buttonText);
            mTextView.setText(text);
            ta.recycle();
        }
        addListener();
    }

    private void addListener() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);
        setBackgroundResource(R.drawable.dialog_button_normal);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundResource(R.drawable.dialog_button_focus);
                    mTextView.setTextColor(getResources().getColor(R.color.dialog_button_text_focus_color));
                } else {
                    setBackgroundResource(R.drawable.dialog_button_normal);
                    mTextView.setTextColor(getResources().getColor(R.color.common_text_color));
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onButtonClick(v);
                }
            }
        });
    }

    public void setTextView(String text) {
        mTextView.setText(text);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        mOnButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        void onButtonClick(View view);
    }
}
