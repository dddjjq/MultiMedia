package com.ktc.media.media.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ktc.media.R;
import com.ktc.media.media.photo.ImageDownLoader;
import com.ktc.media.media.view.OnListItemClickListener;
import com.ktc.media.media.view.PictureListItemView;
import com.ktc.media.model.FileData;
import com.ktc.media.view.OnItemClickListener;

import java.util.List;

public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.ViewHolder> {

    private Context mContext;
    private List<FileData> mPictureData;
    private OnListItemClickListener mOnListItemClickListener;
    private ImageDownLoader mImageDownLoader;

    public PictureListAdapter(Context context, List<FileData> pictureData) {
        mContext = context;
        mPictureData = pictureData;
        mImageDownLoader = new ImageDownLoader(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_picture_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final int position = i;
        viewHolder.mPictureListItemView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (mOnListItemClickListener != null) {
                    mOnListItemClickListener.onItemClick(viewHolder.mPictureListItemView, position);
                }
            }
        });
        mImageDownLoader.downloadImage(i, mPictureData.get(i).getPath(), new ImageDownLoader.onImageLoaderListener() {
            @Override
            public void onImageLoader(Drawable drawable, int position, String url) {
                viewHolder.mPictureListItemView.setImageView(drawable);
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
        if (mPictureData == null) {
            return 0;
        }
        return mPictureData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        PictureListItemView mPictureListItemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPictureListItemView = (PictureListItemView) itemView.findViewById(R.id.picture_list_item);
        }
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        mOnListItemClickListener = onListItemClickListener;
    }
}
