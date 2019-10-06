package com.ktc.media.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.view.MarqueeTextView;
import com.ktc.media.view.OnItemClickListener;

public class VideoListItemView extends RelativeLayout {

    private ImageView mImageView;
    private MarqueeTextView mTextView;
    private OnItemClickListener mOnItemClickListener;

    public VideoListItemView(Context context) {
        super(context);
        init(context);
    }

    public VideoListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_dialog_video_list_item, this, true);
        mImageView = (ImageView) findViewById(R.id.video_item_image);
        mTextView = (MarqueeTextView) findViewById(R.id.video_item_text);
        addListener();
    }

    private void addListener() {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundResource(R.drawable.dialog_video_back_normal);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v);
                }
            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundResource(R.drawable.dialog_video_back_focus);
                    mTextView.setSelected(true);
                } else {
                    setBackgroundResource(R.drawable.dialog_video_back_normal);
                    mTextView.setSelected(false);
                }
            }
        });
    }

    public void setTextView(String text) {
        mTextView.setText(text);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
