package com.ktc.media.menu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ktc.media.R;

public class MajorMenuView extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;
    private ImageView mSelectView;

    private OnMajorItemClickListener mOnMajorItemClickListener;

    public MajorMenuView(Context context) {
        super(context);
        init(context, null);
    }

    public MajorMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_major_menu, this, true);
        mImageView = (ImageView) findViewById(R.id.major_menu_image);
        mTextView = (TextView) findViewById(R.id.major_menu_text);
        mSelectView = (ImageView) findViewById(R.id.major_menu_select_image);
        addListener();
    }

    private void addListener() {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMajorItemClickListener != null) {
                    mOnMajorItemClickListener.onItemClick(v);
                }
            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isSelected()) {
                        mImageView.setSelected(false);
                        mTextView.setTextColor(getResources().getColor(R.color.major_view_text_normal_color));
                        mSelectView.setVisibility(GONE);
                    }
                } else {
                    if (isSelected()) {
                        mImageView.setSelected(true);
                        mTextView.setTextColor(getResources().getColor(R.color.major_view_text_focus_color));
                        mSelectView.setVisibility(VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected && !hasFocus()) {
            mImageView.setSelected(true);
            mTextView.setTextColor(getResources().getColor(R.color.major_view_text_focus_color));
            mSelectView.setVisibility(VISIBLE);
        } else {
            mImageView.setSelected(false);
            mTextView.setTextColor(getResources().getColor(R.color.major_view_text_normal_color));
            mSelectView.setVisibility(GONE);
        }
    }

    public void setImageView(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setTextView(String text) {
        mTextView.setText(text);
    }

    public void setOnMajorItemClickListener(OnMajorItemClickListener onMajorItemClickListener) {
        mOnMajorItemClickListener = onMajorItemClickListener;
    }

    public interface OnMajorItemClickListener {
        void onItemClick(View view);
    }
}
