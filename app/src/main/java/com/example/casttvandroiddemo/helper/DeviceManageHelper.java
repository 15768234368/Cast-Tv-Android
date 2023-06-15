package com.example.casttvandroiddemo.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeviceManageHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "deviceManage.db";
    private static final int VERSION = 1;
    public static final String TABLE_HISTORY = "history";
    public static final String USER_DEVICE_UDN = "user_device_udn";
    public static final String USER_DEVICE_NAME = "user_device_name";
    public static final String USER_DEVICE_LOCATION = "user_device_location";
    public static final String USER_DEVICE_IPADDRESS = "user_device_ipAddress";
    private final String CREATE_TABLE_HISTORY = "create table if not exists " + TABLE_HISTORY + " ("
            + USER_DEVICE_UDN + " text not null primary key unique,"
            + USER_DEVICE_IPADDRESS + " text not null,"
            + USER_DEVICE_NAME + " text not null,"
            + USER_DEVICE_LOCATION + " text not null" + ") ";

    public DeviceManageHelper(Context context) {
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
