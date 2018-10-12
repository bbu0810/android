package com.hitchtransporter.smart.framework;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmartInternetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SmartUtils.setNetworkStateAvailability(context);
        Intent broadcastIntent=new Intent("NetworkState");
        context.sendBroadcast(broadcastIntent);
    }
}
