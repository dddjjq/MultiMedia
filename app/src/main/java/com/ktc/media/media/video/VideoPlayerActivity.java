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

package com.ktc.media.media.video;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaScanner;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.ktc.media.R;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.media.business.video.BreakPointRecord;
import com.ktc.media.media.business.video.MediaError;
import com.ktc.media.media.business.video.VideoPlayView;
import com.ktc.media.media.music.MusicPlayerActivity;
import com.ktc.media.media.util.BasePlayListTool;
import com.ktc.media.media.util.ToastFactory;
import com.ktc.media.media.util.Tools;
import com.ktc.media.media.view.MediaControllerView;
import com.ktc.media.media.view.MessageDialog;
import com.ktc.media.media.view.OnListItemClickListener;
import com.ktc.media.media.view.VideoListDialog;
import com.ktc.media.menu.base.BaseMenuActivity;
import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.menu.holder.video.VideoEntityHolder;
import com.ktc.media.model.VideoData;
import com.mstar.android.MDisplay;
import com.mstar.android.media.MMediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class VideoPlayerActivity extends BaseMenuActivity implements OnListItemClickListener
        , View.OnClickListener, MediaControllerView.OnMediaEventRefreshListener {

    private static final String TAG = "VideoPlayerActivity";
    private static final String LOCAL_MEDIA_SERVICE = "LOCAL_MEDIA_SERVICE";
    // Video buffer progress bar
    private MessageDialog progressDialog;
    private MessageDialog breakPointDialog;
    // Video control bar to be automatic hidden time
    private static final int DEFAULT_TIMEOUT = 15 * 1000;
    private int playSpeed = 1;
    public static final int SEEK_POS = 14;
    private static final int IO_ERROR = 9000;
    public VideoPlayerViewHolder videoPlayerHolder;
    private boolean isPrepared = false;
    protected int video_position;
    private int currentPlayerPosition = 0;
    private int duration = 0;
    protected List<VideoData> mVideoPlayList = new ArrayList<>();
    // Video is in play
    public boolean isPlaying = true;
    private static int seekTimes = 0;
    private static boolean seekComplete = true;
    private int mSeekPosition = 0;
    private boolean isAudioSupport = true;
    private boolean isVideoSupport = true;
    private boolean isVideoNone = false;
    public int breakPoint;
    public boolean isBreakPointPlay = false;
    private static boolean isNeedBreakPointPlay = false;
    private boolean isResourceLost = false;
    private LocalMediaController mLocalMediaController = null;
    private String mNetUrlFrom = null;
    private Uri mNetVideoUri;
    private Map<String, String> mHeaders = null;
    private TaskThread mMediaPlayerTaskThread = null;

    // playMode
    private static final int ORDER = 0;//顺序播放
    private static final int RANDOM = 1;//随机播放
    private static final int SINGE = 2;//单曲循环
    private static final int LIST = 3;//列表循环

    public static int currentPlayMode = 3;
    private static final String LOCAL_MEDIA = "localMedia";
    private static final String VIDEO_PLAY_MODE = "videoPlayMode";
    private VideoListDialog videoListDialog;
    private boolean isFromFolder = false;

    @Override
    public void onItemClick(View view, int position) {
        video_position = position;
        doMoveToNextOrPrevious(0);
    }

    @Override
    public void onClick(View v) {
        refreshController();
    }

    @Override
    public void onEventRefresh() {
        refreshController();
    }

    public class errorStruct {
        protected String strMessage = "";

        // The default mode with error display
        protected boolean showStateWithError = true;

        errorStruct() {
            super();
            showStateWithError = true;
            strMessage = "";
        }
    }

    // Video processing related handler
    @SuppressLint("all")
    public Handler videoHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.HIDE_PLAYER_CONTROL:
                    hideController();
                    break;
                case SEEK_POS:
                    if (videoPlayerHolder.mVideoPlayView != null) {
                        refreshProgressBar();
                    }
                    break;
                case Constants.RefreshCurrentPositionStatusUI:
                    Log.i(TAG, "RefreshCurrentPositionStatusUI msg.obj:" + (String) msg.obj);
                    videoPlayerHolder.mMediaControllerView.setSeekBarProgress(currentPlayerPosition);
                    videoPlayerHolder.mMediaControllerView.setCurrentTime((String) msg.obj);
                    videoHandler.sendEmptyMessageDelayed(SEEK_POS, 500);
                    break;
                case Constants.SEEK_TIME:
                    videoPlayerHolder.mVideoPlayView.seekTo(mSeekPosition);
                    videoHandler.removeMessages(SEEK_POS);
                    videoHandler.sendEmptyMessageDelayed(SEEK_POS, 500);
                    videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(!videoPlayerHolder.mVideoPlayView.isPlaying());
                    seekTimes = 0;
                    break;
                case Constants.HANDLE_MESSAGE_PLAYER_EXIT:
                    VideoPlayerActivity.this.finish();
                    break;
                case Constants.HANDLE_MESSAGE_SKIP_BREAKPOINT:
                    if (breakPointDialog != null && breakPointDialog.isShowing() && !isFinishing()) {
                        breakPointDialog.dismiss();
                    }
                    break;
                case Constants.ShowController:
                    if (!videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                        showController();
                        hideControlDelay();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        showProgressDialog(R.string.buffering);
        String video_path;
        String IntentPath = getIntent().getStringExtra(Constants.BUNDLE_PATH_KEY);
        isFromFolder = getIntent().getBooleanExtra(Constants.FROM_FOLDER, false);
        Bundle bundle = getIntent().getBundleExtra("headers");
        mNetUrlFrom = getIntent().getStringExtra("URL_FROM");
        mNetVideoUri = getIntent().getData();
        if (bundle != null) {
            Set<String> keySet = bundle.keySet();
            Iterator<String> iter = keySet.iterator();
            mHeaders = new HashMap<>();
            while (iter.hasNext()) {
                String key = iter.next();
                mHeaders.put(key, bundle.getString(key));
            }
        }
        Log.i(TAG, "mNetUrlFrom:" + mNetUrlFrom + " IntentPath:" + IntentPath);
        if (IntentPath != null) {
            boolean isPlayListFile = IntentPath.toLowerCase().endsWith(".mplt");
            if (isPlayListFile) {
                BasePlayListTool tool = new BasePlayListTool<VideoData>();
                mVideoPlayList = tool.parsePlaylist(IntentPath);
                if (mVideoPlayList == null || mVideoPlayList.isEmpty()) {
                    Log.e(TAG, "onCreate: No video path in " + IntentPath, new RuntimeException("No video path!"));
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
                for (VideoData data : mVideoPlayList) {
                    String path = data.getPath();
                    if (path != null && path.contains(usbNameTarget) && usbName != null) {
                        path = path.replace(usbNameTarget, usbName);
                        data.setPath(path);
                    }
                    stringBuilder.append(path);
                    stringBuilder.append("\n");
                }
                stringBuilder.append("]");
                Log.i(TAG, "onCreate: mVideoPlayList path: " + stringBuilder.toString());
            } else {
                VideoData videoData = new VideoData();
                videoData.setPath(IntentPath);
                videoData.setName(Tools.getFileName(IntentPath));
                initVideoList(videoData);
            }
        } else {
            mVideoPlayList = FileDataManager.getInstance(this).getAllVideoData();
        }
        if (mVideoPlayList.size() == 0) {
            Log.i(TAG, "mVideoPlayList.size() == 0 !!!");
            this.finish();
            return;
        }
        video_path = mVideoPlayList.get(video_position).getPath();
        isNeedBreakPointPlay = true;
        InitVideoPlayer(video_path, false);
        hideControlDelay();
        // register shutdown broadcast
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutDownReceiver, intentFilter);
        Thread breakPointThread = new Thread(VideoPlayerActivity.this.breakPointRunnable);
        breakPointThread.setName("breakPointThread");
        breakPointThread.start();
        mMediaPlayerTaskThread = new TaskThread(this);
        mMediaPlayerTaskThread.start();

        if (mNetUrlFrom != null && LOCAL_MEDIA_SERVICE.equalsIgnoreCase(mNetUrlFrom)) {
            mLocalMediaController = new LocalMediaController(this);
        }
        Log.i(TAG, "onCreate: end");
        addListener();
    }

    @Override
    public RelativeLayout getMainContainer() {
        return videoPlayerHolder.mVideoPlayLayout;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initView() {
        videoPlayerHolder = new VideoPlayerViewHolder(VideoPlayerActivity.this);
        videoPlayerHolder.mMediaControllerView.requestFocus();
        videoPlayerHolder.mMediaControllerView.mediaSeekBar.setOnClickListener(this);
        videoPlayerHolder.mMediaControllerView.setOnMediaEventRefreshListener(this);
    }

    @Override
    public BaseEntityHolder getBaseEntityHolder() {
        return new VideoEntityHolder(this, mVideoPlayList.get(video_position));
    }

    @Override
    public void setAllViewCanFocus() {
        showController();
        hideControlDelay();
        videoPlayerHolder.mMediaControllerView.setSettingFocus();
    }

    private void addListener() {
        videoPlayerHolder.mMediaControllerView.setOnControllerClickListener(mOnControllerClickListener);
        videoPlayerHolder.mMediaControllerView.setOnSeekBarChangeListener(seekBarListener);
        videoPlayerHolder.mMediaControllerView.setTitleText(mVideoPlayList.get(video_position).getName());
    }

    private void initVideoList(VideoData videoData) {
        if (!isFromFolder) {
            mVideoPlayList.clear();
            mVideoPlayList.addAll(FileDataManager.getInstance(this).getAllVideoData());
        } else {
            mVideoPlayList.clear();
            mVideoPlayList.addAll(FileDataManager.getInstance(this).getAllVideoData(videoData.getPath()));
        }
        for (VideoData vd : mVideoPlayList) {
            if (videoData.getPath().equals(vd.getPath())) {
                vd.setSize("1024");
                video_position = mVideoPlayList.indexOf(vd);
            }
        }
    }

    @Override
    public void finish() {
        //send a broadcast when finish , pip/pop will use it
        sendBroadcast(new Intent("com.jrm.localmm.VideoPlayerActivity.FINISH_SELF"));
        super.finish();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");
        videoHandler.removeMessages(Constants.DOLBY_SHOW_VISION_LOGO);
        videoHandler.removeMessages(Constants.DOLBY_HIDE_VISION_LOGO);
        if (videoPlayerHolder != null) {
            if (videoPlayerHolder.mVideoPlayView != null) {
                videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
            }
        }
        Thread closeDBThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "closeDBThread start to closeDB");
                BreakPointRecord.closeDB();
            }
        });
        closeDBThread.setName("closeDBThread");
        closeDBThread.start();
        dismissProgressDialog();
        try {
            unregisterReceiver(diskChangeReceiver);
            unregisterReceiver(shutDownReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (mLocalMediaController != null) {
            mLocalMediaController.exitMediaPlayer();
        }
        Tools.setMainPlay4K2KModeOn(this, false);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        videoHandler.removeMessages(Constants.HIDE_PLAYER_CONTROL);
        videoHandler.sendEmptyMessageDelayed(Constants.HIDE_PLAYER_CONTROL, 2000);

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        if (videoPlayerHolder.mVideoPlayView != null) {
            videoPlayerHolder.mVideoPlayView.stopPlayer();
            videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
        }
        MDisplay.set3DDisplayMode(0);
        super.onStop();
        releaseAllResource();
        videoHandler.sendEmptyMessageDelayed(Constants.HANDLE_MESSAGE_PLAYER_EXIT, 300);
        Intent intent = new Intent("mtk.intent.action.dolby.exit");
        intent.setPackage("com.mediatek.tv.agent");
        sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        stopMediascanner();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        // Registered disk change broadcast receiver
        registerReceiver(diskChangeReceiver, filter);
        Log.i(TAG, "onResume: register VoiceRecognitionReceiver");
        Log.i(TAG, "onResume: end");
        super.onResume();
    }

    /**
     *
     */
    private OnSeekBarChangeListener seekBarListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // The progress of the progress bar is changed
            Log.i(TAG, "onProgressChanged fromUser:" + fromUser + " progress:" + String.valueOf(progress));
            videoPlayerHolder.mMediaControllerView.onProgressChanged(seekBar, progress, fromUser);
            if (fromUser) {
                cancelDelayHide();
                mSeekPosition = progress;
                videoHandler.removeMessages(Constants.SEEK_TIME);
                if (++seekTimes == 1 && seekComplete) {
                    seekComplete = false;
                    videoHandler.sendEmptyMessage(Constants.SEEK_TIME);
                    hideControlDelay();
                    return;
                } else {
                    videoHandler.sendEmptyMessageDelayed(Constants.SEEK_TIME, 500);
                    videoHandler.removeMessages(SEEK_POS);
                    videoHandler.sendEmptyMessageDelayed(SEEK_POS, 500);
                }
                hideControlDelay();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            int i = seekBar.getProgress();
            Log.i(TAG, "onStartTrackingTouch seekBar.getProgress():" + String.valueOf(i));
            if (videoPlayerHolder.mVideoPlayView.getDuration() == 0) {
                showToastTip(VideoPlayerActivity.this.getString(R.string.choose_time_failed));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int i = seekBar.getProgress();
            Log.i(TAG, "onStopTrackingTouch seekBar.getProgress():" + String.valueOf(i));
        }
    };

    public void refreshProgressBar() {
        mMediaPlayerTaskThread.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (videoPlayerHolder.mVideoPlayView != null) {
                    Log.d(TAG, "before getCurrentPosition, currentPlayerPosition = " + currentPlayerPosition);
                    currentPlayerPosition = videoPlayerHolder.mVideoPlayView.getCurrentPosition();
                    String time = Tools.formatDuration(currentPlayerPosition);
                    if (mLocalMediaController != null) {
                        mLocalMediaController.seekToPosition(currentPlayerPosition);
                    }
                    Message msg = videoHandler.obtainMessage(Constants.RefreshCurrentPositionStatusUI);
                    msg.obj = time;
                    videoHandler.sendMessage(msg);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                showController();
                hideControlDelay();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "---------- onKeyDown -------- keyCode:" + keyCode);
        boolean bRet = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                isPlaying = videoPlayerHolder.mVideoPlayView.isPlaying();
                if (!isPlaying) {
                    localResume(true);
                } else {
                    playSpeed = videoPlayerHolder.mVideoPlayView.getPlayMode();
                    if (playSpeed != 1) {
                        localResumeFromSpeed();
                    }
                }
                videoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                bRet = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (!videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    showController();
                    hideControlDelay();
                }
                isPlaying = videoPlayerHolder.mVideoPlayView.isPlaying();
                if (!isPlaying) {
                    localResume(true);
                } else {
                    localPause(true);
                }
                bRet = true;
                videoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                hideControlDelay();
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                isPlaying = videoPlayerHolder.mVideoPlayView.isPlaying();
                Log.i(TAG, "onKeyDown: isPlaying " + isPlaying);
                if (isPlaying) {
                    localPause(true);
                }
                bRet = true;
                videoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                if (isPrepared()) {
                    moveToNextOrPrevious(1);
                }
                bRet = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                Log.i(TAG, "KEYCODE_DPAD_UP");
                if (isPrepared()) {
                    moveToNextOrPrevious(-1);
                }
                bRet = true;
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                if (Tools.isSambaVideoPlayBack(VideoPlayerActivity.this)) {
                    break;
                }
                slowForward();
                break;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (Tools.isSambaVideoPlayBack(VideoPlayerActivity.this)) {
                    break;
                }
                fastForward();
                break;
            case KeyEvent.KEYCODE_MEDIA_STOP:
                videoHandler.removeMessages(SEEK_POS);
                if (videoPlayerHolder.mVideoPlayView != null) {
                    if (isPrepared && mVideoPlayList.size() > 0 && isNeedBreakPointPlay) {
                        BreakPointRecord.AddDataThread(mVideoPlayList.get(video_position).getPath()
                                , videoPlayerHolder.mVideoPlayView.getCurrentPosition(), mVideoPlayList.get(video_position).getSize(), this);
                    }
                    videoPlayerHolder.mVideoPlayView.stopPlayer();
                    videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
                }
                videoHandler.sendEmptyMessageDelayed(Constants.HANDLE_MESSAGE_PLAYER_EXIT, 300);
                bRet = true;
                break;
            case KeyEvent.KEYCODE_BACK:
                if (isMenuShow()) {
                    return super.onKeyDown(keyCode, event);
                }
                if (videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    hideController();
                    return true;
                }
                videoHandler.removeMessages(SEEK_POS);
                if (videoPlayerHolder.mVideoPlayView != null) {
                    if (isPrepared && mVideoPlayList.size() > 0 && isNeedBreakPointPlay) {
                        BreakPointRecord.AddDataThread(
                                mVideoPlayList.get(video_position).getPath(),
                                videoPlayerHolder.mVideoPlayView.getCurrentPosition(),
                                mVideoPlayList.get(video_position).getSize(), this);
                    }
                    videoPlayerHolder.mVideoPlayView.stopPlayer();
                    videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
                }
                videoHandler.sendEmptyMessageDelayed(Constants.HANDLE_MESSAGE_PLAYER_EXIT, 300);
                bRet = true;
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!isMenuShow()
                        && !videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    showListDialog();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                cancelDelayHide();
                hideControlDelay();
                break;
            case KeyEvent.KEYCODE_MENU:
                if (!videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    showController();
                    hideControlDelay();
                } else {
                    hideController();
                }
                bRet = true;
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (!videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
                    showController();
                    hideControlDelay();
                }
            default:
                bRet = false;
                break;
        }
        return bRet || super.onKeyDown(keyCode, event);
    }

    private void showListDialog() {
        videoListDialog = new VideoListDialog(this, R.style.MediaDialog);
        Window window = videoListDialog.getWindow();
        window.setWindowAnimations(R.style.MediaDialog);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0;
        window.setAttributes(lp);
        videoListDialog.prepareData(mVideoPlayList, video_position);
        videoListDialog.setOnListItemClickListener(this);
        videoListDialog.show();
    }

    private void hideListDialog() {
        if (videoListDialog != null
                && videoListDialog.isShowing()) {
            videoListDialog.dismiss();
        }
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    private MediaControllerView.OnControllerClickListener mOnControllerClickListener =
            new MediaControllerView.OnControllerClickListener() {
                @Override
                public void onPreClick() {
                    if (!refreshController()) {
                        if (isPrepared()) {
                            cancelDelayHide();
                            moveToNextOrPrevious(-1);
                            hideControlDelay();
                        }
                    }
                }

                @Override
                public void onPlayPauseClick() {
                    if (!refreshController()) {
                        if (isPrepared()) {
                            if (videoPlayerHolder.mVideoPlayView.isPlaying()) {
                                playSpeed = videoPlayerHolder.mVideoPlayView.getPlayMode();
                                if (playSpeed != 1) {
                                    localResumeFromSpeed();
                                } else {
                                    localPause(true);
                                    videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(true);
                                }
                            } else {
                                videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(false);
                                localResume(true);
                            }
                        }
                    }
                }

                @Override
                public void onNextClick() {
                    if (!refreshController()) {
                        if (isPrepared()) {
                            cancelDelayHide();
                            moveToNextOrPrevious(1);
                            hideControlDelay();
                        }
                    }
                }

                @Override
                public void onZoomOutClick() {

                }

                @Override
                public void onZoomInClick() {

                }

                @Override
                public void onRotateClick() {

                }

                @Override
                public void onSettingClick() {
                    if (!refreshController()) {
                        startMenu();
                        hideController();
                    }
                }
            };

    private boolean refreshController() {
        if (!videoPlayerHolder.mMediaControllerView.mIsControllerShow) {
            showController();
            hideControlDelay();
            return true;
        } else {
            hideControlDelay();
        }
        return false;
    }

    public void hideControlDelay() {
        videoHandler.removeMessages(Constants.HIDE_PLAYER_CONTROL);
        videoHandler.sendEmptyMessageDelayed(Constants.HIDE_PLAYER_CONTROL, DEFAULT_TIMEOUT);
    }

    public void cancelDelayHide() {
        videoHandler.removeMessages(Constants.HIDE_PLAYER_CONTROL);
    }

    private void hideController() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        videoPlayerHolder.mMediaControllerView.hideController();
    }

    /**
     * Show Controller
     */
    public void showController() {
        videoPlayerHolder.mMediaControllerView.showController();
    }


    // play model to deal with start
    protected void processPlayCompletion(int delta) {
        boolean isNextMusic = delta > 0;
        switch (currentPlayMode) {
            case SINGE:
                break;
            case RANDOM:
                if (mVideoPlayList.size() - 1 <= 0) {
                    video_position = 0;
                } else {
                    video_position = new Random().nextInt(mVideoPlayList.size());
                }
                break;
            case ORDER:
                if (isNextMusic) {
                    if (video_position < mVideoPlayList.size() - 1) {
                        video_position++;
                    }
                } else {
                    if (mVideoPlayList.size() > 0) {
                        if (video_position - 1 >= 0) {
                            video_position--;
                        }
                    }
                }
                break;
            case LIST:
                if (isNextMusic) {
                    if (video_position >= mVideoPlayList.size() - 1) {
                        video_position = 0;
                    } else {
                        video_position++;
                    }
                } else {
                    if (mVideoPlayList.size() > 0) {
                        if (video_position - 1 >= 0) {
                            video_position--;
                        } else {
                            video_position = mVideoPlayList.size() - 1;
                        }
                    }
                }
                break;
        }
    }

    /**
     * Playback modes change
     */
    public void changePlayMode(int playMode) {
        currentPlayMode = playMode;
        setPlayMode(playMode);
    }

    /**
     * Set the current playback modes
     */
    private void setPlayMode(int mode) {
        SharedPreferences preference = getSharedPreferences(LOCAL_MEDIA, MusicPlayerActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putInt(VIDEO_PLAY_MODE, mode);
        editor.apply();
    }

    public int getPlayMode() {
        SharedPreferences preference = getSharedPreferences(LOCAL_MEDIA, MusicPlayerActivity.MODE_PRIVATE);
        return preference.getInt(VIDEO_PLAY_MODE, LIST);
    }
    // play model to deal with end

    private void doMoveToNextOrPrevious(int alpha) {
        isPrepared = false;
        if (isVideoNone) {
            isVideoNone = false;
        }
        if (mVideoPlayList.size() == 0) {
            this.finish();
            return;
        }

        // If the video  break point is near to the end of the video , and then just set the break point to 0.
        if (isNeedBreakPointPlay) {
            if (videoPlayerHolder.mVideoPlayView.getDuration()
                    - videoPlayerHolder.mVideoPlayView.getCurrentPosition() > 2000) {
                BreakPointRecord.AddDataThread(mVideoPlayList.get(video_position).getPath(),
                        videoPlayerHolder.mVideoPlayView.getCurrentPosition(),
                        mVideoPlayList.get(video_position).getSize(), this);
            } else {
                BreakPointRecord.AddDataThread(mVideoPlayList.get(video_position).getPath(), 0,
                        mVideoPlayList.get(video_position).getSize(), this);
            }
        }
        if (alpha != 0) {
            processPlayCompletion(alpha);
        }
        String video_name = mVideoPlayList.get(video_position).getName();
        videoPlayerHolder.mMediaControllerView.setTitleText(video_name);
        cancelDelayHide();
        InitVideoPlayer(mVideoPlayList.get(video_position).getPath(), true);
    }

    /**
     * @param alpha
     */
    private void moveToNextOrPrevious(int alpha) {
        doMoveToNextOrPrevious(alpha);
    }

    private void InitVideoPlayer(String videoPlayPath, boolean resetSubtitleView) {
        Log.i(TAG, "InitVideoPlayer");
        if (isMenuShow()) {
            exitMenu();
        }
        hideListDialog();
        if (mNetVideoUri != null) {
            videoPlayerHolder.mVideoPlayView.setNetVideoUri(mNetVideoUri, mHeaders);
        }
        isResourceLost = false;
        isPrepared = false;
        // Setting error of the monitor
        if (videoPlayerHolder.mVideoPlayView != null) {
            videoPlayerHolder.mVideoPlayView.stopPlayback();
            videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(myPlayerCallback);
        }
        Log.i(TAG, "*******videoPlayPath*****" + videoPlayPath + "SDK_INT: " + Build.VERSION.SDK_INT);
        videoPlayPath = Tools.fixPath(videoPlayPath);
        if (isNeedBreakPointPlay) {
            breakPoint = BreakPointRecord.getData(mVideoPlayList.get(video_position)
                            .getPath(), mVideoPlayList.get(video_position).getSize(),
                    VideoPlayerActivity.this);
        } else {
            breakPoint = 0;
            isBreakPointPlay = false;
        }
        Log.i(TAG, "********breakPoint********" + breakPoint);
        if (breakPoint > 0) {
            breakPointPlay(videoPlayPath);
        } else {
            videoPlayerHolder.mVideoPlayView.setVideoPath(videoPlayPath);
        }
        videoPlayerHolder.mVideoPlayView.setHandler(videoHandler);
        isAudioSupport = true;
        isVideoSupport = true;
    }

    /**
     * Display buffer progress
     *
     * @param id
     */
    private void showProgressDialog(int id) {
        if (!isFinishing()) {
            progressDialog = new MessageDialog(this, R.style.MessageDialog);
            MessageDialog.Builder builder = new MessageDialog.Builder(progressDialog);
            builder.setMessageText(getResources().getString(id))
                    .setIsLoading(true)
                    .setDialogSize(500, 150);
            progressDialog.show();
        }
    }

    /**
     *
     */
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            Log.d(TAG, "dismissProgressDialog");
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * FF
     */
    private void fastForward() {
        showController();
        playSpeed = videoPlayerHolder.mVideoPlayView.getPlayMode();
        Log.d(TAG, "get play mode ---" + playSpeed);
        int currentSpeed = 1 * 2;
        if (playSpeed < 64 && playSpeed > 0) {
            currentSpeed = playSpeed * 2;
        }
        playSpeed = currentSpeed;
        videoPlayerHolder.mVideoPlayView.setPlayMode(currentSpeed);
        // Set the current approaching speed display string
    }

    /**
     * FB
     */
    private void slowForward() {
        showController();
        playSpeed = videoPlayerHolder.mVideoPlayView.getPlayMode();
        int currentSpeed = 1 * (-2);
        if (playSpeed < 64 && playSpeed < 0) {
            currentSpeed = playSpeed * 2;
        }
        playSpeed = currentSpeed;
        videoPlayerHolder.mVideoPlayView.setPlayMode(currentSpeed);
    }

    /**
     * Local broadcast suspended
     */
    protected void localPause(boolean bSelect) {
        Log.i(TAG, "localPause: ");
        cancelDelayHide();
        showController();
        videoPlayerHolder.mVideoPlayView.setVoice(false);
        videoPlayerHolder.mVideoPlayView.setPlayMode(1);
        videoPlayerHolder.mVideoPlayView.pause();
        videoPlayerHolder.mVideoPlayView.setVoice(true);
        isPlaying = false;
        videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(true);
        hideControlDelay();
    }

    /**
     * Local broadcast recovery
     */
    protected void localResume(boolean bSelect) {
        cancelDelayHide();
        showController();
        // Player resume from pause state, need make sure the play speed is 1 first. mantis:1280279
        videoPlayerHolder.mVideoPlayView.setPlayMode(1);
        videoPlayerHolder.mVideoPlayView.start();
        isPlaying = true;
        playSpeed = 1;
        videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(false);
        videoHandler.removeMessages(SEEK_POS);
        videoHandler.sendEmptyMessageDelayed(SEEK_POS, 500);
        hideControlDelay();
    }

    /**
     * Local broadcast recovery
     */
    protected void localResumeFromSpeed() {
        Log.i(TAG, "localResumeFromSpeed: ");
        cancelDelayHide();
        showController();
        videoPlayerHolder.mVideoPlayView.setVoice(true);
        videoPlayerHolder.mVideoPlayView.setPlayMode(1);
        isPlaying = true;
        playSpeed = 1;
        videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(false);
        videoHandler.removeMessages(SEEK_POS);
        videoHandler.sendEmptyMessage(SEEK_POS);
        hideControlDelay();
    }

    // show Tip
    public void showToastTip(String strMessage) {
        if (!isVideoSupport && !isAudioSupport) {
            if (mVideoPlayList.size() <= 1) {
                videoHandler.removeMessages(SEEK_POS);
                if (videoPlayerHolder.mVideoPlayView != null) {
                    videoPlayerHolder.mVideoPlayView.stopPlayer();
                    videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
                }
                videoHandler.sendEmptyMessageDelayed(Constants.HANDLE_MESSAGE_PLAYER_EXIT, 300);
            }
            videoPlayerHolder.mVideoPlayView.stopPlayback();
            showErrorDialog(getResources().getString(
                    R.string.video_media_error_video_unsupport));
        } else {
            ToastFactory.showToast(VideoPlayerActivity.this, strMessage, Toast.LENGTH_SHORT);
        }
    }

    /**
     * show onError callback info.
     *
     * @param strMessage toast info
     */
    private void showErrorToast(String strMessage) {
        if (isResourceLost) {
            if (videoPlayerHolder.mVideoPlayView != null) {
                videoPlayerHolder.mVideoPlayView.stopPlayer();
                videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
                finish();
            }
            return;
        }
        String svd = getResources().getString(R.string.video_media_error_video_unsupport);
        if (strMessage.equals(getResources().getString(R.string.video_media_error_server_died))) {
            VideoPlayerActivity.this.finish();
        } else {
            if (!strMessage.equals(getResources().getString(R.string.video_media_info_video_decoder_over_capability))
                    && mVideoPlayList.size() > 1) {
                String sName = mVideoPlayList.get(video_position).getName();
                String showTip = sName + " " + strMessage + ",\n" + getResources().getString(R.string.video_media_play_next);
                showErrorDialog(showTip);
            } else {
                ToastFactory.showToast(VideoPlayerActivity.this, strMessage, Toast.LENGTH_SHORT);
                VideoPlayerActivity.this.finish();
            }
        }
    }

    private void showErrorDialog(final String strMessage) {
        // Prevent activity died when the popup menu
        if (!isFinishing()) {
            MessageDialog messageDialog = new MessageDialog(this, R.style.MessageDialog);
            MessageDialog.Builder builder = new MessageDialog.Builder(messageDialog);
            builder.setMessageText(getResources().getString(R.string.show_info))
                    .setIsLoading(false)
                    .setDialogSize(500, 220)
                    .setContentText(strMessage)
                    .setButtonClickListener(new MessageDialog.OnDialogButtonClickListener() {
                        @Override
                        public void onNegativeClick() {
                            VideoPlayerActivity.this.finish();
                        }

                        @Override
                        public void onPositiveClick() {
                            dismissProgressDialog();
                            if (strMessage.equals(getResources().getString(
                                    R.string.video_media_error_server_died))) {
                                VideoPlayerActivity.this.finish();
                            } else {
                                moveToNextOrPrevious(1);
                            }
                        }
                    });
            dismissProgressDialog();
            messageDialog.show();
        }
    }

    // Unknown error handling
    private errorStruct processErrorUnknown(MediaPlayer mp, int what, int extra) {
        errorStruct retStruct = new errorStruct();
        int strID = R.string.video_media_error_unknown;
        switch (extra) {
            case MediaError.ERROR_MALFORMED:
                strID = R.string.video_media_error_malformed;
                break;
            case MediaError.ERROR_IO:
                strID = R.string.video_media_error_io;
                break;
            case MediaError.ERROR_UNSUPPORTED:
                strID = R.string.video_media_error_unsupported;
                break;
            case MediaError.ERROR_FILE_FORMAT_UNSUPPORT:
                strID = R.string.video_media_error_format_unsupport;
                break;
            case MediaError.ERROR_NOT_CONNECTED:
                strID = R.string.video_media_error_not_connected;
                break;
            case MediaError.ERROR_AUDIO_UNSUPPORT:
                strID = R.string.video_media_error_audio_unsupport;
                retStruct.showStateWithError = false;
                break;
            case MediaError.ERROR_VIDEO_UNSUPPORT:
                strID = R.string.video_media_error_video_unsupport;
                break;
            case MediaError.ERROR_DRM_NO_LICENSE:
                strID = R.string.video_media_error_no_license;
                break;
            case MediaError.ERROR_DRM_LICENSE_EXPIRED:
                strID = R.string.video_media_error_license_expired;
                break;
            case MMediaPlayer.MEDIA_ERROR_VIDEO_RESOURCE_LOST:
                isResourceLost = true;
                break;
            default:
                //usb storage off
                strID = R.string.video_media_other_error_unknown;
                break;
        }
        retStruct.strMessage = getResources().getString(strID);
        return retStruct;
    }

    public VideoPlayView.playerCallback myPlayerCallback = new VideoPlayView.playerCallback() {
        @Override
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.d("dingyl", "onError");
            if (mLocalMediaController != null) {
                mLocalMediaController.onError(framework_err, impl_err);
            }
            String strMessage = "";
            switch (framework_err) {
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    strMessage = getResources().getString(R.string.video_media_error_server_died);
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    errorStruct retStruct = processErrorUnknown(mp, framework_err, impl_err);
                    strMessage = retStruct.strMessage;
                    if (!retStruct.showStateWithError) {
                        return true;
                    }
                    break;
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    strMessage = getResources().getString(R.string.video_media_error_not_valid);
                    break;
                case IO_ERROR:
                    strMessage = getResources().getString(R.string.open_file_fail);
                    break;
                default:
                    strMessage = getResources().getString(R.string.video_media_other_error_unknown);
                    break;
            }
            Log.i(TAG, "player onError---start to stop playerback.");
            videoPlayerHolder.mVideoPlayView.stopPlayback();
            showErrorToast(strMessage);
            return true;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "----------- onCompletion ------------");
            if (mLocalMediaController != null) {
                mLocalMediaController.onCompletion();
            }
            moveToNextOrPrevious(1);
            showController();
            hideControlDelay();
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "*******onInfo******" + what + " getVideoWidth:" + mp.getVideoWidth() + " getVideoHeight:" + mp.getVideoHeight());

            if (mLocalMediaController != null) {
                mLocalMediaController.onInfo(what, extra);
            }

            switch (what) {
                case MMediaPlayer.MEDIA_INFO_SUBTITLE_UPDATA:
                    Log.i(TAG, "MEDIA_INFO_SUBTITLE_UPDATA");
                    break;
                case MMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.i(TAG, "*******MEDIA_INFO_NOT_SEEKABLE******");
                    String strMessage = getResources().getString(
                            R.string.video_media_infor_no_index);
                    playSpeed = 1;
                    showToastTip(strMessage);
                    return true;
                case MMediaPlayer.MEDIA_INFO_AUDIO_UNSUPPORT:
                    Log.i(TAG, "MEDIA_INFO_AUDIO_UNSUPPORT");
                    isAudioSupport = false;
                    showToastTip(getResources().getString(
                            R.string.video_media_error_audio_unsupport));
                    break;
                case MMediaPlayer.MEDIA_INFO_VIDEO_UNSUPPORT:
                    Log.i(TAG, "MEDIA_INFO_VIDEO_UNSUPPORT");
                    isPrepared = true;
                    isVideoSupport = false;
                    videoHandler.sendEmptyMessageDelayed(SEEK_POS, 500);
                    showToastTip(getResources().getString(
                            R.string.video_media_error_video_unsupport));
                    isVideoNone = true;
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.i(TAG, "*********MEDIA_INFO_RENDERING_START************");
                    dismissProgressDialog();
                    break;
                case MMediaPlayer.MEDIA_INFO_TRICK_PLAY_COMPLETE:
                    // Trick play complete notify MEDIA_INFO_TRICK_PLAY_COMPLETE = 1006
                    Log.i(TAG, "MEDIA_INFO_TRICK_PLAY_COMPLETE");
                    localResumeFromSpeed();
                    break;
                case MMediaPlayer.MEDIA_INFO_VIDEO_DECODER_OVER_CAPABILITY:
                    Log.i(TAG, "MEDIA_INFO_VIDEO_DECODER_OVER_CAPABILITY");
                    videoPlayerHolder.mVideoPlayView.stopPlayback();
                    strMessage = getResources().getString(R.string.video_media_info_video_decoder_over_capability);
                    showErrorToast(strMessage);
                    break;
                default:
                    Log.i(TAG, "Play onInfo::: default onInfo!");
                    break;
            }
            return false;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
        }

        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.i(TAG, "onVideoSizeChanged width:" + width + " height:" + height);
            if (width == 0 || height == 0) {
                isVideoSupport = false;
                isPrepared = true;
                videoHandler.sendEmptyMessageDelayed(SEEK_POS, 500);
                isVideoNone = true;
            }
            if (mLocalMediaController != null) {
                mLocalMediaController.onVideoSizeChanged(width, height);
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mp == null) {
                return;
            }
            isPrepared = true;
            if (mLocalMediaController != null) {
                mLocalMediaController.onPrepared();
            }
            if (isBreakPointPlay) {
                videoPlayerHolder.mVideoPlayView.seekTo(breakPoint);
            }

            videoPlayerHolder.mVideoPlayView.start();
            initPlayer();
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            seekComplete = true;
            playSpeed = 1;
            videoPlayerHolder.mVideoPlayView.setVoice(true);
        }

        @Override
        public void onCloseMusic() {
        }

        @Override
        public void onUpdateSubtitle(String sub) {

        }
    };

    private void breakPointPlay(final String path) {
        if (!isAudioSupport && !isVideoSupport) {
            return;
        }
        isBreakPointPlay = false;
        videoHandler.sendEmptyMessageDelayed(Constants.HANDLE_MESSAGE_SKIP_BREAKPOINT, 5000);
        breakPointDialog = new MessageDialog(this, R.style.MessageDialog);
        MessageDialog.Builder builder = new MessageDialog.Builder(breakPointDialog);
        builder.setMessageText(getResources().getString(R.string.video_breakpoint_play))
                .setIsLoading(false)
                .setDialogSize(500, 180)
                .setButtonClickListener(new MessageDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onNegativeClick() {
                        videoHandler
                                .removeMessages(Constants.HANDLE_MESSAGE_SKIP_BREAKPOINT);
                        isBreakPointPlay = false;
                    }

                    @Override
                    public void onPositiveClick() {
                        videoHandler
                                .removeMessages(Constants.HANDLE_MESSAGE_SKIP_BREAKPOINT);
                        isBreakPointPlay = true;
                    }
                });
        breakPointDialog.setCancelable(true);
        breakPointDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                videoHandler.removeMessages(Constants.HANDLE_MESSAGE_SKIP_BREAKPOINT);
                isBreakPointPlay = false;
            }
        });
        breakPointDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (path != null) {
                    videoPlayerHolder.mVideoPlayView.setVideoPath(path);
                }
            }
        });
        breakPointDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_MEDIA_PLAY == keyCode
                        || KeyEvent.KEYCODE_MEDIA_PAUSE == keyCode
                        || KeyEvent.KEYCODE_MEDIA_NEXT == keyCode
                        || KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode) {
                    return true;
                }
                return false;
            }
        });
        breakPointDialog.show();
    }

    public boolean isVideoSupport() {
        if (videoPlayerHolder.mVideoPlayView.isInPlaybackState()) {
            return isVideoSupport;
        }
        return true;
    }

    public void initPlayer() {
        if (videoPlayerHolder.mVideoPlayView.isVideoWidthHeightEqualZero() || (!isVideoSupport())) {
            dismissProgressDialog();
        }
        videoPlayerHolder.mVideoPlayView.setVoice(true);
        isPrepared = true;
        String time = "";
        duration = (int) videoPlayerHolder.mVideoPlayView.getDuration();
        time = Tools.formatDuration(duration);
        Log.i(TAG, "initPlayer getDuration()" + time);
        if (mLocalMediaController != null) {
            mLocalMediaController.setDuration(duration);
        }
        videoPlayerHolder.mMediaControllerView.setDurationTime(time);
        videoPlayerHolder.mMediaControllerView.setSeekBarMax(duration);
        videoPlayerHolder.mMediaControllerView.setPlayPauseFocus();
        videoPlayerHolder.mMediaControllerView.setPlayPauseStatus(!videoPlayerHolder.mVideoPlayView.isPlaying());
        playSpeed = 1;
        hideControlDelay();
        videoHandler.sendEmptyMessage(SEEK_POS);
    }

    public void stopMediascanner() {
        MediaScanner.stopMediaScanner();
    }

    public void startMediascanner() {
        MediaScanner.startMediaScanner();
    }

    private void exitPlayer() {
        videoHandler.removeMessages(SEEK_POS);
        if (videoPlayerHolder.mVideoPlayView != null) {
            videoPlayerHolder.mVideoPlayView.stopPlayer();
            videoPlayerHolder.mVideoPlayView.setPlayerCallbackListener(null);
        }
    }

    public void setBreakPoint() {
        if (mVideoPlayList.size() == 0 || (!isNeedBreakPointPlay)) {
            return;
        }
        if (videoPlayerHolder.mVideoPlayView != null) {
            if (isPrepared && videoPlayerHolder.mVideoPlayView.getCurrentPosition() > 0) {
                BreakPointRecord.AddDataThread(mVideoPlayList.get(video_position).getPath(),
                        videoPlayerHolder.mVideoPlayView.getCurrentPosition(), mVideoPlayList
                                .get(video_position).getSize(), this);
            }
        }
    }

    private Runnable breakPointRunnable = new Runnable() {
        @Override
        public void run() {
            while (!VideoPlayerActivity.this.isFinishing()) {
                try {
                    if (isNeedBreakPointPlay) {
                        Log.i(TAG, ">>>>>record break point>>>>>>");
                        setBreakPoint();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // To receive off the radio exit play interface(Or switching source)
    BroadcastReceiver shutDownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "*******BroadcastReceiver**********" + intent.getAction());
            exitPlayer();
        }
    };

    // Disk change monitoring
    private BroadcastReceiver diskChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // Disk remove
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                Log.i(TAG, "DiskChangeReceiver: " + action);
                String devicePath = intent.getData().getPath();
                Log.i(TAG, "DiskChangeReceiver: " + devicePath + " "
                        + mVideoPlayList.get(0).getPath());

                if (mVideoPlayList.get(0).getPath().contains(devicePath)) {
                    videoHandler.removeMessages(SEEK_POS);
                    // Avoid activity is lost player anomaly to upper send
                    // anomaly Lead to error when showErrorDialog
                    exitPlayer();
                    ToastFactory.showToast(VideoPlayerActivity.this, getString(R.string.disk_eject), Toast.LENGTH_SHORT);
                    videoHandler.sendEmptyMessage(Constants.HANDLE_MESSAGE_PLAYER_EXIT);
                }
            }
        }
    };

    private void releaseAllResource() {
        Log.i(TAG, "releaseAllResource");
        mMediaPlayerTaskThread.clearEvent();
        mMediaPlayerTaskThread.requestExit();
    }
}
