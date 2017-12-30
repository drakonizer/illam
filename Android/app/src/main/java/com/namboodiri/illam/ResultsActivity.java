package com.namboodiri.illam;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class ResultsActivity extends AppCompatActivity {

    String key;
    DatabaseHelper myDbHelper = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        String searchKey = getIntent().getStringExtra("KEY");
        key = searchKey;
        // Log.e("ILLAM: SELECTED: ", searchKey);
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
        TextView name = findViewById(R.id.name);
        TextView father = findViewById(R.id.father);
        TextView mother = findViewById(R.id.mother);
        name.setText(searchKey);
        father.setText(myDbHelper.getParents(searchKey, 2));
        mother.setText(myDbHelper.getParents(searchKey, 3));
        // Set values of dynamic cards (spouse)
        RecyclerView spouse = findViewById(R.id.recycler_spouse);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(myDbHelper.getResults(searchKey, 4, 7));
        TextView sp = findViewById(R.id.spouse_head);
        if (adapter.getItemCount()==0)
            sp.setVisibility(View.GONE);
        spouse.setAdapter(adapter);
        spouse.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        spouse.setLayoutManager(llm);
        // Set values of dynamic cards (child)
        RecyclerView child = findViewById(R.id.recycler_child);
        child.setNestedScrollingEnabled(false);
        RecyclerViewAdapter adapter2 = new RecyclerViewAdapter(myDbHelper.getResults(searchKey, 8, 23));
        TextView ch = findViewById(R.id.child_head);
        if (adapter2.getItemCount()==0)
            ch.setVisibility(View.GONE);
        child.setAdapter(adapter2);
        LinearLayoutManager llm2 = new LinearLayoutManager(this);
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        child.setLayoutManager(llm2);
    }

    public void clicked(View v)
    {
        TextView t;
        if(v.getId()== R.id.card2)
            t = findViewById(R.id.father);
        else
            t = findViewById(R.id.mother);
        String selected = t.getText().toString();
        if(!selected.equalsIgnoreCase("N/A")) {
            Intent intent;
            intent = new Intent(this, ResultsActivity.class);
            intent.putExtra("KEY", selected);
            // Log.e("ILLAM: SELECTED: ", selected);
            startActivity(intent);
        }
    }

}
