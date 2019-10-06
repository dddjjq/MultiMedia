package com.ktc.media.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DiskData implements Parcelable {
    private String path;
    private String name;
    private long totalSpace;
    private long availableSpace;

    public DiskData() {

    }

    protected DiskData(Parcel in) {
        path = in.readString();
        name = in.readString();
        totalSpace = in.readLong();
        availableSpace = in.readLong();
    }

    public static final Creator<DiskData> CREATOR = new Creator<DiskData>() {
        @Override
        public DiskData createFromParcel(Parcel in) {
            return new DiskData(in);
        }

        @Override
        public DiskData[] newArray(int size) {
            return new DiskData[size];
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

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getAvailableSpace() {
        return availableSpace;
    }

    public void setAvailableSpace(long availableSpace) {
        this.availableSpace = availableSpace;
    }

    @Override
    public boolean equals(Object obj) {
        return ((DiskData) obj).getPath().equals(getPath());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeLong(totalSpace);
        dest.writeLong(availableSpace);
    }
}
