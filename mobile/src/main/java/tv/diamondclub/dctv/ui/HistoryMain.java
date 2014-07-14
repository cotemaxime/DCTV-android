package tv.diamondclub.dctv.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

import tv.diamondclub.dctv.R;
import tv.diamondclub.dctv.core.Item;
import tv.diamondclub.dctv.extern.SwipeDismissListViewTouchListener;
import tv.diamondclub.dctv.persistence.Persistence;
import tv.diamondclub.dctv.services.GCMNotificationManager;
import tv.diamondclub.dctv.services.PlayServiceService;


public class HistoryMain extends Activity {
    private PlayServiceService playService;
    private ListView history;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);

        playService = new PlayServiceService(this);
        this.setupPlayService();
        Persistence.setup(this.getBaseContext());

        GCMNotificationManager.cancelAllNotification(this.getApplicationContext());

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
        adapter = new ItemAdapter(this.getApplicationContext(),
                R.layout.item_list,
                Persistence.getInstance().loadNotifications());
        history.setAdapter(adapter);
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
        GCMNotificationManager.cancelAllNotification(this.getApplicationContext());
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
        return super.onOptionsItemSelected(item);
    }
}
