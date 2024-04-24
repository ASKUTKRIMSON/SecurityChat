package com.example.testrv;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class Notificator {

    public static void scheduleNotificator(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, NotificationReciver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null){
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60*1000, pendingIntent);
            Log.d("BACK_TASK", "ALARM");
        }

    }

    public static void sendNotifycation(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Увед")
                .setContentText("БЛААА")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
        Log.d("BACK_TASK", "NOTIFY");
    }

    public static class NotificationReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Notificator.scheduleNotificator(context);
            sendNotifycation(context);
        }
    }
}
