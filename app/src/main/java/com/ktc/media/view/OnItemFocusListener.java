package com.ktc.media.view;

import android.view.View;

import com.ktc.media.model.BaseData;

public interface OnItemFocusListener {
    void onItemFocusChange(View view, boolean hasFocus, BaseData data);
}
