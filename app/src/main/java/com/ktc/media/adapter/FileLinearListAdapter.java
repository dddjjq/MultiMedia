package com.ktc.media.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.data.ThreadPoolManager;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.FileData;
import com.ktc.media.util.FileIconUtil;
import com.ktc.media.view.MediaLinearItemView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.OnItemFocusListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class FileLinearListAdapter extends RecyclerView.Adapter<FileLinearListAdapter.ViewHolder>
        implements OnItemFocusListener, OnItemClickListener {

    private Context mContext;
    private List<FileData> mDataList;
    private OnItemFocusListener mOnItemFocusListener;
    private OnItemClickListener mOnItemClickListener;
    private boolean isItemContentVisible = true;
    private String spanText = null;

    public FileLinearListAdapter(Context context, List<FileData> dataList) {
        mContext = context;
        mDataList = dataList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_file_linear_list_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        viewHolder.mMediaLinearItemView.setTitle(mDataList.get(i).getName());
        viewHolder.mMediaLinearItemView.setContent(mDataList.get(i).getSizeDescription());
        viewHolder.mMediaLinearItemView.setData(mDataList.get(i));
        viewHolder.mMediaLinearItemView.setOnItemFocusListener(this);
        viewHolder.mMediaLinearItemView.setContentVisible(isItemContentVisible);
        viewHolder.mMediaLinearItemView.setImage(FileIconUtil.getFileTypeIcon(mDataList.get(i).getType()));
        viewHolder.mMediaLinearItemView.setOnItemClickListener(this);
        if (spanText != null) {
            viewHolder.mMediaLinearItemView.setSpanText(spanText);
        }
        loadFileSize(mDataList.get(i), new OnFileSizeLoadListener() {
            @Override
            public void onFileSizeLoad(String description) {
                viewHolder.mMediaLinearItemView.setContent(description);
            }
        });
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

    public void setItemContentVisible(boolean isVisible) {
        isItemContentVisible = isVisible;
        notifyDataSetChanged();
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

    public void release() {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        MediaLinearItemView mMediaLinearItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mMediaLinearItemView = (MediaLinearItemView) itemView.findViewById(R.id.file_linear_list_item);
        }
    }

    private void loadFileSize(final FileData fileData, final OnFileSizeLoadListener onFileSizeLoadListener) {

        if (fileData.getSizeDescription() != null) {
            onFileSizeLoadListener.onFileSizeLoad(fileData.getSizeDescription());
            return;
        }

        @SuppressWarnings("HandlerLeak") final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String description = (String) msg.obj;
                onFileSizeLoadListener.onFileSizeLoad(description);
            }
        };

        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(fileData.getPath());
                if (!f.exists()) return;
                long time = f.lastModified();
                SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                String description = dateFormat.format(time);
                fileData.setSizeDescription(description);
                Message message = mHandler.obtainMessage();
                message.obj = description;
                mHandler.sendMessage(message);
            }
        });
    }

    private interface OnFileSizeLoadListener {
        void onFileSizeLoad(String description);
    }
}
