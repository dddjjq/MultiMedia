package com.ktc.media.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.MusicData;
import com.ktc.media.view.MediaLinearItemView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.OnItemFocusListener;

import java.util.List;

public class MusicLinearListAdapter extends RecyclerView.Adapter<MusicLinearListAdapter.ViewHolder>
        implements OnItemFocusListener, OnItemClickListener {

    private Context mContext;
    private List<MusicData> mDataList;
    private OnItemFocusListener mOnItemFocusListener;
    private OnItemClickListener mOnItemClickListener;
    private boolean isItemContentVisible = true;
    private String spanText = null;

    public MusicLinearListAdapter(Context context, List<MusicData> dataList) {
        mContext = context;
        mDataList = dataList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music_linear_list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mMediaLinearItemView.setTitle(mDataList.get(i).getName());
        viewHolder.mMediaLinearItemView.setContent(mDataList.get(i).getDurationString());
        viewHolder.mMediaLinearItemView.setData(mDataList.get(i));
        viewHolder.mMediaLinearItemView.setOnItemFocusListener(this);
        viewHolder.mMediaLinearItemView.setOnItemClickListener(this);
        viewHolder.mMediaLinearItemView.setContentVisible(isItemContentVisible);
        if (spanText != null) {
            viewHolder.mMediaLinearItemView.setSpanText(spanText);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList != null) {
            return mDataList.size();
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

    public void setSpanText(String text) {
        spanText = text;
    }

    public void setItemContentVisible(boolean isVisible) {
        isItemContentVisible = isVisible;
        notifyDataSetChanged();
    }

    public void setOnItemFocusListener(OnItemFocusListener onItemFocusListener) {
        mOnItemFocusListener = onItemFocusListener;
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, BaseData data) {
        if (mOnItemFocusListener != null) {
            mOnItemFocusListener.onItemFocusChange(view, hasFocus, data);
        }
    }

    @Override
    public void onItemClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        MediaLinearItemView mMediaLinearItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaLinearItemView = (MediaLinearItemView) itemView.findViewById(R.id.music_linear_list_item);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
