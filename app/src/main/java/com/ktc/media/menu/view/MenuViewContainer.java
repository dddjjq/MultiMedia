package com.ktc.media.menu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.ktc.media.R;

public class MenuViewContainer extends RelativeLayout
        implements ViewTreeObserver.OnGlobalFocusChangeListener, ViewTreeObserver.OnGlobalLayoutListener {

    private LayoutParams mFocusLayoutParams;
    private MenuFlyBorderView mFocusView;
    private View mNewFocus;
    private boolean mIsFirstInit = true;

    public MenuViewContainer(Context context) {
        super(context);
        init(null);
    }

    public MenuViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MenuViewContainer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setClipChildren(false);
        this.mFocusLayoutParams = new LayoutParams(0, 0);
        this.mFocusView = new MenuFlyBorderView(getContext());
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MenuViewContainer);
            int focusBg = a.getResourceId(R.styleable.MenuViewContainer_focusBackGround, R.drawable.menu_focus_item_image);
            a.recycle();
            this.mFocusView.setBackgroundResource(focusBg);
        } else {
            int focusBg = R.drawable.menu_focus_item_image;
            this.mFocusView.setBackgroundResource(focusBg);
        }
        this.addView(this.mFocusView, this.mFocusLayoutParams);
        addListener();
    }

    private void addListener() {
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (indexOfChild(newFocus) == -1) {
            mFocusView.setVisibility(GONE);
            return;
        }
        if (indexOfChild(oldFocus) == -1) {
            mFocusView.setDuration(0);
        } else {
            mFocusView.setDuration(200);
        }
        if (newFocus != null
                && indexOfChild(newFocus) != -1) {
            mFocusView.attachToView(newFocus);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (mIsFirstInit) {
            mIsFirstInit = false;
            onGlobalFocusChanged(null, mNewFocus);
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }

    public void setNewFocus(View v) {
        mNewFocus = v;
    }

    public void setFlyBorderViewDuration(int duration) {
        mFocusView.setDuration(duration);
    }
}
