package com.namboodiri.illam;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Button search = findViewById(R.id.searchButton);
        search.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText searchKey = findViewById(R.id.searchInput);
                String srch;
                srch = searchKey.getText().toString();
                if(srch!=null)
                {
                    Intent intent;
                    intent = new Intent(searchKey.getContext(), SearchResults.class);
                    intent.putExtra("KEY", srch);
                    startActivity(intent);
                }
            }
        });
    }
}
