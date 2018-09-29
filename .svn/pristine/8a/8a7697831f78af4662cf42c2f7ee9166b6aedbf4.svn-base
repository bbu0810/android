package com.hitchtransporter.transporter.AA_Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by ebiztrait321 on 2/1/18.
 */

public class SmartLocalBrodcast {
    public static void setRegisetBrodCast(Context context, BroadcastReceiver broadcastReceiver, String action) {
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver,
                new IntentFilter(action));
    }

    public static void setUnregisterReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }

    public static void setSendBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
