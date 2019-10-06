package com.ktc.media.menu.entity;


import com.ktc.media.menu.view.MinorMenuView.OnItemClickListener;


public class MinorMenuEntity {

    private MinorType type;
    private String textString;
    private OnItemClickListener mListener;
    private boolean isSelected = false;

    public MinorType getType() {
        return type;
    }

    public void setType(MinorType type) {
        this.type = type;
    }

    public String getTextString() {
        return textString;
    }

    public void setTextString(String textString) {
        this.textString = textString;
    }

    public OnItemClickListener getListener() {
        return mListener;
    }

    public void setListener(OnItemClickListener listeners) {
        mListener = listeners;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
