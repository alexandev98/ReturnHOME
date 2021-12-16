package com.returnhome.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returnhome.R;
import com.returnhome.receivers.ViewPetFoundReceiver;
import com.returnhome.utils.AppConfig;
import com.returnhome.utils.NotificationUtil;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private AppConfig mAppConfig;

    private static final int NOTIFICATION_CODE = 100;



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        mAppConfig = new AppConfig(getApplicationContext());

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        String petName = data.get("pet_name");
        double pet_lat = Double.parseDouble(data.get("pet_lat"));
        double pet_lng  = Double.parseDouble(data.get("pet_lng"));

        LatLng petLatLng = new LatLng(pet_lat, pet_lng);


        if (title != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showNotificationApiOreo(title, body, petName, petLatLng);

            }
            else{
                showNotification(title, body, petName, petLatLng);
            }

        }

    }

    private void showNotification(String title, String body, String petName, LatLng petLatLng) {

        Intent showPetFoundIntent = new Intent(this, ViewPetFoundReceiver.class);
        showPetFoundIntent.putExtra("pet_name", petName);
        showPetFoundIntent.putExtra("pet_lat", petLatLng.latitude);
        showPetFoundIntent.putExtra("pet_lng", petLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetFoundIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action showPetFoundAction= new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "VER EN MAPA",
                viewPendingIntent
        ).build();


        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationUtil notificationUtil = new NotificationUtil(getBaseContext());
        NotificationCompat.Builder builder = notificationUtil.getNotificationOldAPI(title, body, sound, showPetFoundAction);
        notificationUtil.getManager().notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotificationApiOreo(String title, String body, String petName, LatLng petLatLng) {

        Intent showPetFoundIntent = new Intent(this, ViewPetFoundReceiver.class);
        showPetFoundIntent.putExtra("pet_name", petName);
        showPetFoundIntent.putExtra("pet_lat", petLatLng.latitude);
        showPetFoundIntent.putExtra("pet_lng", petLatLng.longitude);
        PendingIntent viewPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_CODE, showPetFoundIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action showPetFoundAction= new Notification.Action.Builder(
                R.mipmap.ic_launcher,
                "VER EN MAPA",
                viewPendingIntent
        ).build();

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationUtil notificationUtil = new NotificationUtil(getBaseContext());
        Notification.Builder builder = notificationUtil.getNotification(title, body, sound, showPetFoundAction);
        notificationUtil.getManager().notify(1, builder.build());
    }
}