package com.ktc.media.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.VideoData;
import com.ktc.media.view.MediaGridItemView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.OnItemFocusListener;

import java.util.List;

public class VideoGridListAdapter extends RecyclerView.Adapter<VideoGridListAdapter.ViewHolder>
        implements OnItemFocusListener, OnItemClickListener {

    private Context mContext;
    private List<VideoData> mVideoDataList;
    private OnItemFocusListener mOnItemFocusListener;
    private OnItemClickListener mOnItemClickListener;
    private String spanText = null;

    public VideoGridListAdapter(Context context, List<VideoData> videoDataList) {
        mContext = context;
        mVideoDataList = videoDataList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_grid_list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mMediaGridItemView.setTitle(mVideoDataList.get(i).getName());
        viewHolder.mMediaGridItemView.setData(mVideoDataList.get(i));
        viewHolder.mMediaGridItemView.setOnItemFocusListener(this);
        viewHolder.mMediaGridItemView.setOnItemClickListener(this);
        if (spanText != null) {
            viewHolder.mMediaGridItemView.setSpanText(spanText);
        }
    }

    @Override
    public int getItemCount() {
        if (mVideoDataList != null) {
            return mVideoDataList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, BaseData data) {
        if (mOnItemFocusListener != null) {
            mOnItemFocusListener.onItemFocusChange(view, hasFocus, data);
        }
    }

    public void setSpanText(String text) {
        spanText = text;
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemFocusListener) {
        mOnItemFocusListener = onItemFocusListener;
    }

    @Override
    public void onItemClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        MediaGridItemView mMediaGridItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaGridItemView = (MediaGridItemView) itemView.findViewById(R.id.video_grid_list_item);
        }
    }
}
