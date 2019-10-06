package com.ktc.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;

public class KeyboardItemView extends RelativeLayout {

    private ImageView mImageView;
    private RelativeLayout mTextContainer;
    private TextView firstTextView;
    private TextView secondTextView;
    private int keyboardItemStyle = 1;
    private OnItemClickListener mOnItemClickListener;
    private int[] normalImages = new int[]{R.drawable.keyboard_item_normal, R.drawable.keyboard_item_normal_long};
    private int[] focusImages = new int[]{R.drawable.keyboard_item_focus, R.drawable.keyboard_item_focus_long};
    private int[] unEnableImage = new int[]{R.drawable.keyboard_item_unenable, R.drawable.keyboard_item_unenable_long};

    public KeyboardItemView(Context context) {
        super(context);
        init(context, null);
    }

    public KeyboardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_keyboard_item_view, this, true);
        mImageView = (ImageView) findViewById(R.id.keyboard_item_image);
        mTextContainer = (RelativeLayout) findViewById(R.id.keyboard_item_text_container);
        firstTextView = (TextView) findViewById(R.id.keyboard_item_text_first);
        secondTextView = (TextView) findViewById(R.id.keyboard_item_text_second);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KeyboardItemView);
            int imageRes = ta.getResourceId(R.styleable.KeyboardItemView_keyboardItemIcon, 0);
            String firstTextString = ta.getString(R.styleable.KeyboardItemView_keyboardFirstText);
            String secondTextString = ta.getString(R.styleable.KeyboardItemView_keyboardSecondText);
            firstTextView.setText(firstTextString);
            secondTextView.setText(secondTextString);
            keyboardItemStyle = ta.getInteger(R.styleable.KeyboardItemView_keyboardItemStyle
                    , getResources().getInteger(R.integer.keyboard_item_style_01));
            mImageView.setImageResource(imageRes);
            ta.recycle();
            initKeyboardItemStyle(keyboardItemStyle);
        }
        addListener();
    }

    private void initKeyboardItemStyle(int style) {
        switch (style) {
            case 1://number
                mImageView.setVisibility(GONE);
                removeContainerStart();
                break;
            case 2://image
                mTextContainer.setVisibility(GONE);
                break;
            case 3://keyboard
                mImageView.setImageResource(R.drawable.keyboard_select_point);
                setFirstTextSize();
                secondTextView.setVisibility(GONE);
                break;
            case 4://0and1
                secondTextView.setVisibility(GONE);
                removeContainerStart();
                break;
            default:
                break;
        }
    }

    private void removeContainerStart() {
        LayoutParams layoutParams = (LayoutParams) mTextContainer.getLayoutParams();
        layoutParams.leftMargin = 0;
    }

    private void setFirstTextSize() {
        firstTextView.setTextSize(20);
    }

    private void addListener() {
        if (keyboardItemStyle == getResources().getInteger(R.integer.keyboard_item_style_03)) {
            setBackgroundResource(normalImages[1]);
        } else {
            setBackgroundResource(normalImages[0]);
        }
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isEnabled()) {
                        if (keyboardItemStyle == getResources().getInteger(R.integer.keyboard_item_style_03)) {
                            setBackgroundResource(focusImages[1]);
                        } else {
                            setBackgroundResource(focusImages[0]);
                        }
                    }
                } else {
                    if (isEnabled()) {
                        if (keyboardItemStyle == getResources().getInteger(R.integer.keyboard_item_style_03)) {
                            setBackgroundResource(normalImages[1]);
                        } else {
                            setBackgroundResource(normalImages[0]);
                        }
                    }
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setClickable(true);
            setFocusable(true);
            setFocusableInTouchMode(true);
            firstTextView.setTextColor(getResources().getColor(R.color.common_text_color));
            secondTextView.setTextColor(getResources().getColor(R.color.common_text_color));
            if (hasFocus()) {
                if (keyboardItemStyle == getResources().getInteger(R.integer.keyboard_item_style_03)) {
                    setBackgroundResource(focusImages[1]);
                } else {
                    setBackgroundResource(focusImages[0]);
                }
            } else {
                if (keyboardItemStyle == getResources().getInteger(R.integer.keyboard_item_style_03)) {
                    setBackgroundResource(normalImages[1]);
                } else {
                    setBackgroundResource(normalImages[0]);
                }
            }
        } else {
            setClickable(false);
            setFocusable(false);
            setFocusableInTouchMode(false);
            firstTextView.setTextColor(getResources().getColor(R.color.common_text_color_alpha_0_5));
            secondTextView.setTextColor(getResources().getColor(R.color.common_text_color_alpha_0_5));
            if (keyboardItemStyle == getResources().getInteger(R.integer.keyboard_item_style_03)) {
                setBackgroundResource(unEnableImage[1]);
            } else {
                setBackgroundResource(unEnableImage[0]);
            }
        }
    }

    /**
     * 两种键盘选择
     */
    public void setPointSelect(boolean isSelect) {
        if (isSelect) {
            mImageView.setVisibility(VISIBLE);
        } else {
            mImageView.setVisibility(GONE);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
