package com.example.myapp.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import com.example.myapp.Activity.MainActivity;
import com.example.myapp.Entity.Mode;
import com.example.myapp.Entity.Mp3Info;
import com.example.myapp.Entity.PlayingType;
import com.example.myapp.Entity.lrcInfo;
import com.example.myapp.Utils.Data;

import java.io.IOException;
import java.util.*;


/**
 * Created by Administrator on 2017/4/20.
 */
public class MusicService extends Service {
    private MediaPlayer player;
    private MusicController musicController;
    private static List<Mp3Info> mp3InfoList;
    private int position = 0;
    private int size;
    private Mode mode;
    private static Random random;

    private Timer timer;
    private static PlayingType playingType=PlayingType.READY;

    //绑定服务时,调用此方法
    @Override
    public IBinder onBind(Intent intent) {
        return musicController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mode==Mode.RANDOM)
                    musicController.play(random.nextInt(size-1));
                else if(mode==Mode.SINGLE_TUNE_CIRCULATION)
                    musicController.play();
                else
                    musicController.next();
            }
        });
        musicController = new MusicController();
        mp3InfoList = Data.mp3InfoList(getApplicationContext());
        size = mp3InfoList.size();
        random=new Random();
        mode= Mode.LIST_CIRCULATION;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        player = null;
    }

    //play
    public void play(int position){
        //for click
        this.position=position;
        switch(playingType){
            case READY:
                String path = mp3InfoList.get(position).getUrl();
                try {
                    player.reset();
                    if(mode==Mode.SINGLE_TUNE_CIRCULATION&&!player.isLooping())
                        player.setLooping(true);
                    //date source
                    player.setDataSource(path);
                    player.prepare();
                    player.start();
                    playingType=PlayingType.PLAYING;
                    addTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PLAYING:
                player.pause();
                playingType=PlayingType.STOP;
                break;
            case STOP:
                player.start();
                playingType=PlayingType.PLAYING;
                break;
        }



    }

    private void addTimer() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //获得歌曲总时长
                if (player.isPlaying()) {
                    Mp3Info musicInfo=mp3InfoList.get(position);
                    List<lrcInfo> lrcInfoList=musicInfo.getLrcInfos();
                    Iterator<lrcInfo> lrcInfoIterator=lrcInfoList.iterator();
                    String lrc="当前无歌词";
                    //获得歌曲的当前播放进度

                    while(lrcInfoIterator.hasNext()){
                        lrcInfo lrcInfo1=lrcInfoIterator.next();
                        while(lrcInfo1.getLrcTime()==player.getCurrentPosition())lrc=lrcInfo1.getLrcContent();
                    }
                    //创建消息对象
                    Message msg = MainActivity.handler.obtainMessage();
                    int currentPosition = player.getCurrentPosition();
                    int duration = player.getDuration();
                    String singName=musicInfo.getTitle();
                    //将音乐的播放进度封装至消息对象中
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    bundle.putString("singName",singName);
                    bundle.putString("lrc",lrc);
                    msg.setData(bundle);

                    //将消息发送到主线程的消息队列
                    MainActivity.handler.sendMessage(msg);
                }
            }
            //开始计时任务后的5毫秒，第一次执行run方法，以后每500毫秒执行一次
        }, 5, 500);
    }

    public void seekTo(int progress) {
        player.seekTo(progress);
    }



    class  MusicController extends Binder implements BaseMusic {
        @Override
        public void play() {
            MusicService.this.play(position);
        }

        @Override
        public void play(int position) {
            MusicService.this.play(position);
        }

        @Override
        public void finish(){if(timer!=null)timer.cancel();}

        @Override
        public void seekTo(int progress) {
            MusicService.this.seekTo(progress);
        }

        @Override
        public void next() {
            position++;
            if(position>size)
                position%=size;
           // MusicService.this.position=position;
            MusicService.this.playingType=PlayingType.READY;
            MusicService.this.play(position);
        }

        @Override
        public void previous() {
            position--;
            if(position<0)
                position+=size;
            //MusicService.this.position=position;
            MusicService.this.playingType=PlayingType.READY;
            MusicService.this.play(position);
        }

        @Override
        public void setRandomMode() {
            mode=Mode.RANDOM;
        }

        @Override
        public void setSingleCirculationMode() {
            mode=Mode.SINGLE_TUNE_CIRCULATION;
        }

        @Override
        public void setListCirculationMode() {
            mode=Mode.LIST_CIRCULATION;
        }

        @Override
        public void setPlaying() {
            playingType=PlayingType.PLAYING;
        }

        @Override
        public void setReady() {
            playingType=PlayingType.READY;
        }

        @Override
        public void setStop() {
            playingType=PlayingType.STOP;
        }

        @Override
        public PlayingType getPlayingType() {
            return playingType;
        }

        @Override
        public List<Mp3Info> getMp3InfoList() {
            return mp3InfoList;
        }
    }
}
