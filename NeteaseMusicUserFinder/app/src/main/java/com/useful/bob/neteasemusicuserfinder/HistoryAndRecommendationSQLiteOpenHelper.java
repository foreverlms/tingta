package com.useful.bob.neteasemusicuserfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bob on 2018/7/23.
 */

public class HistoryAndRecommendationSQLiteOpenHelper extends SQLiteOpenHelper {

    //数据库tingta
    private static final String DATA_BASE_NAME = "tingta";

    //两张表
    private static final String HISTORY_TABLE = "history";

    //history table的columns
    public static final String NICK_NAME_ID = "_id";
    public static final String NICK_NAME = "name";

    //创建history表
    private static final String HISTORY_TABLE_CREATE = "CREATE TABLE " + HISTORY_TABLE + "(" + NICK_NAME_ID +
            " INTEGER PRIMARY KEY, " + NICK_NAME + " TEXT);";


    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public HistoryAndRecommendationSQLiteOpenHelper(Context context) {
        super(context, DATA_BASE_NAME, null, 1);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE);
        onCreate(sqLiteDatabase);
    }

    public Cursor query(int position){
        String query;
        Cursor cursor =null;

        if (position != -2){
            position++;
            query = "SELECT "+NICK_NAME_ID+","+NICK_NAME+" FROM "+HISTORY_TABLE
                    +" WHERE "+NICK_NAME_ID+"="+position+";";
        }else {
            query = "SELECT * FROM "+HISTORY_TABLE+" ORDER BY "+NICK_NAME+" ASC ";
        }

        try {
            if(mReadableDB == null){
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query,null);
        }catch (Exception e){
            Log.d("SQLiteOpenHelper",e.getMessage());
        }finally {
            return cursor;
        }
    }
    public long insert(String name){
        Log.d("INSERT",name);
        long newID = 0;
        ContentValues value = new ContentValues();
        value.put(NICK_NAME,name);
        try{
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }
            newID = mWritableDB.insert(HISTORY_TABLE,null,value);
        }catch (Exception e){
            Log.d("SQLiteOpenHelper",e.getMessage());
        }finally {
            return newID;
        }
    }

    public int delete(int id){
        int deleted = 0;
        try {
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(HISTORY_TABLE,NICK_NAME_ID + "=?",new String[]{String.valueOf(id)});
        }catch (Exception e){
            Log.d("DELETE",e.getMessage());
        }
        return deleted;
    }

    public Cursor count(){
        MatrixCursor cursor = new MatrixCursor(new String[]{"names"});
        try {
            if (mReadableDB == null){
                mReadableDB = getReadableDatabase();
            }
            int count = (int) DatabaseUtils.queryNumEntries(mReadableDB,HISTORY_TABLE);
            cursor.addRow(new Object[]{count});
        }catch (Exception e){
            Log.d("COUNT",e.getMessage());
        }

        return cursor;
    }
}
