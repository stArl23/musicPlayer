package com.example.myapp.Service;

import com.example.myapp.Entity.Mp3Info;
import com.example.myapp.Entity.PlayingType;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */
public interface BaseMusic {
    //基本操作
    public void play();
    public void play(int position);
    public void finish();
    public void seekTo(int progress);
    //切歌
    public void next();
    public void previous();

    //模式配置
    public void setRandomMode();
    public void setSingleCirculationMode();
    public void setListCirculationMode();

    //状态配置
    public void setPlaying();
    public void setReady();
    public void setStop();
    public PlayingType getPlayingType();

    public List<Mp3Info> getMp3InfoList();
}
