package com.example.myapp.Utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import com.example.myapp.Entity.Mp3Info;
import com.example.myapp.Entity.lrcInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/18.
 */
public class Data {
    public static List<Mp3Info> mp3InfoList(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Mp3Info mp3Info = new Mp3Info();
            cursor.moveToNext();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));   //音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));//音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));//艺术家
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));//时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));  //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));   //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐
            if (isMusic != 0) {     //只把音乐添加到集合当中
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Info.setLrcInfos(pauseLrc(url));
                mp3Infos.add(mp3Info);
            }
        }
        return mp3Infos;
    }

    public static String toTime(int time) {
        int minute = time / 1000 / 60;
        int second = time / 1000 % 60;

        String strMinute = (minute >= 10) ? minute + "" : "0" + minute;
        String strSecond = (second >= 10) ? second + "" : "0" + second;
        return strMinute + ":" + strSecond;
    }

    public static int toInt(String time) {
        String[] s = time.split(":");
        return (int) ((Integer.parseInt(s[0]) * 60 + Double.parseDouble(s[1])) * 1000);
    }

    private static List<lrcInfo> pauseLrc(String url) {
        List<lrcInfo> list = new ArrayList<lrcInfo>();
        url = url.replace("歌曲", "歌词");
        url = url.substring(0, url.lastIndexOf(".")) + ".lrc";
        //File file=new File(url);
        try {
            File file = new File(url);
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(in, "utf-8");
                BufferedReader bfr = new BufferedReader(isr);

                String s;
                while ((s = bfr.readLine()) != null) {

                    s = s.replace("[", ""); // 去掉左边括号

                    String lrcData[] = s.split("]");

                    // 这句是歌词
                    if (lrcData[0].matches("^\\d{2}:\\d{2}.\\d+$")) {
                        int len = lrcData.length;
                        int end = lrcData[len - 1].matches("^\\d{2}:\\d{2}.\\d+$") ? len
                                : len - 1;

                        for (int i = 0; i < end; i++) {
                            lrcInfo lrcContent = new lrcInfo();
                            int lrcTime = toInt(lrcData[i]);
                            lrcContent.setLrcTime(lrcTime);
                            if (lrcData.length == end)
                                lrcContent.setLrcContent("");// 空白行
                            else
                                lrcContent.setLrcContent(lrcData[len - 1]);

                            list.add(lrcContent);
                        }

                    }

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
