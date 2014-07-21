package tv.diamondclub.dctv.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import tv.diamondclub.dctv.ui.HistoryMain;

/**
 * Created by maxime on 7/7/14.
 */
public class PlayServiceService {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "1.0";
    private String SENDER_ID = "177457496813";
    private Activity parent;

    public PlayServiceService(Activity parent) {
        this.parent = parent;
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(parent);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, parent,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("DCTV", "This device is not supported.");
                return false;
            }
            return false;
        }
        return true;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return parent.getSharedPreferences(HistoryMain.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("DCTV", "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = this.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("DCTV", "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public void registerInBackground(final GoogleCloudMessaging gcm) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... param) {
                String msg = "";
                try {
                    String regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    sendRegistrationIdToBackend(gcm);
                    storeRegistrationId(parent.getApplicationContext(), regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("DCTV", msg + "\n");
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(GoogleCloudMessaging gcm) {
        AtomicInteger msgId = new AtomicInteger();
        try {
            Bundle data = new Bundle();
            data.putString("ACTION", "register");
            String id = Integer.toString(msgId.incrementAndGet());
            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
        } catch (IOException ex) {
            Log.e("DCTV", "Error :" + ex.getMessage());
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i("DCTV", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}

