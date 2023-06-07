package com.example.casttvandroiddemo.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InternetHistoryHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "internet.db";
    private static final int VERSION = 1;
    public static final String TABLE_HISTORY = "history";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String TIMESTAMP = "timestamp";

    private static final String CREATE_TABLE_HISTORY = "create table if not exists " + TABLE_HISTORY + " ("
            + ID + " integer primary key autoincrement,"
            + TITLE + " text,"
            + URL + " text,"
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ") ";
    public InternetHistoryHelper(Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
