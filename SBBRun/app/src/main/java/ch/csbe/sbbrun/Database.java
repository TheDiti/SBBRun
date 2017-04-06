package ch.csbe.sbbrun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Endrit on 05.04.2017.
 */

public class Database extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "sbbrun";

    private static final String TABLE_HOME = "home";

    private static final String KEY_ID = "id";
    private static final String KEY_HOME = "home";

    public Database(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_HOME_TABLE = "CREATE TABLE " + TABLE_HOME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_HOME + " TEXT" + ")";

        db.execSQL(CREATE_HOME_TABLE);
    }

    public void onInsert(String home){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " +
                TABLE_HOME + "(" +
                KEY_HOME + ")" +
                "VALUES " +
                "('"+ home +"')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HOME);

        onCreate(db);
    }


    public int onUpdate(String hm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_HOME, hm);
// updating row
        return db.update(TABLE_HOME, values, KEY_ID + " = 1",
                new String[]{KEY_ID});
    }

    public static Cursor data;
    public static String s;

    public void onSelect(SQLiteDatabase db){

        db = this.getReadableDatabase();
        //String query = "SELECT " + KEY_HOME + " FROM" + TABLE_HOME + " WHERE id=`1`";
        data = db.query(TABLE_HOME, new String[] { KEY_ID,
                        KEY_HOME }, KEY_HOME + "=?",
                new String[] { KEY_ID }, null, null, null, null);
        if (data != null)
            data.moveToFirst();
            s = data.toString();

    }

    public static String getS() {
        return s;
    }

    public static Cursor getData() {
        return data;
    }

}
