package com.example.myapp.Entity;

import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 */
public class Mp3Info {
    private long id;   //音乐id
    private String title ;//音乐标题
    private String artist ;//艺术家
    private long duration;//时长
    private long size;  //文件大小
    private String url;              //文件路径
    private int isMusic;//是否为音乐
    private List<lrcInfo> lrcInfos;//歌词

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public List<lrcInfo> getLrcInfos() {
        return lrcInfos;
    }

    public void setLrcInfos(List<lrcInfo> lrcInfos) {
        this.lrcInfos = lrcInfos;
    }

    @Override
    public String toString() {
        return "Mp3Info{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", isMusic=" + isMusic +
                ", lrcInfos=" + lrcInfos +
                '}';
    }
}
