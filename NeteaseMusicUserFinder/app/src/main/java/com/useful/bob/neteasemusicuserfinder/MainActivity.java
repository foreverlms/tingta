package com.useful.bob.neteasemusicuserfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sackcentury.shinebuttonlib.ShineButton;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.linkEditText)
    EditText linkEditText;
    @BindView(R.id.song_recommend)
    TextView songTextView;
    @BindView(R.id.go)
    ShineButton goButton;
//    @BindView(R.id.parseButton)
//    Button parseButton;

    private static final String LINK_SAVE = "link";

    //网页版网易云音乐地址
    private static final String USERID = "id";
    private static final String BASE_URL = "http://music.163.com/user/home?";

    private int songIndex = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        linkEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
                    );

                    parseLink();
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            linkEditText.setText(savedInstanceState.getString(LINK_SAVE));
        }
        setRecommendedSong();
    }

    @Override
    protected void onResume() {
        setRecommendedSong();
        goButton.setChecked(false);
        super.onResume();
    }

    @OnClick(R.id.go)
    public void goToSong() {
        String[] songs = getResources().getStringArray(R.array.songs);
        String songId = songs[songIndex + 1];
        Uri songUri = Uri.parse("https://music.163.com/song?id=" + songId);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(songUri);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!linkEditText.getText().toString().equals("")) {
            outState.putString(LINK_SAVE, linkEditText.getText().toString());
        }
    }

    private void setRecommendedSong() {
        String[] songs = getResources().getStringArray(R.array.songs);
        int max = songs.length / 2;
        Random random = new Random();
        songIndex = 2 * random.nextInt(max);
        String song = songs[songIndex];

        songTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        songTextView.setText(song);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.history:
                Intent historyIntent = new Intent(this, HistoryActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.about:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/coderchaser/tingta"));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //    @OnClick(R.id.parseButton)
    public void parseLink() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            String id;
            String uri = linkEditText.getText().toString();
            String pattern = "^https?://music\\.163\\.com/m/song\\?id=\\d+&userid=(\\d+)&\\w+";
            Pattern mPattern = Pattern.compile(pattern);
            Matcher matcher = mPattern.matcher(uri);
            if (!matcher.find() || uri.isEmpty()) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("小拳拳捶你胸口！")
                        .setMessage("输入的网易云音乐分享链接不对哦！")
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.show().dismiss();
                            }
                        }).show();
                return;
            }
            id = matcher.group(1);
            //举例：http://music.163.com/user/home?id=%d{9}
            Uri homeUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(USERID, id).build();
            WeakReference<MainActivity> ref = new WeakReference<MainActivity>(this);
            NeteaseMusicUserFinder neteaseMusicUserFinder = new NeteaseMusicUserFinder(ref);
//            NeteaseMusicUserFinder neteaseMusicUserFinder = new NeteaseMusicUserFinder(this);
            neteaseMusicUserFinder.execute(homeUri);
        } else {
            Toast.makeText(this, R.string.toast_message_for_no_internet, Toast.LENGTH_SHORT).show();
        }
    }
}
