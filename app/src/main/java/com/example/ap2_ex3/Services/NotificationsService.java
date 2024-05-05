package com.example.ap2_ex3.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.ap2_ex3.R;
import com.example.ap2_ex3.activities.ChatActivity;
import com.example.ap2_ex3.activities.MainActivity;
import com.example.ap2_ex3.adapters.db_entities.Message;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationsService extends FirebaseMessagingService {
    public NotificationsService() {
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ChatApp";
            String description = "notifications channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        MainActivity.database.getAppSettingsDao().updateToken(1, s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            createNotificationChannel();
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(remoteMessage.getData().get("senderName"))
                .setContentText(remoteMessage.getData().get("content"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        notificationManager.notify(1, builder.build());
        String messageIdString = remoteMessage.getData().get("id");
        int messageId = Integer.parseInt(messageIdString);
        String senderName = remoteMessage.getData().get("senderName");
        String created = remoteMessage.getData().get("created");
        String content = remoteMessage.getData().get("content");

        Message message = new Message(messageId, senderName, created, content);

        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.newMessage.setValue(message);
                MainActivity.newChat.setValue(MainActivity.newChat.getValue() != null && MainActivity.newChat.getValue());
            }
        });

    }
}