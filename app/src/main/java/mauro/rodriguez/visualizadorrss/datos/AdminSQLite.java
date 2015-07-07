package mauro.rodriguez.visualizadorrss.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class AdminSQLite extends SQLiteOpenHelper {
    public AdminSQLite(Context context, String nombre, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists rss(id integer primary key, nombre text, url text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists rss");
        db.execSQL("create table rss(id integer primary key, nombre text, url text)");
    }
}
