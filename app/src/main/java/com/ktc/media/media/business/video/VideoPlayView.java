//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
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

package com.ktc.media.media.business.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnTimedTextListener;
import android.media.TimedText;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ktc.media.constant.Constants;
import com.ktc.media.media.util.Tools;
import com.mstar.android.media.MMediaPlayer;
import com.mstar.android.media.VideoCodecInfo;

import java.io.IOException;
import java.util.Map;

/**
 * VideoPlayView.
 *
 * @author
 * @since 1.0
 */

@SuppressLint("NewApi")
public class VideoPlayView extends SurfaceView {

    private String TAG = "VideoPlayView";
    // settable by the client

    private Uri mUri;
    private Uri mNetVideoUri;
    private Map<String, String> mHeaders = null;

    private int mDuration;

    // all possible internal states

    private static final int STATE_ERROR = -1;

    private static final int STATE_IDLE = 0;

    private static final int STATE_PREPARING = 1;

    private static final int STATE_PREPARED = 2;

    private static final int STATE_PLAYING = 3;

    private static final int STATE_PAUSED = 4;

    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private static final int STATE_STREAMLESS_TO_NEXT = 6;

    private static final String MVC = "MVC";

    // mCurrentState is a VideoPlayView object's current state.

    // mTargetState is the state that a method caller intends to reach.

    // For instance, regardless the VideoPlayView object's current state,

    // calling pause() intends to bring the object to a target state

    // of STATE_PAUSED.

    private int mCurrentState = STATE_IDLE;

    private int mTargetState = STATE_IDLE;

    // All the stuff we need for playing and showing a video

    private SurfaceHolder mSurfaceHolder = null;
    // private MMediaPlayer mMMMediaPlayer = null;

    // use MMMediaPlayer class for sta
    private MMediaPlayer mMMediaPlayer = null;

    private MMediaPlayer mNextMMediaPlayer = null;

    private int mVideoWidth;

    private int mVideoHeight;

    private boolean bVideoDisplayByHardware = false;

    private playerCallback myPlayerCallback = null;

    private int mSeekWhenPrepared; // recording the seek position while

    private AudioManager mAudioManager = null;

    private boolean isVoiceOpen = true;

    private float currentVoice = 1.0f;

    private long startTime;

    private long startSeekTime;

    private long endSeekTime;
    private static final int IO_ERROR = 9000;
    private Context mContext;

    private Handler mHandler;

