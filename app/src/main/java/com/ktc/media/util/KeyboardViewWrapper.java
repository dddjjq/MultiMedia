package com.ktc.media.util;

import android.widget.RelativeLayout;

import com.ktc.media.view.KeyboardView;

public class KeyboardViewWrapper {

    private KeyboardView view;
    private int keyboardWidth;

    public KeyboardViewWrapper(KeyboardView view) {
        this.view = view;
        keyboardWidth = DestinyUtil.dp2px(view.getContext(), 413.3f);
    }

    public void setWidth(int width) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.leftMargin = width - keyboardWidth;
        view.setLayoutParams(layoutParams);
        view.requestLayout();
    }

    public int getWidth() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        return keyboardWidth + layoutParams.leftMargin;
    }
}
