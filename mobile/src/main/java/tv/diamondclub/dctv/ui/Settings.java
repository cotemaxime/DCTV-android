package tv.diamondclub.dctv.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import tv.diamondclub.dctv.R;
import tv.diamondclub.dctv.persistence.Persistence;

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
    }
}
