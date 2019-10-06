package com.ktc.media.menu.entity;

import java.util.List;

public class MajorMenuEntity {

    private int imageRes;
    private String textString;
    private List<MinorMenuEntity> mMinorMenuEntities;

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getTextString() {
        return textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;
    }

    public List<MinorMenuEntity> getMinorMenuEntities() {
        return mMinorMenuEntities;
    }

    public void setMinorMenuEntities(List<MinorMenuEntity> minorMenuEntities) {
        mMinorMenuEntities = minorMenuEntities;
    }
}
