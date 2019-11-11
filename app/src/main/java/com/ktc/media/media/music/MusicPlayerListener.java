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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.ktc.media.R;
import com.ktc.media.media.util.ToastFactory;
import com.ktc.media.media.view.MediaControllerView;
import com.ktc.media.model.MusicData;

import java.io.RandomAccessFile;
import java.util.List;
import java.util.Locale;

import static com.ktc.media.media.music.MusicPlayerActivity.HANDLE_MESSAGE_SEEKBAR_UPDATE;

public class MusicPlayerListener implements View.OnClickListener, MediaControllerView.OnMediaEventRefreshListener {

    private static final String TAG = "MusicPlayerListener";
    private MusicPlayerActivity mMusicPlayerActivity;
    private MusicPlayerViewHolder mMusicPlayerViewHolder;
    // playMode
    public static final int ORDER = 0;//顺序播放
    public static final int RANDOM = 1;//随机播放
    public static final int SINGE = 2;//单曲循环
    public static final int LIST = 3;//列表循环

    public static int currentPlayMode = 3;
    private static final String LOCAL_MEDIA = "localMedia";
    private static final String PLAYMODE = "playMode";

    // Music is in the play
    protected static boolean isPlaying = true;

    public MusicPlayerListener(MusicPlayerActivity musicPlayerActivity,
                               MusicPlayerViewHolder musicPlayerViewHolder) {
        this.mMusicPlayerActivity = musicPlayerActivity;
        this.mMusicPlayerViewHolder = musicPlayerViewHolder;
    }

    protected void addMusicListener() {
        mMusicPlayerViewHolder.mMediaControllerView
                .setOnSeekBarChangeListener(seekBarChangeListener);
        mMusicPlayerActivity.musicPlayHandle
                .sendEmptyMessage(HANDLE_MESSAGE_SEEKBAR_UPDATE);
        mMusicPlayerViewHolder.mMediaControllerView.setOnControllerClickListener(mOnControllerClickListener);
        mMusicPlayerViewHolder.mMediaControllerView.mediaSeekBar.setOnClickListener(this);
        mMusicPlayerViewHolder.mMediaControllerView.setOnMediaEventRefreshListener(this);
    }

