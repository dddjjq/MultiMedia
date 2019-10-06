package com.ktc.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;

public class VideoCardView extends RelativeLayout {

    private TextView countText;
    private OnItemClickListener mOnItemClickListener;

    public VideoCardView(Context context) {
        super(context);
        init(context, null);
    }

    public VideoCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_video_card_view, this, true);
        countText = (TextView) findViewById(R.id.video_card_count_text);
        addListener();
    }

    private void addListener() {
        setFocusable(true);
        setClickable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundResource(R.drawable.video_card_focus);
                } else {
                    setBackgroundResource(R.drawable.video_card_normal);
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

    public void setCountText(int count) {
        if (count < Constants.FILE_LIMIT) {
            countText.setText(String.valueOf(count));
        } else {
            countText.setText(String.valueOf(Constants.FILE_LIMIT));
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
