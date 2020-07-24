package com.example.kf96_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

public class DatabaseManager {

    static final String DB_NAME = "Dust.db";
    static final String TABLE_NAME = "Dusts";
    static final int DB_VERSION = 1;

    Context myContext = null;

    private static DatabaseManager myDBManger = null;
    private SQLiteDatabase mydatabase = null;

    public DatabaseManager(Context context) {
        myContext = context;

        // DB OPEN
        mydatabase = context.openOrCreateDatabase(DB_NAME,context.MODE_PRIVATE,null);

        // TABLE 생성하기
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(" + "_date TEXT PRIMARY KEY,"+
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
                "pm25Grade1h TEXT);");
    }

    // 싱글톤 패턴 구현
    public static DatabaseManager getInstance(Context context){
        if (myDBManger == null){
            myDBManger = new DatabaseManager(context);
        }
        return myDBManger;
    }

    public long insert(ContentValues addRowValue)
    {
        return mydatabase.insert(TABLE_NAME, null, addRowValue);
    }

    public Cursor query(String[] colums,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderby)
    {
        return mydatabase.query(TABLE_NAME,
                colums,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderby);
    }

}
