package tv.diamondclub.dctv.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import tv.diamondclub.dctv.R;
import tv.diamondclub.dctv.persistence.Persistence;
import tv.diamondclub.dctv.services.PlayServiceService;


public class HistoryMain extends Activity {
    private PlayServiceService playService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);

        playService = new PlayServiceService(this);
        this.setupPlayService();
        Persistence.setup(this.getBaseContext());

        //Temporary just to create the db and prep things at fist
        Persistence.getInstance();
    }

    private void setupPlayService()
    {
        if (playService.checkPlayServices()) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String regid = playService.getRegistrationId(getApplicationContext());

            if (regid.isEmpty()) {
                playService.registerInBackground(gcm);
            }
        } else {
            Log.e("DCTV", "No valid Google Play Services APK found.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupPlayService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
