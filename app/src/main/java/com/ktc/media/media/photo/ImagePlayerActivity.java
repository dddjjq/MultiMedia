//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.ktc.media.media.photo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.media.business.photo.GifDecoder;
import com.ktc.media.media.business.photo.MyInputStream;
import com.ktc.media.media.music.MusicPlayerActivity;
import com.ktc.media.media.util.PlaylistTool;
import com.ktc.media.media.util.ToastFactory;
import com.ktc.media.media.util.Tools;
import com.ktc.media.media.view.MediaControllerView;
import com.ktc.media.media.view.MessageDialog;
import com.ktc.media.media.view.OnListItemClickListener;
import com.ktc.media.media.view.PictureListDialog;
import com.ktc.media.menu.base.BaseMenuActivity;
import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.menu.holder.image.ImageMajorHolder;
import com.ktc.media.model.FileData;
import com.mstar.android.MDisplay;
import com.mstar.android.MDisplay.PanelMode;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImagePlayerActivity extends BaseMenuActivity implements OnClickListener
        , OnListItemClickListener, MediaControllerView.OnMediaEventRefreshListener {

    private static final String TAG = "ImagePlayerActivity";

    private static final String ACTION_CHANGE_SOURCE = "source.switch.from.storage";
    private static final String ACTION_MCAST_STATE_CHANGED = "com.mstar.android.mcast.MCAST_STATE_CHANGED";

    // Set transfer news value，Pictures are playing
    private static final int PPT_PLAYER = 0xb;

    private static final int PHOTO_3D = 0xd;

    private static final int PHOTO_DECODE_PROGRESS = 0xe;

    private static final int PHOTO_DECODE_FINISH = 0xf;

    private static final int INIT_THREAD = 0xbe;

    protected static final int PHOTO_PLAY_SETTING = 0x9;

    // Hidden article control
    private static final int HIDE_PLAYER_CONTROL = 0x10;
    private static final int PHOTO_NAME_UPDATE = 0x11;
    private static final int SHOW_TOAST = 0x12;

    // Article control default disappear time 3s
    private static final int DEFAULT_TIMEOUT = 3000;

    private static final int IMAGE_VIEW = 0;

    private static final int GIF_VIEW = 3;

    // the largest size of file can be decode
    // private static final long LARGEST_FILE_SIZE = 30 * 1024 * 1024;
    // the largest pix of photo can be decode successful
    private static final long UPPER_BOUND_PIX = 1920 * 8 * 1080 * 8;

    private static final double UPPER_BOUND_WIDTH_PIX = 1920.0f;

    private static final double UPPER_BOUND_HEIGHT_PIX = 1080.0f;

    private static final int mStep = 60;

    // Picture player all control container
    private ImagePlayerViewHolder mPhotoPlayerHolder;

    // all photo file
    private List<FileData> mPhotoFileList = new ArrayList<>();

    // Video buffer progress bar
    private MessageDialog mProgressDialog;

    // Disk pull plug monitor
    private DiskChangeReceiver mDiskChangeReceiver;

    private FileInputStream mFileInputStream = null;

    private InputStream is = null;

    // Key shielding switch
    private boolean mCanResponse = true;

    // Whether in the playing mode
    private boolean mPPTPlayer = false;

    // To determine which control for focus
    // focus by default in the play button

    // index in list
    private int mCurrentPosition = 0;
    public int mDelta = 0;

    // Picture enlarge or reduce The Times
    private float mZoomTimes = 1.0f;
    public float mMaxZoomInTimes = 2.4f;
    public float mMinZoomOutTimes = 0.4f;

    private float mRotateAngle = 0f;
    private int mRotateTimes = 0;

    // screen resolution
    private int mWindowResolutionWidth = 0;
    private int mWindowResolutionHeight = 0;

    private int mCurrentView = IMAGE_VIEW;

    // Image analytical thread
    private static Thread mThread = new Thread();

    private Thread mZoomThread = new Thread();

    private Thread mRotateThread = new Thread();

    private static BitmapFactory.Options options;

    private boolean isOnPause = false;

    private boolean isAnimationOpened = false;

    public static int slideTime = 3000;

    public static final int SLIDE_3S = 0;
    public static final int SLIDE_5S = 1;
    public static final int SLIDE_10S = 2;
    public static final int SLIDE_15S = 3;
    public static final int SLIDE_30S = 4;

    // playMode
    public static final int ORDER = 0;//顺序播放
    public static final int SINGE = 1;//单曲循环
    public static final int LIST = 2;//列表循环

    public static int currentPlayMode = LIST;

    private static final String LOCAL_MEDIA = "localMedia";
    private static final String IMAGE_PLAY_MODE = "imagePlayMode";

    private boolean firstShowPicture = true;

    protected boolean isDefaultPhoto = false;

    private boolean mWillExit = false;

    private boolean isErrorDialogShow = false;

    public boolean mIsSourceChange = false;

    private PictureListDialog mPictureListDialog;

    private boolean isFromFolder = false;

    // processing images play and pause
    @SuppressLint("all")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PPT_PLAYER: // Slide mode play pictures
                    if (mPPTPlayer) {
                        if (currentPlayMode == SINGE) {
                            moveNextOrPrevious(0);
                            break;
                        }
                        if (Constants.bPhotoSeamlessEnable
                                && !checkIfItIsGifPhoto(0)
                                && !checkIfItIsGifPhoto(1)) {
                            int position = mCurrentPosition + 1;
                            if (position >= mPhotoFileList.size()) {
                                if (currentPlayMode == LIST) {
                                    initParameterBeforeShowNextPhoto();
                                    mPhotoPlayerHolder.mSurfaceView.showNextPhoto(1);
                                    mHandler.removeMessages(PPT_PLAYER);
                                    mHandler.sendEmptyMessageDelayed(PPT_PLAYER, slideTime);
                                } else {
                                    stopPPTPlayer();
                                    finish();
                                }
                            } else {
                                initParameterBeforeShowNextPhoto();
                                mPhotoPlayerHolder.mSurfaceView.showNextPhoto(1);
                                mHandler.removeMessages(PPT_PLAYER);
                                mHandler.sendEmptyMessageDelayed(PPT_PLAYER, slideTime);
                            }
                        } else {
                            moveNextOrPrevious(1);
                            if (!checkIfItIsGifPhoto(0)) {
                                mHandler.removeMessages(PPT_PLAYER);
                                mHandler.sendEmptyMessageDelayed(PPT_PLAYER, slideTime);
                            }
                        }
                    }
                    break;
                case PHOTO_3D:
                    if (hasScaleOrRotate()) {
                        showToastAtBottom(getString(R.string.photo_scale_or_rotate_toast));
                        break;
                    }
                    break;
                case INIT_THREAD:
                    break;
                case PHOTO_DECODE_PROGRESS:
                    dismissProgressDialog();
                    break;
                case PHOTO_DECODE_FINISH:
                    dismissProgressDialog();
                    break;
                case HIDE_PLAYER_CONTROL:
                    hideController();
                    break;
                case PHOTO_PLAY_SETTING:
                    Bundle mBundle = msg.getData();
                    isAnimationOpened = mBundle.getBoolean("isOpen");
                    slideTime = mBundle.getInt("time");
                    SharedPreferences mShared = getSharedPreferences("photoPlayerInfo",
                            Context.MODE_PRIVATE);
                    Editor editor = mShared.edit();
                    editor.putBoolean("isAnimationOpened", isAnimationOpened);
                    editor.commit();
                    break;
                case Constants.HANDLE_MESSAGE_PLAYER_EXIT:
                    ImagePlayerActivity.this.finish();
                    break;
                case PHOTO_NAME_UPDATE:
                    mPhotoPlayerHolder.mMediaControllerView.setTitleText(mPhotoFileList.get(mCurrentPosition).getName());
                    break;
                case SHOW_TOAST:
                    showToast(getString(msg.arg1), Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }
    };

    // for 4K2K
    protected static boolean is4K2KMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "------onCreate ------");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        mHandler.sendEmptyMessage(PHOTO_DECODE_PROGRESS);
        String IntentPath = getIntent().getStringExtra(Constants.BUNDLE_PATH_KEY);
        isFromFolder = getIntent().getBooleanExtra(Constants.FROM_FOLDER, false);
        mPhotoPlayerHolder.mMediaControllerView.setPlayPauseStatus(true);
        // get all cache photo files
        if (IntentPath != null) {
            boolean isPlayListFile = IntentPath.toLowerCase().endsWith(".mplt");
            if (isPlayListFile) {
                PlaylistTool tool = new PlaylistTool();
                mPhotoFileList = tool.parsePlaylist(IntentPath);
                if (mPhotoFileList == null || mPhotoFileList.isEmpty()) {
                    Log.e(TAG, "onCreate: No Photo path in " + IntentPath, new RuntimeException("No Photo path!"));
                    this.finish();
                    return;
                }
                String usbNameTarget = "$USB_NAME";
                // get the real usb_name
                String usbName = Tools.getUSBNameFromPath(IntentPath);
                Log.i(TAG, "onCreate: usbName = " + usbName);
                // stringBuilder for log
                StringBuilder stringBuilder = new StringBuilder("Play list path:[\n");
                // Replace the real USB name to "$USB_NAME"
                for (FileData data : mPhotoFileList) {
                    String path = data.getPath();
                    if (path != null && path.contains(usbNameTarget) && usbName != null) {
                        path = path.replace(usbNameTarget, usbName);
                        data.setPath(path);
                    }
                    stringBuilder.append(path);
                    stringBuilder.append("\n");
                }
                stringBuilder.append("]");
                Log.i(TAG, "onCreate: mPhotoFileList path: " + stringBuilder.toString());
            } else {
                FileData fd = new FileData();
                fd.setPath(IntentPath);
                initPictureList(fd);
            }
        } else {
            mPhotoFileList = FileDataManager.getInstance(this).getAllPictureData();
        }
        if (mPhotoFileList == null || mPhotoFileList.get(mCurrentPosition) == null) {
            return;
        }
        mPhotoPlayerHolder.mMediaControllerView.setTitleText(mPhotoFileList.get(mCurrentPosition).getName());
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        mWindowResolutionWidth = point.x; // display.getWidth();
        mWindowResolutionHeight = point.y;
        Log.i(TAG, "mWindowResolutionWidth:" + mWindowResolutionWidth + " mWindowResolutionHeight:" + mWindowResolutionHeight);
        mPhotoPlayerHolder.mMediaControllerView.setOnControllerClickListener(mControllerClickListener);
        mPhotoPlayerHolder.mMediaControllerView.setOnMediaEventRefreshListener(this);
        mPhotoPlayerHolder.mSurfaceView.setOnClickListener(this);
        // switch source monitor
        IntentFilter sourceChange = new IntentFilter(ACTION_CHANGE_SOURCE);
        this.registerReceiver(mSourceChangeReceiver, sourceChange);
        IntentFilter castStateChangeIntentFilter = new IntentFilter(ACTION_MCAST_STATE_CHANGED);
        this.registerReceiver(mCastStateChangeReceiver, castStateChangeIntentFilter);
        Constants.bPhotoSeamlessEnable = Tools.isPhotoStreamlessModeOn();
        if (Tools.getTotalMem() < 512 * 1024) {
            Constants.bSupportPhotoScale = false;
        }
        SharedPreferences mShared = getSharedPreferences("photoPlayerInfo", Context.MODE_PRIVATE);
        isAnimationOpened = mShared.getBoolean("isAnimationOpened", false);
        startToShowPhoto();
    }

    @Override
    public RelativeLayout getMainContainer() {
        return mPhotoPlayerHolder.mImagePlayerLayout;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_player;
    }

    @Override
    public void initView() {
        findView();
    }

    @Override
    public BaseEntityHolder getBaseEntityHolder() {
        return new ImageMajorHolder(this, mPhotoFileList.get(mCurrentPosition));
    }

    @Override
    public void setAllViewCanFocus() {
        showController(false);
        hideControlDelay();
        mPhotoPlayerHolder.mMediaControllerView.setSettingFocus();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "********onDestroy*******");
        dismissProgressDialog();
        stopPPTPlayer();
        unregisterReceiver(mSourceChangeReceiver);
        unregisterReceiver(mCastStateChangeReceiver);
        Constants.isExit = true;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "---------- onStop ---------");
        mWillExit = true;
        if (mCurrentView == IMAGE_VIEW) {
            mPhotoPlayerHolder.mSurfaceView.stopPlayback(true);
        }
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "********onPause*******" + Constants.isExit);
        isOnPause = true;
        if (mThread.isAlive() && options != null) {
            options.requestCancelDecode();
        }

        // Began to disk scan
        new Thread(new Runnable() {
            @Override
            public void run() {
                Tools.startMediascanner(ImagePlayerActivity.this);
            }
        }).start();
        unregisterReceiver(mDiskChangeReceiver);
        // Close file resources
        closeSilently(mFileInputStream);
        closeSilently(is);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "********onResume*******" + Constants.isExit);
        isOnPause = false;
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        mDiskChangeReceiver = new DiskChangeReceiver();
        registerReceiver(mDiskChangeReceiver, filter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Tools.stopMediascanner(ImagePlayerActivity.this);
            }
        }).start();
        if (firstShowPicture) {
            firstShowPicture = false;
            int count = mPhotoFileList.size();
            if (count == 0) {
                finish();
                return;
            } else if (count <= mCurrentPosition) {
                mCurrentPosition = count - 1;
            }
            Constants.isExit = false;
        } else {
            showController(true);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!mPhotoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                showController(false);
                hideControlDelay();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        Log.i(TAG, "********onTouchEvent*******" + event.getAction());
        return super.onTouchEvent(event);
    }

    private void initPictureList(FileData fileData) {
        if (!isFromFolder) {
            mPhotoFileList.clear();
            mPhotoFileList.addAll(FileDataManager.getInstance(this).getAllPictureData());
        } else {
            mPhotoFileList.clear();
            mPhotoFileList.addAll(FileDataManager.getInstance(this).getAllPictureData(fileData.getPath()));
        }
        for (FileData fd : mPhotoFileList) {
            if (fileData.getPath().equals(fd.getPath())) {
                mCurrentPosition = mPhotoFileList.indexOf(fd);
            }
        }
    }

    public void startShowProgress() {
        mHandler.sendEmptyMessage(PHOTO_DECODE_PROGRESS);
    }

    public void stopShowingProgress() {
        mHandler.sendEmptyMessage(PHOTO_DECODE_FINISH);
    }

    private void startToShowPhoto() {
        String url = mPhotoFileList.get(mCurrentPosition).getPath();
        url = Tools.fixPath(url);
        boolean bgif = url.substring(url.lastIndexOf(".") + 1).equalsIgnoreCase(Constants.GIF);
        if (bgif) {
            decodeGif(url);
            mCurrentView = GIF_VIEW;
        } else {
            mCurrentView = IMAGE_VIEW;
            mPhotoPlayerHolder.mSurfaceView.setHandler(mHandler);
            mPhotoPlayerHolder.mSurfaceView.setImagePath(url, ImagePlayerActivity.this);
        }
    }

    public void onError(MediaPlayer mp, int framework_err, int impl_err) {
        if (!mWillExit) {
            showToastAtCenter(getString(R.string.picture_decode_failed));
        }
        int position = mCurrentPosition + 1;
        if (position >= mPhotoFileList.size() || mCurrentPosition < 0) {
            stopPPTPlayer();
            this.finish();
        } else {
            showTipDialog("framework_err:" + framework_err + " impl_err:" + impl_err);
            if (isErrorDialogShow) {
                stopPPTPlayer();
            }
        }

    }

    public void showTipDialog(String msg) {
        String sName = mPhotoFileList.get(mCurrentPosition).getName();
        String showTip = sName + " " + msg + "!\n" + getResources().getString(R.string.photo_media_play_next);
        showErrorDialog(showTip);
    }

    // Pop up display an error dialog box
    private void showErrorDialog(final String strMessage) {
        dismissProgressDialog();
        // Prevent activity died when the popup menu
        if (!isFinishing()) {
            MessageDialog messageDialog = new MessageDialog(this, R.style.MessageDialog);
            MessageDialog.Builder builder = new MessageDialog.Builder(messageDialog);
            builder.setMessageText(getResources().getString(R.string.show_info))
                    .setIsLoading(false)
                    .setDialogSize(500, 300)
                    .setContentText(strMessage)
                    .setButtonClickListener(new MessageDialog.OnDialogButtonClickListener() {
                        @Override
                        public void onNegativeClick() {
                            isErrorDialogShow = false;
                            ImagePlayerActivity.this.finish();
                        }

                        @Override
                        public void onPositiveClick() {
                            isErrorDialogShow = false;
                            moveNextOrPrevious(1);
                        }
                    });
            messageDialog.show();
            isErrorDialogShow = true;
        }
    }

    /**
     * show module
     */
    private void findView() {
        mPhotoPlayerHolder = new ImagePlayerViewHolder(this);
        mPhotoPlayerHolder.findViews();
        showController(true);
        hideControlDelay();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if (isPhotoParsing()) {
                return true;
            }
            if (mPPTPlayer) {
                stopPPTPlayer();
            }
            if (currentPlayMode == SINGE) {
                moveNextOrPrevious(0);
                return true;
            }
            if (Constants.bPhotoSeamlessEnable
                    && !checkIfItIsGifPhoto(0)
                    && !checkIfItIsGifPhoto(-1)) {
                initParameterBeforeShowNextPhoto();
                mPhotoPlayerHolder.mSurfaceView.showNextPhoto(-1);
            } else {
                moveNextOrPrevious(-1);
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            if (isPhotoParsing()) {
                return true;
            }
            if (mPPTPlayer) {
                stopPPTPlayer();
            }
            if (currentPlayMode == SINGE) {
                moveNextOrPrevious(0);
                return true;
            }
            if (Constants.bPhotoSeamlessEnable
                    && !checkIfItIsGifPhoto(0)
                    && !checkIfItIsGifPhoto(1)) {
                initParameterBeforeShowNextPhoto();
                mPhotoPlayerHolder.mSurfaceView.showNextPhoto(1);
            } else {
                moveNextOrPrevious(1);
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!isMenuShow()
                    && !mPhotoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                showListDialog();
                return true;
            }
        }
        if (!isMenuShow()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    cancelDelayHide();
                    hideControlDelay();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    cancelDelayHide();
                    hideControlDelay();
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    Log.i(TAG, "onKeyDown: KEYCODE_ENTER");
                    cancelDelayHide();
                    hideControlDelay();
                    break;
                default:
                    Log.i(TAG, "onKeyDown: default");
                    break;
            }
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isMenuShow()) {
                return super.onKeyDown(keyCode, event);
            }
            if (isPhotoParsing()) {
                return true;
            }
            if (mPhotoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                hideController();
                return true;
            }
            mWillExit = true;
            if (is4K2KMode) {
                hideController();
                is4K2KMode = false;
                MDisplay.setPanelMode(PanelMode.E_PANELMODE_NORMAL);
                this.finish();
            }
            mHandler.removeMessages(Constants.HANDLE_MESSAGE_PLAYER_EXIT);
            if (mThread.isAlive()) {
                mHandler.sendEmptyMessageDelayed(Constants.HANDLE_MESSAGE_PLAYER_EXIT, 1000);
            } else {
                mHandler.sendEmptyMessage(Constants.HANDLE_MESSAGE_PLAYER_EXIT);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyUp" + keyCode);
        if (!mCanResponse) {
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (!mPhotoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    showController(false);
                    hideControlDelay();
                }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                if (!mPPTPlayer) {
                    PlayProcess();
                    mPhotoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                }
                return true;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                if (mPPTPlayer) {
                    stopPPTPlayer();
                    mPhotoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                }
            case KeyEvent.KEYCODE_MEDIA_STOP:
                mPhotoFileList.clear();
                finish();
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (mPPTPlayer) {
                    stopPPTPlayer();
                    mPhotoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                    hideControlDelay();
                } else {
                    PlayProcess();
                    mPhotoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                    hideControlDelay();
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                if (!mPhotoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    showController(false);
                    hideControlDelay();
                } else {
                    hideController();
                }
                return true;
            default:
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void showListDialog() {
        mPictureListDialog = new PictureListDialog(this, R.style.MediaDialog);
        Window window = mPictureListDialog.getWindow();
        window.setWindowAnimations(R.style.MediaDialog);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0;
        window.setAttributes(lp);
        mPictureListDialog.prepareData(mPhotoFileList, mCurrentPosition);
        mPictureListDialog.setOnListItemClickListener(this);
        mPictureListDialog.show();
    }

    private void hideListDialog() {
        if (mPictureListDialog != null
                && mPictureListDialog.isShowing()) {
            mPictureListDialog.dismiss();
        }
    }

    private void listDialogSkipToNextOrBefore(int delta) {
        if (mPictureListDialog != null && mPictureListDialog.isShowing()) {
            mPictureListDialog.skipToNextOrBeforeThumb(delta);
        }
    }

    private void PlayProcess() {
        if (mPPTPlayer) {
            // If is playing.
            stopPPTPlayer();
        } else {
            // Determine whether the current for the last one
            if ((mCurrentPosition + 1) < mPhotoFileList.size()) {
                mPPTPlayer = true;
                hideControlDelay();
                mHandler.sendEmptyMessage(PPT_PLAYER);
            } else if (mCurrentPosition == mPhotoFileList.size() - 1) {
                mPPTPlayer = true;
                mHandler.sendEmptyMessage(PPT_PLAYER);
            }
        }
        mPhotoPlayerHolder.mMediaControllerView.setPlayPauseStatus(false);
    }

    private void zoomIn() {
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
        if (mZoomTimes >= mMaxZoomInTimes) {
            showToast(getString(R.string.max_tip), 500);
            return;
        }
        if (mZoomThread.isAlive()) {
            showToast(getString(R.string.photo_zooming), 500);
            return;
        }
        mZoomTimes += 0.2;
        mZoomThread = new Thread(new Runnable() {
            public void run() {
                mPhotoPlayerHolder.mSurfaceView.scaleImage(mRotateAngle, mZoomTimes);
            }
        });
        mZoomThread.start();
    }

    private void zoomOut() {
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
        if (mZoomTimes < mMinZoomOutTimes) {
            showToast(getString(R.string.min_tip), 500);
            return;
        }
        if (mZoomThread.isAlive()) {
            showToast(getString(R.string.photo_zooming), 500);
            return;
        }
        mZoomTimes -= 0.2;
        mZoomThread = new Thread(new Runnable() {
            public void run() {
                mPhotoPlayerHolder.mSurfaceView.scaleImage(mRotateAngle, mZoomTimes);
            }
        });
        mZoomThread.start();
    }

    private void rotateImageLeft() {
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
        if (mRotateAngle == -360)
            mRotateAngle = 0;
        mRotateAngle -= 90;
        if (mRotateThread.isAlive()) {
            showToast(getString(R.string.photo_rotating), 500);
            return;
        }
        mRotateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mPhotoPlayerHolder.mSurfaceView.rotateImage(mRotateAngle, mZoomTimes);
            }
        });
        mRotateThread.start();
    }

    private void rotateImageRight() {
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
        if (mRotateAngle == 360)
            mRotateAngle = 0;
        mRotateAngle += 90;
        if (mRotateThread.isAlive()) {
            showToast(getString(R.string.photo_rotating), 500);
            return;
        }
        mRotateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mPhotoPlayerHolder.mSurfaceView.rotateImage(mRotateAngle, mZoomTimes);
            }
        });
        mRotateThread.start();
        if (mRotateTimes >= 4 || mRotateTimes <= -4) {
            mRotateTimes = 0;
        }
        mRotateTimes++;
        String angle;
        if (mRotateTimes > 0) {
            angle = mRotateTimes * 90 + "";
            showToastAtBottom(angle);
        } else {
            angle = mRotateTimes * 90 + "";
            showToastAtBottom(angle);
        }
    }

    private MediaControllerView.OnControllerClickListener mControllerClickListener =
            new MediaControllerView.OnControllerClickListener() {

                @Override
                public void onPreClick() {
                    if (!refreshController()) {
                        if (isPhotoParsing()) {
                            return;
                        }
                        cancelDelayHide();
                        if (mPPTPlayer) {
                            stopPPTPlayer();
                        }
                        if (currentPlayMode == SINGE) {
                            moveNextOrPrevious(0);
                            return;
                        }
                        if (Constants.bPhotoSeamlessEnable
                                && !checkIfItIsGifPhoto(0)
                                && !checkIfItIsGifPhoto(-1)) {
                            int position = mCurrentPosition - 1;
                            if (position <= -1) {
                                if (currentPlayMode == LIST) {
                                    initParameterBeforeShowNextPhoto();
                                    mPhotoPlayerHolder.mSurfaceView.showNextPhoto(-1);
                                } else {
                                    stopPPTPlayer();
                                    finish();
                                }
                            } else {
                                initParameterBeforeShowNextPhoto();
                                mPhotoPlayerHolder.mSurfaceView.showNextPhoto(-1);
                            }
                        } else {
                            moveNextOrPrevious(-1);
                        }
                    }
                }

                @Override
                public void onPlayPauseClick() {
                    if (!refreshController()) {
                        if (isPhotoParsing()) {
                            return;
                        }
                        cancelDelayHide();
                        PlayProcess();
                        hideControlDelay();
                        mPhotoPlayerHolder.mMediaControllerView.setPlayPauseStatus(!mPPTPlayer);
                    }
                }

                @Override
                public void onNextClick() {
                    if (!refreshController()) {
                        if (isPhotoParsing()) {
                            return;
                        }
                        cancelDelayHide();
                        if (mPPTPlayer) {
                            stopPPTPlayer();
                        }
                        if (currentPlayMode == SINGE) {
                            moveNextOrPrevious(0);
                            return;
                        }
                        if (Constants.bPhotoSeamlessEnable
                                && !checkIfItIsGifPhoto(0)
                                && !checkIfItIsGifPhoto(1)) {
                            int position = mCurrentPosition + 1;
                            if (position >= mPhotoFileList.size()) {
                                if (currentPlayMode == LIST) {
                                    initParameterBeforeShowNextPhoto();
                                    mPhotoPlayerHolder.mSurfaceView.showNextPhoto(1);
                                } else {
                                    stopPPTPlayer();
                                    finish();
                                }
                            } else {
                                initParameterBeforeShowNextPhoto();
                                mPhotoPlayerHolder.mSurfaceView.showNextPhoto(1);
                            }
                        } else {
                            moveNextOrPrevious(1);
                        }
                    }
                }

                @Override
                public void onZoomOutClick() {
                    if (!refreshController()) {
                        if (!Constants.bSupportPhotoScale) {
                            showToastAtBottom(getString(R.string.photo_not_support_scale));
                            return;
                        }
                        cancelDelayHide();
                        if (mCurrentView == IMAGE_VIEW) {
                            zoomIn();
                        } else {
                            showToastAtBottom(getString(R.string.photo_GIF_toast));
                        }
                        hideControlDelay();
                    }
                }

                @Override
                public void onZoomInClick() {
                    if (!refreshController()) {
                        if (!Constants.bSupportPhotoScale) {
                            showToastAtBottom(getString(R.string.photo_not_support_scale));
                            return;
                        }
                        cancelDelayHide();
                        if (mCurrentView == IMAGE_VIEW) {
                            zoomOut();
                        } else {
                            showToastAtBottom(getString(R.string.photo_GIF_toast));
                        }
                        hideControlDelay();
                    }
                }

                @Override
                public void onRotateClick() {
                    if (!refreshController()) {
                        cancelDelayHide();
                        if (mCurrentView == IMAGE_VIEW) {
                            rotateImageRight();
                        } else if (mCurrentView == GIF_VIEW) {
                            showToastAtBottom(getString(R.string.photo_GIF_toast));
                        } else {
                            showToastAtBottom(getString(R.string.photo_3D_toast));
                        }
                        hideControlDelay();
                    }
                }

                @Override
                public void onSettingClick() {
                    if (!refreshController()) {
                        startMenu();
                        hideController();
                    }
                }
            };

    @Override
    public void onClick(View v) {
        refreshController();
    }

    private boolean refreshController() {
        if (!mPhotoPlayerHolder.mMediaControllerView.mIsControllerShow) {
            showController(false);
            hideControlDelay();
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            return true;
        } else {
            hideControlDelay();
        }
        if (!mCanResponse) {
            return true;
        }
        return false;
    }

    private GifDecoder.IGifCallBack mGifCallBack = new GifDecoder.IGifCallBack() {
        public void onFrameIndexChanged(int index) {

        }

        public void onFinalFrame() {
            Log.d(TAG, "GifDecoder.IGifCallBack: onFinalFrame!!!");
            if (mPPTPlayer) {
                mHandler.sendEmptyMessageDelayed(PPT_PLAYER, 100);
            }
        }
    };

    private boolean isLargerThanLimit(BitmapFactory.Options options) {
        long pixSize = options.outWidth * options.outHeight;
        // largest pix is 1920 * 8 * 1080 * 8
        if (pixSize <= UPPER_BOUND_PIX) {
            return false;
        }
        return true;
    }

    private boolean isErrorPix(BitmapFactory.Options options) {
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            return true;
        }
        return false;
    }

    private void decodeGif(final String url) {
        File f = new File(url);
        if ((!f.exists()) && (!Tools.isNetPlayback(url))) {
            Log.e(TAG, "file not exists or not http url");
            return;
        }
        ImagePlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPosition < 0) {
                    mCurrentPosition = 0;
                }
                mPhotoPlayerHolder.mMediaControllerView.setTitleText(mPhotoFileList.get(mCurrentPosition).getName());
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isDecodeSuccess;
                if (!Tools.isNetPlayback(url)) {
                    isDecodeSuccess = mPhotoPlayerHolder.mSurfaceView.setSrc(url,
                            ImagePlayerActivity.this);
                } else {
                    isDecodeSuccess = mPhotoPlayerHolder.mSurfaceView.decodeBitmapFromNet(url,
                            ImagePlayerActivity.this);
                }
                if (isDecodeSuccess) {
                    mHandler.sendEmptyMessage(PHOTO_DECODE_FINISH);
                    if (mPhotoPlayerHolder.mSurfaceView.getFrameCount() > 1) {
                        mPhotoPlayerHolder.mSurfaceView.setStart(mGifCallBack);
                    } else {
                        if (mPPTPlayer) {
                            mHandler.removeMessages(PPT_PLAYER);
                            mHandler.sendEmptyMessageDelayed(PPT_PLAYER, slideTime);
                            listDialogSkipToNextOrBefore(1);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * decode local picture.
     */
    private Bitmap decodeBitmapFromLocal(final String imagePath) {
        // file no found
        if (!Tools.isFileExist(imagePath)) {
            mCanResponse = true;
            return null;
        }
        Bitmap bitmap = null;
        /* BitmapFactory.Options */
        options = new BitmapFactory.Options();
        try {
            closeSilently(mFileInputStream);
            mFileInputStream = new FileInputStream(imagePath);
            FileDescriptor fd = mFileInputStream.getFD();
            if (fd == null) {
                closeSilently(mFileInputStream);
                decodeBitmapFailed(R.string.picture_decode_failed);
                return null;
            }
            // Plug disk, the following must be set to false.
            options.inPurgeable = false;
            options.inInputShareable = true;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            Log.d(TAG, "options " + options.outHeight + " " + options.outWidth);

            is4K2KMode = false;

            if (isLargerThanLimit(options)) {
                closeSilently(mFileInputStream);
                mCanResponse = true;
                Log.d(TAG, "**show default photo**");
                return setDefaultPhoto();
            }
            if (isErrorPix(options)) {
                closeSilently(mFileInputStream);
                mCanResponse = true;
                if (!isOnPause)
                    decodeBitmapFailed(R.string.picture_decode_failed);
                return null;
            }
            // options.forceNoHWDoecode = false;
            // According to the 1920 * 1080 high-definition format picture as
            // the restriction condition
            options.inSampleSize = computeSampleSizeLarger(options.outWidth, options.outHeight);

            Log.d(TAG, "options.inSampleSize : " + options.inSampleSize);
            options.inJustDecodeBounds = false;
            if (fd != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
                // jpeg png gif use the open source third-party library，bmp is
                // decoded by skia
                // Open source third-party library have default exception
                // handling methods（In the exit will interrupt analytic，return
                // null
                if (bitmap != null) {
                    bitmap = resizeDownIfTooBig(bitmap, true);
                } else {
                    if (!isOnPause)
                        decodeBitmapFailed(R.string.picture_can_not_decode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.isExit) {
                return null;
            }
            try {
                closeSilently(mFileInputStream);
                mFileInputStream = new FileInputStream(imagePath);
                bitmap = BitmapFactory.decodeStream(mFileInputStream, null, options);
                if (bitmap == null) {
                    decodeBitmapFailed(R.string.picture_can_not_decode);
                    return setDefaultPhoto();
                }
            } catch (Exception error) {
                error.printStackTrace();
                decodeBitmapFailed(R.string.picture_can_not_decode);
                return setDefaultPhoto();
            } finally {
                closeSilently(mFileInputStream);
            }
        } finally {
            closeSilently(mFileInputStream);
        }
        mCanResponse = true;
        // ARGB_8888 is flexible and offers the best quality
        if (options != null && options.inPreferredConfig != Config.ARGB_8888) {
            return ensureGLCompatibleBitmap(bitmap);
        }
        return bitmap;
    }

    /**
     * decode net picture.
     */
    private Bitmap decodeBitmapFromNet(final String imagePath) {
        Bitmap bitmap = null;
        InputStream is = null;
        MyInputStream mIs = null;
        try {
            closeSilently(is);
            closeSilently(mIs);
            is = new URL(imagePath).openStream();
            if (is == null) {
                decodeBitmapFailed(R.string.picture_decode_failed);
                return null;
            }
            mIs = new MyInputStream(is, imagePath);
            options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(mIs, null, options);
            Log.d(TAG, "options " + options.outHeight + " " + options.outWidth);
            if (Constants.isExit) {
                return null;
            }
            // Test the image's resolution.
            if (isLargerThanLimit(options)) {
                closeSilently(is);
                closeSilently(mIs);
                mCanResponse = true;
                return setDefaultPhoto();
            }
            if (isErrorPix(options)) {
                closeSilently(is);
                closeSilently(mIs);
                mCanResponse = true;
                if (!isOnPause)
                    decodeBitmapFailed(R.string.picture_decode_failed);
                return null;
            }
            options.inSampleSize = computeSampleSizeLarger(options.outWidth, options.outHeight);
            options.inJustDecodeBounds = false;
            closeSilently(mIs);
            is = new URL(imagePath).openStream();
            if (is == null) {
                decodeBitmapFailed(R.string.picture_decode_failed);
                return null;
            }
            mIs = new MyInputStream(is, imagePath);
            Log.d(TAG, "mIs : " + mIs);
            Log.i(TAG, "*****mIs*******" + mIs.markSupported());
            bitmap = BitmapFactory.decodeStream(mIs, null, options);
            if (bitmap == null) {
                Log.d(TAG, "BitmapFactory.decodeStream return null");
                if (isOnPause) {
                    return null;
                }
                decodeBitmapFailed(R.string.picture_decode_failed);
            }
            closeSilently(is);
            closeSilently(mIs);
            mCanResponse = true;
            return bitmap;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException in decodeBitmap");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "IOException in decodeBitmap");
            e.printStackTrace();
        } finally {
            closeSilently(is);
            closeSilently(mIs);
        }
        if (Constants.isExit) {
            return null;
        }
        decodeBitmapFailed(R.string.picture_decode_failed);
        return null;
    }

    /**
     * Decode bitmap from local path or HTTP URL.
     *
     * @param url path of image.
     * @return bitmap with the specified URL or null.
     */
    public Bitmap decodeBitmap(final String url) {
        Log.d(TAG, "decodeBitmap, url : " + url);
        mCanResponse = false;
        isDefaultPhoto = false;
        if (Tools.isNetPlayback(url)) {
            return decodeBitmapFromNet(url);
        } else {
            return decodeBitmapFromLocal(url);
        }
    }

    public Bitmap setDefaultPhoto() {
        return BitmapFactory.decodeResource(getResources(), 0);
    }

    public void showToastAtCenter(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    private void showToastAtBottom(String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    private void showToast(final String text, int duration) {
        ToastFactory.showToast(ImagePlayerActivity.this, text, duration);
    }

    private void closeSilently(final Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // Resize the bitmap if each side is >= targetSize * 2
    private Bitmap resizeDownIfTooBig(Bitmap bitmap, boolean recycle) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float widthScale = (float) mWindowResolutionWidth / srcWidth;
        float heightScale = (float) mWindowResolutionHeight / srcHeight;
        Log.d(TAG, "srcWidth : " + srcWidth + " srcHeight : " + srcHeight + " widthScale : " + widthScale + " heightScale:" + heightScale);
        return resizeBitmapByScale(bitmap, widthScale, heightScale, recycle);
    }

    private Bitmap resizeBitmapByScale(Bitmap bitmap, float widthScale, float heightScale, boolean recycle) {
        int width = Math.round(bitmap.getWidth() * widthScale);
        int height = Math.round(bitmap.getHeight() * heightScale);
        if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
            return bitmap;
        }
        Bitmap target = Bitmap.createBitmap(width, height, getConfig(bitmap));
        Canvas canvas = new Canvas(target);
        canvas.scale(widthScale, heightScale);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (recycle) {
            bitmap.recycle();
        }
        return target;
    }

    private Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    // This function should not be called directly from
    // DecodeUtils.requestDecode(...), since we don't have the knowledge
    // if the bitmap will be uploaded to GL.
    private Bitmap ensureGLCompatibleBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        if (bitmap.getConfig() != null) {
            return bitmap;
        }
        Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, false);
        bitmap.recycle();
        System.gc();
        return newBitmap;
    }

    // This computes a sample size which makes the longer side at least
    // minSideLength long. If that's not possible, return 1.
    private int computeSampleSizeLarger(double w, double h) {
        double initialSize = Math.max(w / UPPER_BOUND_WIDTH_PIX, h / UPPER_BOUND_HEIGHT_PIX);
        if (initialSize <= 2.0f) {
            return 1;
        } else if (initialSize < 4.0f) {
            return 2;
        } else if (initialSize < 8.0f) {
            return 4;
        } else {
            return 8;
        }
    }

    // Returns the previous power of two.
    // Returns the input if it is already power of 2.
    // Throws IllegalArgumentException if the input is <= 0
    @SuppressWarnings("unused")
    private int prevPowerOf2(int n) {
        if (n <= 0)
            throw new IllegalArgumentException();
        return Integer.highestOneBit(n);
    }

    /**
     * Show the pictures there is detailed information
     */
    private void showPhotoInfo() {
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
        Log.d(TAG, "mCurrentPosition : " + mCurrentPosition + " size : " + mPhotoFileList.size());
    }

    public void setCurrentPos(int delta) {
        int position = mCurrentPosition + delta;
        if (position <= -1) {
            position = mPhotoFileList.size() - 1;
        } else if (position >= mPhotoFileList.size()) {
            position = 0;
        }
        mCurrentPosition = position;
        mHandler.sendEmptyMessage(PHOTO_NAME_UPDATE);
        listDialogSkipToNextOrBefore(delta);
    }

    public String getNextPhotoPath(int delta) {
        int position = mCurrentPosition + delta;
        if (mPhotoFileList.size() == 0)
            return null;
        if (position <= -1) {
            position = mPhotoFileList.size() - 1;
        } else if (position >= mPhotoFileList.size()) {
            position = 0;
        }
        return mPhotoFileList.get(position).getPath();
    }

    public boolean checkIfItIsGifPhoto(final int delta) {
        int position = mCurrentPosition + delta;
        if (position <= -1) {
            position = mPhotoFileList.size() - 1;
        } else if (position >= mPhotoFileList.size()) {
            position = 0;
        }
        String url = mPhotoFileList.get(position).getPath();
        url = Tools.fixPath(url);
        if (url.substring(url.lastIndexOf(".") + 1).equalsIgnoreCase(Constants.GIF)) {
            return true;
        }
        return false;
    }

    // Init relative parameter before call "showNextPhoto" function in photo seamless playback .
    public void initParameterBeforeShowNextPhoto() {
        mZoomTimes = 1.0f;
        mRotateAngle = 0f;
        mRotateTimes = 0;
    }

    /**
     * play next or previous picture.
     */
    public void moveNextOrPrevious(final int delta) {
        hideListDialog();
        mPhotoPlayerHolder.mSurfaceView.setStop();
        int position = mCurrentPosition + delta;
        if (position <= -1) {
            position = mPhotoFileList.size() - 1;
        } else if (position >= mPhotoFileList.size()) {
            if (!(currentPlayMode == LIST)) {
                stopPPTPlayer();
                finish();
                return;
            } else {
                position = 0;
            }
        }
        initParameterBeforeShowNextPhoto();
        String url = mPhotoFileList.get(position).getPath();
        url = Tools.fixPath(url);
        if (url.substring(url.lastIndexOf(".") + 1).equalsIgnoreCase(Constants.GIF)) {
            mPhotoPlayerHolder.mSurfaceView.stopPlayback(false);
            decodeGif(url);
            mCurrentPosition = position;
            mCurrentView = GIF_VIEW;
        } else {
            mCurrentView = IMAGE_VIEW;
            mCurrentPosition = position;
            if (!mPhotoPlayerHolder.mSurfaceView
                    .startNextVideo(url, ImagePlayerActivity.this)) {
                showToastAtCenter(getString(R.string.busy_tip));
            } else {
                if (mPPTPlayer) {
                    listDialogSkipToNextOrBefore(1);
                }
            }
        }
        mPhotoPlayerHolder.mMediaControllerView.setTitleText(mPhotoFileList.get(mCurrentPosition).getName());
    }

    private void processPlayMode(boolean isNext) {
        switch (currentPlayMode) {
            case ORDER:
                if (isNext) {
                    if (mCurrentPosition < mPhotoFileList.size() - 1) {
                        mDelta = 1;
                    }
                } else {
                    if (mPhotoFileList.size() > 0) {
                        if (mCurrentPosition - 1 >= 0) {
                            mDelta = -1;
                        }
                    }
                }
                break;
            case SINGE:
                mDelta = 0;
                break;
            case LIST:
                if (isNext) {
                    mDelta = 1;
                } else {
                    if (mPhotoFileList.size() > 0) {
                        mDelta = -1;
                    }
                }
                break;
        }
    }

    public void changePlayMode(int playMode) {
        currentPlayMode = playMode;
        setPlayMode(playMode);
    }

    private void setPlayMode(int mode) {
        SharedPreferences preference = getSharedPreferences(LOCAL_MEDIA, MusicPlayerActivity.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putInt(IMAGE_PLAY_MODE, mode);
        editor.apply();
    }

    public int getPlayMode() {
        SharedPreferences preference = getSharedPreferences(LOCAL_MEDIA, MusicPlayerActivity.MODE_PRIVATE);
        return preference.getInt(IMAGE_PLAY_MODE, LIST);
    }

    /**
     * start play slide.
     */
    public void startPPT_Player() {
        if (mPPTPlayer) {
            mHandler.sendEmptyMessageDelayed(PPT_PLAYER, slideTime);
        }
    }

    /**
     * stop play slide.
     */
    private void stopPPTPlayer() {
        if (!mPPTPlayer) {
            return;
        }
        mPPTPlayer = false;// stop cycling.
        mHandler.removeMessages(PPT_PLAYER);
        mPhotoPlayerHolder.mMediaControllerView.setPlayPauseStatus(true);
    }

    /**
     * exit player after Mhl event callback.
     */
    private void exitPlayer() {
        mPhotoPlayerHolder.mSurfaceView.stopPlayback(false);
        if (mPPTPlayer) {
            stopPPTPlayer();
        }
    }

    /**
     * Display buffer progress.
     *
     * @param id
     */
    private void showProgressDialog(int id) {
        if (!isFinishing()) {
            mProgressDialog = new MessageDialog(this, R.style.MessageDialog);
            MessageDialog.Builder builder = new MessageDialog.Builder(mProgressDialog);
            builder.setMessageText(getResources().getString(id))
                    .setIsLoading(true)
                    .setDialogSize(500, 150);
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }
    }

    /**
     * hide buffer progress.
     */
    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            Log.i(TAG, "dismissProgressDialog");
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * show control
     */
    private void showController(boolean needRequest) {
        mPhotoPlayerHolder.mMediaControllerView.showController();
        if (needRequest) {
            mPhotoPlayerHolder.mMediaControllerView.requestFocus();
        }
    }

    /**
     * After how long hidden article control
     */
    public void hideControlDelay() {
        do {
            mHandler.removeMessages(ImagePlayerActivity.HIDE_PLAYER_CONTROL);
        } while (mHandler.hasMessages(ImagePlayerActivity.HIDE_PLAYER_CONTROL));
        mHandler.sendEmptyMessageDelayed(ImagePlayerActivity.HIDE_PLAYER_CONTROL, DEFAULT_TIMEOUT);
    }

    /**
     * Cancel time delay hidden.
     */
    private void cancelDelayHide() {
        mHandler.removeMessages(ImagePlayerActivity.HIDE_PLAYER_CONTROL);
    }

    /**
     * Hidden article control.
     */
    private void hideController() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        mPhotoPlayerHolder.mMediaControllerView.hideController();
    }

    private BroadcastReceiver mSourceChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "*******BroadcastReceiver**********" + intent.getAction());
            mIsSourceChange = true;
            mPhotoPlayerHolder.mSurfaceView.stopPlayback(false);
            ImagePlayerActivity.this.finish();
        }
    };

    private BroadcastReceiver mCastStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "------mcastStateChangeReceiver ---intent.getExtras(extraArg):" + intent.getStringExtra("extraArg"));
            if ("mairplay_playphoto".equalsIgnoreCase(intent.getStringExtra("extraArg"))) {
                mPhotoPlayerHolder.mSurfaceView.stopPlayback(true);
                ImagePlayerActivity.this.finish();
                Constants.isExit = true;
            }
        }
    };

    @Override
    public void onItemClick(View view, int position) {
        mCurrentPosition = position;
        if (checkIfItIsGifPhoto(0)) {
            moveNextOrPrevious(0);
        } else {
            initParameterBeforeShowNextPhoto();
            mPhotoPlayerHolder.mSurfaceView
                    .startNextVideo(Tools.fixPath(mPhotoFileList.get(position).getPath())
                            , ImagePlayerActivity.this);
        }
    }

    @Override
    public void onEventRefresh() {
        refreshController();
    }

    private class DiskChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String devicePath = intent.getDataString().substring(7);
            // Disk remove
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                if (mPhotoFileList.get(0).getPath().contains(devicePath)) {
                    showToastAtCenter(getString(R.string.disk_eject));
                    // Close file resources
                    closeSilently(mFileInputStream);
                    ImagePlayerActivity.this.finish();
                }
            }
        }
    }

    private void decodeBitmapFailed(final int id) {
        mCanResponse = true;
        ImagePlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastAtCenter(getString(id));
            }
        });
    }

    private boolean hasScaleOrRotate() {
        Log.i(TAG, "---- hasScaleOrRotate ---- mRotateAngle:" + mRotateAngle);
        if ((360 == mRotateAngle) || (-360 == mRotateAngle)) {
            mRotateAngle = 0;
        }
        if ((mRotateAngle == 0) && (mZoomTimes == 1.0f)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isPhotoParsing() {
        if (mPhotoPlayerHolder.mSurfaceView.getImagePlayerThread() != null) {
            if (mPhotoPlayerHolder.mSurfaceView.getImagePlayerThread().isAlive()) {
                if (mProgressDialog != null && !mProgressDialog.isShowing()) {
                    mProgressDialog.show();
                } else if (mProgressDialog == null) {
                    showProgressDialog(R.string.picture_decoding);
                }
                return true;
            }
        }
        return false;
    }

    public void changePPTSlideDuration(int durationType) {
        switch (durationType) {
            case SLIDE_3S:
                slideTime = 3000;
                break;
            case SLIDE_5S:
                slideTime = 5000;
                break;
            case SLIDE_10S:
                slideTime = 10000;
                break;
            case SLIDE_15S:
                slideTime = 15000;
                break;
            case SLIDE_30S:
                slideTime = 30000;
                break;
            default:
                slideTime = 3000;
                break;
        }
    }

    public int getPPTDurationTimeType() {
        switch (slideTime) {
            case 3000:
                return SLIDE_3S;
            case 5000:
                return SLIDE_5S;
            case 10000:
                return SLIDE_10S;
            case 15000:
                return SLIDE_15S;
            case 30000:
                return SLIDE_30S;
            default:
                return SLIDE_3S;
        }
    }
}
