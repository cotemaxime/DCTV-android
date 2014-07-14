package tv.diamondclub.dctv.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tv.diamondclub.dctv.core.Item;
import tv.diamondclub.dctv.persistence.Persistence;
import tv.diamondclub.dctv.ui.HistoryMain;

/**
 * Created by maxime on 7/7/14.
 */
public class GCMNotificationManager extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final int MESSAGE_ID = 2;

    public GCMNotificationManager() { super("NotificationGCM"); }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        Persistence persistence = Persistence.getInstance();

        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
                Log.e("DCTV", "Send error: " + extras.toString());
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
                Log.e("DCTV", "Deleted messages on server: " + extras.toString());
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                String message = extras.getString("message");

                if (extras.getString("notification").equals("true"))
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy - kk:mm");
                    persistence.saveNotification(new Item(persistence.getNextId(), message, sdf.format(new Date())), false);
                    if (persistence.loadSettingNotification())
                        this.sendNotification(message);
                }
                else
                {
                    persistence.saveNotification(new Item(persistence.getNextId(), message, extras.getString("content")), true);
                    if (persistence.loadSettingMessage())
                        this.sendMessage(message, extras.getString("content"));
                }
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    public static void cancelAllNotification(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancel(MESSAGE_ID);
    }

    private void sendNotification(String msg)
    {
        Log.i("DCTV", "You shall notify!");
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HistoryMain.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.alert_dark_frame)
                .setContentTitle("Diamond Club Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setContentIntent(contentIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendMessage(String title, String content)
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

        mNotificationManager.notify(MESSAGE_ID, mBuilder.build());
    }
}
