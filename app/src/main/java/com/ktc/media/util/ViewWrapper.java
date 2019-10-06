package com.ktc.media.util;

import android.view.View;

public class ViewWrapper<T extends View> {

    private T view;

    public ViewWrapper(T view) {
        this.view = view;
    }

    public void setWidth(int width) {
        view.getLayoutParams().width = width;
        view.requestLayout();
    }

    public int getWidth() {
        return view.getLayoutParams().width;
    }

    public void setHeight(int height) {
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    public int getHeight() {
        return view.getLayoutParams().height;
    }
}
