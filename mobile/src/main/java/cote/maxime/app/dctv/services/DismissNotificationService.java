package cote.maxime.app.dctv.services;

import android.app.IntentService;
import android.content.Intent;

import cote.maxime.app.dctv.persistence.Persistence;

/**
 * Created by maxime on 7/30/14.
 */
public class DismissNotificationService extends IntentService {

    public DismissNotificationService()
    {
        super("DismissNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Persistence.getInstance().clearNotifications();
    }
}
