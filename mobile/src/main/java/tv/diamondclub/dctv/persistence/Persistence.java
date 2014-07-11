package tv.diamondclub.dctv.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

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

    public void saveNotification(String text, Date date)
    {
        ContentValues data = new ContentValues();
        data.put("id", getNextId());
        data.put("content", text);
        data.put("dateTime", date.toString());
    }

    public int getNextId()
    {
        Cursor cur = database.query("notification_id", null, null, null, null, null, null);
        cur.moveToFirst();
        int id = cur.getInt(0);

        id++;

        ContentValues data = new ContentValues();
        data.put("currentId", id);
        database.delete("notification_id", null, null);
        database.insert("notification_id", null, data);

        return id;
    }
}

