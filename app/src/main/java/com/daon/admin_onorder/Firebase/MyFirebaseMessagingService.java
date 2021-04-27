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

package com.daon.admin_onorder.Firebase;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import com.daon.admin_onorder.AdminApplication;
import com.daon.admin_onorder.MainActivity;
import com.daon.admin_onorder.OrderDialog;
import com.daon.admin_onorder.PopupActivity;
import com.daon.admin_onorder.Popup_Noti;
import com.daon.admin_onorder.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    AdminApplication app = new AdminApplication();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        sendNotification(notification, data);
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification FCM notification payload received.
     * @param data FCM data payload received.
     */
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentInfo(data.get("title"))
                .setLargeIcon(icon)
                .setColor(Color.RED)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher);


        Intent intent_ = new Intent(this, PopupActivity.class);
        intent_.putExtra("title", data.get("title"));
        intent_.putExtra("body", data.get("body"));
        intent_.putExtra("type", data.get("type"));
        Log.d("daon", "adf = "+data.get("body"));
        if (data.get("body") == null){

        }
        String body = data.get("body");

        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        Date date1 = new Date(tsLong);
        SimpleDateFormat datef = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault());
        String date22 =  datef.format(date1) ;
        if (data.get("type").equals("order")) {
            Sam4sPrint sam4sPrint = app.getPrinter();
            Sam4sPrint sam4sPrint2 = app.getPrinter2();
            try {
                Log.d("daon_test","print ="+sam4sPrint.getPrinterStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
            try {
                builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                builder.addFeedLine(2);
                builder.addTextSize(3,3);
                builder.addText(data.get("title"));
                builder.addFeedLine(2);
                builder.addTextSize(2,2);
                builder.addTextAlign(builder.ALIGN_RIGHT);
                builder.addText(body);
                builder.addFeedLine(2);
                builder.addTextSize(1,1);
                builder.addText(date22);
                builder.addFeedLine(1);
                builder.addCut(Sam4sBuilder.CUT_FEED);
                sam4sPrint.sendData(builder);
                sam4sPrint2.sendData(builder);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Sam4sPrint sam4sPrint = app.getPrinter();
            Sam4sBuilder builder = new Sam4sBuilder("ELLIX30", Sam4sBuilder.LANG_KO);
            try {
//            builder.addPageBegin();
//            builder.addPageArea(100, 50, 800, 800);
//            builder.addPagePosition(50, 30);

//            builder.addTextSize(2, 2);
                builder.addTextAlign(Sam4sBuilder.ALIGN_CENTER);
                builder.addFeedLine(2);
                builder.addTextSize(2, 2);
                builder.addText(data.get("title"));
                builder.addFeedLine(2);
                builder.addTextSize(1,1);
                builder.addTextAlign(builder.ALIGN_RIGHT);
                builder.addText(date22);
                builder.addFeedLine(2);
                builder.addTextSize(2, 2);
                builder.addTextAlign(builder.ALIGN_LEFT);
                builder.addText(body);
                builder.addFeedLine(2);
                builder.addCut(Sam4sBuilder.CUT_FEED);
                sam4sPrint.sendData(builder);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bell);
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        this.startActivity(intent_);


//        Handler mHandler = new Handler(Looper.getMainLooper());
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext())
//                        .setTitle("Title")
//                        .setMessage("Are you sure?")
//                        .create();
//
//                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
//                alertDialog.show();
//
//            }
//        }, 0);




        try {
            String picture_url = data.get("picture_url");
            if (picture_url != null && !"".equals(picture_url)) {
                URL url = new URL(picture_url);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}