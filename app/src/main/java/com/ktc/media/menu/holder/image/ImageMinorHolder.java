package com.ktc.media.menu.holder.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import com.ktc.media.R;
import com.ktc.media.media.photo.ImagePlayerActivity;
import com.ktc.media.menu.entity.MinorMenuEntity;
import com.ktc.media.menu.entity.MinorType;
import com.ktc.media.menu.view.MinorMenuView;
import com.ktc.media.model.FileData;
import com.ktc.media.tv.picture.PictureModeManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageMinorHolder {

    private Resources mResources;
    private FileData mFileData;
    private ImagePlayerActivity mImagePlayerActivity;
    private List<MinorMenuEntity> pptDurationEntities;
    private List<MinorMenuEntity> repeatModeList;
    private List<MinorMenuEntity> pictureModeList;
    private PictureModeManager mPictureModeManager;

    public ImageMinorHolder(Context context, FileData fileData) {
        mResources = context.getResources();
        mFileData = fileData;
        mImagePlayerActivity = (ImagePlayerActivity) context;
        mPictureModeManager = PictureModeManager.getInstance(context);
    }

    public List<MinorMenuEntity> getPictureInfoEntity() {
        if (mFileData == null) return null;
        List<MinorMenuEntity> entities = new ArrayList<>();
        entities.add(getInfoNameEntity());
        entities.add(getModifyTimeEntity());
        entities.add(getSizeEntity());
        entities.add(getFormatEntity());
        return entities;
    }

    private MinorMenuEntity getInfoNameEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setTextString(mResources.getString(R.string.picture_info_name)
                + " " + mFileData.getName());
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        return minorMenuEntity;
    }

    @SuppressLint("all")
    private MinorMenuEntity getModifyTimeEntity() {
        File file = new File(mFileData.getPath());
        if (file.exists()) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            long modifiedTime = file.lastModified();
            Date date = new Date(modifiedTime);
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (modifiedTime != 0) {
                minorMenuEntity.setTextString(mResources.getString(R.string.picture_info_time)
                        + " " + mFormat.format(date));
            }
            minorMenuEntity.setType(MinorType.TYPE_NORMAL);
            return minorMenuEntity;
        }
        return null;
    }

    private MinorMenuEntity getSizeEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setTextString(mResources.getString(R.string.picture_info_size)
                + " " + mFileData.getSizeDescription());
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        return minorMenuEntity;
    }

    private MinorMenuEntity getFormatEntity() {
        MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
        minorMenuEntity.setTextString(mResources.getString(R.string.picture_info_format)
                + " " + mFileData.getPath().substring(mFileData.getPath().lastIndexOf(".") + 1,
                mFileData.getPath().length()));
        minorMenuEntity.setType(MinorType.TYPE_NORMAL);
        return minorMenuEntity;
    }

    public List<MinorMenuEntity> getPictureModeEntity() {
        String[] pictureModeArrays = mResources.getStringArray(R.array.picture_menu_mode_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < pictureModeArrays.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(pictureModeArrays[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (i == mPictureModeManager.getKtcPictureModeValueInt()) {
                minorMenuEntity.setSelected(true);
            }
            addPictureModeEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        pictureModeList = entities;
        return entities;
    }

    public List<MinorMenuEntity> getPictureSettingEntity() {
        String[] pictureSettingArrays = mResources.getStringArray(R.array.menu_setting_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (String s : pictureSettingArrays) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(s);
            minorMenuEntity.setType(MinorType.TYPE_NORMAL);
            entities.add(minorMenuEntity);
        }
        return entities;
    }

    public List<MinorMenuEntity> getPPTDurationEntity() {
        String[] picturePPTDurationArrays = mResources.getStringArray(R.array.picture_menu_ppt_duration_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < picturePPTDurationArrays.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(picturePPTDurationArrays[i]);
            if (i == mImagePlayerActivity.getPPTDurationTimeType())
                minorMenuEntity.setSelected(true);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            addPPTDurationListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        pptDurationEntities = entities;
        return entities;
    }

    public List<MinorMenuEntity> getPictureRepeatModeEntity() {
        String[] pictureRepeatArrays = mResources.getStringArray(R.array.picture_menu_repeat_list);
        List<MinorMenuEntity> entities = new ArrayList<>();
        for (int i = 0; i < pictureRepeatArrays.length; i++) {
            MinorMenuEntity minorMenuEntity = new MinorMenuEntity();
            minorMenuEntity.setTextString(pictureRepeatArrays[i]);
            minorMenuEntity.setType(MinorType.TYPE_POINT);
            if (i == mImagePlayerActivity.getPlayMode())
                minorMenuEntity.setSelected(true);
            addRepeatEntityListener(minorMenuEntity, i);
            entities.add(minorMenuEntity);
        }
        repeatModeList = entities;
        return entities;
    }

    private void addPictureModeEntityListener(final MinorMenuEntity minorMenuEntity, final int position) {
        MinorMenuView.OnItemClickListener onItemClickListener = new MinorMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                mPictureModeManager.setKtcPictureMode(position);
                for (MinorMenuEntity entity : pictureModeList) {
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

    private void addPPTDurationListener(final MinorMenuEntity minorMenuEntity, final int position) {
        MinorMenuView.OnItemClickListener onItemClickListener = new MinorMenuView.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                mImagePlayerActivity.changePPTSlideDuration(position);
                for (MinorMenuEntity entity : pptDurationEntities) {
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
                mImagePlayerActivity.changePlayMode(position);
                for (MinorMenuEntity entity : repeatModeList) {
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
