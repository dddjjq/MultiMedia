package com.ktc.media.media.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ktc.media.util.DestinyUtil;

public class MediaDecoration extends RecyclerView.ItemDecoration {

    private int mDecoration;
    private Context mContext;

    public MediaDecoration(Context context, int decoration) {
        mDecoration = decoration;
        mContext = context;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (layoutManager != null) {
            int position = layoutManager.getPosition(view);
            if (position == 0) {
                outRect.left = DestinyUtil.dp2px(mContext, mDecoration) * 2;
            } else if (position == layoutManager.getItemCount() - 1) {
                outRect.right = DestinyUtil.dp2px(mContext, mDecoration) * 2;
            } else {
                outRect.left = DestinyUtil.dp2px(mContext, mDecoration);
            }
        } else {
            outRect.left = DestinyUtil.dp2px(mContext, mDecoration);
        }
    }
}
