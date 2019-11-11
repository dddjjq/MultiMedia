package com.ktc.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.ktc.media.R;
import com.ktc.media.model.DiskData;
import com.ktc.media.util.StorageUtil;

public class DiskCardView extends RelativeLayout {

    private MarqueeTextView diskNameText;
    private MarqueeTextView diskLastMemoryText;
    private OnItemClickListener mOnItemClickListener;
    private DiskData mDiskData;

    public DiskCardView(Context context) {
        super(context);
        init(context, null);
    }

    public DiskCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_disk_card_view, this, true);
        diskNameText = (MarqueeTextView) findViewById(R.id.disk_card_name);
        diskLastMemoryText = (MarqueeTextView) findViewById(R.id.disk_card_last_memory);
        addListener();
    }

    private void addListener() {
        setBackgroundResource(R.drawable.disk_card_normal);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setBackgroundResource(R.drawable.disk_card_focus);
                    diskNameText.setSelected(true);
                    diskLastMemoryText.setSelected(true);
                } else {
                    setBackgroundResource(R.drawable.disk_card_normal);
                    diskNameText.setSelected(false);
                    diskLastMemoryText.setSelected(false);
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v);
                }
            }
        });
        setOnHoverListener(new OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                requestFocus();
                requestFocusFromTouch();
                return true;
            }
        });
    }

    public void setDiskName(String text) {
        diskNameText.setText(text);
    }

    public void setDiskLastMemory(String text) {
        diskLastMemoryText.setText(text);
    }

    public void setData(DiskData data) {
        mDiskData = data;
        initDiskSize(data);
    }

    private void initDiskSize(final DiskData diskData) {
        new Thread() {
            @Override
            public void run() {
                String path = diskData.getPath();
                long totalSize = StorageUtil.getTotalSpace(path);
                long availableSize = StorageUtil.getAvailSpace(path);
                String total = StorageUtil.getFileSizeDescription(totalSize);
                String available = StorageUtil.getFileSizeDescription(availableSize);
                final String result = getResources().getString(R.string.str_disk_last_memory) + " " + available
                        + "/" + total;
                DiskCardView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        setDiskLastMemory(result);
                    }
                });
            }
        }.start();
    }

    public DiskData getDiskData() {
        return mDiskData;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
