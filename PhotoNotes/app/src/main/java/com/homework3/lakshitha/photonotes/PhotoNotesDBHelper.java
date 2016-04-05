package com.homework3.lakshitha.photonotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by lakshitha on 2/1/16.
 */
public class PhotoNotesDBHelper extends SQLiteOpenHelper{

    public static final String ID_COLUMN = "_id";
    public static final String CAPTION_COLUMN = "caption";
    public static final String PHOTO_PATH_COLUMN = "photopath";

    public static final String DATABASE_TABLE = "PhotoNotes";
    public static final int DATABASE_VERSION = 3;

    private static final String DATABASE_CREATE = String.format(
            "CREATE TABLE %s (" +
                    "  %s integer primary key autoincrement, " +
                    "  %s VARCHAR," +
                    "  %s VARCHAR)",
            DATABASE_TABLE, ID_COLUMN, CAPTION_COLUMN, PHOTO_PATH_COLUMN);

    public PhotoNotesDBHelper(Context context) {
        super(context, DATABASE_TABLE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}
