package tv.diamondclub.dctv.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tv.diamondclub.dctv.core.Item;

public class Persistence
{
    private static Persistence instance = null;
    private SQLiteDatabase database;
    private static Context context;

    private Persistence()
    {
        DCTVDatabaseHelper dbHelper = new DCTVDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public static void setup(Context con) { context = con; }

    public static Persistence getInstance()
    {
        if (instance == null)
            instance = new Persistence();

        return instance;
    }

    public void saveNotification(Item notification, boolean message)
    {
        ContentValues data = new ContentValues();
        data.put("id", notification.getId());
        data.put("title", notification.getText());
        data.put("content", notification.getContent());
        data.put("message", message);

        database.insert("notification", null, data);
    }

    public List<Item> loadNotifications()
    {
        List<Item> ret = new ArrayList<Item>();

        Cursor cur = database.query("notification", null, null, null, null, null, null);
        cur.moveToFirst();

        while(!cur.isAfterLast())
        {
            Item i = this.extractItem(cur);
            if (i != null)
                ret.add(i);
            cur.moveToNext();
        }

        return ret;
    }

    private Item extractItem(Cursor cur)
    {
        return new Item(cur.getString(0), cur.getString(1), cur.getString(2));
    }

    public String getNextId()
    {
        Cursor cur = database.query("notification_id", null, null, null, null, null, null);
        cur.moveToFirst();
        int id = cur.getInt(0);

        id++;

        ContentValues data = new ContentValues();
        data.put("currentId", id);
        database.delete("notification_id", null, null);
        database.insert("notification_id", null, data);

        return "" + id;
    }

    public void removeItem(Item item)
    {
        database.delete("notification", "id = \"" + item.getId() + "\"", null);
    }
}

