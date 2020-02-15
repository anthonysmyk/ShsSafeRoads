package com.example.workplacedamagemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper2 extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Damage2.db";
    public static final String TABLE_NAME = "damage_table";
    public static final String col_1 = "ID";
    public static final String col_2 = "DATE";
    public static final String col_3 = "GPS";

    private static final String TAG = "Message:";



    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME,null,5);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE TEXT, GPS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTs " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData( String date, String GPS)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_2, date);
        contentValues.put(col_3, GPS);



        long result  = db.insert(TABLE_NAME,null, contentValues);
        return(result != -1);

    }
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + col_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    public Cursor search(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + col_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }



    /**
     * Delete from database
     * @param id
     * @param date
     */
    public void deleteName(int id, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + col_1 + " = '" + id + "'" +
                " AND " + col_2 + " = '" + date + "'";
        db.execSQL(query);
    }
    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        return res;
    }

}
