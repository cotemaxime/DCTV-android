package cote.maxime.app.dctv.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import cote.maxime.app.dctv.R;
import cote.maxime.app.dctv.extern.SwipeDismissListViewTouchListener;
import cote.maxime.app.dctv.persistence.Persistence;
import cote.maxime.app.dctv.services.GCMNotificationManager;
import cote.maxime.app.dctv.services.PlayServiceService;

public class HistoryMain extends Activity {
    private ListView history;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);

        PlayServiceService.setup(this);
        this.setupPlayService();
        Persistence.setup(this);

        history = (ListView) findViewById(R.id.historyList);
        this.setupList();
        this.refreshList();
    }

    private void setupList()
    {
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        history,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        history.setOnTouchListener(touchListener);
        history.setOnScrollListener(touchListener.makeScrollListener());
    }

    public void refreshList()
    {
        adapter = new ItemAdapter(this,
                R.layout.item_list,
                Persistence.getInstance().loadNotifications());
        history.setAdapter(adapter);
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
        }
        else
            Log.e("DCTV", "No valid Google Play Services APK found.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupPlayService();
        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_clearall)
        {
            adapter.removeAll();
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