    private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            refreshController();
            int position = seekBar.getProgress();
            int duration = MusicPlayerActivity.countTime;
            if (duration > 0) {
                mMusicPlayerActivity.seekTo(position);
            } else {
                String strMessage = "This operation is not supported !";
                ToastFactory.showToast(mMusicPlayerActivity, strMessage, Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser) {
                refreshController();
            }
            mMusicPlayerViewHolder.mMediaControllerView.onProgressChanged(seekBar, progress, fromUser);
            if (fromUser) {
                int duration = MusicPlayerActivity.countTime;
                if (duration > 0) {
                    mMusicPlayerActivity.seekTo(progress);
                    mMusicPlayerActivity.musicPlayHandle.sendEmptyMessage(HANDLE_MESSAGE_SEEKBAR_UPDATE);
                } else {
                    String strMessage = "This operation is not supported !";
                    ToastFactory.showToast(mMusicPlayerActivity, strMessage, Toast.LENGTH_SHORT);
                }
            }
        }
    };

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
        SharedPreferences preference = mMusicPlayerActivity
                .getSharedPreferences(LOCAL_MEDIA, MusicPlayerActivity.MODE_PRIVATE);
        Editor editor = preference.edit();
        editor.putInt(PLAYMODE, mode);
        editor.apply();
    }

    public int getPlayMode() {
        SharedPreferences preference = mMusicPlayerActivity
                .getSharedPreferences(LOCAL_MEDIA, MusicPlayerActivity.MODE_PRIVATE);
        return preference.getInt(PLAYMODE, LIST);
    }

    /**
     * Show the current information songs
     */
    @SuppressLint("all")
    protected void setSongsContent(List<MusicData> musicList, int currentPosition) {
        Log.i(TAG, "setSongsContent currentPosition:" + currentPosition);
        if (musicList == null || musicList.size() < 1) {
            Log.i(TAG, "setSongsContent musicList:" + musicList + " musicList.size():" + musicList);
            return;
        }
        if (currentPosition < 0 || currentPosition > (musicList.size() - 1)) {
            Log.i(TAG, "setSongsContent currentPosition:" + currentPosition + " musicList.size():" + musicList.size());
            return;
        }
        mMusicPlayerViewHolder.articleText.setText("");
        mMusicPlayerViewHolder.albumText.setText("");
        if (musicList.get(currentPosition).getArtist() != null) {
            mMusicPlayerViewHolder.articleText
                    .setText(mMusicPlayerActivity.getResources().getString(
                            R.string.singer)
                            + " " + dealWithMp3Messy(musicList.get(currentPosition)).getArtist());
        }
        if (musicList.get(currentPosition).getAlbumName() != null) {
            mMusicPlayerViewHolder.albumText
                    .setText(mMusicPlayerActivity.getResources().getString(
                            R.string.album_text)
                            + " " + dealWithMp3Messy(musicList.get(currentPosition)).getAlbumName());
        }
        if (musicList.get(currentPosition).getName() != null) {
            mMusicPlayerViewHolder.titleText
                    .setText(musicList.get(currentPosition).getSongName());
        }
    }

    private void showMusicSetting() {
        mMusicPlayerActivity.startMenu();
        mMusicPlayerViewHolder.setAllCanFocus(false);
    }

    private synchronized MusicData dealWithMp3Messy(MusicData currentMusic) {
        String country = Locale.getDefault().getCountry();
        if ((!country.equalsIgnoreCase("CN"))
                && (!country.equalsIgnoreCase("HK"))
                && (!country.equalsIgnoreCase("TW"))) {
            return currentMusic;
        }
        String currentMp3Path = currentMusic.getPath();
        if (!currentMp3Path.toLowerCase().trim().endsWith("mp3")) {
            return currentMusic;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(currentMp3Path, "r");
            raf.seek(raf.length() - 128);
            byte[] buff = new byte[128];
            raf.read(buff);
            String Tag = new String(buff, 0, 3, "GBK");
            if (buff.length == 128 && "TAG".equals(Tag)) {
                String artist = new String(buff, 33, 30, "GBK").trim();
                Log.i(TAG, "deal with mp3 messy,mp3 artist=******" + artist + "****** and currentMp3Path=" + currentMp3Path);
                if (!TextUtils.isEmpty(artist)) {
                    currentMusic.setArtist(artist);
                    return currentMusic;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return currentMusic;
    }

    private boolean refreshController() {
        if (!mMusicPlayerViewHolder.mMediaControllerView.mIsControllerShow) {
            mMusicPlayerActivity.showController(false);
            mMusicPlayerActivity.hideControlDelay();
            return true;
        } else {
            mMusicPlayerActivity.hideControlDelay();
        }
        return false;
    }

    private MediaControllerView.OnControllerClickListener mOnControllerClickListener
            = new MediaControllerView.OnControllerClickListener() {
        @Override
        public void onPreClick() {
            if (!refreshController()) {
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
                mMusicPlayerActivity.isNextMusic = false;
                mMusicPlayerActivity.clickable = true;
                mMusicPlayerActivity.musicPlayHandle
                        .removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SERVICE_START);
                mMusicPlayerActivity.processPlayCompletion();
                isPlaying = true;
            }
        }

        @Override
        public void onPlayPauseClick() {
            if (!refreshController()) {
                mMusicPlayerActivity.pauseMusic();
            }
        }

        @Override
        public void onNextClick() {
            if (!refreshController()) {
                mMusicPlayerViewHolder.mMediaControllerView.setPlayPauseStatus(false);
                mMusicPlayerActivity.isNextMusic = true;
                mMusicPlayerActivity.clickable = true;
                mMusicPlayerActivity.musicPlayHandle
                        .removeMessages(MusicPlayerActivity.HANDLE_MESSAGE_SERVICE_START);
                mMusicPlayerActivity.processPlayCompletion();
                isPlaying = true;
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
                showMusicSetting();
                mMusicPlayerActivity.hideController();
            }
        }
    };

    @Override
    public void onClick(View v) {
        refreshController();
    }

    @Override
    public void onEventRefresh() {
        refreshController();
    }
}
