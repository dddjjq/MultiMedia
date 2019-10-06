//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
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
package com.ktc.media.media.music;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ktc.media.IMediaService;
import com.ktc.media.IMusicStatusListener;
import com.ktc.media.R;
import com.ktc.media.constant.Constants;
import com.ktc.media.data.FileDataManager;
import com.ktc.media.media.util.MusicUtils;
import com.ktc.media.media.util.ToastFactory;
import com.ktc.media.media.util.Tools;
import com.ktc.media.media.view.MusicListDialog;
import com.ktc.media.media.view.OnListItemClickListener;
import com.ktc.media.menu.base.BaseMenuActivity;
import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.menu.holder.music.MusicMajorHolder;
import com.ktc.media.model.MusicData;
import com.ktc.media.util.DestinyUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayerActivity extends BaseMenuActivity implements OnListItemClickListener {
    private static final String TAG = "MusicPlayerActivity";

    public static final String ACTION_STOP_MUSIC = "com.jrm.dtv.stop.music";
    public static final String ACTION_CHANGE_SOURCE = "source.switch.from.storage";
    public static final int HANDLE_MESSAGE_LRC = 0;
    public static final int HANDLE_MESSAGE_SEEKBAR_UPDATE = 1;
    public static final int HANDLE_MESSAGE_SERVICE_START = 2;
    public static final int HANDLE_MESSAGE_MUSIC_PREPARE = 3;
    public static final int HANDLE_MESSAGE_PLAYER_EXIT = 4;
    public static final int HANDLE_MESSAGE_UPDATE_ARTIST = 5;
    public static final int HANDLE_MESSAGE_IMAGE_ALBUM_UPDATE = 6;
    private static final int HIDE_PLAYER_CONTROL = 7;

    private static final int DEFAULT_TIMEOUT = 10000;

    private MusicPlayerListener mMusicPlayerListener;
    private MusicPlayerViewHolder mMusicPlayerViewHolder;

    public static final int PLAY_PAUSE = 1;
    public static final int PLAY_PLAY = 2;
    public static int currentPlayStatus = 0;
    private DiskChangeReceiver receiver = null;
    // data
    public static List<MusicData> musicList = new ArrayList<>();

    public static int currentPosition = 0;
    private MusicUtils.ServiceToken stToken = null;
    public boolean isLrcShow = false;
    // Music is prepared
    public boolean isPrepared = false;
    private static LrcRead mLrcRead;
    private static int index = 0;
    private static int currentTime = 0;
    public static int countTime = 0;
    private List<LyricContent> mLyricList = new ArrayList<>();
    protected IMediaService mService = null;
    protected boolean isNextMusic = true;
    protected boolean clickable = false;
    private boolean isAudioSupport = true;
    private boolean isVideoSupport = true;
    private MediaScannerConnection mScanConnection;
    protected boolean isErrorDialogShow = false;
    private String mMusicListDevicePath = null;
    private int mServiceId = 0;
    private MusicListDialog mMusicListDialog;
    private boolean isFromFolder = false;

    // play model to deal with
    protected void processPlayCompletion() {
        int playMode = MusicPlayerListener.currentPlayMode;
        switch (playMode) {
            case MusicPlayerListener.SINGE:
                break;
            case MusicPlayerListener.RANDOM:
                if (musicList.size() - 1 <= 0) {
                    currentPosition = 0;
                } else {
                    currentPosition = new Random().nextInt(musicList.size());
                }
                break;
            case MusicPlayerListener.ORDER:
                if (isNextMusic) {
                    if (currentPosition < musicList.size() - 1) {
                        currentPosition++;
                    }
                } else {
                    if (musicList.size() > 0) {
                        if (currentPosition - 1 >= 0) {
                            currentPosition--;
                        }
                    }
                }
                isNextMusic = true;
                break;
            case MusicPlayerListener.LIST:
                if (isNextMusic) {
                    if (currentPosition >= musicList.size() - 1) {
                        currentPosition = 0;
                    } else {
                        currentPosition++;
                    }
                } else {
                    if (musicList.size() > 0) {
                        if (currentPosition - 1 >= 0) {
                            currentPosition--;
                        } else {
                            currentPosition = musicList.size() - 1;
                        }
                    }
                }
                isNextMusic = true;
                break;
        }
        new Thread(new Runnable() {
            public void run() {
                if (clickable) {
                    clickable = false;
                    musicPlayHandle
                            .sendEmptyMessage(HANDLE_MESSAGE_SERVICE_START);
                } else {
                    musicPlayHandle
                            .removeMessages(HANDLE_MESSAGE_SERVICE_START);
                    musicPlayHandle
                            .sendEmptyMessage(HANDLE_MESSAGE_SERVICE_START);
                }
            }
        }).start();
    }

    private void processPlayPrepare() {
        // Get countTime and audioSessionId
        try {
            if (mService != null) {
                countTime = (int) mService.getDuration();
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseFocus();
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
                getPicOfAlbum(musicList, currentPosition);
                mMusicPlayerListener.setSongsContent(musicList, currentPosition);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        currentTime = 0;
        mMusicPlayerViewHolder.mMediaControllerView.setCurrentTime(Tools
                .formatDuration(currentTime));
        mMusicPlayerViewHolder.mMediaControllerView.setSeekBarProgress(currentTime);
        try {
            if (mService != null) {
                countTime = (int) mService.getDuration();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (countTime > 0) {
            mMusicPlayerViewHolder.mMediaControllerView.setSeekBarMax(countTime);
            mMusicPlayerViewHolder.mMediaControllerView.setDurationTime(Tools
                    .formatDuration(countTime));
        }
        musicPlayHandle.sendEmptyMessageDelayed(
                MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE, 100);
        Log.i(TAG, "MusicPlayActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE");
        isPrepared = true;
    }

    private IMusicStatusListener musicStatusListener = new IMusicStatusListener.Stub() {
        @Override
        public void musicSeekCompleted() throws RemoteException {
            Log.d(TAG, "IMusicStatusListener.Stub, musicSeekCompleted");
            musicPlayHandle.sendEmptyMessage(
                    MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
        }

        @Override
        public void musicPrepared() {
            // Ready to play, get countTime and audioSessionId
            // processPlayPrepare();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "IMusicStatusListener.Stub, musicPrepared");
                    musicPlayHandle.sendEmptyMessage(HANDLE_MESSAGE_MUSIC_PREPARE);
                }
            }).start();
        }

        @Override
        public boolean musicPlayErrorWithMsg(String errMessage)
                throws RemoteException {
            Log.d(TAG, "IMusicStatusListener.Stub, musicPlayErrorWithMsg");
            exceptionProcess(errMessage);
            musicPlayHandle.removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
            return true;
        }

        @Override
        public void musicPlayException(String errMessage)
                throws RemoteException {
            Log.d(TAG, "IMusicStatusListener.Stub, musicPlayException");
            exceptionProcess(errMessage);
            musicPlayHandle.removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
        }

        @Override
        public void musicCompleted() throws RemoteException {
            Log.d(TAG, "IMusicStatusListener.Stub, musicCompleted");
            musicPlayHandle.removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
            if (!isErrorDialogShow) {
                processPlayCompletion();
            }
        }

        @Override
        public void handleSongSpectrum() throws RemoteException {
            // Spectrum processing
            Log.d(TAG, "IMusicStatusListener.Stub, handleSongSpectrum");
        }

        @Override
        public void handleMessageInfo(String strMessage) throws RemoteException {
            if (strMessage.equals(getResources().getString(R.string.video_media_error_video_unsupport))) {
                isVideoSupport = false;
            }
            if (strMessage.equals(getResources().getString(R.string.video_media_error_audio_unsupport))) {
                isAudioSupport = false;
                ToastFactory.showToast(MusicPlayerActivity.this, strMessage,
                        Gravity.CENTER);
            }
            if (!isVideoSupport && !isAudioSupport) {
                if (musicList.size() <= 1) {
                    exitMusicPlay();
                } else {
                    musicPlayHandle
                            .removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
                    processPlayCompletion();
                }
            }
            Log.d(TAG, "IMusicStatusListener.Stub, handleMessageInfo");
        }
    };

    @SuppressLint("all")
    public Handler musicPlayHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_MESSAGE_SEEKBAR_UPDATE:
                    try {
                        if (mService != null) {
                            currentTime = (int) mService.getCurrentPosition();
                            if (isLrcShow) {
                                setLRCIndex();
                            }
                            mMusicPlayerViewHolder.mMediaControllerView.setCurrentTime(Tools
                                    .formatDuration(currentTime));
                            mMusicPlayerViewHolder.mMediaControllerView.setSeekBarProgress(currentTime);
                            if (mService.isPlaying()) {
                                musicPlayHandle
                                        .sendEmptyMessageDelayed(
                                                MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE,
                                                100);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLE_MESSAGE_LRC:
                    // Lyrics treatment
                    mMusicPlayerViewHolder.mLyricView.SetIndex(index);
                    mMusicPlayerViewHolder.mLyricView.invalidate();
                    break;
                case HANDLE_MESSAGE_SERVICE_START:
                    initPlay();
                    break;
                case HANDLE_MESSAGE_MUSIC_PREPARE:
                    processPlayPrepare();
                    break;
                case HANDLE_MESSAGE_PLAYER_EXIT:
                    Log.i(TAG, "finish()[1]");
                    MusicPlayerActivity.this.finish();
                    break;
                case HANDLE_MESSAGE_UPDATE_ARTIST:
                    getPicOfAlbum(musicList, currentPosition);
                    mMusicPlayerListener.setSongsContent(musicList, currentPosition);
                    break;
                case HANDLE_MESSAGE_IMAGE_ALBUM_UPDATE:
                    if (msg.obj != null) {
                        Bitmap targetBitmap = createCircleAlbumBitmap((Bitmap) msg.obj);
                        Bitmap albumBitmap = getAlbumFinalBitmap(R.drawable.music_player_article_background, targetBitmap);
                        mMusicPlayerViewHolder.articleImage.setImageBitmap(albumBitmap);
                    } else {
                        mMusicPlayerViewHolder.articleImage.setImageResource(R.drawable.music_player_article_null);
                    }
                    break;
                case HIDE_PLAYER_CONTROL:
                    hideController();
                    break;
                default:
                    break;
            }
        }
    };

    //Bitmap与背景叠加
    private Bitmap getAlbumFinalBitmap(int backgroundRes, Bitmap resource) {
        Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundRes)
                .copy(Bitmap.Config.ARGB_8888, true);
        int backgroundWidth = backgroundBitmap.getWidth();
        int backgroundHeight = backgroundBitmap.getHeight();
        int resourceSize = DestinyUtil.dp2px(this, 266.7f);
        Bitmap newBitmap = Bitmap.createBitmap(backgroundWidth, backgroundHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(resource, (backgroundWidth - resourceSize) / 2
                , (backgroundHeight - resourceSize) / 2, null);
        return newBitmap;
    }

    //创建大小为400的圆形bitmap
    private Bitmap createCircleAlbumBitmap(Bitmap resource) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int size = resource.getWidth();
        int finalSize = DestinyUtil.dp2px(this, 266.7f);
        Bitmap backgroundBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resource, 0, 0, paint);
        float scaleSize = ((float) finalSize) / size;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleSize, scaleSize);
        return Bitmap.createBitmap(backgroundBitmap, 0, 0, size, size, matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        String IntentPath = getIntent().getStringExtra(Constants.BUNDLE_PATH_KEY);
        isFromFolder = getIntent().getBooleanExtra(Constants.FROM_FOLDER, false);
        if (IntentPath != null) {
            MusicData musicData = new MusicData();
            musicData.setPath(IntentPath);
            initMusicList(musicData);
        } else {
            musicList.clear();
            musicList.addAll(FileDataManager.getInstance(this).getAllMusicData());
        }
        if (musicList != null && musicList.size() > 0) {
            mMusicListDevicePath = musicList.get(0).getPath();
        }
        mMusicPlayerListener = new MusicPlayerListener(this, mMusicPlayerViewHolder);
        mMusicPlayerListener.addMusicListener();
        MusicPlayerListener.currentPlayMode = mMusicPlayerListener.getPlayMode();
        // Registered monitor shutdown broadcast
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutdownReceiver, intentFilter);
        IntentFilter intentFilterStop = new IntentFilter(ACTION_STOP_MUSIC);
        registerReceiver(stopMusicReceiver, intentFilterStop);
        IntentFilter sourceChange = new IntentFilter(ACTION_CHANGE_SOURCE);
        registerReceiver(sourceChangeReceiver, sourceChange);
        stToken = MusicUtils.bindToService(MusicPlayerActivity.this,
                musicServiceConnection);
    }

    @Override
    public RelativeLayout getMainContainer() {
        return mMusicPlayerViewHolder.mMusicPlayerLayout;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_music_player;
    }

    @Override
    public void initView() {
        mMusicPlayerViewHolder = new MusicPlayerViewHolder(this);
        mMusicPlayerViewHolder.findView();
    }

    @Override
    public BaseEntityHolder getBaseEntityHolder() {
        return new MusicMajorHolder(this, musicList.get(currentPosition));
    }

    @Override
    public void setAllViewCanFocus() {
        mMusicPlayerViewHolder.mMediaControllerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMusicPlayerViewHolder.mMediaControllerView.setAllCanFocus(true);
                mMusicPlayerViewHolder.mMediaControllerView.setSettingFocus();
                showController(false);
                hideControlDelay();
            }
        }, getResources().getInteger(R.integer.menu_anim_duration_long));
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        if (stToken != null) {
            MusicUtils.unbindFromService(stToken);
            stToken = null;
        }
        // Quit music broadcast service
        stopScanFile();
        unregisterReceiver(shutdownReceiver);
        unregisterReceiver(stopMusicReceiver);
        unregisterReceiver(sourceChangeReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop(), mServiceId = " + mServiceId);
        if (null != receiver) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        if (mService != null) {
            try {
                mService.stopById(mServiceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Tools.startMediascanner(MusicPlayerActivity.this);

        musicPlayHandle.removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
        musicPlayHandle.removeMessages(HANDLE_MESSAGE_PLAYER_EXIT);
        Log.i(TAG, "finish()[2]");
        finish();
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume()");
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        if (null != receiver) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        receiver = new DiskChangeReceiver();
        registerReceiver(receiver, filter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Tools.stopMediascanner(MusicPlayerActivity.this);
            }
        }).start();
        showController(true);
        hideControlDelay();
    }

    private void initMusicList(MusicData musicData) {
        if (!isFromFolder) {
            musicList.clear();
            musicList.addAll(FileDataManager.getInstance(this).getAllMusicData());
        } else {
            musicList.clear();
            musicList.addAll(FileDataManager.getInstance(this).getAllMusicData(musicData.getPath()));
        }
        for (MusicData md : musicList) {
            if (musicData.getPath().equals(md.getPath())) {
                currentPosition = musicList.indexOf(md);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!mMusicPlayerViewHolder.mMediaControllerView.mIsControllerShow) {
                showController(false);
                hideControlDelay();
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        Log.i(TAG, "********onTouchEvent*******" + event.getAction());
        return super.onTouchEvent(event);
    }

    /*************** Music played processing area ****************************/
    /***
     *
     * Music player Service connection
     */
    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IMediaService.Stub.asInterface(obj);
            Log.i(TAG, "ServiceConnection:::onServiceConnected mService:" + mService);
            if (mService != null) {
                try {
                    mService.setMusicStatusListener(musicStatusListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                musicPlayHandle.sendEmptyMessage(HANDLE_MESSAGE_SERVICE_START);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "ServiceConnection:::onServiceDisconnected mService:" + mService);
            mService = null;
        }
    };

    /**
     * pause and play
     */
    protected void pauseMusic() {
        if (musicList.size() > 0) {
            if (currentPlayStatus == PLAY_PLAY) {
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(true);
                pause();
            } else if (currentPlayStatus == PLAY_PAUSE) {
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
                continuing();
            }
        }
    }

    /**
     * previous
     */
    protected void preMusic() {
        mMusicPlayerViewHolder.clearData();
        isNextMusic = false;
        processPlayCompletion();
        mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
    }

    /**
     * next
     */
    protected void nextMusic() {
        mMusicPlayerViewHolder.clearData();
        isNextMusic = true;
        processPlayCompletion();
        mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
    }

    /**
     * Jump to the appointed position play.
     *
     * @param position The current broadcast position.
     */
    protected void seekTo(int position) {
        if (mService != null) {
            try {
                Log.i(TAG, "seekTo, pos = " + position + ", duration = " + mService.getDuration());
                if (position < mService.getDuration()) {
                    mService.playerToPosiztion(position);
                } else {
                    isPrepared = false;
                    nextMusic();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        currentPosition = position;
        musicPlayHandle.sendEmptyMessage(HANDLE_MESSAGE_SERVICE_START);
    }

    static class ClientProxy implements MediaScannerConnection.MediaScannerConnectionClient {
        final String[] mPaths;
        final String[] mMimeTypes;
        final OnScanCompletedListener mClient;
        MediaScannerConnection mConnection;
        int mNextPath;

        ClientProxy(String[] paths, String[] mimeTypes, OnScanCompletedListener client) {
            mPaths = paths;
            mMimeTypes = mimeTypes;
            mClient = client;
        }

        public void onMediaScannerConnected() {
            scanNextPath();
        }

        public void onScanCompleted(String path, Uri uri) {
            if (mClient != null) {
                mClient.onScanCompleted(path, uri);
            }
            scanNextPath();
        }

        void scanNextPath() {
            if (mNextPath >= mPaths.length) {
                mConnection.disconnect();
                return;
            }
            String mimeType = mMimeTypes != null ? mMimeTypes[mNextPath] : null;
            mConnection.scanFile(mPaths[mNextPath], mimeType);
            mNextPath++;
        }
    }


    public void startScanFile(Context context, String[] paths, String[] mimeTypes,
                              OnScanCompletedListener callback) {
        ClientProxy client = new ClientProxy(paths, mimeTypes, callback);
        stopScanFile();
        mScanConnection = new MediaScannerConnection(context, client);
        client.mConnection = mScanConnection;
        mScanConnection.connect();
    }

    public void stopScanFile() {
        if (mScanConnection != null && mScanConnection.isConnected()) {
            Log.i(TAG, "stop Scan File!");
            mScanConnection.disconnect();
        }
    }

    /**
     * Play music
     */
    private void initPlay() {
        if (musicList.size() == 0) {
            Log.i(TAG, "finish()[3]");
            finish();
            return;
        }
        if (isMenuShow()) {
            exitMenu();
        }
        hideListDialog();
        mMusicPlayerViewHolder.mMediaControllerView.setAllCanFocus(true);
        mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseFocus();
        showController(false);
        hideControlDelay();
        if (currentPosition >= 0 && currentPosition < musicList.size()) {
            String path = musicList.get(currentPosition).getPath();
            if (path == null) {
                return;
            }
            isVideoSupport = true;
            isAudioSupport = true;
            isPrepared = false;
            try {
                if (mService != null) {
                    mServiceId = mService.initPlayer(path);
                    Log.i(TAG, "initPlayer(), mServiceId = " + mServiceId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] pathArray = new String[1];
            pathArray[0] = path;

            startScanFile(this, pathArray, null, scanListener);

            getPicOfAlbum(musicList, currentPosition);
            // Because setSongsContent will be called several times and cause music title be reseted several times
            // And cause the ui flashing several times(Mantis:1308442)
            // So pause here and resume at last time in msg of "HANDLE_MESSAGE_UPDATE_ARTIST"
            mMusicPlayerListener.setSongsContent(musicList, currentPosition);
            new Thread(new Runnable() {
                public void run() {
                    initLrc();
                }
            }).start();
            initCurrentPosition();
            currentPlayStatus = PLAY_PLAY;
        }
        mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseFocus();
    }

    private OnScanCompletedListener scanListener = new OnScanCompletedListener() {

        @Override
        public void onScanCompleted(String arg0, Uri arg1) {
            Log.i(TAG, "onScanCompleted()");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Because queryCurrentMusicExtraInfo takes some time, and will cause ANR,
                    // so run it in Not-UI thread instead of UI thread.
                    queryCurrentMusicExtraInfo();
                    musicPlayHandle.sendEmptyMessage(HANDLE_MESSAGE_UPDATE_ARTIST);
                }
            }).start();
        }
    };

    public String getAudioCodecType() {
        String sRet = "";
        try {
            if (mService != null) {
                sRet = mService.getAudioCodecType();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sRet;
    }

    /**
     * pause
     */
    private void pause() {
        MusicPlayerListener.isPlaying = false;
        try {
            if (mService != null)
                mService.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentPlayStatus = PLAY_PAUSE;
    }

    /**
     * Continue to play
     */
    private void continuing() {
        MusicPlayerListener.isPlaying = true;
        currentPlayStatus = PLAY_PLAY;
        musicPlayHandle.sendEmptyMessage(
                MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE);
        try {
            if (mService != null) {
                mService.continuePlay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown(), event:" + event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isMenuShow()) {
                return super.onKeyDown(keyCode, event);
            }
            if (mMusicPlayerViewHolder.mMediaControllerView.mIsControllerShow) {
                hideController();
                return true;
            }
            if (mService != null) {
                try {
                    mService.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            finish();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
            if (mService != null) {
                try {
                    mService.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "finish()[4]");
            finish();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (currentPlayStatus == PLAY_PAUSE) {
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
                continuing();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            Log.i(TAG, "keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE");
            if (currentPlayStatus == PLAY_PLAY) {
                Log.i(TAG, "keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE2");
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(true);
                pause();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            if (isPrepared) {
                isPrepared = false;
                nextMusic();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if (isPrepared) {
                isPrepared = false;
                preMusic();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
            if (isPrepared) {
                pause();
                rewind();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            if (isPrepared) {
                pause();
                wind();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!mMusicPlayerViewHolder.mMediaControllerView.mIsControllerShow) {
                showController(false);
                hideControlDelay();
            } else {
                hideController();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (!isMenuShow()
                    && !mMusicPlayerViewHolder.mMediaControllerView.mIsControllerShow) {
                showListDialog();
                return true;
            }
        }
        if (!isMenuShow()) {
            switch (keyCode) {
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
        return super.onKeyDown(keyCode, event);
    }

    private void showListDialog() {
        mMusicListDialog = new MusicListDialog(this, R.style.MediaDialog);
        Window window = mMusicListDialog.getWindow();
        window.setWindowAnimations(R.style.MediaDialog);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.dimAmount = 0;
        window.setAttributes(lp);
        mMusicListDialog.prepareData(musicList, currentPosition);
        mMusicListDialog.setOnListItemClickListener(this);
        mMusicListDialog.show();
    }

    private void hideListDialog() {
        if (mMusicListDialog != null
                && mMusicListDialog.isShowing()) {
            mMusicListDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyUp(), event:" + event);
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN || keyCode == 0) {
            return true;
        }
        if (event.getAction() != KeyEvent.ACTION_UP) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND ||
                keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            if (isPrepared) {
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
                continuing();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /*************** Private method processing area *************/
    /**
     * When playing after abnormal end the current broadcast page
     */
    private void exitMusicPlay() {
        if (mService != null) {
            try {
                mService.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        musicPlayHandle.sendEmptyMessageDelayed(HANDLE_MESSAGE_PLAYER_EXIT, 1000);
    }

    private void exceptionProcess(String strMessage) {
        String sName = musicList.get(currentPosition).getPath();
        String showTip = sName + " " + strMessage + ",\n" + getResources().getString(R.string.music_media_play_next);
        if (musicList.size() <= 1) {
            Toast toast = Toast.makeText(MusicPlayerActivity.this,
                    sName + " " + strMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            exitMusicPlay();
        } else {
            showErrorDialog(showTip);
        }
    }

    // Pop up display an error dialog box
    private void showErrorDialog(final String strMessage) {
        // Prevent activity died when the popup menu
        if (!isFinishing()) {
            new AlertDialog.Builder(MusicPlayerActivity.this)
                    .setTitle(getResources().getString(R.string.show_info))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(strMessage)
                    .setPositiveButton(getResources().getString(R.string.exit_ok),
                            new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isErrorDialogShow = false;
                                    if (strMessage.equals(getResources().getString(
                                            R.string.video_media_error_server_died))) {
                                        Log.i(TAG, "finish()[5]");
                                        MusicPlayerActivity.this.finish();
                                    } else {
                                        processPlayCompletion();
                                    }
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.exit_cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isErrorDialogShow = false;
                                    Log.i(TAG, "finish()[6]");
                                    MusicPlayerActivity.this.finish();
                                }
                            }).setCancelable(false).show();
            isErrorDialogShow = true;
        }
    }

    /******************* Lyrics handling area **************************/
    /**
     * Initialization lyrics
     */
    private void initLrc() {
        if (mLrcRead != null) {
            mLrcRead.close();
        }
        isLrcShow = false;
        mLrcRead = new LrcRead();
        try {
            String tempPath = musicList.get(currentPosition).getPath();
            int index = musicList.get(currentPosition).getPath()
                    .lastIndexOf(".");
            String lrcPath = tempPath.substring(0, index) + ".lrc";
            // LrcPath judge whether there is
            File file = new File(lrcPath);
            if (file.exists() || Tools.isSambaPlaybackUrl(lrcPath)) {
                mLrcRead.Read(lrcPath);
                isLrcShow = true;
            } else {
                String txtPath = tempPath.substring(0, index) + ".txt";
                file = new File(txtPath);
                if (file.exists()) {
                    mLrcRead.Read(txtPath);
                    isLrcShow = true;
                } else {
                    isLrcShow = false;
                }
            }
        } catch (FileNotFoundException e) {
            isLrcShow = false;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mLyricList = mLrcRead.GetLyricContent();
        // Set lyrics resources
        mMusicPlayerViewHolder.mLyricView
                .setSentenceEntities(mLyricList);
        mMusicPlayerViewHolder.mLyricView.invalidate();
    }

    /**
     * Set lyrics position index
     */
    private void setLRCIndex() {
        if (currentTime < countTime) {
            int size = mLyricList.size();
            for (int i = 0; i < size; i++) {
                if (i < size - 1) {
                    if (currentTime < mLyricList.get(i).getLyricTime()
                            && i == 0) {
                        index = i;
                        musicPlayHandle
                                .sendEmptyMessage(MusicPlayerActivity.HANDLE_MESSAGE_LRC);
                    } else if (currentTime > mLyricList.get(i).getLyricTime()
                            && currentTime < mLyricList.get(i + 1)
                            .getLyricTime()) {
                        index = i;
                        musicPlayHandle
                                .sendEmptyMessage(MusicPlayerActivity.HANDLE_MESSAGE_LRC);
                    }
                } else if (i == size - 1
                        && currentTime > mLyricList.get(i).getLyricTime()) {
                    index = i;
                    musicPlayHandle
                            .sendEmptyMessage(MusicPlayerActivity.HANDLE_MESSAGE_LRC);
                }
            }
        }
    }

    /**
     * Initialize the current music serial number
     */
    private void initCurrentPosition() {
        Log.i(TAG, "initCurrentPosition musicList.size():" + musicList.size());
        if (musicList.size() > 0) {
            int duration = musicList.get(currentPosition).getDuration();
            mMusicPlayerViewHolder.mMediaControllerView.setDurationTime(Tools.formatDuration(duration));
            mMusicPlayerViewHolder.mMediaControllerView.setSeekBarMax(duration);
        }
    }

    /**
     * Fast back 10 s
     */
    private void rewind() {
        int currentTime;
        if (mService != null) {
            try {
                currentTime = (int) mService.getCurrentPosition();
                currentTime = currentTime - 10000;
                if (currentTime < 0) {
                    currentTime = 0;
                }
                mService.playerToPosiztion(currentTime);
                mMusicPlayerViewHolder.mMediaControllerView.setCurrentTime(Tools.formatDuration(currentTime));
                mMusicPlayerViewHolder.mMediaControllerView.setSeekBarProgress(currentTime);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fast forward 10 s
     */
    private void wind() {
        int currentTime;
        if (mService != null) {
            try {
                currentTime = (int) mService.getCurrentPosition();
                currentTime = currentTime + 10000;
                if (currentTime < mService.getDuration()) {
                    mService.playerToPosiztion(currentTime);
                    mMusicPlayerViewHolder.mMediaControllerView.setCurrentTime(Tools.formatDuration(currentTime));
                    mMusicPlayerViewHolder.mMediaControllerView.setSeekBarProgress(currentTime);
                } else {
                    isPrepared = false;
                    nextMusic();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /******************* Radio receiving area *******************************/
    private class DiskChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;
            if (intent.getDataString() == null) return;
            String devicePath = intent.getDataString().substring(7);
            Log.d(TAG, "DiskChangeReceiver: " + action);
            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                // Disk remove
                Log.i(TAG, "mMusicListDevicePath:" + mMusicListDevicePath);
                if (mMusicListDevicePath != null && mMusicListDevicePath.contains(devicePath)) {
                    Toast toast = Toast.makeText(MusicPlayerActivity.this,
                            R.string.disk_eject, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.i(TAG, "finish()[7]");
                    MusicPlayerActivity.this.finish();
                }
            }
        }
    }

    // To receive off the radio exit play interface
    private BroadcastReceiver shutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // standby button.
            if (action != null && action.equals(Intent.ACTION_SHUTDOWN)) {
                // music off
                stopMusicService();
            }
        }
    };
    private BroadcastReceiver stopMusicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "stop music:   ");
            // music off
            stopMusicService();
        }
    };
    private BroadcastReceiver sourceChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: sourceChangeReceiver");
            stopMusicService();
        }
    };

    /*
     *
     * Stop music broadcast service
     */
    private void stopMusicService() {
        Log.i(TAG, "stopMusicService: ");
        Intent i = new Intent(MusicPlayerActivity.this, MediaService.class);
        stopService(i);
    }

    public void queryCurrentMusicExtraInfo() {
        if (musicList == null || musicList.size() < 1) {
            Log.i(TAG, "queryCurrentMusicExtraInfo musicList:" + musicList);
            return;
        }
        if (currentPosition < 0 || currentPosition > (musicList.size() - 1)) {
            Log.i(TAG, "queryCurrentMusicExtraInfo currentPosition:" + currentPosition + " musicList.size():" + musicList.size());
            return;
        }
        Cursor cursor = null;
        String where = MediaColumns.DATA + "=?";
        MusicData item = musicList.get(currentPosition);
        String path = item.getPath();
        Log.i(TAG, "queryCurrentMusicExtraInfo, path = " + path);
        try {
            // According to the path query the database, access file
            // information
            cursor = getContentResolver().query(
                    Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{Audio.Media._ID, Audio.Media.TITLE,
                            Audio.Media.ARTIST, Audio.Media.ALBUM_ID, Audio.Media.YEAR,
                            Audio.Media.TRACK, Audio.Media.ALBUM},
                    where, new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                item.setId(cursor.getInt(cursor.getColumnIndex(Audio.Media._ID)));
                item.setSongName(cursor.getString(cursor.getColumnIndex(Audio.Media.TITLE)));
                item.setArtist(cursor.getString(cursor.getColumnIndex(Audio.Media.ARTIST)));
                item.setAlbum(cursor.getLong(cursor.getColumnIndex(Audio.Media.ALBUM_ID)));
                item.setAlbumName(cursor.getString(cursor.getColumnIndex(Audio.Media.ALBUM)));
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Obtain album pictures
     */
    private void getPicOfAlbum(final List<MusicData> musicList, final int currentPosition) {
        if (musicList == null || musicList.size() < 1) {
            Log.i(TAG, "getPicOfAlbum musicList:" + musicList + " musicList.size():" + musicList.size());
            return;
        }
        final long songid = (long) musicList.get(currentPosition).getId();
        final long albumid = musicList.get(currentPosition).getAlbum();
        Log.i(TAG, "getPicOfAlbum songid:" + songid + " albumid:" + albumid);
        // Because getPicOfAlbum takes some time, and will cause ANR,
        // so run it in Not-UI thread instead of UI thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Don't allow default artwork here, because we want to fall back to song-specific
                // album art if we can't find anything for the album.
                Bitmap bm = createAlbumThumbnail(musicList.get(currentPosition).getPath());
                Log.i(TAG, "createAlbumThumbnail bm:" + bm);
                if (bm == null) {
                    bm = MusicUtils.getArtwork(MusicPlayerActivity.this, songid, albumid, false);
                    Log.i(TAG, "getPicOfAlbum getArtwork bm:" + bm);
                }

                if (bm != null) {
                    Message msg = musicPlayHandle.obtainMessage(HANDLE_MESSAGE_IMAGE_ALBUM_UPDATE, bm);
                    musicPlayHandle.removeMessages(HANDLE_MESSAGE_IMAGE_ALBUM_UPDATE);
                    musicPlayHandle.sendMessage(msg);
                } else {
                    Message msg = musicPlayHandle.obtainMessage(HANDLE_MESSAGE_IMAGE_ALBUM_UPDATE);
                    musicPlayHandle.removeMessages(HANDLE_MESSAGE_IMAGE_ALBUM_UPDATE);
                    musicPlayHandle.sendMessage(msg);
                }
            }
        }).start();
    }

    private Bitmap createAlbumThumbnail(String filePath) {
        Log.i(TAG, "createAlbumThumbnail(), filePath:" + filePath);
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                Log.i(TAG, "art.length:" + art.length);
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "IllegalArgumentException");
        } catch (RuntimeException ex) {
            Log.i(TAG, "RuntimeException");
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * show control
     */
    protected void showController(boolean needRequest) {
        mMusicPlayerViewHolder.mMediaControllerView.showController();
        if (needRequest) {
            mMusicPlayerViewHolder.mMediaControllerView.requestFocus();
        }
        cancelDelayHide();
    }

    /**
     * After how long hidden article control
     */
    protected void hideControlDelay() {
        do {
            musicPlayHandle.removeMessages(HIDE_PLAYER_CONTROL);
        } while (musicPlayHandle.hasMessages(HIDE_PLAYER_CONTROL));
        musicPlayHandle.sendEmptyMessageDelayed(HIDE_PLAYER_CONTROL, DEFAULT_TIMEOUT);
    }

    /**
     * Cancel time delay hidden.
     */
    protected void cancelDelayHide() {
        musicPlayHandle.removeMessages(HIDE_PLAYER_CONTROL);
    }

    /**
     * Hidden article control.
     */
    protected void hideController() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        mMusicPlayerViewHolder.mMediaControllerView.hideController();
    }

    public void changePlayMode(int playMode) {
        mMusicPlayerListener.changePlayMode(playMode);
    }

    public int getPlayMode() {
        return mMusicPlayerListener.getPlayMode();
    }
}
