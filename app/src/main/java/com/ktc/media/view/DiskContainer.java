package com.ktc.media.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.ktc.media.R;
import com.ktc.media.model.DiskData;
import com.ktc.media.util.DestinyUtil;
import com.ktc.media.util.StorageUtil;

import java.util.HashMap;

public class DiskContainer extends HorizontalScrollView implements OnItemClickListener {

    private LinearLayout mLinearLayout;
    private HashMap<DiskData, DiskCardView> diskHashMap;
    private OnDiskItemClickListener mOnDiskItemClickListener;

    public DiskContainer(Context context) {
        super(context);
        init(context);
    }

    public DiskContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mLinearLayout = new LinearLayout(context);
        diskHashMap = new HashMap<>();
        addView(mLinearLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(DestinyUtil.dp2px(getContext(), 526f)
                , MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addDiskView(DiskData diskData) {
        DiskCardView diskCardView = new DiskCardView(getContext());
        diskCardView.setDiskName(diskData.getName());
        diskCardView.setDiskLastMemory(getDiskLastMemoryString(diskData));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DestinyUtil.dp2px(getContext(), 272)
                , DestinyUtil.dp2px(getContext(), 392));
        if (mLinearLayout.getChildCount() > 0) {
            layoutParams.setMarginStart(DestinyUtil.dp2px(getContext(), -18.7f));
        }
        diskCardView.setLayoutParams(layoutParams);
        diskCardView.setOnItemClickListener(this);
        diskCardView.setData(diskData);
        mLinearLayout.addView(diskCardView);
        diskHashMap.put(diskData, diskCardView);
    }

    public void removeDiskView(DiskData diskData) {
        for (DiskData data : diskHashMap.keySet()) {
            if (data.equals(diskData)) {
                mLinearLayout.removeView(diskHashMap.get(data));
            }
        }
    }

    public void removeAllDiskViews() {
        mLinearLayout.removeAllViews();
    }

    private String getDiskLastMemoryString(DiskData diskData) {
        String available = StorageUtil.getFileSizeDescription(diskData.getAvailableSpace());
        String total = StorageUtil.getFileSizeDescription(diskData.getTotalSpace());
        return getResources().getString(R.string.str_disk_last_memory) + " " + available
                + "/" + total;
    }

    public void setOnDiskItemClickListener(OnDiskItemClickListener onDiskItemClickListener) {
        mOnDiskItemClickListener = onDiskItemClickListener;
    }

    @Override
    public void onItemClick(View view) {
        for (DiskData diskData : diskHashMap.keySet()) {
            if (view == diskHashMap.get(diskData)) {
                if (mOnDiskItemClickListener != null) {
                    mOnDiskItemClickListener.onDiskItemClick(diskHashMap.get(diskData), diskData);
                }
            }
        }
    }

    public interface OnDiskItemClickListener {
        void onDiskItemClick(DiskCardView diskCardView, DiskData diskData);
    }
}
