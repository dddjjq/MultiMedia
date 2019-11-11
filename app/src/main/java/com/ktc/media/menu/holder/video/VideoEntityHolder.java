package com.ktc.media.menu.holder.video;

import android.content.Context;

import com.ktc.media.R;
import com.ktc.media.menu.entity.MajorMenuEntity;
import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.model.VideoData;

import java.util.ArrayList;
import java.util.List;

public class VideoEntityHolder extends BaseEntityHolder {

    private String[] videoMenuTitleList;
    private int[] videoMenuTitleImage = {R.drawable.menu_info_image, R.drawable.menu_zoom_image
            , R.drawable.menu_picture_image, R.drawable.menu_sound_image, R.drawable.menu_setting_image
            , R.drawable.menu_repeat_image};
    private VideoMinorHolder mMinorHolder;
    private List<MajorMenuEntity> mMajorMenuEntities;

    public VideoEntityHolder(Context context, VideoData videoData) {
        videoMenuTitleList = context.getResources().getStringArray(R.array.video_menu_list);
        mMinorHolder = new VideoMinorHolder(context, videoData);
        mMajorMenuEntities = new ArrayList<>();
        init();
    }

    public List<MajorMenuEntity> getMajorMenuEntities() {
        return mMajorMenuEntities;
    }

    public void init() {
        initInfoEntity();
        //initZoomEntity();
        initPictureEntity();
        initSoundEntity();
        //initSettingEntity();
        initRepeatEntity();
    }

    private void initInfoEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(videoMenuTitleList[0]);
        majorMenuEntity.setImageRes(videoMenuTitleImage[0]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getVideoInfoEntities());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initZoomEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(videoMenuTitleList[1]);
        majorMenuEntity.setImageRes(videoMenuTitleImage[1]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getVideoZoomEntities());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initPictureEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(videoMenuTitleList[2]);
        majorMenuEntity.setImageRes(videoMenuTitleImage[2]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getVideoPictureModeEntities());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initSoundEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(videoMenuTitleList[3]);
        majorMenuEntity.setImageRes(videoMenuTitleImage[3]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getVideoSoundModeEntities());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initSettingEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(videoMenuTitleList[4]);
        majorMenuEntity.setImageRes(videoMenuTitleImage[4]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getVideoSettingEntities());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initRepeatEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(videoMenuTitleList[5]);
        majorMenuEntity.setImageRes(videoMenuTitleImage[5]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getVideoRepeatModeEntities());
        mMajorMenuEntities.add(majorMenuEntity);
    }
}
