package com.namboodiri.illam;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.io.IOException;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final RecyclerView myView = findViewById(R.id.recycler);
        int val = getIntent().getIntExtra("ACTION", 0);
        if(val == 1) {
            RecyclerViewAdapter.caller = 1;
            RecyclerViewAdapter.toSend = getIntent().getIntExtra("CALLER", 0);
            myView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please search for a person and click on their profile to continue",
                    Toast.LENGTH_LONG).show();
        }
        final FloatingSearchView mSearchView = findViewById(R.id.floating_search_view);
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                //do nothing for now
            }
            @Override
            public void onSearchAction(String query) {
                DatabaseHelper myDbHelper = new DatabaseHelper(myView.getContext());
                try {
                    myDbHelper.createDataBase();
                } catch (IOException ioe) {
                    throw new Error("Unable to create database");
                }
                try {
                    myDbHelper.openDataBase();
                } catch (SQLException sqle) {
                    throw sqle;
                }
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(myDbHelper.getDbData(query));
                myView.setAdapter(adapter);
                LinearLayoutManager llm = new LinearLayoutManager(myView.getContext());
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                myView.setLayoutManager(llm);
                myView.setVisibility(View.VISIBLE);
            }
        });
    }
    public void onRelClick(View v)
    {
        Intent intent = new Intent (v.getContext(), RelationshipSearch.class);
        startActivity(intent);
    }
}
