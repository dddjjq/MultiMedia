package com.ktc.media.media.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ktc.media.R;

public class MediaSeekPopWindow extends PopupWindow {

    private TextView popText;
    private View mView;

    public MediaSeekPopWindow(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.pop_seek_bar_layout, null);
        setContentView(mView);
        popText = (TextView) mView.findViewById(R.id.seek_pop_text);
        setBackgroundDrawable(null);
    }

    public void setPopText(String text) {
        popText.setText(text);
    }
}
