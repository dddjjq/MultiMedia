package com.ktc.media.media.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.ktc.media.R;
import com.ktc.media.media.adapter.MusicListAdapter;
import com.ktc.media.model.MusicData;
import com.ktc.media.util.DestinyUtil;

import java.util.List;

public class MusicListDialog extends Dialog implements OnListItemClickListener {

    private RecyclerView mRecyclerView;
    private List<MusicData> mMusicData;
    private int currentPosition;
    private OnListItemClickListener mOnListItemClickListener;

    public MusicListDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MusicListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_music_list, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.dialog_music_list_view);
        setContentView(view);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void prepareData(List<MusicData> musicData, int position) {
        mMusicData = musicData;
        currentPosition = position;
        initRecyclerView();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL
                , false));
        MusicListAdapter adapter = new MusicListAdapter(getContext(), mMusicData);
        adapter.setOnListItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new MediaDecoration(getContext(), DestinyUtil.dp2px(getContext(), 8)));
        mRecyclerView.scrollToPosition(currentPosition);
        mRecyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int position = layoutManager.getPosition(view);
                    if (position == currentPosition) {
                        view.requestFocus();
                        mRecyclerView.removeOnChildAttachStateChangeListener(this);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mOnListItemClickListener != null) {
            mOnListItemClickListener.onItemClick(view, position);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mOnListItemClickListener = onListItemClickListener;
    }
}
