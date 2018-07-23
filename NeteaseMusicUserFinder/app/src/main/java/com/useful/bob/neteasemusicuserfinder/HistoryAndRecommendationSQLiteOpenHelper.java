package com.useful.bob.neteasemusicuserfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bob on 2018/7/23.
 */

public class HistoryAndRecommendationSQLiteOpenHelper extends SQLiteOpenHelper{

    //数据库tingta
    private static final String DATA_BASE_NAME = "tingta";

    //两张表
    private static final String HISTORY_TABLE = "history";

    //history table的columns
    private static final String NICK_NAME_ID = "_id";
    private static final String NICK_NAME = "name";

    //创建history表
    private static final String HISTORY_TABLE_CREATE = "CREATE TABLE"+HISTORY_TABLE+"("+NICK_NAME_ID+
            "INTEGER PRIMARY KEY, "+ NICK_NAME+"TEXT);";


    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public HistoryAndRecommendationSQLiteOpenHelper(Context context){
        super(context,DATA_BASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(HISTORY_TABLE_CREATE);

        fillDatabaseWithData(sqLiteDatabase);
    }

    private void fillDatabaseWithData(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
