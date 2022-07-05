package com.almusand.aaber.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.almusand.aaber.R;
import com.almusand.aaber.model.NotifyFcm;
import com.almusand.aaber.ui.chat.ChatActivity;
import com.almusand.aaber.ui.main.MainActivityProvider;
import com.almusand.aaber.ui.mapOrders.AllOrdersActivity;
import com.almusand.aaber.ui.notification.NotificationActivity;
import com.almusand.aaber.ui.orderOffers.OrderOffersActivity;
import com.almusand.aaber.utils.AppPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String isUserInConversation;
    private NotifyFcm notifyFcm;
    private String clickAction;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        isUserInConversation = AppPreferences.getIsUserInConversation(getBaseContext());
        try {
            JSONObject data = new JSONObject(remoteMessage.getData().toString());
            notifyFcm = new Gson().fromJson(data.getJSONObject("model").toString(), NotifyFcm.class);

            clickAction = remoteMessage.getNotification().getClickAction();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isUserInConversation.equals("no"))
            sendNotification(remoteMessage);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e("remoteMessagemmm", remoteMessage.getNotification().toString());

            if (isUserInConversation.equals("no"))
                sendNotification(remoteMessage);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            if (isUserInConversation.equals("no"))
//                sendNotification(remoteMessage);

            Log.e("Message Notification", remoteMessage.getNotification().getBody());
        }


    }

    private void sendNotification(RemoteMessage message) {

        Intent intent;
        switch (clickAction) {

            case "clientAcceptOffer":
            case "newMessage":
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                break;

            case "newOffer":
                intent = new Intent(getApplicationContext(), OrderOffersActivity.class);
                break;

            case "newOrder":
                intent = new Intent(getApplicationContext(), AllOrdersActivity.class);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + clickAction);
        }

        intent.putExtra("notifyFcm", notifyFcm);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "101";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_MAX);

            //Configure Notification Channel
            notificationChannel.setDescription(" Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.aaber_logo)
                .setContentTitle(message.getNotification().getTitle())
                .setContentInfo(message.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message.getNotification().getBody()));

        notificationManager.notify(1, notificationBuilder.build());


    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        AppPreferences.saveFcmToken(getApplicationContext(), s);
    }

}