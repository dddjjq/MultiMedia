package com.ktc.media.menu.holder.music;

import android.content.Context;
import android.view.View;

import com.ktc.media.R;
import com.ktc.media.media.music.MusicPlayerActivity;
import com.ktc.media.media.util.Tools;
import com.ktc.media.menu.entity.MinorMenuEntity;
import com.ktc.media.menu.entity.MinorType;
import com.ktc.media.menu.view.MinorMenuView;
import com.ktc.media.model.MusicData;
import com.ktc.media.tv.sound.SoundModeManager;

import java.util.ArrayList;
import java.util.List;

public class MusicMinorHolder {

    private Context mContext;
    private MusicData mMusicData;
    private MusicPlayerActivity mMusicPlayerActivity;
    private List<MinorMenuEntity> mRepeatModeList;
    private List<MinorMenuEntity> mSoundModeList;
    private SoundModeManager mSoundModeManager;

    public MusicMinorHolder(Context context, MusicData musicData) {
        mContext = context;
        mMusicData = musicData;
        mMusicPlayerActivity = (MusicPlayerActivity) mContext;
        mSoundModeManager = SoundModeManager.getInstance(context);
    }

    public List<MinorMenuEntity> getMusicInfoEntity() {
        if (mMusicData == null) return null;
        List<MinorMenuEntity> entities = new ArrayList<>();
        entities.add(getInfoNameEntity());
        entities.add(getInfoDurationEntity());
        entities.add(getInfoArtistEntity());
        entities.add(getInfoFormatEntity());
        entities.add(getInfoCodecEntity());
        return entities;
    }

    private MinorMenuEntity getInfoNameEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_name)
                + " " + mMusicData.getSongName());
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoDurationEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_duration)
                + " " + Tools
                .formatDuration(MusicPlayerActivity.countTime));
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoArtistEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_artist)
                + " " + mMusicData.getArtist());
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoFormatEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_format)
                + " " + mMusicData.getPath().substring(mMusicData.getPath().lastIndexOf(".") + 1,
                mMusicData.getPath().length()));
        return minorMenuEntity;
    }

    private MinorMenuEntity getInfoCodecEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        String audioCodec = mMusicPlayerActivity.getAudioCodecType();
        if (null != audioCodec) {
            if ("AC3".equals(audioCodec)) {
                minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_codec)
                        + " " + "Dolby Digital (AC-3)");
            } else if ("EAC3".equals(audioCodec)) {
                minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_codec)
                        + " " + "Dolby Digital Plus (EAC-3)");
            } else {
                minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_codec)
                        + " " + audioCodec);
            }
        } else {
            minorMenuEntity.setTextString(mContext.getResources().getString(R.string.music_info_codec)
                    + " " + mContext.getResources().getString(
                    R.string.unknown));
        }
        return minorMenuEntity;
    }

    public List<MinorMenuEntity> getMusicSoundModeEntity() {
        String[] soundModes = mContext.getResources().getStringArray(R.array.music_menu_sound_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < soundModes.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(soundModes[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (i == mSoundModeManager.getKtcSoundMode()) {
                minorMenuEntity.setSelected(true);
            }
            addSoundModeEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        mSoundModeList = entities;
        return entities;
    }

    public List<MinorMenuEntity> getMusicSettingEntity() {
        String[] settingArrays = mContext.getResources().getStringArray(R.array.menu_setting_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (String s : settingArrays) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(s);
            minorMenuEntity.setType(MinorType.TYPE_NORMAL);
            entities.add(minorMenuEntity);
        }
        return entities;
    }

    public List<MinorMenuEntity> getMusicRepeatEntity() {
        String[] repeatArrays = mContext.getResources().getStringArray(R.array.music_menu_repeat_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < repeatArrays.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(repeatArrays[i]);
            if (i == mMusicPlayerActivity.getPlayMode())
                minorMenuEntity.setSelected(true);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            addRepeatEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        mRepeatModeList = entities;
        return entities;
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
                mMusicPlayerActivity.changePlayMode(position);
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
