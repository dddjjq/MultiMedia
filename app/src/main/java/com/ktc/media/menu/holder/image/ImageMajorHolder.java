package com.ktc.media.menu.holder.image;

import android.content.Context;

import com.ktc.media.R;
import com.ktc.media.menu.entity.MajorMenuEntity;
import com.ktc.media.menu.holder.BaseEntityHolder;
import com.ktc.media.model.FileData;

import java.util.ArrayList;
import java.util.List;

public class ImageMajorHolder extends BaseEntityHolder {

    private String[] imageMenuTitleList;
    private int[] imageMenuTitleImage = {R.drawable.menu_info_image, R.drawable.menu_picture_image
            , R.drawable.menu_setting_image, R.drawable.menu_slide_image
            , R.drawable.menu_repeat_image};
    private ImageMinorHolder mMinorHolder;
    private List<MajorMenuEntity> mMajorMenuEntities;
    private Context mContext;

    public ImageMajorHolder(Context context, FileData fileData) {
        mContext = context;
        mMinorHolder = new ImageMinorHolder(mContext, fileData);
        mMajorMenuEntities = new ArrayList<>();
        imageMenuTitleList = mContext.getResources().getStringArray(R.array.picture_menu_list);
        init();
    }

    @Override
    public List<MajorMenuEntity> getMajorMenuEntities() {
        return mMajorMenuEntities;
    }

    @Override
    public void init() {
        initInfoEntity();
        initPictureModeEntity();
        initSettingEntity();
        initPPTDurationEntity();
        initRepeatEntity();
    }

    private void initInfoEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(imageMenuTitleList[0]);
        majorMenuEntity.setImageRes(imageMenuTitleImage[0]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getPictureInfoEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initPictureModeEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(imageMenuTitleList[1]);
        majorMenuEntity.setImageRes(imageMenuTitleImage[1]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getPictureModeEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initSettingEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(imageMenuTitleList[2]);
        majorMenuEntity.setImageRes(imageMenuTitleImage[2]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getPictureSettingEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initPPTDurationEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(imageMenuTitleList[3]);
        majorMenuEntity.setImageRes(imageMenuTitleImage[3]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getPPTDurationEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }

    private void initRepeatEntity() {
        MajorMenuEntity majorMenuEntity = new MajorMenuEntity();
        majorMenuEntity.setTextString(imageMenuTitleList[4]);
        majorMenuEntity.setImageRes(imageMenuTitleImage[4]);
        majorMenuEntity.setMinorMenuEntities(mMinorHolder.getPictureRepeatModeEntity());
        mMajorMenuEntities.add(majorMenuEntity);
    }
}
