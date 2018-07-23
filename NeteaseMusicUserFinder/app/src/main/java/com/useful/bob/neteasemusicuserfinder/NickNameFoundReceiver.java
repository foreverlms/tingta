package com.useful.bob.neteasemusicuserfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 网易云音乐id已找到广播接收器
 * 但是没用到
 */
@Deprecated
public class NickNameFoundReceiver extends BroadcastReceiver {

    private static final String NICKNAME_FOUND_ACTION="NETEASEMUSICUSERNAMEFOUND";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        switch (action){
            case NICKNAME_FOUND_ACTION:
                String nickname = intent.getStringExtra("nickname");
                showResult(context,nickname);
        }
    }

    private void showResult(Context context, String nickname){
        Toast.makeText(context,nickname,Toast.LENGTH_LONG).show();
    }
}