    public VideoPlayView(Context context) {
        super(context);
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initVideoView();
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initVideoView();
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        initVideoView();
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    public void setNetVideoUri(Uri uri, Map<String, String> headers) {
        mNetVideoUri = uri;
        mHeaders = headers;
    }

    public void setVideoPath(String path) {
        mUri = Uri.parse(path);
        Tools.setSambaVideoPlayBack(mContext, false);
        mSeekWhenPrepared = 0;
        Log.i(TAG, "***********setVideoURI:" + mUri);
        bVideoDisplayByHardware = false;
        Log.i(TAG, "openPlayer for Nonstreamless mode.");
        openPlayer();
        requestLayout();
        invalidate();
    }

    public boolean is4kVideo() {
        Log.i(TAG, "VideoPlayview is4kVideo mVideoWidth:" + mVideoWidth + " mVideoHeight:" + mVideoHeight);
        if (mVideoWidth >= 3840 && mVideoHeight >= 1080) {
            return true;
        }
        return false;
    }

    /**
     * call before play next.
     */
    public void stopPlayback() {
        if (mMMediaPlayer != null && mTargetState != STATE_IDLE) {
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            mMMediaPlayer.stop();
            Log.i(TAG, "stopPlayback: *****release start*****");
            mMMediaPlayer.release();
            Log.i(TAG, "stopPlayback: *****release end*****");
            Log.i(TAG, "stopPlayback: *****mMMediaPlayer*****" + mMMediaPlayer);
            mMMediaPlayer = null;
        }
    }

    /**
     * When abnormal stop play.
     */
    public void stopPlayer() {
        synchronized (this) {
            if (mMMediaPlayer != null && mTargetState != STATE_IDLE) {
                mCurrentState = STATE_IDLE;
                mTargetState = STATE_IDLE;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mMMediaPlayer.isPlaying()) {
                                Log.i(TAG, "*****stop start*****");
                                mMMediaPlayer.stop();
                                Log.i(TAG, "*****stop end*****");
                            }
                            Log.i(TAG, "***stopPlayer()**release start*****");
                            mMMediaPlayer.release();
                            Log.i(TAG, "***stopPlayer()**release end*****");
                            Log.i(TAG, "***stopPlayer()**mMMediaPlayer*****" + mMMediaPlayer);
                            mMMediaPlayer = null;
                        } catch (Exception e) {
                            Log.i(TAG, "Exception:" + e);
                        }

                    }
                }).start();
            }
        }
    }

    /**
     * Start player.
     */
    private void openPlayer() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        // Close the user's music callback interface
        if (myPlayerCallback != null)
            myPlayerCallback.onCloseMusic();

        // we shouldn't clear the target state, because somebody might have
        // called start() previously
        release(false);
        try {
            mMMediaPlayer = new MMediaPlayer();
            mDuration = -1;
            mMMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMMediaPlayer.setOnErrorListener(mErrorListener);
            mMMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMMediaPlayer.setOnInfoListener(mInfoListener);
            mMMediaPlayer.setOnTimedTextListener(mTimedTextListener);
            mMMediaPlayer.setOnSeekCompleteListener(mMMediaPlayerSeekCompleteListener);
            if (mNetVideoUri != null && mHeaders != null) {
                mMMediaPlayer.setDataSource(this.getContext(), mNetVideoUri, mHeaders);
            } else {
                mMMediaPlayer.setDataSource(this.getContext(), mUri);
            }
            if (Tools.isVideoFreezeLastFrameOn(this.getContext())) {
                setFreezeLastFrameMode(true);
            }

            boolean isRotateModeOn = Tools.isRotateModeOn(mContext);
            Log.i(TAG, "isRotateModeOn:" + isRotateModeOn);
            if (isRotateModeOn) {
                int rotateDegrees = Tools.getRotateDegrees(mContext);
                Log.i(TAG, "rotateDegrees:" + rotateDegrees);
                imageRotate(rotateDegrees);
            }

            if (mSurfaceHolder != null) {
                mMMediaPlayer.setDisplay(mSurfaceHolder);
            }
            mMMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMMediaPlayer.setScreenOnWhilePlaying(true);
            Log.i(TAG, "***********prepareAsync: " + mSurfaceHolder);

            startTime = System.currentTimeMillis();
            mMMediaPlayer.prepareAsync();
            // we don't set the target state here either, but preserve the
            // target state that was there before.
            Log.i(TAG, "*******prepareAsync  end*****");
            mCurrentState = STATE_PREPARING;
            mTargetState = STATE_PREPARED;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            if (myPlayerCallback != null) {
                myPlayerCallback.onError(mMMediaPlayer, IO_ERROR, 0);
            }
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
        } catch (IllegalStateException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
        } catch (SecurityException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            errorCallback(0);
        }
    }

    private void errorCallback(int errId) {
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if (myPlayerCallback != null)
            myPlayerCallback.onError(mMMediaPlayer,
                    MMediaPlayer.MEDIA_ERROR_UNKNOWN, errId);
    }

    // The following is a series of the player listener in callback
    MMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MMediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            Log.e(TAG, "MediaPlayer: " + mp + "    Video Size Changed: (" + mVideoWidth + "," + mVideoHeight + ")");
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                // Note: can't literally change the size of the SurfaceView, can
                // affect the effect of the PIP
                // getHolder().setFixedSize(mVideoWidth, mVideoHeight);
            }
            if (myPlayerCallback != null) {
                myPlayerCallback.onVideoSizeChanged(mMMediaPlayer, mVideoWidth, mVideoHeight);
            }
        }
    };

    MMediaPlayer.OnPreparedListener mPreparedListener = new MMediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            Log.i(TAG, "******onPrepared*myPlayerCallback*****" + myPlayerCallback);
            requestLayout();
            invalidate();

            if (myPlayerCallback != null) {
                myPlayerCallback.onPrepared(mMMediaPlayer);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            // mSeekWhenPrepared may be changed after seekTo() call
            int seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0) {
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            } else {
                if (mTargetState == STATE_PLAYING) {
                    start();
                }
            }
            if (is4kVideo()) {
                Tools.setMainPlay4K2KModeOn(mContext, true);
            } else {
                Tools.setMainPlay4K2KModeOn(mContext, false);
            }
            if (mHandler != null) {
                Message msg = new Message();
                msg.what = Constants.CHECK_IS_SUPPORTED;
                mHandler.sendMessage(msg);
            }
        }
    };

    private MMediaPlayer.OnCompletionListener mCompletionListener = new MMediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            Log.i(TAG, "MediaPlayer  call  onCompletion ..");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (myPlayerCallback != null) {
                myPlayerCallback.onCompletion(mMMediaPlayer);
            }
        }
    };

    private MMediaPlayer.OnErrorListener mErrorListener = new MMediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            Log.e(TAG, "Error: " + framework_err + "," + impl_err);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMMediaPlayer == null) {
                mMMediaPlayer = mNextMMediaPlayer;
                mNextMMediaPlayer = null;
            }
            /* If an error handler has been supplied, use it and finish. */
            if (myPlayerCallback != null) {
                if (myPlayerCallback.onError(mMMediaPlayer, framework_err, impl_err)) {
                    return true;
                }
            }
            return true;
        }
    };


    private MMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (myPlayerCallback != null)
                myPlayerCallback.onBufferingUpdate(mp, percent);
        }
    };

    private MMediaPlayer.OnInfoListener mInfoListener = new MMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            Log.i(TAG, "onInfo what:" + what + " extra:" + extra);
            if (MMediaPlayer.MEDIA_INFO_VIDEO_DISPLAY_BY_HARDWARE == what) {
                Log.i(TAG, "******MEDIA_INFO_VIDEO_DISPLAY_BY_HARDWARE******");
                bVideoDisplayByHardware = true;
            }

            if (myPlayerCallback != null) {
                myPlayerCallback.onInfo(mp, what, extra);
                return true;
            }
            return false;
        }
    };

    private MMediaPlayer.OnTimedTextListener mTimedTextListener = new OnTimedTextListener() {
        @Override
        public void onTimedText(MediaPlayer arg0, TimedText arg1) {
            if (arg1 != null && arg1.getText() != null) {
                Log.i(TAG, "********mTimedTextListener********" + arg1.getText());
                if (myPlayerCallback != null) {
                    myPlayerCallback.onUpdateSubtitle(arg1.getText());
                }
            } else {
                Log.i(TAG, "********mTimedTextListener********  null");
                if (myPlayerCallback != null) {
                    myPlayerCallback.onUpdateSubtitle(" ");
                }
            }
        }
    };

    private MMediaPlayer.OnSeekCompleteListener mMMediaPlayerSeekCompleteListener = new MMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            endSeekTime = System.currentTimeMillis();
            Log.i(TAG, ">>>SeekComplete>>>>>>seek time : "
                    + (endSeekTime - startSeekTime) + " ms");
            //setVoice(true);
            if (myPlayerCallback != null) {
                myPlayerCallback.onSeekComplete(mp);
            }
        }
    };

    /**
     * Surface relevant callback interface.
     */
    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int w,
                                   int h) {
            mSurfaceHolder = holder;
            Log.i(TAG, "*************surfaceChanged************" + w + " " + h);
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
            if (mMMediaPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
            }
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            // mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
            Log.i(TAG, "*************surfaceCreated************");
            openPlayer();

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            Log.i(TAG, "*************surfaceDestroyed************");
            release(true);
        }
    };

    /*
     * release the media player in any state.
     */
    private void release(boolean cleartargetstate) {
        Log.i(TAG, "***********release*******" + (mTargetState == STATE_IDLE));
        if (mTargetState == STATE_IDLE) {
            return;
        }
        mCurrentState = STATE_IDLE;
        if (cleartargetstate) {
            mTargetState = STATE_IDLE;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (mMMediaPlayer != null && mMMediaPlayer.isPlaying()) {
                        try {
                            mMMediaPlayer.stop();
                        } catch (IllegalStateException e) {
                            Log.i(TAG, "stop fail! please try again!");
                            try {
                                this.wait(2000);
                                mMMediaPlayer.stop();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    if (mMMediaPlayer != null) {
                        Log.i(TAG, "*****release start*****");
                        mMMediaPlayer.release();// release will done reset
                        Log.i(TAG, "*****release end*****");
                    }
                    mMMediaPlayer = null;
                }
            }).start();
        } else {
            if (mMMediaPlayer != null) {
                Log.i(TAG, "***********release Player");
                mMMediaPlayer.release();
            }
            mMMediaPlayer = null;
        }

    }

    public void start() {
        if (isInPlaybackState()) {
            mMMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    public void pause() {
        Log.i(TAG, "pause: mMMediaPlayer.isPlaying() " + mMMediaPlayer.isPlaying());
        if (isInPlaybackState()) {
            if (mMMediaPlayer.isPlaying()) {
                Log.i(TAG, "pause: ");
                mMMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public boolean isVideoWidthHeightEqualZero() {
        if (isInPlaybackState()) {
            if (mVideoWidth == 0 && mVideoHeight == 0) {
                Log.i(TAG, "isVideoWidthHeightEqualZero yes");
                return true;
            }
        }
        return false;
    }

    /**
     * cache duration as mDuration for faster access.
     *
     * @return
     */
    public int getDuration() {
        if (isInPlaybackState()) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = mMMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    /**
     * Get the current play time.
     *
     * @return
     */
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * Jump to a certain time.
     *
     * @param msec
     */
    public void seekTo(int msec) {
        Log.i(TAG, "seekTo start");
        if (isInPlaybackState()) {
            if (msec > getDuration()) {
                Log.i(TAG, "seekTo is bigger than Duration");
                return;
            }
            startSeekTime = System.currentTimeMillis();
            setVoice(false);
            mMMediaPlayer.seekTo(msec);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = msec;
        }
        Log.i(TAG, "seekTo end");
    }

    public boolean isPlaying() {
        Log.i(TAG, "isPlaying: ");
        if (mMMediaPlayer == null) {
            return false;
        }
        try {
            return isInPlaybackState() && mMMediaPlayer.isPlaying();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Determine whether normal play.
     *
     * @return
     */
    public boolean isInPlaybackState() {
        Log.i(TAG, "isInPlaybackState: ");
        return (mMMediaPlayer != null
                && mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING
                && mCurrentState != STATE_STREAMLESS_TO_NEXT);
    }

    public void setVoice(boolean isSetOpen) {
        Log.i(TAG, "booelan setVoice:" + isSetOpen);
        if (isInPlaybackState()) {
            if (isSetOpen) {
                Log.i(TAG, "mMMediaPlayer.setVolume:" + isSetOpen);
                mMMediaPlayer.setVolume(currentVoice, currentVoice);
                isVoiceOpen = true;
            } else {
                Log.i(TAG, "mMMediaPlayer.setVolume:" + isSetOpen);
                mMMediaPlayer.setVolume(0, 0);
                isVoiceOpen = false;
            }
        }
    }

    public void setVoice(int voice) {
        Log.i(TAG, "int setVoice:" + voice);
        if (isInPlaybackState()) {
            if (voice >= 0 && voice <= 10) {
                currentVoice = voice * 0.1f;
            }
            Log.i(TAG, "******currentVoice*******" + currentVoice);
            mMMediaPlayer.setVolume(currentVoice, currentVoice);
        }
    }

    /**
     * Register a callback to be invoked
     *
     * @param l The callback that will be run
     */
    public void setPlayerCallbackListener(playerCallback l) {
        myPlayerCallback = l;
    }


    /**
     * User callback interface.
     */
    public interface playerCallback {
        // error tip
        boolean onError(MediaPlayer mp, int framework_err, int impl_err);

        // play complete
        void onCompletion(MediaPlayer mp);

        boolean onInfo(MediaPlayer mp, int what, int extra);

        void onBufferingUpdate(MediaPlayer mp, int percent);

        void onPrepared(MediaPlayer mp);

        // Finish back
        void onSeekComplete(MediaPlayer mp);

        // Video began to play before, closed music.
        void onCloseMusic();

        void onUpdateSubtitle(String sub);

        void onVideoSizeChanged(MediaPlayer mp, int width, int height);
    }

    /****************************************/
    // mstar Extension APIs start

    /**
     * Set the speed of the video broadcast.
     *
     * @param speed
     * @return
     */
    public boolean setPlayMode(int speed) {
        Log.i(TAG, "setPlayMode: ");
        if (speed < -32 || speed > 32)
            return false;

        if (isInPlaybackState()) {
            Log.i(TAG, "****setPlayMode***" + speed);
            mMMediaPlayer.start();
            return mMMediaPlayer.setPlayMode(speed);
        }
        return false;
    }

    /**
     * For video broadcast speed.
     *
     * @return
     */
    public int getPlayMode() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getPlayMode();
        }
        return 64;
    }

    /**
     * get audio codec type.
     *
     * @return
     */
    public String getAudioCodecType() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getAudioCodecType();
        }
        return null;
    }

    /**
     * get video Info.
     *
     * @return
     */
    public VideoCodecInfo getVideoInfo() {
        if (isInPlaybackState()) {
            return mMMediaPlayer.getVideoInfo();
        }
        return null;
    }

    /**
     * Adds an external timed text source file.
     * <p>
     * Currently supported format is SubRip with the file extension .srt, case insensitive.
     * Note that a single external timed text source may contain multiple tracks in it.
     * One can find the total number of available tracks using {@link #getTrackInfo()} to see what
     * additional tracks become available after this method call.
     *
     * @param path     The file path of external timed text source file.
     * @param mimeType The mime type of the file. Must be one of the mime types listed above.
     * @throws IOException              if the file cannot be accessed or is corrupted.
     * @throws IllegalArgumentException if the mimeType is not supported.
     * @throws IllegalStateException    if called in an invalid state.
     */
    public void addTimedTextSource(String path, String mimeType) {
        if (isInPlaybackState()) {
            Log.i(TAG, "addTimedTextSource path:" + path + " mimeType:" + mimeType);
            try {
                mMMediaPlayer.addTimedTextSource(path, mimeType);
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
    }

    /**
     * Returns an array of track information.
     *
     * @return Array of track info. The total number of tracks is the array length.
     * Must be called again if an external timed text source has been added after any of the
     * addTimedTextSource methods are called.
     * @throws IllegalStateException if it is called in an invalid state.
     */
    public MediaPlayer.TrackInfo[] getTrackInfo() {
        if (isInPlaybackState()) {
            Log.i(TAG, "getTrackInfo");
            try {
                return mMMediaPlayer.getTrackInfo();
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
        return null;
    }

    /**
     * Returns an array of track information.
     *
     * @return Array of track info. The total number of tracks is the array length.
     * Must be called again if an external timed text source has been added after any of the
     * addTimedTextSource methods are called.
     * @throws IllegalStateException if it is called in an invalid state.
     */

    public MMediaPlayer.MsTrackInfo[] getMsTrackInfo() {
        if (isInPlaybackState()) {
            Log.i(TAG, "getMsTrackInfo");
            try {
                return mMMediaPlayer.getMsTrackInfo();
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
        return null;
    }

    public MMediaPlayer.MsTrackInfo getMsTrackInfo(int index) {
        if (isInPlaybackState()) {
            Log.i(TAG, "getMsTrackInfo index:" + index);
            try {
                MMediaPlayer.MsTrackInfo[] trackInfo = mMMediaPlayer.getMsTrackInfo();
                if (trackInfo != null && trackInfo.length > index) {
                    return trackInfo[index];
                }
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
        return null;
    }

    public int getMsAudioTrackCount() {
        return getMsTrackInfoCount(MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO);
    }

    public int getMsTimedTextTrackCount() {
        return getMsTrackInfoCount(MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT);
    }

    public int getMsTimedBitmapTrackCount() {
        return getMsTrackInfoCount(MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP);
    }

    private int[] mMsAudioTrackIndex = null;
    private int[] mMsTimedTextTrackIndex = null;
    private int[] mMsTimedBitmapTrackIndex = null;

    public int getMsTrackSelectedIndex(int type, int index) {
        switch (type) {
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                if (mMsAudioTrackIndex != null) {

                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                if (mMsTimedTextTrackIndex != null) {

                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP:
                if (mMsTimedBitmapTrackIndex != null) {

                }
                break;
            default:
                break;
        }
        return -1;
    }

    public void setMsTrackIndex(int type, int index) {
        switch (type) {
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                if (mMsAudioTrackIndex != null) {
                    selectTrack(mMsAudioTrackIndex[index]);
                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                if (mMsTimedTextTrackIndex != null) {
                    selectTrack(mMsTimedTextTrackIndex[index]);
                }
                break;
            case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP:
                if (mMsTimedBitmapTrackIndex != null) {
                    selectTrack(mMsTimedBitmapTrackIndex[index]);
                }
                break;
            default:
                break;
        }
    }

    public int getMsTrackInfoCount(int type) {
        Log.i(TAG, "getMsTrackInfoCount type:" + type);
        int getMsTrackInfoCount = 0;
        if (isInPlaybackState()) {
            MMediaPlayer.MsTrackInfo[] getMsTrackInfo = getMsTrackInfo();
            if (getMsTrackInfo != null) {
                Log.i(TAG, "getMsTrackInfo.length:" + getMsTrackInfo.length);

                // Get TrackType Count
                int length = getMsTrackInfo.length;
                for (int i = 0; i < length; i++) {
                    Log.i(TAG, "getMsTrackInfo[" + i + "].getTrackType:" + getMsTrackInfo[i].getTrackType());
                    if (type == getMsTrackInfo[i].getTrackType()) {
                        getMsTrackInfoCount++;
                    }
                }

                // Product Model: MStar Android TV,19,4.4.4
                Log.i(TAG, "Product Model: " + android.os.Build.MODEL + "," + Build.VERSION.SDK_INT + "," + android.os.Build.VERSION.RELEASE);
                // Check if we're running on Android 5.0 or higher
                // public static final int LOLLIPOP = 21;
                if (Build.VERSION.SDK_INT < 21) {
                    int[] trackIndex = null;
                    if (getMsTrackInfoCount > 0) {
                        trackIndex = new int[getMsTrackInfoCount];
                    }

                    // Store TrackType Index
                    int j = 0;
                    for (int i = 0; i < length; i++) {
                        Log.i(TAG, "getMsTrackInfo[" + i + "].getTrackType:" + getMsTrackInfo[i].getTrackType());
                        if (type == getMsTrackInfo[i].getTrackType()) {
                            trackIndex[j++] = i;
                        }
                    }

                    switch (type) {
                        case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                            mMsAudioTrackIndex = new int[getMsTrackInfoCount];
                            mMsAudioTrackIndex = trackIndex;
                            break;
                        case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                            mMsTimedTextTrackIndex = new int[getMsTrackInfoCount];
                            mMsTimedTextTrackIndex = trackIndex;
                            break;
                        case MMediaPlayer.MsTrackInfo.MEDIA_TRACK_TYPE_TIMEDBITMAP:
                            mMsTimedBitmapTrackIndex = new int[getMsTrackInfoCount];
                            mMsTimedBitmapTrackIndex = trackIndex;
                            break;
                        default:
                            break;
                    }
                }
                Log.i(TAG, "getMsTrackInfoCount:" + getMsTrackInfoCount);
            }
        }
        return getMsTrackInfoCount;
    }


    /**
     * Selects a track.
     * <p>
     * If a MediaPlayer is in invalid state, it throws an IllegalStateException exception.
     * If a MediaPlayer is in <em>Started</em> state, the selected track is presented immediately.
     * If a MediaPlayer is not in Started state, it just marks the track to be played.
     * </p>
     * <p>
     * In any valid state, if it is called multiple times on the same type of track (ie. Video,
     * Audio, Timed Text), the most recent one will be chosen.
     * </p>
     * <p>
     * The first audio and video tracks are selected by default if available, even though
     * this method is not called. However, no timed text track will be selected until
     * this function is called.
     * </p>
     * <p>
     * Currently, only timed text tracks or audio tracks can be selected via this method.
     * In addition, the support for selecting an audio track at runtime is pretty limited
     * in that an audio track can only be selected in the <em>Prepared</em> state.
     * </p>
     *
     * @param index the index of the track to be selected. The valid range of the index
     *              is 0..total number of track - 1. The total number of tracks as well as the type of
     *              each individual track can be found by calling {@link #getTrackInfo()} method.
     * @throws IllegalStateException if called in an invalid state.
     * @see android.media.MediaPlayer#getTrackInfo
     */
    public void selectTrack(int index) {
        if (isInPlaybackState()) {
            Log.i(TAG, "selectTrack");
            try {
                mMMediaPlayer.selectTrack(index);
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
    }

    /**
     * Deselect a track.
     * <p>
     * Currently, the track must be a timed text track and no audio or video tracks can be
     * deselected. If the timed text track identified by index has not been
     * selected before, it throws an exception.
     * </p>
     *
     * @param index the index of the track to be deselected. The valid range of the index
     *              is 0..total number of tracks - 1. The total number of tracks as well as the type of
     *              each individual track can be found by calling {@link #getTrackInfo()} method.
     * @throws IllegalStateException if called in an invalid state.
     * @see android.media.MediaPlayer#getTrackInfo
     */
    public void deselectTrack(int index) {
        if (isInPlaybackState()) {
            Log.i(TAG, "deselectTrack");
            try {
                mMMediaPlayer.deselectTrack(index);
            } catch (Exception e) {
                Log.i(TAG, "Exception:" + e);
            }
        }
    }

    /**
     * check mvc.
     */
    public boolean isMVCSource() {
        if (isInPlaybackState()) {
            VideoCodecInfo vcInfo = mMMediaPlayer.getVideoInfo();
            if (vcInfo != null) {
                String vcType = vcInfo.getCodecType();
                Log.i(TAG, "getCodecType:" + vcType);
                if (MVC.equals(vcType)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean imageRotate(int degrees) {
        return mMMediaPlayer.ImageRotate(degrees, false);
    }

    public boolean imageRotate(int degrees, MMediaPlayer mp) {
        return mp.ImageRotate(degrees, false);
    }

    public void setFreezeLastFrameMode(final boolean isOn) {
        MMediaPlayer.EnumPlayerSeamlessMode mode = MMediaPlayer.EnumPlayerSeamlessMode.E_PLAYER_SEAMLESS_NONE;
        if (isOn) {
            mode = MMediaPlayer.EnumPlayerSeamlessMode.E_PLAYER_SEAMLESS_DS;
        }
        if (null != mMMediaPlayer) {
            Log.i(TAG, "setFreezeLastFrameMode(), mode = " + mode);
            mMMediaPlayer.SetSeamlessMode(mode);
        }
    }
    // mstar Extension APIs end
    /********************************************************/
}
