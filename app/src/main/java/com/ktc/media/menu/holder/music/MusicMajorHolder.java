package com.ktc.media.menu.holder.music;

import android.content.Context;

import com.ktc.media.R;
import com.ktc.media.menu.entity.MajorMenuEntity;
import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.model.MusicData;

import java.util.ArrayList;
import java.util.List;

public class MusicMajorHolder extends BaseEntityHolder {

    private String[] musicMenuTitleList;
    private int[] musicMenuTitleImage = {R.drawable.menu_info_image, R.drawable.menu_sound_image
            , R.drawable.menu_setting_image, R.drawable.menu_repeat_image};
    private MusicMinorHolder mMinorHolder;
    private List<MajorMenuEntity> mMajorMenuEntities;

    public MusicMajorHolder(Context context, MusicData musicData) {
        mMinorHolder = new MusicMinorHolder(context, musicData);
        musicMenuTitleList = context.getResources().getStringArray(R.array.music_menu_list);
        mMajorMenuEntities = new ArrayList<>();
        init();
    }

    @Override
    public List<MajorMenuEntity> getMajorMenuEntities() {
        return mMajorMenuEntities;
    }

    @Override
    public void init() {
        initInfoEntity();
        initSoundModeEntity();
        initSettingEntity();
        initRepeatEntity();
    }

    private void initInfoEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(musicMenuTitleList[0]);
        majorMenuEntity.setImageRes(musicMenuTitleImage[0]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getMusicInfoEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initSoundModeEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(musicMenuTitleList[1]);
        majorMenuEntity.setImageRes(musicMenuTitleImage[1]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getMusicSoundModeEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initSettingEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(musicMenuTitleList[2]);
        majorMenuEntity.setImageRes(musicMenuTitleImage[2]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getMusicSettingEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initRepeatEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(musicMenuTitleList[3]);
        majorMenuEntity.setImageRes(musicMenuTitleImage[3]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getMusicRepeatEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }
}
