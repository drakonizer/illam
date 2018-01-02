package com.namboodiri.illam;


import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.io.IOException;


public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_search, container, false);
        final FloatingSearchView mSearchView = v.findViewById(R.id.floating_search_fragment);
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                //do nothing for now
            }

            @Override
            public void onSearchAction(String query) {
                final RecyclerView myView = v.findViewById(R.id.recycler_frag);
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
        return v;
    }

}
