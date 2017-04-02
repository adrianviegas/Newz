package adrian.news;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Adrian on 28/08/2016.
 */
public class DatabaseAdapter {
    Context context;

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public boolean checkLogin(String username, String password) {
        boolean returnVal = false;
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = db.query("login", new String[]{"username", "password"}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getString(0).equals(username) && cursor.getString(1).equals(password)) {
                    returnVal = true;
                }
            } while (cursor.moveToNext());
        }
        if (!cursor.isClosed())
            cursor.close();
        db.close();
        return returnVal;
    }

    public void newUser(String username, String password) {
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        db.insert("login", null, cv);
        db.close();
    }

    public class Database extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "db";
        private static final int DATABASE_VERSION = 1;


        public Database(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE IF NOT EXISTS login (ids INTEGER PRIMARY KEY AUTOINCREMENT,username VARCHAR,password VARCHAR);";

            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
