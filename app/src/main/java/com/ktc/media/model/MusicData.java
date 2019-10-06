package com.ktc.media.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicData extends BaseData implements Parcelable {

    private String artist;//演唱者
    private long album;//专辑
    private int duration;//时长
    private long id;
    private String durationString;
    private String albumName;
    private String songName;

    public MusicData() {
        super();
    }

    protected MusicData(Parcel in) {
        super(in);
        artist = in.readString();
        album = in.readLong();
        duration = in.readInt();
        id = in.readLong();
        durationString = in.readString();
        albumName = in.readString();
        songName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(artist);
        dest.writeLong(album);
        dest.writeInt(duration);
        dest.writeLong(id);
        dest.writeString(durationString);
        dest.writeString(albumName);
        dest.writeString(songName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MusicData> CREATOR = new Creator<MusicData>() {
        @Override
        public MusicData createFromParcel(Parcel in) {
            return new MusicData(in);
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getAlbum() {
        return album;
    }

    public void setAlbum(long album) {
        this.album = album;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDurationString() {
        return durationString;
    }

    public void setDurationString(String durationString) {
        this.durationString = durationString;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
