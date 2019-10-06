package com.ktc.media.media.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.view.OnItemClickListener;

public class PictureListItemView extends RelativeLayout {

    private ImageView mImageView;
    private OnItemClickListener mOnItemClickListener;

    public PictureListItemView(Context context) {
        super(context);
        init(context);
    }

    public PictureListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_dialog_picture_list_item, this, true);
        mImageView = findViewById(R.id.picture_item_image);
        addListener();
    }

    private void addListener() {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
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
                    setBackgroundResource(R.drawable.dialog_picture_focus);
                } else {
                    setBackground(null);
                }
            }
        });
    }

    public void setImageView(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
