/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hitchtransporter.transporter.Services;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hitchtransporter.R;
import com.hitchtransporter.smart.common.NotificationHelper;
import com.hitchtransporter.smart.framework.Constants;
import com.hitchtransporter.smart.framework.SmartApplication;
import com.hitchtransporter.transporter.Activities.Chat;
import com.hitchtransporter.transporter.Activities.ChatList;
import com.hitchtransporter.transporter.Activities.Home;
import com.hitchtransporter.transporter.Activities.Notification;
import com.hitchtransporter.transporter.HomeFragments.TransporterHome;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService implements Constants {

    private static final String TAG = "@@Firebase";
    private static int count = 0;
    private NotificationHelper noti;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String s = remoteMessage.getNotification().getBody();
        Map s1 = remoteMessage.getData();
        Log.e("@@", s1.toString());
        Log.d(TAG, "Notification Body: " + s);
        Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Message Data Payload: " + remoteMessage.getData());


        if (remoteMessage.getNotification() != null) {
            if (!SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(CURRENT_OPPONENT_ID, NO_DATA).equalsIgnoreCase(remoteMessage.getData().get(CHAT_ID))) {
                sendNotificationTest(remoteMessage.getData());
            }else {
                if (ChatList.isChatListIsOpen()) {//or just else. . . ( or else if, does't matter)
                    //"section_id":"8","type":3,"message":"ok"
                    Intent intentNew = new Intent();
                    intentNew.setAction(ChatList.RECEIVE_MESSAGE_FROM_CHAT);
                    sendBroadcast(intentNew);
                }
            }
        }
    }


    private void sendNotificationTest(Map<String, String> data) {
        Intent intent = null;
        if (data.containsKey(TYPE)) {
            if (data.get(TYPE).equalsIgnoreCase(CHAT_NOTIFICATION)) {
                intent = new Intent(this, Chat.class);
                intent.putExtra("is_from_list", false);
                intent.putExtra(CHAT_DIALOG, data.get(CHAT_ID));
                intent.putExtra(OPPONENT_NAME, data.get(SENDER_NAME));
                if (ChatList.isChatListIsOpen()) {
                    Intent intentNew = new Intent();
                    intentNew.setAction(ChatList.RECEIVE_MESSAGE_FROM_CHAT);
                    sendBroadcast(intentNew);
                }
            } else {
                if (data.get(TYPE).equalsIgnoreCase(ORDER_PAYMENT)) {
                    intent = new Intent(this, Home.class);
                    intent.putExtra(ORDER_REQUEST_ID, data.get(ORDER_REQUEST_ID));
                } else {
                    if (data.get(TYPE).equalsIgnoreCase("notification")) {
                        if (TransporterHome.isTransporterHomeIsOpen()) {
                            Intent intentNew = new Intent();
                            intentNew.setAction(TransporterHome.RECEIVE_MESSAGE_FROM_FCM);
                            sendBroadcast(intentNew);
                        }
                    }
                    intent = new Intent(this, Notification.class);
                }

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100),
                    intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_logo)
                    .setContentTitle(data.get(TITLE))
                    .setContentText(data.get(DATA))
                    .setTicker(getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setDefaults(-1)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notificationBuilder.setPriority(android.app.Notification.PRIORITY_HIGH);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                noti = new NotificationHelper(this, data.get(TITLE), data.get(DATA));
                notificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                notificationBuilder.setChannelId(NotificationHelper.PRIMARY_CHANNEL);

            }
            notificationManager.notify(count, notificationBuilder.build());
            count++;
        }
    }
}