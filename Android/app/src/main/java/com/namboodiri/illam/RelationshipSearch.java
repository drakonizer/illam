package com.namboodiri.illam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RelationshipSearch extends AppCompatActivity {
    public static String name1 = "Please select a user to begin";
    public static String name2 = "Please select a user to begin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_search);
        String name = getIntent().getStringExtra("NAME");
        int personSelected = getIntent().getIntExtra("ACTION", 0);
        TextView person1 = findViewById(R.id.rel1);
        TextView person2 = findViewById(R.id.rel2);
        if(personSelected == 1) {
            name1 = name;
            person1.setText(name1);
            person2.setText(name2);
        }
        else if (personSelected == 2) {
            name2 = name;
            person2.setText(name2);
            person1.setText(name1);
        }
        if(!person1.getText().toString().equalsIgnoreCase("Please select a user to begin") && !person1.getText().toString().equalsIgnoreCase("Please select a user to begin"))
        {
            Button search = findViewById(R.id.find_rel);
            search.setEnabled(true);
        }

    }
    public void selectPerson(View v)
    {
        int a;
        if (v.getId() == R.id.rel1_button)
            a = 1;
        else
            a = 2;
        Intent intent = new Intent(v.getContext(), SearchActivity.class);
        intent.putExtra("CALLER", a);
        intent.putExtra("ACTION", 1);
        startActivity(intent);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        name1 = "Please select a user to begin";
        name2 = "Please select a user to begin";
    }

    public void onSearchClick(View v)
    {
        // This is the function that gets executed when the search button is clicked
    }
}
