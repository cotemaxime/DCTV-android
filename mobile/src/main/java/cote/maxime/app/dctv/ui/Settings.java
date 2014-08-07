package cote.maxime.app.dctv.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import cote.maxime.app.dctv.R;
import cote.maxime.app.dctv.persistence.Persistence;
import cote.maxime.app.dctv.services.PlayServiceService;

/**
 * Created by maxime on 7/13/14.
 */
public class Settings extends Activity
{
    private Persistence persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        persistence = Persistence.getInstance();

        Switch message = (Switch) findViewById(R.id.message);
        message.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                persistence.saveSettingMessage(isChecked);
            }
        });
        message.setChecked(persistence.loadSettingMessage());

        Switch notification = (Switch) findViewById(R.id.notification);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                persistence.saveSettingNotification(isChecked);
            }
        });
        notification.setChecked(persistence.loadSettingNotification());
        Button done = (Button) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button resetID = (Button) findViewById(R.id.resetID);
        resetID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPlayService();
            }
        });
    }

    private void setupPlayService()
    {
        PlayServiceService playService = PlayServiceService.getInstance();

        if (playService.checkPlayServices())
        {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String regid = playService.getRegistrationId(getApplicationContext());

            if (regid.isEmpty())
                playService.registerInBackground(gcm);
            else
                sendRegistrationInBackground(gcm);
        }
        else
            Log.e("DCTV", "No valid Google Play Services APK found.");
    }

    private void sendRegistrationInBackground(final GoogleCloudMessaging gcm)
    {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... param) {
                PlayServiceService.getInstance().sendRegistrationIdToBackend(gcm);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("DCTV", msg + "\n");
            }
        }.execute(null, null, null);
    }
}
