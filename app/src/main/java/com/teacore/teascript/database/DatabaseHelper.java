package com.teacore.teascript.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 * @author 陈晓帆
 * @version 1.0
 * Created 2017-4-30
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String TS_DATABASE_NAME="teascript";
    public static final String NOTE_TABLE_NAME="notebook";

    public static final String CREATE_NOTE_TABLE="create table "+NOTE_TABLE_NAME
            +" (_id integer primary key autoincrement,iid integet,"
            +"time varchar(10),date varchar(10),content text,color integer)";

    public DatabaseHelper(Context context){
        super(context,TS_DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


}
