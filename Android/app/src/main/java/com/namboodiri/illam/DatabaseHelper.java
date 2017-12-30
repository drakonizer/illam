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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.lang.String;
import java.util.Comparator;

import me.xdrop.fuzzywuzzy.FuzzySearch;


class CompareObj implements Comparator<Person>{
    @Override
    public int compare(Person p1, Person p2) {
        return p2.score - p1.score;
    }
}

public class DatabaseHelper extends SQLiteOpenHelper {

    ArrayList<Person> plist;

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

        // search and find records
        plist = new ArrayList<Person>();
        ArrayList<Person> candidates = new ArrayList<Person>();
        Iterator<Person> personIterator = plist.iterator();

        if(cursor.moveToFirst()) {
            do {
                Person p = new Person();
                p.name = cursor.getString(1);
                p.father = cursor.getString(2);
                p.mother = cursor.getString(3);
                if(FuzzySearch.tokenSetPartialRatio(key, p.name)>70) {
                    p.score = FuzzySearch.tokenSetPartialRatio(key, p.name);
                    candidates.add(p);
                } else {
                    p.score = 0;
                }

                p.spouses = new ArrayList<String>();
                for(int col=4; col<8; col++) {
                    if(cursor.getString(col) != null) {
                        p.spouses.add(cursor.getString(col));
                    }
                }

                p.children = new ArrayList<String>();
                for(int col=8; col<16; col++) {
                    if(cursor.getString(col) != null) {
                        p.children.add(cursor.getString(col));
                    }
                }
                plist.add(p);
            } while(cursor.moveToNext());
        }

        Collections.sort(candidates, new CompareObj());

        ArrayList<String> list = new ArrayList<>();
        cursor.close();
        personIterator = candidates.iterator();
        while(personIterator.hasNext()) {
            list.add(personIterator.next().name);
        }
        return list;
    }

    public ArrayList<Person> getPersons () {
        final String TABLE_NAME = "table1";
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // search and find records
        ArrayList<Person> persons = new ArrayList<Person>();
        Iterator<Person> personIterator = persons.iterator();

        if(cursor.moveToFirst()) {
            do {
                Person p = new Person();
                p.name = cursor.getString(1);
                p.father = cursor.getString(2);
                p.mother = cursor.getString(3);

                p.spouses = new ArrayList<String>();
                for(int col=4; col<8; col++) {
                    if(cursor.getString(col) != null) {
                        p.spouses.add(cursor.getString(col));
                    }
                }

                p.children = new ArrayList<String>();
                for(int col=8; col<16; col++) {
                    if(cursor.getString(col) != null) {
                        p.children.add(cursor.getString(col));
                    }
                }

                persons.add(p);
            } while(cursor.moveToNext());
        }

        return persons;
    }

    public Person getPerson(ArrayList<Person> persons, String key) {
        Person p;
        Iterator<Person> personIterator = persons.iterator();
        personIterator = persons.iterator();
        while(personIterator.hasNext()) {
            p = personIterator.next();
            if(p.name.equals(key))
                return p;
        }
        return null;
    }
}
