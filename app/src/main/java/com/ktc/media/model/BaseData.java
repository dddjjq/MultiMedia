package com.ktc.media.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseData implements Parcelable {

    private String path;
    private String name;
    private String date;
    private int type;

    public BaseData() {

    }

    protected BaseData(Parcel in) {
        path = in.readString();
        name = in.readString();
        date = in.readString();
        type = in.readInt();
    }

    public static final Creator<BaseData> CREATOR = new Creator<BaseData>() {
        @Override
        public BaseData createFromParcel(Parcel in) {
            return new BaseData(in);
        }

        @Override
        public BaseData[] newArray(int size) {
            return new BaseData[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        BaseData baseData = (BaseData) obj;
        if (path.equals(baseData.getPath())) {
            return true;
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeInt(type);
    }
}
