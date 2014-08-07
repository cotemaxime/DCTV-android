package cote.maxime.app.dctv.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cote.maxime.app.dctv.core.Item;
import cote.maxime.app.dctv.persistence.Persistence;
import cote.maxime.app.dctv.ui.HistoryMain;

/**
 * Created by maxime on 7/7/14.
 */
public class GCMNotificationManager extends IntentService {
    private static List<Integer> notificationId = new ArrayList<Integer>();
    public static List<Integer> messageId = new ArrayList<Integer>();

    public GCMNotificationManager() { super("NotificationGCM"); }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
                Log.e("DCTV", "Send error: " + extras.toString());
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
                Log.e("DCTV", "Deleted messages on server: " + extras.toString());
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                int number = Integer.parseInt(extras.getString("messageNumber"));
                String message = extras.getString("message");
                if(number > 1)
                {
                    String[] messages = message.split("\\*\\*");
                    String[] links = extras.getString("link").split("\\*\\*");

                    for(int i = 0; i < messages.length; i++)
                        this.prepareAndSend(extras.getString("notification").equals("true"), links[i], messages[i], extras.getString("content"), 0);
                }
                else
                {
                    message = extras.getString("message").split("\\*\\*")[0];
                    String[] tmpLink = extras.getString("link").split("\\*\\*");
                    String link = "";
                    if (tmpLink.length > 0)
                        link = tmpLink[0];
                    this.prepareAndSend(extras.getString("notification").equals("true"), link, message, extras.getString("content"), 1);
                }
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void prepareAndSend(boolean notification, String link, String message, String content, int id)
    {
        Persistence persistence = Persistence.getInstance();
        if (notification)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy - kk:mm");
            persistence.saveNotification(new Item(persistence.getNextId(), message, sdf.format(new Date()), link), false);
            if (persistence.loadSettingNotification())
                this.sendNotification(message, id);
        }
        else
        {
            persistence.saveNotification(new Item(persistence.getNextId(), message, content, ""), true);
            if (persistence.loadSettingMessage())
                this.sendMessage(message, content, id);
        }
    }

    private void sendNotification(String msg, int id)
    {
        Persistence persistence = Persistence.getInstance();
        Log.i("DCTV", "You shall notify!");
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HistoryMain.class), 0);
        PendingIntent deleteIntent = PendingIntent.getService(this, 0, new Intent(this, DismissNotificationService.class), 0);

        NotificationCompat.InboxStyle notifBuilder = new NotificationCompat.InboxStyle()
                .setBigContentTitle((Persistence.getInstance().getNotificationNumber() + 1) + " new notifications");

        for(String notif: persistence.getPreviousNotifications())
            notifBuilder.addLine(notif);

        notifBuilder.addLine(msg);
        persistence.addNotificationNumber();
        persistence.addPreviousNotifications(msg);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Diamond Club Notification")
                .setStyle(notifBuilder)
                .setAutoCancel(true)
                .setDeleteIntent(deleteIntent)
                .setContentText(persistence.getNotificationNumber() + " new notifications")
                .setContentIntent(contentIntent);

        mNotificationManager.notify(id, mBuilder.build());
        notificationId.add(id);
    }

    private void sendMessage(String title, String content, int id)
    {
        Log.i("DCTV", "You shall notify!");
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HistoryMain.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.alert_light_frame)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentText(content)
                .setContentIntent(contentIntent);

        mNotificationManager.notify(id, mBuilder.build());
        messageId.add(id);
    }
}
