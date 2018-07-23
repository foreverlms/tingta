package com.useful.bob.neteasemusicuserfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.linkEditText)
    EditText linkEditText;
    @BindView(R.id.parseButton)
    Button parseButton;

    //网页版网易云音乐
    private static final String USERID = "id";
    private static final String BASE_URL = "http://music.163.com/user/home?";


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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.history:
                Intent historyIntent = new Intent(this,HistoryActivity.class);
                startActivity(historyIntent);
                break;
            case R.id.about:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/coderchaser/NeteaseMusicUserFinder"));
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @OnClick(R.id.parseButton)
    public void parseLink(){
        String id;
        String uri = linkEditText.getText().toString();
        String pattern = "^https?://music\\.163\\.com/m/song\\?id=\\d+&userid=(\\d+)&\\w+";
        Pattern mPattern = Pattern.compile(pattern);
        Matcher matcher = mPattern.matcher(uri);
        if (!matcher.find() || uri.isEmpty()){
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
                .appendQueryParameter(USERID,id).build();

        NeteaseMusicUserFinder neteaseMusicUserFinder = new NeteaseMusicUserFinder(this);
        neteaseMusicUserFinder.execute(homeUri);
    }
}
