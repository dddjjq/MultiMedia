package com.ktc.media.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.FileData;
import com.ktc.media.util.FileIconUtil;
import com.ktc.media.view.MediaGridItemView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.OnItemFocusListener;

import java.util.List;

public class FileGridListAdapter extends RecyclerView.Adapter<FileGridListAdapter.ViewHolder>
        implements OnItemFocusListener, OnItemClickListener {
    private Context mContext;
    private List<FileData> mDataList;
    private OnItemFocusListener mOnItemFocusListener;
    private OnItemClickListener mOnItemClickListener;
    private String spanText = null;

    public FileGridListAdapter(Context context, List<FileData> dataList) {
        mContext = context;
        mDataList = dataList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_grid_list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mMediaGridItemView.setTitle(mDataList.get(i).getName());
        viewHolder.mMediaGridItemView.setData(mDataList.get(i));
        viewHolder.mMediaGridItemView.setOnItemFocusListener(this);
        viewHolder.mMediaGridItemView.setImage(FileIconUtil.getFileTypeIcon(mDataList.get(i).getType()));
        viewHolder.mMediaGridItemView.setOnItemClickListener(this);
        if (spanText != null) {
            viewHolder.mMediaGridItemView.setSpanText(spanText);
        }

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
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
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

    class ViewHolder extends RecyclerView.ViewHolder {

        MediaGridItemView mMediaGridItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaGridItemView = (MediaGridItemView) itemView.findViewById(R.id.file_grid_list_item);
        }
    }
}
