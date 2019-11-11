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
import com.ktc.media.adapter.PictureGridListAdapter;
import com.ktc.media.adapter.PictureLinearListAdapter;
import com.ktc.media.base.BaseActivity;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.media.photo.ImagePlayerActivity;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.FileData;
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

public class PictureListActivity extends BaseActivity implements OnItemFocusListener
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
    private RecyclerView pictureListView;
    private PictureLinearListAdapter mPictureLinearListAdapter;
    private PictureGridListAdapter mPictureGridListAdapter;
    private List<FileData> mDataList;
    private boolean isCurrentLinear = true;
    private boolean isKeyboardVisible = false;
    private static final int spanCount = 4;
    private String currentLeftTitle;
    private ExecutorService mExecutorService;

    @Override
    public int getLayoutId() {
        return R.layout.activity_picture_list;
    }

    @Override
    public void initView() {
        keyboardView = (KeyboardView) findViewById(R.id.picture_keyboard_view);
        subtitleLeftText = (MarqueeTextView) findViewById(R.id.picture_list_sub_title_left);
        subtitleRightText = (TextView) findViewById(R.id.picture_list_sub_title_right);
        switchImageBtn = (ImageView) findViewById(R.id.picture_list_switch_image);
        dividerView = (ImageView) findViewById(R.id.picture_list_title_divider);
        searchImageBtn = (ImageView) findViewById(R.id.picture_list_search_image);
        mListContainer = (FileListContainer) findViewById(R.id.picture_list_container);
        pictureListView = (RecyclerView) findViewById(R.id.picture_list_view);
        emptyView = (RelativeLayout) findViewById(R.id.media_empty_layout);
        emptyImage = (ImageView) findViewById(R.id.media_empty_image);
        keyboardView.setEnabled(false);
        changeLeftFocusable(false);
        pictureListView.setHasFixedSize(true);
    }

    @Override
    public void initData() {
        mExecutorService = Executors.newSingleThreadExecutor();
        mDataList = getIntent().getParcelableArrayListExtra(Constants.TYPE_PICTURE);
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            changeEmptyStatus(mDataList, false);
            subtitleRightText.setText(getString(R.string.str_picture_list_count, mDataList.size()));
        }
        mPictureLinearListAdapter = new PictureLinearListAdapter(this, mDataList);
        mPictureGridListAdapter = new PictureGridListAdapter(this, mDataList);
        setLinearAdapter();
        if (mDataList == null || mDataList.size() == 0) {
            updateDataList(null);
        }
    }

    @Override
    public void initFocus() {
        pictureListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
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
                pictureListView.removeOnChildAttachStateChangeListener(this);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    @Override
    public void addListener() {
        mPictureLinearListAdapter.setOnItemFocusListener(this);
        mPictureGridListAdapter.setOnItemFocusListener(this);
        mPictureLinearListAdapter.setOnItemClickListener(this);
        mPictureGridListAdapter.setOnItemClickListener(this);
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
                || type.equals(Constants.PICTURE_REFRESH_ACTION)) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.picture_list_switch_image:
                isCurrentLinear = !isCurrentLinear;
                changeListLayout(isCurrentLinear);
                break;
            case R.id.picture_list_search_image:
                isKeyboardVisible = !isKeyboardVisible;
                changeKeyboardStatus(isKeyboardVisible);
                break;
        }
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, BaseData data) {
        currentLeftTitle = getPathDescription(data);
        if (!isKeyboardVisible) subtitleLeftText.setText(currentLeftTitle);
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
                final List<FileData> fileDataList = FileDataManager.getInstance(PictureListActivity.this).getAllPictureData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.clear();
                        setAdapterAndNotify();
                        mDataList.addAll(fileDataList);
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

    class UpdateThread extends Thread{
        String fromSearch;

        public UpdateThread(String fromSearch) {
            this.fromSearch = fromSearch;
        }

        @Override
        public void run() {
            final List<FileData> fileDataList = FileDataManager.getInstance(PictureListActivity.this).getAllPictureData();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDataList.clear();
                    setAdapterAndNotify();
                    mDataList.addAll(fileDataList);
                    if (fromSearch == null) {
                        refreshList();
                    } else {
                        refreshListFromSearch(fromSearch);
                    }
                }
            });
        }
    }

    private void refreshList() {
        changeEmptyStatus(mDataList, false);
        setAdapterAndNotify();
        if (isKeyboardVisible) {
            subtitleLeftText.setText(getString(R.string.str_picture_list_count, mDataList.size()));
        }
        subtitleRightText.setText(getString(R.string.str_picture_list_count, mDataList.size()));
    }

    private void setAdapterAndNotify() {
        if (isCurrentLinear) {
            pictureListView.setAdapter(mPictureLinearListAdapter);
            mPictureLinearListAdapter.notifyDataSetChanged();
        } else {
            pictureListView.setAdapter(mPictureGridListAdapter);
            mPictureGridListAdapter.notifyDataSetChanged();
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
                layoutParams.leftMargin = -(int) (DestinyUtil.dp2px(PictureListActivity.this, 413.3f)
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
            subtitleLeftText.setText(getString(R.string.str_picture_list_count, mDataList.size()));
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
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) pictureListView.getLayoutParams();
            recyclerLayoutParam.rightMargin = 0;
            recyclerLayoutParam.addRule(RelativeLayout.ALIGN_LEFT, dividerView.getId());
            if (!isCurrentLinear) pictureListView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mListContainer.getLayoutParams();
            layoutParam.rightMargin = DestinyUtil.dp2px(this, 148);
            layoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mListContainer.requestLayout();
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) pictureListView.getLayoutParams();
            recyclerLayoutParam.removeRule(RelativeLayout.ALIGN_LEFT);
            recyclerLayoutParam.rightMargin = DestinyUtil.dp2px(this, 13.3f);
            if (!isCurrentLinear) pictureListView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    //设置列表文字
    private void setLinearContentVisible(boolean isVisible) {
        if (isCurrentLinear) {
            mPictureLinearListAdapter.setItemContentVisible(isVisible);
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
        pictureListView.setLayoutManager(new LinearLayoutManager(this));
        if (pictureListView.getItemDecorationCount() == 0) {
            pictureListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, -6.67f)));
        }
        pictureListView.setAdapter(mPictureLinearListAdapter);
    }

    private void setGridAdapter() {
        pictureListView.setLayoutManager(new GridLayoutManager(this, spanCount));
        if (pictureListView.getItemDecorationCount() == 0) {
            pictureListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, 6.67f)));
        }
        pictureListView.setAdapter(mPictureGridListAdapter);
    }

    private void changeEmptyStatus(List<FileData> dataList, boolean isFromSearch) {
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
            mPictureLinearListAdapter.setSpanText(null);
            mPictureGridListAdapter.setSpanText(null);
            updateDataList(null);
        } else {
            updateDataList(s);
        }
    }

    private void refreshListFromSearch(String s) {
        Iterator<FileData> fileDataIterator = mDataList.iterator();
        while (fileDataIterator.hasNext()) {
            FileData pictureData = fileDataIterator.next();
            if (!pictureData.getName().toUpperCase().contains(s.toUpperCase())) {
                fileDataIterator.remove();
            }
        }
        changeEmptyStatus(mDataList, true);
        if (isCurrentLinear) {
            pictureListView.setAdapter(mPictureLinearListAdapter);
            mPictureLinearListAdapter.setSpanText(s);
            mPictureLinearListAdapter.notifyDataSetChanged();
        } else {
            pictureListView.setAdapter(mPictureGridListAdapter);
            mPictureGridListAdapter.setSpanText(s);
            mPictureGridListAdapter.notifyDataSetChanged();
        }
        subtitleLeftText.setText(getString(R.string.str_picture_list_count, mDataList.size()));
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
        Intent intent = new Intent(this, ImagePlayerActivity.class);
        intent.putExtra(Constants.BUNDLE_PATH_KEY, baseData.getPath());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorUtil.shutdownAndAwaitTermination(mExecutorService);
        mPictureLinearListAdapter.release();
    }
}
