package com.ktc.media.menu.holder.video;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.ktc.media.R;
import com.ktc.media.media.util.Tools;
import com.ktc.media.media.video.VideoPlayerActivity;
import com.ktc.media.menu.entity.MinorMenuEntity;
import com.ktc.media.menu.entity.MinorType;
import com.ktc.media.menu.view.MinorMenuView;
import com.ktc.media.model.VideoData;
import com.ktc.media.tv.picture.PictureModeManager;
import com.ktc.media.tv.sound.SoundModeManager;
import com.ktc.media.tv.zoom.ZoomModeManager;
import com.ktc.media.util.FileSizeUtil;
import com.mstar.android.media.VideoCodecInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoMinorHolder {

    private Context mContext;
    private Resources mResources;
    private VideoData mVideoData;
    private VideoPlayerActivity mVideoPlayerActivity;
    private List<MinorMenuEntity> mRepeatModeList;
    private List<MinorMenuEntity> mZoomModeList;
    private List<MinorMenuEntity> mPictureModeList;
    private List<MinorMenuEntity> mSoundModeList;
    private ZoomModeManager mZoomModeManager;
    private PictureModeManager mPictureModeManager;
    private SoundModeManager mSoundModeManager;

    public VideoMinorHolder(Context context, VideoData videoData) {
        mContext = context;
        mResources = context.getResources();
        mVideoData = videoData;
        mVideoPlayerActivity = (VideoPlayerActivity) context;
        mZoomModeManager = ZoomModeManager.getInstance(context);
        mPictureModeManager = PictureModeManager.getInstance(context);
        mSoundModeManager = SoundModeManager.getInstance(context);
    }

    public List<MinorMenuEntity> getVideoInfoEntities() {
        if (mVideoData == null) return null;
        List<MinorMenuEntity> entities = new ArrayList<>();
        entities.add(getInfoNameEntity());
        entities.add(getInfoDurationEntity());
        entities.add(getInfoSizeEntity());
        entities.add(getInfoFormatEntity());
        entities.add(getInfoSoundCodecEntity());
        entities.add(getInfoVideoCodecEntity());
        return entities;
    }

    private MinorMenuEntity getInfoNameEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mResources.getString(R.string.video_info_name)
                + " " + mVideoData.getName());
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoDurationEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        int duration = mVideoPlayerActivity.videoPlayerHolder.mVideoPlayView.getDuration();
        minorMenuEntity.setTextString(mResources.getString(R.string.video_info_duration)
                + " " + Tools.formatDuration(duration));
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoSizeEntity() {
        File file = new File(mVideoData.getPath());
        if (!file.exists()) return null;
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mResources.getString(R.string.video_info_size)
                + " " + FileSizeUtil.getFileSizeDescription(file.getAbsolutePath()));
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoFormatEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mResources.getString(R.string.video_info_format)
                + " " + mVideoData.getPath().substring(mVideoData.getPath().lastIndexOf(".") + 1,
                mVideoData.getPath().length()));
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoSoundCodecEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        String audioCodec = mVideoPlayerActivity.videoPlayerHolder.mVideoPlayView.getAudioCodecType();
        minorMenuEntity.setTextString(mResources.getString(R.string.video_info_audio_codec)
                + " " + audioCodec);
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoVideoCodecEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        VideoCodecInfo videoCodec = mVideoPlayerActivity.videoPlayerHolder.mVideoPlayView.getVideoInfo();
        if (videoCodec == null) return null;
        String videoCodecType = videoCodec.getCodecType();
        minorMenuEntity.setTextString(mResources.getString(R.string.video_info_video_codec)
                + " " + videoCodecType);
        return minorMenuEntity;
    }

    public List<MinorMenuEntity> getVideoZoomEntities() {
        String[] zoomList = mResources.getStringArray(R.array.video_menu_zoom_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < zoomList.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(zoomList[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (i == mZoomModeManager.getKtcZoomModeValueInt()) {
                minorMenuEntity.setSelected(true);
            }
            addZoomEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        mZoomModeList = entities;
        return entities;
    }

    public List<MinorMenuEntity> getVideoPictureModeEntities() {
        String[] pictureModeList = mResources.getStringArray(R.array.video_menu_picture_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < pictureModeList.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(pictureModeList[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (i == mPictureModeManager.getKtcPictureModeValueInt()) {
                minorMenuEntity.setSelected(true);
            }
            addPictureModeEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        mPictureModeList = entities;
        return entities;
    }

    public List<MinorMenuEntity> getVideoSoundModeEntities() {
        String[] soundModeList = mResources.getStringArray(R.array.video_menu_sound_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < soundModeList.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(soundModeList[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (mSoundModeManager.getKtcSoundMode() == i) {
                minorMenuEntity.setSelected(true);
            }
            addSoundModeEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        mSoundModeList = entities;
        return entities;
    }

    public List<MinorMenuEntity> getVideoSettingEntities() {
        String[] settingList = mResources.getStringArray(R.array.menu_setting_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < settingList.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(settingList[i]);
            minorMenuEntity.setType(MinorType.TYPE_NORMAL);
            entities.add(minorMenuEntity);
        }
        return entities;
    }

    public List<MinorMenuEntity> getVideoRepeatModeEntities() {
        String[] repeatModeList = mResources.getStringArray(R.array.video_menu_repeat_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < repeatModeList.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(repeatModeList[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (i == mVideoPlayerActivity.getPlayMode())
                minorMenuEntity.setSelected(true);
            addRepeatEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        mRepeatModeList = entities;
        return entities;
    }

    private void addZoomEntityListener(final MinorMenuEntity minorMenuEntity, final int position) {
        MinorMenuView.OnItemClickListener onItemClickListener = new MinorMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                mZoomModeManager.setKtcZoomMode(position);
                for (MinorMenuEntity entity : mZoomModeList) {
                    if (minorMenuEntity == entity) {
                        entity.setSelected(true);
                    } else {
                        entity.setSelected(false);
                    }
                }
            }
        };
        minorMenuEntity.setListener(onItemClickListener);
    }

    private void addPictureModeEntityListener(final MinorMenuEntity minorMenuEntity, final int position) {
        MinorMenuView.OnItemClickListener onItemClickListener = new MinorMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                mPictureModeManager.setKtcPictureMode(position);
                for (MinorMenuEntity entity : mPictureModeList) {
                    if (minorMenuEntity == entity) {
                        entity.setSelected(true);
                    } else {
                        entity.setSelected(false);
                    }
                }
            }
        };
        minorMenuEntity.setListener(onItemClickListener);
    }

    private void addSoundModeEntityListener(final MinorMenuEntity minorMenuEntity, final int position) {
        MinorMenuView.OnItemClickListener onItemClickListener = new MinorMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                mSoundModeManager.setKtcSoundMode(position);
                for (MinorMenuEntity entity : mSoundModeList) {
                    if (minorMenuEntity == entity) {
                        entity.setSelected(true);
                    } else {
                        entity.setSelected(false);
                    }
                }
            }
        };
        minorMenuEntity.setListener(onItemClickListener);
    }

    private void addRepeatEntityListener(final MinorMenuEntity minorMenuEntity, final int position) {
        MinorMenuView.OnItemClickListener onClickListener = new MinorMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(View v) {
                mVideoPlayerActivity.changePlayMode(position);
                for (MinorMenuEntity entity : mRepeatModeList) {
                    if (minorMenuEntity == entity) {
                        entity.setSelected(true);
                    } else {
                        entity.setSelected(false);
                    }
                }
            }
        };
        minorMenuEntity.setListener(onClickListener);
    }
}
