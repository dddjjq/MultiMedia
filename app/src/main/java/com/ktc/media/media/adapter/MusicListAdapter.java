package com.ktc.media.media.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.media.view.MusicListItemView;
import com.ktc.media.media.view.OnListItemClickListener;
import com.ktc.media.model.MusicData;
import com.ktc.media.view.OnItemClickListener;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private Context mContext;
    private List<MusicData> mMusicData;
    private OnListItemClickListener mOnListItemClickListener;

    public MusicListAdapter(Context context, List<MusicData> musicData) {
        mContext = context;
        mMusicData = musicData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final int position = i;
        viewHolder.mMusicListItemView.setTextView(mMusicData.get(i).getName());
        viewHolder.mMusicListItemView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (mOnListItemClickListener != null) {
                    mOnListItemClickListener.onItemClick(viewHolder.mMusicListItemView, position);
                }
            }
        });
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
    public int getItemCount() {
        if (mMusicData == null) {
            return 0;
        }
        return mMusicData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        MusicListItemView mMusicListItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMusicListItemView = (MusicListItemView) itemView.findViewById(R.id.music_list_item);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mOnListItemClickListener = onListItemClickListener;
    }
}
