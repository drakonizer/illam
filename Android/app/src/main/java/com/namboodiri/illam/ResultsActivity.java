package com.namboodiri.illam;

import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class ResultsActivity extends AppCompatActivity {

    DatabaseHelper myDbHelper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        String searchKey = getIntent().getStringExtra("KEY");
        Button name = findViewById(R.id.name);
        Button father = findViewById(R.id.father);
        Button mother = findViewById(R.id.mother);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
        setButtonText(searchKey);
        father.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonText(((Button)view).getText().toString());
            }
        });
        mother.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonText(((Button)view).getText().toString());
            }
        });
    }
    private void setButtonText(String k){
        Button name = findViewById(R.id.name);
        Button father = findViewById(R.id.father);
        Button mother = findViewById(R.id.mother);
        String texts[];
        texts = myDbHelper.getResults(k);
        if(texts[0]!=null)
        {
            name.setText(texts[0]);
            father.setText(texts[1]);
            mother.setText(texts[2]);
        }
    }
}
