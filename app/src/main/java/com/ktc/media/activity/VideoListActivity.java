package com.ktc.media.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.adapter.VideoGridListAdapter;
import com.ktc.media.adapter.VideoLinearListAdapter;
import com.ktc.media.base.BaseActivity;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.data.ThreadPoolManager;
import com.ktc.media.media.video.VideoPlayerActivity;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.VideoData;
import com.ktc.media.util.DestinyUtil;
import com.ktc.media.util.ExecutorUtil;
import com.ktc.media.util.SpaceItemDecoration;
import com.ktc.media.view.FileListContainer;
import com.ktc.media.view.KeyboardView;
import com.ktc.media.view.MarqueeTextView;
import com.ktc.media.view.MediaGridItemView;
import com.ktc.media.view.MediaLinearItemView;
import com.ktc.media.view.OnItemClickListener;
import com.ktc.media.view.OnItemFocusListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoListActivity extends BaseActivity implements OnItemFocusListener
        , View.OnClickListener, KeyboardView.OnTextChangeListener, OnItemClickListener {

    private MarqueeTextView subtitleLeftText;
    private TextView subtitleRightText;
    private ImageView switchImageBtn;
    private ImageView searchImageBtn;
    private ImageView dividerView;
    private FileListContainer mListContainer;
    private KeyboardView keyboardView;
    private RelativeLayout emptyView;
    private ImageView emptyImage;
    private RecyclerView videoListView;
    private VideoLinearListAdapter mVideoLinearListAdapter;
    private VideoGridListAdapter mVideoGridListAdapter;
    private List<VideoData> mDataList;
    private boolean isCurrentLinear = true;
    private boolean isKeyboardVisible = false;
    private static final int spanCount = 4;
    private String currentLeftTitle;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_list;
    }

    @Override
    public void initView() {
        keyboardView = (KeyboardView) findViewById(R.id.video_keyboard_view);
        subtitleLeftText = (MarqueeTextView) findViewById(R.id.video_list_sub_title_left);
        subtitleRightText = (TextView) findViewById(R.id.video_list_sub_title_right);
        switchImageBtn = (ImageView) findViewById(R.id.video_list_switch_image);
        dividerView = (ImageView) findViewById(R.id.video_list_title_divider);
        searchImageBtn = (ImageView) findViewById(R.id.video_list_search_image);
        mListContainer = (FileListContainer) findViewById(R.id.video_list_container);
        videoListView = (RecyclerView) findViewById(R.id.video_list_view);
        emptyView = (RelativeLayout) findViewById(R.id.media_empty_layout);
        emptyImage = (ImageView) findViewById(R.id.media_empty_image);
        keyboardView.setEnabled(false);
        changeLeftFocusable(false);
        videoListView.setHasFixedSize(true);
    }

    @Override
    public void initData() {
        mDataList = getIntent().getParcelableArrayListExtra(Constants.TYPE_VIDEO);
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            changeEmptyStatus(mDataList, false);
            subtitleRightText.setText(getString(R.string.str_video_list_count, mDataList.size()));
        }
        mVideoLinearListAdapter = new VideoLinearListAdapter(this, mDataList);
        mVideoGridListAdapter = new VideoGridListAdapter(this, mDataList);
        setLinearAdapter();
        if (mDataList == null || mDataList.size() == 0) {
            updateDataList(null);
        }
    }

    @Override
    public void initFocus() {
        videoListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull final View view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.requestFocus();
                        changeLeftFocusable(true);
                    }
                }, 100);
                mListContainer.setNewFocus(view);
                videoListView.removeOnChildAttachStateChangeListener(this);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    @Override
    public void addListener() {
        mVideoLinearListAdapter.setOnItemFocusListener(this);
        mVideoGridListAdapter.setOnItemFocusListener(this);
        mVideoLinearListAdapter.setOnItemClickListener(this);
        mVideoGridListAdapter.setOnItemClickListener(this);
        switchImageBtn.setOnClickListener(this);
        searchImageBtn.setOnClickListener(this);
        keyboardView.setOnTextChangeListener(this);
    }

    @Override
    public void handleDiskIntent(Intent intent) {

    }

    @Override
    public void handleUpdate(String type) {
        if (type.equals(Constants.ALL_REFRESH_ACTION)
                || type.equals(Constants.PATH_DELETE_ACTION)
                || type.equals(Constants.VIDEO_REFRESH_ACTION)) {
            updateDataList(null);
        }
    }

    @Override
    public void blockFocus() {
        changeLeftFocusable(false);
    }

    @Override
    public void releaseFocus() {
        changeLeftFocusable(true);
    }


    @Override
    public void onItemFocusChange(final View view, boolean hasFocus, BaseData data) {
        currentLeftTitle = getPathDescription(data);
        if (!isKeyboardVisible) subtitleLeftText.setText(currentLeftTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_list_switch_image:
                isCurrentLinear = !isCurrentLinear;
                changeListLayout(isCurrentLinear);
                break;
            case R.id.video_list_search_image:
                isKeyboardVisible = !isKeyboardVisible;
                changeKeyboardStatus(isKeyboardVisible);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardVisible) {
            isKeyboardVisible = false;
            changeKeyboardStatus(false);
        } else {
            super.onBackPressed();
        }
    }

    private void updateDataList(final String fromSearch) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<VideoData> videoDataList = FileDataManager.getInstance(VideoListActivity.this).getAllVideoData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.clear();
                        setAdapterAndNotify();
                        mDataList.addAll(videoDataList);
                        if (fromSearch == null) {
                            refreshList();
                        } else {
                            refreshListFromSearch(fromSearch);
                        }
                    }
                });
            }
        });
    }


    private void refreshList() {
        changeEmptyStatus(mDataList, false);
        setAdapterAndNotify();
        if (isKeyboardVisible) {
            subtitleLeftText.setText(getString(R.string.str_video_list_count, mDataList.size()));
        }
        subtitleRightText.setText(getString(R.string.str_video_list_count, mDataList.size()));
    }

    private void setAdapterAndNotify() {
        if (isCurrentLinear) {
            videoListView.setAdapter(mVideoLinearListAdapter);
            mVideoLinearListAdapter.notifyDataSetChanged();
        } else {
            videoListView.setAdapter(mVideoGridListAdapter);
            mVideoGridListAdapter.notifyDataSetChanged();
        }
    }

    private void changeLeftFocusable(boolean canFocus) {
        switchImageBtn.setFocusable(canFocus);
        switchImageBtn.setFocusableInTouchMode(canFocus);
        searchImageBtn.setFocusable(canFocus);
        searchImageBtn.setFocusableInTouchMode(canFocus);
    }

    private void changeKeyboardStatus(boolean isVisible) {
        changeTitle(isVisible);
        changeRightMargin(isVisible);
        setLinearContentVisible(!isVisible);
        changeLeftImageBtnVisible(!isVisible);
        adjustFlyBoardView();//重新调整飞框
        startKeyboardAnimation(isVisible);
        if (!isVisible) updateDataList(null);
        clearKeyboard();
    }

    private void startKeyboardAnimation(boolean isVisible) {
        ValueAnimator widthAnimator;
        if (isVisible) {
            keyboardView.setEnabled(true);
            widthAnimator = ValueAnimator.ofFloat(1, 0).setDuration(300);
            keyboardView.editTextRequestFocus();
        } else {
            keyboardView.setEnabled(false);
            widthAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
            searchImageBtn.requestFocus();
        }
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) keyboardView.getLayoutParams();
                layoutParams.leftMargin = -(int) (DestinyUtil.dp2px(VideoListActivity.this, 413.3f)
                        * Float.parseFloat(valueAnimator.getAnimatedValue().toString()));
                keyboardView.requestLayout();
            }
        });
        widthAnimator.start();
    }

    private void clearKeyboard() {
        keyboardView.clearKeyboard();
    }

    private void changeTitle(boolean isVisible) {
        if (isVisible) {
            subtitleLeftText.setText(getString(R.string.str_video_list_count, mDataList.size()));
            if (isKeyboardVisible || emptyView.getVisibility() == View.VISIBLE) {
                subtitleRightText.setVisibility(View.GONE);
            }
        } else {
            subtitleLeftText.setText(currentLeftTitle);
            if (!isKeyboardVisible && emptyView.getVisibility() == View.GONE) {
                subtitleRightText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void changeRightMargin(boolean isVisible) {
        if (isVisible) {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mListContainer.getLayoutParams();
            layoutParam.rightMargin = -DestinyUtil.dp2px(this, 22);
            layoutParam.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            mListContainer.requestLayout();
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) videoListView.getLayoutParams();
            recyclerLayoutParam.rightMargin = 0;
            recyclerLayoutParam.addRule(RelativeLayout.ALIGN_LEFT, dividerView.getId());
            if (!isCurrentLinear) videoListView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mListContainer.getLayoutParams();
            layoutParam.rightMargin = DestinyUtil.dp2px(this, 148);
            layoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mListContainer.requestLayout();
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) videoListView.getLayoutParams();
            recyclerLayoutParam.removeRule(RelativeLayout.ALIGN_LEFT);
            recyclerLayoutParam.rightMargin = DestinyUtil.dp2px(this, 13.3f);
            if (!isCurrentLinear) videoListView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    //设置列表文字
    private void setLinearContentVisible(boolean isVisible) {
        if (isCurrentLinear) {
            mVideoLinearListAdapter.setItemContentVisible(isVisible);
        }
    }

    private void changeLeftImageBtnVisible(boolean isVisible) {
        if (isVisible) {
            if (!isKeyboardVisible && emptyView.getVisibility() == View.GONE) {
                switchImageBtn.setVisibility(View.VISIBLE);
                searchImageBtn.setVisibility(View.VISIBLE);
            }
        } else {
            if (isKeyboardVisible || emptyView.getVisibility() == View.VISIBLE) {
                switchImageBtn.setVisibility(View.GONE);
                searchImageBtn.setVisibility(View.GONE);
            }
        }
    }

    private void changeListLayout(boolean isLinear) {
        if (isLinear) {
            setLinearAdapter();
            switchImageBtn.setImageResource(R.drawable.switch_grid_image);
        } else {
            setGridAdapter();
            switchImageBtn.setImageResource(R.drawable.switch_list_image);
        }
    }

    private void adjustFlyBoardView() {
        mListContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListContainer.reAttachView();
            }
        }, 300);
    }

    private void setLinearAdapter() {
        videoListView.setLayoutManager(new LinearLayoutManager(this));
        if (videoListView.getItemDecorationCount() == 0) {
            videoListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, -6.67f)));
        }
        videoListView.setAdapter(mVideoLinearListAdapter);
    }

    private void setGridAdapter() {
        videoListView.setLayoutManager(new GridLayoutManager(this, spanCount));
        if (videoListView.getItemDecorationCount() == 0) {
            videoListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, 6.67f)));
        }
        videoListView.setAdapter(mVideoGridListAdapter);
    }

    private void changeEmptyStatus(List<VideoData> dataList, boolean isFromSearch) {
        if (isFromSearch) {
            emptyImage.setImageResource(R.drawable.search_empty);
        } else {
            emptyImage.setImageResource(R.drawable.media_list_empty);
        }
        if (dataList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            mListContainer.setVisibility(View.GONE);
            changeLeftImageBtnVisible(false);
            subtitleRightText.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            mListContainer.setVisibility(View.VISIBLE);
            changeLeftImageBtnVisible(true);
            if (!isKeyboardVisible) {
                subtitleRightText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            mVideoLinearListAdapter.setSpanText(null);
            mVideoGridListAdapter.setSpanText(null);
            updateDataList(null);
        } else {
            updateDataList(s);
        }
    }

    private void refreshListFromSearch(String s) {
        Iterator<VideoData> videoMusicDataIterator = mDataList.iterator();
        while (videoMusicDataIterator.hasNext()) {
            VideoData videoData = videoMusicDataIterator.next();
            if (!videoData.getName().toUpperCase().contains(s.toUpperCase())) {
                videoMusicDataIterator.remove();
            }
        }
        changeEmptyStatus(mDataList, true);
        if (isCurrentLinear) {
            videoListView.setAdapter(mVideoLinearListAdapter);
            mVideoLinearListAdapter.setSpanText(s);
            mVideoLinearListAdapter.notifyDataSetChanged();
        } else {
            videoListView.setAdapter(mVideoGridListAdapter);
            mVideoGridListAdapter.setSpanText(s);
            mVideoGridListAdapter.notifyDataSetChanged();
        }
        subtitleLeftText.setText(getString(R.string.str_video_list_count, mDataList.size()));
    }

    @Override
    public void onItemClick(View view) {
        BaseData baseData = null;
        if (view instanceof MediaLinearItemView) {
            baseData = ((MediaLinearItemView) view).getBaseData();
        } else if (view instanceof MediaGridItemView) {
            baseData = ((MediaGridItemView) view).getBaseData();
        }
        if (baseData == null) return;
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra(Constants.BUNDLE_PATH_KEY, baseData.getPath());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorUtil.shutdownAndAwaitTermination(mExecutorService);
        mVideoLinearListAdapter.release();
    }
}
