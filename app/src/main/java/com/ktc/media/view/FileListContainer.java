package com.ktc.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ktc.media.R;

public class FileListContainer extends RelativeLayout
        implements ViewTreeObserver.OnGlobalFocusChangeListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private LayoutParams mFocusLayoutParams;
    private FlyBorderView mFocusView;
    private View mNewFocus;

    private boolean mIsFirstInit = true;
    private int mDuration = 200;

    public FileListContainer(Context context) {
        super(context);
    }

    public FileListContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FileListContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setClipChildren(false);
        this.mFocusLayoutParams = new LayoutParams(0, 0);
        this.mFocusView = new FlyBorderView(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FileListContainer);
        int focusBg = a.getResourceId(R.styleable.FileListContainer_focusBg, R.drawable.list_background_focus);
        mDuration = a.getInteger(R.styleable.FileListContainer_duration, 200);
        a.recycle();
        this.mFocusView.setBackgroundResource(focusBg);
        this.addView(this.mFocusView, this.mFocusLayoutParams);
        getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        float scaleRatio = 1.03f;
        if (newFocus != null) {
            newFocus.requestFocus();
            if (newFocus instanceof Button
                    || newFocus instanceof RecyclerView
                    || newFocus instanceof ScrollView
                    || (indexOfChild((View) newFocus.getParent()) == -1)) {
                scaleRatio = 1;
            } else {
                scaleRatio = 1.03f;
            }

            if (oldFocus == newFocus) {
                return;
            }
            if (oldFocus != null && !(oldFocus instanceof RecyclerView) && !(oldFocus instanceof ScrollView)) {
                newFocus.animate().scaleX(scaleRatio).translationZ(1).setDuration(mDuration).start();
                mFocusView.setDuration(mDuration);
            } else {
                newFocus.animate().scaleX(scaleRatio).translationZ(1).setDuration(0).start();
                mFocusView.setDuration(0);
            }
        }
        if (oldFocus != null) {
            oldFocus.animate().scaleX(1.0f).translationZ(0).setDuration(mDuration).start();
        }
        if (newFocus != null) {
            if (oldFocus != null
                    && !(oldFocus.getParent() instanceof RecyclerView)
                    && indexOfChild(oldFocus) == -1
                    || oldFocus == null
                    || oldFocus instanceof RecyclerView
                    || (newFocus.getWidth() == 0 && newFocus.getHeight() == 0)) {
                mFocusView.setDuration(0);
            } else {
                mFocusView.setDuration(getResources().getInteger(R.integer.fly_border_duration));
            }
            if (newFocus instanceof MediaGridItemView) {
                mFocusView.setBackgroundResource(R.drawable.grid_background_focus);
            } else if (newFocus instanceof MediaLinearItemView) {
                mFocusView.setBackgroundResource(R.drawable.list_background_focus);
            }
            mFocusView.attachToView(newFocus, scaleRatio, scaleRatio);
        } else {
            mFocusView.setVisibility(View.GONE);
        }
    }

    public void reAttachView() {
        View focus = getFocusedChild();
        if (focus != null) {
            if (focus instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) focus;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i).hasFocus()) {
                        mFocusView.attachToView(viewGroup.getChildAt(i), 1.03f, 1.03f);
                        return;
                    }
                }
            }
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
}
