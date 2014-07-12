package tv.diamondclub.dctv.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DCTVDatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "dctvdata";

    private static final int DATABASE_VERSION = 1;

    public DCTVDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        this.onCreate(database, "");
    }

    private void onCreate(SQLiteDatabase database, String option)
    {
        Log.i("DCTV", "create database");
        String create = "CREATE TABLE " + option + " notification(" +
                "id text primary key, " +
                "title text, " +
                "content text," +
                "message boolean);";
        database.execSQL(create);

        create = "CREATE TABLE " + option + " notification_id(" +
                "currentId int);";
        database.execSQL(create);

        if(option.equals(""))
            database.execSQL("INSERT INTO notification_id VALUES(0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldV, int newV)
    {
        if (newV > oldV)
        {
            for(String name: this.getTableList())
                this.backupAndCopy(database, name);
        }
    }

    private void backupAndCopy(SQLiteDatabase database, String name)
    {
        List<String> columns = this.getColumns(database, name);
        database.execSQL("ALTER TABLE " + name + " RENAME TO 'temp_" + name + "'");
        this.onCreate(database, "IF NOT EXISTS");
        columns.retainAll(this.getColumns(database, name));
        String cols = TextUtils.join(",", columns);
        database.execSQL(String.format("INSERT INTO %s (%s) SELECT %s FROM temp_%s", name, cols, cols, name));
        database.execSQL("DROP TABLE 'temp_" + name + "'");
    }

    public List<String> getColumns(SQLiteDatabase db, String tableName)
    {
        List<String> ar = null;
        Cursor c = null;
        try
        {
            c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (c != null)
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
        }
        catch (Exception e)
        {
            Log.e(tableName, e.getMessage(), e);
        }
        finally
        {
            if (c != null)
                c.close();
        }

        return ar;
    }

    private List<String> getTableList()
    {
        List<String> ret = new ArrayList<String>();
        ret.add("notification");
        ret.add("notification_id");

        return ret;
    }
}
