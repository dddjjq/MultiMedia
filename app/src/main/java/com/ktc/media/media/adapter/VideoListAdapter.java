package com.ktc.media.media.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.media.view.OnListItemClickListener;
import com.ktc.media.media.view.VideoListItemView;
import com.ktc.media.model.VideoData;
import com.ktc.media.view.OnItemClickListener;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private Context mContext;
    private List<VideoData> mVideoData;
    private OnListItemClickListener mOnListItemClickListener;

    public VideoListAdapter(Context context, List<VideoData> videoData) {
        mContext = context;
        mVideoData = videoData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int position = i;
        viewHolder.mVideoListItemView.setTextView(mVideoData.get(i).getName());
        viewHolder.mVideoListItemView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (mOnListItemClickListener != null) {
                    mOnListItemClickListener.onItemClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mVideoData == null) {
            return 0;
        }
        return mVideoData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        VideoListItemView mVideoListItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVideoListItemView = (VideoListItemView) itemView.findViewById(R.id.video_list_item);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mOnListItemClickListener = onListItemClickListener;
    }

}
