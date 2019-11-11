package com.ktc.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.model.BaseData;

public class MediaGridItemView extends RelativeLayout {

    private ImageView image;
    private MarqueeTextView title;
    private OnItemFocusListener mOnItemFocusListener;
    private OnItemClickListener mOnItemClickListener;
    private BaseData mBaseData;

    public MediaGridItemView(Context context) {
        super(context);
        init(context, null);
    }

    public MediaGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_media_grid_item_view, this, true);
        image = (ImageView) findViewById(R.id.media_grid_item_image);
        title = (MarqueeTextView) findViewById(R.id.media_grid_item_title);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MediaGridItemView);
            int resId = ta.getResourceId(R.styleable.MediaGridItemView_gridIcon, 0);
            image.setImageResource(resId);
            ta.recycle();
        }
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
                if (!hasFocus) title.setSelected(false);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (hasFocus()) {
                            title.setSelected(true);
                        }
                    }
                }, getResources().getInteger(R.integer.fly_border_duration));
                if (mOnItemFocusListener != null) {
                    mOnItemFocusListener.onItemFocusChange(v, hasFocus, mBaseData);
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

    public void setSpanText(String text) {
        if (TextUtils.isEmpty(text)) {
            title.setText(title.getText());
            return;
        }
        String currentText = title.getText().toString();
        if (currentText.toUpperCase().contains(text.toUpperCase())) {
            SpannableString spannableString = new SpannableString(currentText);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.span_text_color));
            spannableString.setSpan(colorSpan, currentText.toUpperCase()
                    .indexOf(text.toUpperCase()), currentText.toUpperCase().indexOf(text.toUpperCase())
                    + text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            title.setText(spannableString);
        }
    }

    public void setImage(int resId) {
        image.setImageResource(resId);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setData(BaseData baseData) {
        mBaseData = baseData;
    }

    public BaseData getBaseData() {
        return mBaseData;
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemFocusListener) {
        mOnItemFocusListener = onItemFocusListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
