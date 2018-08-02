package com.useful.bob.neteasemusicuserfinder;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 异步的网络访问，用于查找网易云音乐id
 * Created by bob on 2018/6/12.
 */

public class NeteaseMusicUserFinder extends AsyncTask<Uri,Void,String> {

    private static final String TAG = NeteaseMusicUserFinder.class.getSimpleName();

    //总是提示会导致内存泄漏，可是我现在觉得就一个应该没事儿
    private MainActivity activity;

    private HistoryAndRecommendationSQLiteOpenHelper mDB;

    //随机选择一个USER-AGENT
    private static final List<String> USER_AGENTS = new ArrayList(){
        {
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:11.0) Gecko/20100101 Firefox/11.0");
            add("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:22.0) Gecko/20100 101 Firefox/22.0");
            add("Mozilla/5.0 (Windows NT 6.1; rv:11.0) Gecko/20100101 Firefox/11.0");
            add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_4) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.46 Safari/536.5");
            add("Mozilla/5.0 (Windows; Windows NT 6.1) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.46Safari/536.5");
        }
    };

    public NeteaseMusicUserFinder(WeakReference<MainActivity> ref){
        this.activity = (MainActivity) ref.get();
        this.mDB = new HistoryAndRecommendationSQLiteOpenHelper(activity.getApplicationContext());
    }

    public NeteaseMusicUserFinder(MainActivity activity){
        this.activity = activity;
    }
    @Override
    protected String doInBackground(Uri... uris) {
        String sourcepage=null;
        String userAgent = USER_AGENTS.get(new Random().nextInt(USER_AGENTS.size()));
        URL requestUrl = null;
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            requestUrl =new URL(uris[0].toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        try {
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.addRequestProperty("User-Agent",userAgent);
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            String location = null;

            if (code == 302 || code == 301){
                location = httpURLConnection.getHeaderField("Location");
            }
            if (location != null){
                httpURLConnection = (HttpURLConnection) new URL(location).openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.addRequestProperty("User-Agent",userAgent);
                httpURLConnection.connect();
            }

            InputStream inputStream = httpURLConnection.getInputStream();
            Log.d("INPUT_STREAM",String.valueOf(inputStream == null));
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder tmp = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null){
                tmp.append(line);
                Log.d("LINE",line);
            }
            sourcepage = tmp.toString();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if (bufferedReader != null){
                try {
                    bufferedReader.close();
                }catch (IOException e){
                    Log.d(TAG,e.toString());
                }
            }
        }



        return getNickName(sourcepage);
    }

    private String getNickName(String sourcepage){


        Document document = Jsoup.parse(sourcepage);
        //DOM昵称元素选择
//        Element span = document.select("#j-name-wrap > span.tit.f-ff2.s-fc0.f-thide").first();
        Element meta = document.select("meta[name=keywords]").first();
        String content = meta.attr("content");
        String nickName = content.split("，")[0];

        if (!mDB.contains(nickName)){
            mDB.insert(nickName);
        }

//        mDB.close();

        return nickName;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s == null){
            Toast.makeText(activity,"可能无法正确连接网络，稍后再试~~",Toast.LENGTH_SHORT).show();
            return;
        }
        final String nickname = s;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle("You deserve it!")
                .setMessage("该网易云音乐用户名：\n"+s)
                .setPositiveButton("复制至剪贴板", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ClipboardManager clipboardManager =(ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("nickname",nickname);
                        clipboardManager.setPrimaryClip(clipData);
                        dialog.show().dismiss();
                        Toast.makeText(activity,"已复制到剪贴板",Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();
    }
}
