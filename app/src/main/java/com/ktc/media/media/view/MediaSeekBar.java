package com.ktc.media.media.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class MediaSeekBar extends SeekBar {

    private OnSeekBarDrawListener mOnSeekBarDrawListener;

    public MediaSeekBar(Context context) {
        super(context);
    }

    public MediaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mOnSeekBarDrawListener != null) {
            mOnSeekBarDrawListener.onSeekBarDraw();
        }
    }

    public void setOnSeekBarDrawListener(OnSeekBarDrawListener onSeekBarDrawListener) {
        mOnSeekBarDrawListener = onSeekBarDrawListener;
    }

    public interface OnSeekBarDrawListener {
        void onSeekBarDraw();
    }
}
