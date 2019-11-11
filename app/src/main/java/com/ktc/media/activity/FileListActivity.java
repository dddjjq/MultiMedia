package com.ktc.media.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktc.media.R;
import com.ktc.media.adapter.FileGridListAdapter;
import com.ktc.media.adapter.FileLinearListAdapter;
import com.ktc.media.base.BaseActivity;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.data.ThreadPoolManager;
import com.ktc.media.media.music.MusicPlayerActivity;
import com.ktc.media.media.photo.ImagePlayerActivity;
import com.ktc.media.media.video.VideoPlayerActivity;
import com.ktc.media.model.BaseData;
import com.ktc.media.model.DiskData;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileListActivity extends BaseActivity implements OnItemFocusListener
        , View.OnClickListener, KeyboardView.OnTextChangeListener, OnItemClickListener {

    private static final boolean IS_NEED_LEFT_IMAGE = false;
    private MarqueeTextView subtitleLeftText;
    private TextView titleText;
    private TextView subtitleRightText;
    private ImageView switchImageBtn;
    private ImageView searchImageBtn;
    private ImageView dividerView;
    private FileListContainer mListContainer;
    private KeyboardView keyboardView;
    private RelativeLayout emptyView;
    private ImageView emptyImage;
    private RecyclerView fileListView;
    private FileLinearListAdapter mFileLinearListAdapter;
    private FileGridListAdapter mFileGridListAdapter;
    private List<FileData> mDataList;
    private boolean isCurrentLinear = true;
    private boolean isKeyboardVisible = false;
    private static final int spanCount = 4;
    private String currentLeftTitle;
    private DiskData currentDiskData;
    private String currentPath = null;
    private Stack<String> pathStack;
    private Stack<Integer> beforePosition; //进入前上一级的data
    private Stack<List<FileData>> fileDataListStack;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    @Override
    public int getLayoutId() {
        return R.layout.activity_file_list;
    }

    @Override
    public void initView() {
        keyboardView = (KeyboardView) findViewById(R.id.file_keyboard_view);
        titleText = (TextView) findViewById(R.id.file_list_title_text);
        subtitleLeftText = (MarqueeTextView) findViewById(R.id.file_list_sub_title_left);
        subtitleRightText = (TextView) findViewById(R.id.file_list_sub_title_right);
        switchImageBtn = (ImageView) findViewById(R.id.file_list_switch_image);
        dividerView = (ImageView) findViewById(R.id.file_list_title_divider);
        searchImageBtn = (ImageView) findViewById(R.id.file_list_search_image);
        mListContainer = (FileListContainer) findViewById(R.id.file_list_container);
        fileListView = (RecyclerView) findViewById(R.id.file_list_view);
        emptyView = (RelativeLayout) findViewById(R.id.media_empty_layout);
        emptyImage = (ImageView) findViewById(R.id.media_empty_image);
        keyboardView.setEnabled(false);
        changeLeftImageBtnVisible(false);
        fileListView.setHasFixedSize(true);
    }

    @Override
    public void initData() {
        pathStack = new Stack<>();
        fileDataListStack = new Stack<>();
        beforePosition = new Stack<>();
        currentDiskData = getIntent().getParcelableExtra("disk");
        currentPath = currentDiskData.getPath();
        pathStack.push(currentPath);
        titleText.setText(currentDiskData.getName());
        mDataList = getIntent().getParcelableArrayListExtra(currentPath);
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            fileDataListStack.push(new ArrayList<>(mDataList));
            changeEmptyStatus(mDataList, false);
            subtitleRightText.setText(getString(R.string.str_file_list_count, mDataList.size()));
        }
        mFileLinearListAdapter = new FileLinearListAdapter(this, mDataList);
        mFileGridListAdapter = new FileGridListAdapter(this, mDataList);
        setLinearAdapter();
        if (mDataList == null || mDataList.size() == 0) {
            updateDataList(currentDiskData.getPath(), true);
        }
    }

    @Override
    public void initFocus() {
        fileListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull final View view) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.requestFocus();
                    }
                }, 200);
                mListContainer.setNewFocus(view);
                fileListView.removeOnChildAttachStateChangeListener(this);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    @Override
    public void addListener() {
        mFileLinearListAdapter.setOnItemFocusListener(this);
        mFileGridListAdapter.setOnItemFocusListener(this);
        mFileLinearListAdapter.setOnItemClickListener(this);
        mFileGridListAdapter.setOnItemClickListener(this);
        switchImageBtn.setOnClickListener(this);
        searchImageBtn.setOnClickListener(this);
        keyboardView.setOnTextChangeListener(this);
    }

    @Override
    public void handleDiskIntent(Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)
                || Intent.ACTION_MEDIA_EJECT.equals(action)
                || Intent.ACTION_MEDIA_REMOVED.equals(action)) {
            if (intent.getData().getPath().equals(currentDiskData.getPath())) {
                finish();
            }
        }
    }

    @Override
    public void handleUpdate(String type) {
        if (type.equals(Constants.ALL_REFRESH_ACTION)
                || type.equals(Constants.PATH_DELETE_ACTION)) {
            updateDataList(currentPath, false);
        }
    }

    @Override
    public void blockFocus() {

    }

    @Override
    public void releaseFocus() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_list_switch_image:
                isCurrentLinear = !isCurrentLinear;
                changeListLayout(isCurrentLinear);
                break;
            case R.id.file_list_search_image:
                isKeyboardVisible = !isKeyboardVisible;
                changeKeyboardStatus(isKeyboardVisible);
                break;
        }
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, BaseData data) {
        if (pathStack.size() > 0) {
            if (data.getPath().contains(pathStack.peek())) {
                currentLeftTitle = getPathDescription(data.getPath());
                if (!isKeyboardVisible) subtitleLeftText.setText(currentLeftTitle);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardVisible) {
            isKeyboardVisible = false;
            changeKeyboardStatus(false);
        } else {
            if (pathStack.pop().equals(currentDiskData.getPath())) {
                super.onBackPressed();
            } else {
                fileDataListStack.pop();
                refreshList(fileDataListStack.peek());
                beforeRefocus();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ExecutorUtil.shutdownAndAwaitTermination(mExecutorService);
        mFileLinearListAdapter.release();
    }

    //上一级重新获取焦点
    private void beforeRefocus() {
        final int position = beforePosition.pop();
        fileListView.scrollToPosition(position);
        fileListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                synchronized (FileListActivity.class) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) fileListView.getLayoutManager();
                    if (layoutManager != null) {
                        int currentPosition = layoutManager.getPosition(view);
                        if (currentPosition == position) {
                            view.requestFocus();
                            adjustFlyBoardView(0);
                            fileListView.removeOnChildAttachStateChangeListener(this);
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });
    }

    private void refreshList(List<FileData> fileDataList) {
        mDataList.clear();
        mDataList.addAll(fileDataList);
        if (isCurrentLinear) {
            fileListView.setAdapter(mFileLinearListAdapter);
            mFileLinearListAdapter.notifyDataSetChanged();
        } else {
            fileListView.setAdapter(mFileGridListAdapter);
            mFileGridListAdapter.notifyDataSetChanged();
        }
        if (isKeyboardVisible) {
            subtitleLeftText.setText(getString(R.string.str_file_list_count, mDataList.size()));
        }
        subtitleRightText.setText(getString(R.string.str_file_list_count, mDataList.size()));
        changeEmptyStatus(mDataList, false);
    }

    private void updateDataList(final String path, final boolean needPush) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                mDataList.clear();
                mDataList.addAll(FileDataManager.getInstance(FileListActivity.this).getPathFileDataWithOutSize(path));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshList();
                        if (needPush) {
                            fileDataListStack.push(new ArrayList<>(mDataList));
                        }
                    }
                });
            }
        });
    }

    private void refreshList() {
        if (isCurrentLinear) {
            fileListView.setAdapter(mFileLinearListAdapter);
            mFileLinearListAdapter.notifyDataSetChanged();
        } else {
            fileListView.setAdapter(mFileGridListAdapter);
            mFileGridListAdapter.notifyDataSetChanged();
        }
        changeEmptyStatus(mDataList, false);
        if (isKeyboardVisible) {
            subtitleLeftText.setText(getString(R.string.str_file_list_count, mDataList.size()));
        }
        subtitleRightText.setText(getString(R.string.str_file_list_count, mDataList.size()));
    }

    private void changeKeyboardStatus(boolean isVisible) {
        changeTitle(isVisible);
        changeRightMargin(isVisible);
        setLinearContentVisible(!isVisible);
        changeLeftImageBtnVisible(!isVisible);
        adjustFlyBoardView(300);//重新调整飞框
        startKeyboardAnimation(isVisible);
        if (!isVisible) updateDataList(currentPath, false);
        clearKeyboard();
    }

    private void startKeyboardAnimation(final boolean isVisible) {
        ValueAnimator widthAnimator;
        if (isVisible) {
            keyboardView.setEnabled(true);
            widthAnimator = ValueAnimator.ofFloat(1, 0).setDuration(300);
        } else {
            keyboardView.setEnabled(false);
            widthAnimator = ValueAnimator.ofFloat(0, 1).setDuration(300);
        }
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) keyboardView.getLayoutParams();
                layoutParams.leftMargin = -(int) (DestinyUtil.dp2px(FileListActivity.this, 413.3f)
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
            subtitleLeftText.setText(getString(R.string.str_file_list_count, mDataList.size()));
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
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) fileListView.getLayoutParams();
            recyclerLayoutParam.rightMargin = 0;
            recyclerLayoutParam.addRule(RelativeLayout.ALIGN_LEFT, dividerView.getId());
            if (!isCurrentLinear) fileListView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            RelativeLayout.LayoutParams layoutParam = (RelativeLayout.LayoutParams) mListContainer.getLayoutParams();
            layoutParam.rightMargin = DestinyUtil.dp2px(this, 148);
            layoutParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mListContainer.requestLayout();
            RelativeLayout.LayoutParams recyclerLayoutParam = (RelativeLayout.LayoutParams) fileListView.getLayoutParams();
            recyclerLayoutParam.removeRule(RelativeLayout.ALIGN_LEFT);
            recyclerLayoutParam.rightMargin = DestinyUtil.dp2px(this, 13.3f);
            if (!isCurrentLinear) fileListView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }

    //设置列表文字
    private void setLinearContentVisible(boolean isVisible) {
        if (isCurrentLinear) {
            mFileLinearListAdapter.setItemContentVisible(isVisible);
        }
    }

    private void changeLeftImageBtnVisible(boolean isVisible) {
        if (!IS_NEED_LEFT_IMAGE) {
            switchImageBtn.setVisibility(View.GONE);
            searchImageBtn.setVisibility(View.GONE);
            return;
        }
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

    private void adjustFlyBoardView(int delayed) {
        mListContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                mListContainer.reAttachView();
            }
        }, delayed);
    }

    private void setLinearAdapter() {
        fileListView.setLayoutManager(new LinearLayoutManager(this));
        if (fileListView.getItemDecorationCount() == 0) {
            fileListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, -6.67f)));
        }
        fileListView.setAdapter(mFileLinearListAdapter);
    }

    private void setGridAdapter() {
        fileListView.setLayoutManager(new GridLayoutManager(this, spanCount));
        if (fileListView.getItemDecorationCount() == 0) {
            fileListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(this, 6.67f)));
        }
        fileListView.setAdapter(mFileGridListAdapter);
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
            mFileLinearListAdapter.setSpanText(null);
            mFileGridListAdapter.setSpanText(null);
            updateDataList(currentPath, false);
        } else {
            refreshListFromSearch(s);
        }
    }

    private void refreshListFromSearch(String s) {
        mDataList.clear();
        Iterator<FileData> fileDataIterator = mDataList.iterator();
        while (fileDataIterator.hasNext()) {
            FileData fileData = fileDataIterator.next();
            if (!fileData.getName().toUpperCase().contains(s.toUpperCase())) {
                fileDataIterator.remove();
            }
        }
        changeEmptyStatus(mDataList, true);
        if (isCurrentLinear) {
            fileListView.setAdapter(mFileLinearListAdapter);
            mFileLinearListAdapter.setSpanText(s);
            mFileLinearListAdapter.notifyDataSetChanged();
        } else {
            fileListView.setAdapter(mFileGridListAdapter);
            mFileGridListAdapter.setSpanText(s);
            mFileGridListAdapter.notifyDataSetChanged();
        }
        subtitleLeftText.setText(getString(R.string.str_file_list_count, mDataList.size()));
    }

    @Override
    public void onItemClick(View view) {
        if (view instanceof MediaLinearItemView) {
            MediaLinearItemView mediaLinearItemView = (MediaLinearItemView) view;
            BaseData data = mediaLinearItemView.getBaseData();
            if (data.getType() == Constants.FILE_TYPE_DIR) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) fileListView.getLayoutManager();
                if (linearLayoutManager != null) {
                    beforePosition.push(linearLayoutManager.getPosition(view));
                }
                currentPath = data.getPath();
                pathStack.push(currentPath);
                updateDataList(currentPath, true);
                //fileDataListStack.push(new ArrayList<>(mDataList));
                initFocus();
                //adjustFlyBoardView(100);
            }
            startMediaPlayer(data);
        } else if (view instanceof MediaGridItemView) {
            MediaGridItemView mediaGridItemView = (MediaGridItemView) view;
            BaseData data = mediaGridItemView.getBaseData();
            if (data.getType() == Constants.FILE_TYPE_DIR) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) fileListView.getLayoutManager();
                if (gridLayoutManager != null) {
                    beforePosition.push(gridLayoutManager.getPosition(view));
                }
                currentPath = data.getPath();
                pathStack.push(currentPath);
                updateDataList(currentPath, true);
                //fileDataListStack.push(new ArrayList<>(mDataList));
                initFocus();
                //adjustFlyBoardView(100);
            }
            startMediaPlayer(data);
        }
    }

    private void startMediaPlayer(BaseData data) {
        switch (data.getType()) {
            case Constants.FILE_TYPE_VIDEO:
                Intent videoIntent = new Intent(this, VideoPlayerActivity.class);
                videoIntent.putExtra(Constants.BUNDLE_PATH_KEY, data.getPath());
                videoIntent.putExtra(Constants.FROM_FOLDER, true);
                startActivity(videoIntent);
                break;
            case Constants.FILE_TYPE_MUSIC:
                Intent musicIntent = new Intent(this, MusicPlayerActivity.class);
                musicIntent.putExtra(Constants.BUNDLE_PATH_KEY, data.getPath());
                musicIntent.putExtra(Constants.FROM_FOLDER, true);
                startActivity(musicIntent);
                break;
            case Constants.FILE_TYPE_PICTURE:
                Intent pictureIntent = new Intent(this, ImagePlayerActivity.class);
                pictureIntent.putExtra(Constants.BUNDLE_PATH_KEY, data.getPath());
                pictureIntent.putExtra(Constants.FROM_FOLDER, true);
                startActivity(pictureIntent);
                break;
        }
    }

    private void installApk(String filePath) {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri contentUri = FileProvider.getUriForFile(
                this
                , "com.ktc.media.fileprovider"
                , apkFile);
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivity(intent);
    }
}
