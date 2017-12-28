package com.namboodiri.illam;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.namboodiri.illam/databases/";
    private static String DB_NAME = "illam.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }
    public void createDataBase() throws IOException
    {
        //boolean dbExist = checkDB();
        boolean dbExist = false;
        if(dbExist) {
            //do nothing
        }
        else
        {
            this.getReadableDatabase();
            try
            {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDB(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
                throw new Error("SQLiteException encountered");
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }
    @Override
    public synchronized void close() {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public List<String> getDbData (String key) {
        final String TABLE_NAME = "table1";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<String> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if(FuzzySearch.tokenSetPartialRatio(key, cursor.getString(1))>70)
                    list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public String[] getResults (String key) {
        final String TABLE_NAME = "table1";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String ret[] = new String[3];
        ret[0] = null;
        boolean found = false;
        if (cursor.moveToFirst()){
            do {
                if(cursor.getString(1).contains(key)){
                    found = true;
                    ret[0] = cursor.getString(1);
                    ret[1] = cursor.getString(2);
                    ret[2] = cursor.getString (3);
                }
            } while (cursor.moveToNext() && !found);
        }
        return ret;
    }
}
