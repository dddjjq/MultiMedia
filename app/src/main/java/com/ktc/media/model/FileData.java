package com.ktc.media.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 普通文件和图片
 */
public class FileData extends BaseData implements Parcelable {
    private String sizeDescription;

    public FileData() {
        super();
    }

    protected FileData(Parcel in) {
        super(in);
        sizeDescription = in.readString();
    }

    public static final Creator<FileData> CREATOR = new Creator<FileData>() {
        @Override
        public FileData createFromParcel(Parcel in) {
            return new FileData(in);
        }

        @Override
        public FileData[] newArray(int size) {
            return new FileData[size];
        }
    };

    public String getSizeDescription() {
        return sizeDescription;
    }

    public void setSizeDescription(String sizeDescription) {
        this.sizeDescription = sizeDescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(sizeDescription);
    }
}
