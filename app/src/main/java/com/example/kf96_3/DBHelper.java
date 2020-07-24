package com.example.kf96_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

public class DBHelper extends SQLiteOpenHelper {

    public static DBHelper dbHelper = null;
    public static final int DATABASE_VERSION = 1;

    public static synchronized DBHelper getInstance(Context context){ // 싱글턴 패턴으로 구현하였다.
        if(dbHelper == null){
            dbHelper = new DBHelper(context.getApplicationContext());
        }
        return dbHelper;
    }

    public DBHelper(Context context){
        super(context,"dustDB",null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String dustSQL = "create table tb_dust "+
                "(_date TEXT primary key,"+
                "mangName TEXT,"+
                "so2Value TEXT,"+
                "coValue TEXT,"+
                "o3Value TEXT,"+
                "no2Value TEXT,"+
                "pm10Value TEXT,"+
                "pm10Value24 TEXT,"+
                "pm25Value TEXT,"+
                "pm25Value24 TEXT,"+
                "khaiValue TEXT,"+
                "khaiGrade TEXT,"+
                "so2Grade TEXT,"+
                "coGrade TEXT,"+
                "o3Grade TEXT,"+
                "no2Grade TEXT,"+
                "pm10Grade TEXT,"+
                "pm25Grade TEXT,"+
                "pm10Grade1h TEXT,"+
                "pm25Grade1h TEXT)";
        db.execSQL(dustSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion == DATABASE_VERSION){
            db.execSQL("drop table tb_dust");
            onCreate(db);
        }
    }
}
