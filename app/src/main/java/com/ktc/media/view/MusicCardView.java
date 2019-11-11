package com.ktc.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;

public class MusicCardView extends RelativeLayout {

    private TextView countText;
    private OnItemClickListener mOnItemClickListener;

    public MusicCardView(Context context) {
        super(context);
        init(context, null);
    }

    public MusicCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_music_card_view, this, true);
        countText = (TextView) findViewById(R.id.music_card_count_text);
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
                    setBackgroundResource(R.drawable.music_card_focus);
                } else {
                    setBackgroundResource(R.drawable.music_card_normal);
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
        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                requestFocus();
                requestFocusFromTouch();
                return true;
            }
        });
    }

    public void setCountText(int count) {
        if (count < Constants.FILE_LIMIT) {
            countText.setText(String.valueOf(count));
        } else {
            countText.setText(Constants.FILE_LIMIT + "+");
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
