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

import java.util.Date;

import tv.diamondclub.dctv.persistence.Persistence;
import tv.diamondclub.dctv.ui.HistoryMain;

/**
 * Created by maxime on 7/7/14.
 */
public class GCMNotificationManager extends IntentService {
    public static final int NOTIFICATION_ID = 1;

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
            {
                sendNotification("Send error: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
            {
                sendNotification("Deleted messages on server: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
                Log.i("DCTV", "Received: " + extras);
                this.sendNotification(extras.getString("message"));
                //this.saveNotification(extras.getString("message"));
            }
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    //private void saveNotification(String msg)
    //{
    //    Persistence.getInstance().saveNotification(msg, new Date());
    //}

    private void sendNotification(String msg)
    {
        Log.i("DCTV", "You shall notify!");
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HistoryMain.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.alert_dark_frame)
                        .setContentTitle("Diamond Club Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
