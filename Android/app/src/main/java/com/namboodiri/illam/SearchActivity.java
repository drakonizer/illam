package com.namboodiri.illam;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.io.IOException;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final FloatingSearchView mSearchView = findViewById(R.id.floating_search_view);
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                //do nothing for now
            }
            @Override
            public void onSearchAction(String query) {
                RecyclerView myView = findViewById(R.id.recycler);
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
}
