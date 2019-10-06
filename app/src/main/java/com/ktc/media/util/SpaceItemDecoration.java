package com.ktc.media.util;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public SpaceItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager != null
                && layoutManager instanceof LinearLayoutManager
                && !(layoutManager instanceof GridLayoutManager)) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int position = linearLayoutManager.getPosition(view);
            if (position == linearLayoutManager.getItemCount() - 1) { //最后一项不加边距
                return;
            }
        }
        outRect.bottom = mSpace;
    }
}

