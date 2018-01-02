package com.namboodiri.illam;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HostActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        int a= 0;
        a = getIntent().getIntExtra("TAB", 0);
        if (a!=0)
            viewPager.setCurrentItem(1);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "Browse");
        adapter.addFragment(new RelationshipFragment(), "Find Relation");
        viewPager.setAdapter(adapter);
    }

    public void selectPerson(View v)
    {
        int a;
        if (v.getId() == R.id.card_rel1)
            a = 1;
        else
            a = 2;
        Intent intent = new Intent(v.getContext(), SearchActivity.class);
        intent.putExtra("CALLER", a);
        intent.putExtra("ACTION", 1);
        startActivity(intent);
    }

    public void invert(View b)
    {
        RelationshipFragment temp = new RelationshipFragment();
        TextView t1 = findViewById(R.id.rel1);
        TextView t2 = findViewById(R.id.rel2);
        temp.swap(t1, t2);
    }

    public void onDestroy()
    {
        super.onDestroy();
        RelationshipFragment.resetSearch();
    }
}
