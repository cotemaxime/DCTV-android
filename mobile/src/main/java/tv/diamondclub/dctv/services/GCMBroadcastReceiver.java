package tv.diamondclub.dctv.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import tv.diamondclub.dctv.persistence.Persistence;

/**
 * Created by maxime on 7/10/14.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Persistence.setup(context);

        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMNotificationManager.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
