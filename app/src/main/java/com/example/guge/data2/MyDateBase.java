package com.example.guge.data2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by GUGE on 2017/12/14.
 */

public class MyDateBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "Contacts.db";
    private static final String TABLE_NAME = "Contacts";
    private static final int DB_VERSION = 1;
    private static final String CREATE_TABLE = "create table if not exists "
            + TABLE_NAME
            + "(name text PRIMARY KEY,"
            + "bir text,"
            + "gift text)";

    public MyDateBase(Context c){
        super(c,DB_NAME,null,DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db){
            db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }

    //插入
    public void insert(String name,String bir,String gift){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name",name);
        values.put("bir",bir);
        values.put("gift",gift);

        db.insert(TABLE_NAME,null,values);
        db.close();

    }
    //删除
    public void delete(String name){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = {name};

        db.delete(TABLE_NAME,whereClause,whereArgs);
        db.close();
    }
    //更新
    public void update(String name,String bir,String gift){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "name = ?";
        String[] whereArgs = {name};
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("bir",bir);
        values.put("gift",gift);
        db.update(TABLE_NAME,values,whereClause,whereArgs);
        db.close();
    }

}
