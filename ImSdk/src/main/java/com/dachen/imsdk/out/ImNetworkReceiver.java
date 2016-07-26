package com.dachen.imsdk.out;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dachen.imsdk.net.ImPolling;

/**
 * Created by Mcp on 2016/4/29.
 */
public class ImNetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if(info!=null&&info.isConnected()){
            ImPolling.getInstance().doRecWebSocket();
        }
    }
    public static ImNetworkReceiver registerReceiver(Context context){
        ImNetworkReceiver receiver=new ImNetworkReceiver();
        context.registerReceiver(receiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        return receiver;
    }
}
