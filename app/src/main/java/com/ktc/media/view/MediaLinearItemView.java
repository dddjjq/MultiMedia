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
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.model.BaseData;
import com.ktc.media.util.DestinyUtil;

public class MediaLinearItemView extends RelativeLayout {

    private ImageView image;
    private MarqueeTextView title;
    private TextView content;
    private OnItemFocusListener mOnItemFocusListener;
    private OnItemClickListener mOnItemClickListener;
    private BaseData mBaseData;

    public MediaLinearItemView(Context context) {
        super(context);
        init(context, null);
    }

    public MediaLinearItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_media_linear_item_view, this, true);
        image = (ImageView) findViewById(R.id.media_linear_item_image);
        title = (MarqueeTextView) findViewById(R.id.media_linear_item_title);
        content = (TextView) findViewById(R.id.media_linear_item_content);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MediaLinearItemView);
            int imageRes = ta.getResourceId(R.styleable.MediaLinearItemView_icon, 0);
            image.setImageResource(imageRes);
            ta.recycle();
        }
        addListener();
    }

    private void addListener() {
        setFocusable(true);
        setClickable(true);
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
                }, 500);
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

    public void setContentVisible(boolean isVisible) {
        if (!isVisible) {
            content.setVisibility(GONE);
            title.setMaxWidth(DestinyUtil.dp2px(getContext(), 516));
        } else {
            content.setVisibility(VISIBLE);
            title.setMaxWidth(DestinyUtil.dp2px(getContext(), 620));
        }
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setContent(String text) {
        content.setText(text);
    }

    public void setImage(int resId) {
        image.setImageResource(resId);
    }

    public void setData(BaseData data) {
        mBaseData = data;
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
