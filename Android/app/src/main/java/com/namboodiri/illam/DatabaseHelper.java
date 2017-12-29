package com.namboodiri.illam;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.namboodiri.illam/databases/";
    private static String DB_NAME = "illam_new.db";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    String stuff[] = new String [10000];
    int stf[] = new int[10000];

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
    public void sortByScore(int size)
    {
        int temp;
        String tmp;
        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size-i; j++)
            {
                if(stf[j] < stf[j+1])
                {
                    //swap ints
                    temp = stf[j];
                    stf[j] = stf[j+1];
                    stf[j+1] = temp;
                    // swap strings
                    tmp = stuff[j];
                    stuff[j] = stuff[j+1];
                    stuff[j+1] = tmp;

                }
            }
        }
    }
    public ArrayList<String> getDbData (String key) {
        final String TABLE_NAME = "table1";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> list = new ArrayList<>();
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                if(FuzzySearch.tokenSetPartialRatio(key, cursor.getString(1))>70) {
                    stuff[i] = cursor.getString(1);
                    stf[i] = FuzzySearch.tokenSetPartialRatio(key, cursor.getString(1));
                    //list.add(cursor.getString(1));
                    // Log.e("ILLAM: ", Integer.toString(stf[i]));
                    // Log.e("ILLAM: PERSON:  ", stuff[i]);
                    i++;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        sortByScore(i);
        for(int j=0; j<=i; j++)
        {
            //Log.e("ILLAM: current array:  ", stuff[j]);
            if(stuff[j]!=null)
            list.add(stuff[j]);
        }
        return list;
    }

    public ArrayList<String> getResults (String key, int start, int end) {
        final String TABLE_NAME = "table1";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<String> ret = new ArrayList<>();
        boolean found = false;
        if (cursor.moveToFirst()){
            do {
                if(cursor.getString(1).contains(key)) {
                    found = true;
                    for (int i = start; i <= end; i++) {
                        if (cursor.getString(i) != null)
                            ret.add(cursor.getString(i));
                        else
                            break;
                    }
                }
            } while (cursor.moveToNext() && !found);
        }
        return ret;
    }

    public String getParents (String key, int j){
        final String TABLE_NAME = "table1";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String ret = null;
        boolean found = false;
        if (cursor.moveToFirst()){
            do {
                if(cursor.getString(1).contains(key)){
                    found = true;
                    if(cursor.getString(j)!=null)
                        ret = cursor.getString(j);
                    else
                        ret = "N/A";
                }
            } while (cursor.moveToNext() && !found);
        }
        return ret;
    }
}
