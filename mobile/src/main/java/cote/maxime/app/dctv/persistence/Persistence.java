package cote.maxime.app.dctv.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cote.maxime.app.dctv.core.Item;

public class Persistence
{
    private static Persistence instance = null;
    private SQLiteDatabase database;
    private static Context context;
    private int notificationNumber;
    private List<String> previousNotifications;

    private Persistence()
    {
        DCTVDatabaseHelper dbHelper = new DCTVDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        previousNotifications = new ArrayList<String>();
        notificationNumber = 0;
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
        data.put("link", notification.getLink());

        database.insert("notification", null, data);
    }

    public List<Item> loadNotifications()
    {
        List<Item> ret = new ArrayList<Item>();

        Cursor cur = database.query("notification", null, null, null, null, null, "content DESC", null);
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

    public boolean loadSettingNotification()
    {
        Cursor cur = database.query("settings", null, null, null, null, null, null);
        cur.moveToFirst();
        return cur.getInt(1) == 1;
    }

    public boolean loadSettingMessage()
    {
        Cursor cur = database.query("settings", null, null, null, null, null, null);
        cur.moveToFirst();
        return cur.getInt(0) == 1;
    }

    public void saveSettingNotification(boolean notification)
    {
        ContentValues data = new ContentValues();
        data.put("notification", notification ? 1 : 0);

        database.update("settings", data, null, null);
    }

    public void saveSettingMessage(boolean message)
    {
        ContentValues data = new ContentValues();
        data.put("message", message ? 1 : 0);

        database.update("settings", data, null, null);
    }

    private Item extractItem(Cursor cur)
    {
        return new Item(cur.getString(0), cur.getString(1), cur.getString(2), cur.getString(4));
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

    public void addNotificationNumber()
    {
        this.notificationNumber++;
    }

    public int getNotificationNumber()
    {
        return this.notificationNumber;
    }

    public List<String> getPreviousNotifications()
    {
        return previousNotifications;
    }

    public void addPreviousNotifications(String notification)
    {
        previousNotifications.add(notification);
    }

    public void clearNotifications()
    {
        previousNotifications.clear();
        notificationNumber = 0;
    }
}

