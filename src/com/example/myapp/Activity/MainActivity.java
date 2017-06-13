package com.example.myapp.Activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.*;
import com.example.myapp.Entity.Mode;
import com.example.myapp.Entity.PlayingType;
import com.example.myapp.R;
import com.example.myapp.Service.BaseMusic;
import com.example.myapp.Entity.Mp3Info;
import com.example.myapp.Service.MusicService;
import com.example.myapp.Utils.Data;

import java.util.*;

public class MainActivity extends Activity {

    /**
     * Called when the activity is first created.
     */

    Intent intent;
    MyServiceConn musicConn;
    BaseMusic musicServiceImpl;

    private static TextView current;
    private static TextView duration;
    private static TextView singName;
    private static TextView singLry;
    private static SeekBar sb;
    private static ListView listView;
    private static Button mode;
    private static SimpleAdapter simpleAdapter;
    private static List<Mp3Info> mp3InfoList;
    private Mode count=Mode.LIST_CIRCULATION;

    private static final String ACTIVITY_TAG="Android";

    private void setListMusicAdpter(List<Mp3Info> mp3Infos) {
        List<HashMap<String, String>> mp3list = new ArrayList<HashMap<String, String>>();
        for (Iterator iterator = mp3Infos.iterator(); iterator.hasNext();) {
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("title", mp3Info.getTitle());
            map.put("Artist", mp3Info.getArtist());
            map.put("duration", String.valueOf(Data.toTime((int)mp3Info.getDuration())));
            map.put("size", String.valueOf(mp3Info.getSize()));
            map.put("url", mp3Info.getUrl());
            mp3list.add(map);
        }
        simpleAdapter = new SimpleAdapter(this, mp3list,
                R.layout.listitem, new String[] { "title", "Artist", "duration" },
                new int[] { R.id.title, R.id.artist, R.id.duration });
        listView.setAdapter(simpleAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        musicConn=new MyServiceConn();
        intent=new Intent(this, MusicService.class);

        MainActivity.this.bindService(intent,musicConn, Context.BIND_AUTO_CREATE);
        MainActivity.this.startService(intent);

        mp3InfoList= Data.mp3InfoList(getApplicationContext());
        listView=(ListView)this.findViewById(R.id.listView);
        current =(TextView)this.findViewById(R.id.textView);
        duration =(TextView)this.findViewById(R.id.textView2);
        sb= (SeekBar) this.findViewById(R.id.seekBar);
        singName= (TextView) this.findViewById(R.id.sing_Name);
        singLry=(TextView)this.findViewById(R.id.sing_Lry);
        mode=(Button)this.findViewById(R.id.button);
        setListMusicAdpter(mp3InfoList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                musicServiceImpl.setReady();
                changeView(mode);
                musicServiceImpl.play(position);
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            //滑动条停止滑动时调用
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //根据拖动的进度改变音乐播放进度
                int process=seekBar.getProgress();
                musicServiceImpl.seekTo(process);
            }
        });
    }

    public void onStart(Bundle savedInstanceState) {

    }

    //创建消息处理对象
    public static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            //获取从子线程发送过来的音乐播放的进度
            Bundle bundle = msg.getData();
            //歌曲的总时长(毫秒)
            singName.setText(bundle.getString("singName"));
            int duration = bundle.getInt("duration");

            //歌曲的当前进度(毫秒)
            int currentPosition = bundle.getInt("currentPosition");
            String lrc=bundle.getString("lrc");
            //System.out.println(lrc);

            //刷新滑块的进度
            sb.setMax(duration);
            sb.setProgress(currentPosition);
            MainActivity.duration.setText(Data.toTime(duration));
            MainActivity.current.setText(Data.toTime(currentPosition));
            MainActivity.singLry.setText(lrc);
            //surface view
        }
    };

    public void play(View view){
        changeView(view);
        musicServiceImpl.play();
    }

    private void changeView(View view){
        PlayingType playingType=musicServiceImpl.getPlayingType();
        if(playingType.equals(PlayingType.PLAYING))
            view.setBackgroundResource(R.drawable.mu_play);
        else
            view.setBackgroundResource(R.drawable.mu_pause);
    }

    public void exit(View view){
        this.stopService(intent);
        this.unbindService(musicConn);
        this.finish();
    }
    public void setMode(View view){
        count=Mode.getNext(count);
        switch (count){
          case SINGLE_TUNE_CIRCULATION:
              view.setBackgroundResource(R.drawable.mu_back);
              musicServiceImpl.setSingleCirculationMode();
              break;
          case RANDOM:
              view.setBackgroundResource(R.drawable.mu_red);
              musicServiceImpl.setRandomMode();
              break;
          default:
              view.setBackgroundResource(R.drawable.mu_list);
              musicServiceImpl.setListCirculationMode();
      }
    }
    public void next(View view){
        musicServiceImpl.next();
    };
    public void previous(View view){
        musicServiceImpl.previous();
    };

    class MyServiceConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicServiceImpl=(BaseMusic) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
