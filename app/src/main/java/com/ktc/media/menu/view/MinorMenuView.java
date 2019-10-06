package com.ktc.media.menu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.util.DestinyUtil;
import com.ktc.media.view.MarqueeTextView;


public class MinorMenuView extends RelativeLayout {

    private ImageView mImageView;
    private MarqueeTextView mTextView;
    private OnItemClickListener mOnItemClickListener;
    private static int scaleDuration = 300;
    private boolean isPoint = false;

    public MinorMenuView(Context context) {
        super(context);
        init(context, null);
    }

    public MinorMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_minor_menu, this, true);
        mImageView = (ImageView) findViewById(R.id.minor_menu_image);
        mTextView = (MarqueeTextView) findViewById(R.id.minor_menu_text);
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
                    startClickAnim();
                }
                if (isPoint) {
                    setListSelectStatus();
                }
            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTextView.setSelected(true);
                } else {
                    mTextView.setSelected(false);
                }
            }
        });
    }

    public void setImageView(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setTextView(String text) {
        mTextView.setText(text);
    }

    public void setIsPoint(boolean isPoint) {
        this.isPoint = isPoint;
        if (!isPoint) {
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT
                    , LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart(DestinyUtil.dp2px(getContext(), 26.7f));
            layoutParams.setMarginEnd(DestinyUtil.dp2px(getContext(), 26.7f));
            mTextView.setLayoutParams(layoutParams);
        }
    }

    public void setSelectStatus(boolean isSelect) {
        if (isSelect) {
            mImageView.setVisibility(VISIBLE);
        } else {
            mImageView.setVisibility(GONE);
        }
    }

    //选中当前view并取消其他view选中状态
    private void setListSelectStatus() {
        if (getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof MinorMenuView) {
                    final MinorMenuView minorMenuView = (MinorMenuView) viewGroup.getChildAt(i);
                    if (minorMenuView == this) {
                        minorMenuView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                minorMenuView.setSelectStatus(true);
                            }
                        }, scaleDuration);
                    } else {
                        minorMenuView.setSelectStatus(false);
                    }
                }
            }
        }
    }

    //点击动画 有Listener时才响应
    private void startClickAnim() {
        ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 0.9f, 1f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation scaleAnimation2 = new ScaleAnimation(0.9f, 1f, 0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f
                , Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation1.setDuration(scaleDuration / 2);
        scaleAnimation2.setDuration(scaleDuration / 2);
        mTextView.startAnimation(scaleAnimation1);
        mTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextView.startAnimation(scaleAnimation2);
            }
        }, scaleDuration / 2);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }
}
