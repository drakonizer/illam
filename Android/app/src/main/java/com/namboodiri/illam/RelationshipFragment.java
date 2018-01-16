package com.namboodiri.illam;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Stack;
import java.util.List;
import java.util.Set;


public class RelationshipFragment extends Fragment {
    public static String name1 = "Select Person 1";
    public static String name2 = "Select Person 2";


    public static void resetSearch()
    {
        name1 = "Select Person 1";
        name2 = "Select Person 2";
    }

    public RelationshipFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frag = inflater.inflate(R.layout.fragment_relationship, container, false);
        TextView person1 = frag.findViewById(R.id.rel1);
        TextView person2 = frag.findViewById(R.id.rel2);
        person1.setText(name1);
        person2.setText(name2);
        if(!person1.getText().toString().equalsIgnoreCase("Select Person 1") && !person1.getText().toString().equalsIgnoreCase("Select Person 2"))
        {
            Button search = frag.findViewById(R.id.find_rel);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchClick(v);
                }
            });
            search.setEnabled(true);
        }

        return frag;
    }

    private void unitTests() {
        DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
        Hashtable<String, Person> ht;
        ht = myDbHelper.getPersons();

        Person me = new Person();
        Set<String> keys = ht.keySet();

        ArrayList<Person> others = new ArrayList<Person>();
        Queue<Person> searchQ = new LinkedList<Person>();

        for(String key: keys) {
            //Log.e("ILLAM","Person is "+key);
            if(ht.containsKey(key)) {
                //Log.e("ILLAM", "Key exists");
                if(key.contains("Deepak Unnikrishnan")) {
                    Log.e("ILLAM", "Found Deepak");
                    me = ht.get(key);
                    break;
                }
            } else {
                Log.e("ILLAM", "Key does not exist");
            }
        }

        Person dpk = me;

        for(int k=0; k<2; k++) {
            searchQ.add(ht.get(me.father));
            searchQ.add(ht.get(me.mother));
            for (int i = 0; i < me.siblings.size(); i++) {
                searchQ.add(ht.get(me.siblings.get(i)));
            }
            for (int i = 0; i < me.children.size(); i++) {
                searchQ.add(ht.get(me.children.get(i)));
            }
            for (int i = 0; i < me.spouses.size(); i++) {
                searchQ.add(ht.get(me.spouses.get(i)));
            }
            me = ht.get(me.father);
        }

        while(!searchQ.isEmpty()) {
            Person p = searchQ.remove();
            Log.e("ILLAM: Calculating ", dpk.name+" and "+p.name);
            //Log.e("ILLAM:", dpk.name+"'s "+getRelation(dpk.name,p.name)+" is "+p.name);
        }

        /*
        String test = new String("Cholamana Diya Deepak");
        if(ht.containsKey(test)) {
            Log.e("ILLAM", "Key exists");
        } else {
            Log.e("ILLAM", "Key does not exist");
        }
        */
        //Person a = ht.get("Cholamana Deepak Unnikrishnan");


    }

    public void onSearchClick(View v)
    {
        // This is the function that gets executed when the search button is clicked
        RelationUtils utils = new RelationUtils();
        DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
        String res = utils.getRelation(name1, name2, myDbHelper);
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        if (res == "")
        {
            alert.setTitle("Relationship Not Found! :(");
            alert.setMessage("We couldn't find any known relationship between the selected users");
        }
        else
        {
            alert.setTitle("Found relation!");
            alert.setMessage(name2.trim() + " is " + name1.trim() + "'s " + res);
            //alert.setMessage(name1.trim()+ "'de " + res +" aanu "+name2.trim());
        }
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
        //unitTests();
    }

    public void swap(TextView t1, TextView t2)
    {
        String temp = name1;
        name1 = name2;
        name2 = temp;
        t1.setText(name1);
        t2.setText(name2);
    }



    private Relation defineCustomRelation(Relation custom, Relation simple)
    {
        Relation rel = new Relation();
        Hashtable<String, String> ht = new Hashtable<String, String>();

        if(custom == null || simple == null) {
            rel.relation = "none";
            return rel;
        }

        // Custom relations
        String[][] relations = {
                //achan
                {"achan"     ,"achan",     "illathe muthashan"},
                {"achan"     ,"amma",      "illathe muthashi"},
                {"achan"     ,"wife",      "amma"},
                {"achan"     ,"makal",     "sister"},
                {"achan"     ,"makan",     "brother"},
                {"achan"     ,"oppol",     "valye achammal"},
                {"achan"     ,"aniyathi",  "achammal"},
                {"achan"     ,"sister",    "achammal"},
                {"achan"     ,"ettan",     "valyachan"},
                {"achan"     ,"aniyan",    "abhan"},
                //
                {"amma"      ,"achan",     "ammathe muthashan"},
                {"amma"      ,"amma",      "ammathe muthashi"},
                {"amma"      ,"husband",   "achan"},
                {"amma"      ,"makal",     "sister"},
                {"amma"      ,"makan",     "brother"},
                {"amma"      ,"oppol",     "perashi"},
                {"amma"      ,"aniyathi",  "chittashi"},
                {"amma"      ,"ettan",     "valye ammaman"},
                {"amma"      ,"aniyan",    "ammaman"},
                {"amma"      ,"brother",   "ammaman"},
                //
                {"wife"      ,"makal",     "makal"},
                {"wife"      ,"makan",     "makan"},
                //
                {"husband"   ,"makal",     "makal"},
                {"husband"   ,"makan",     "makan"},
                //
                {"makal"     ,"achan",     "husband"},
                {"makal"     ,"amma",      "wife"},
                {"makal"     ,"oppol",     "makal"},
                {"makal"     ,"aniyathi",  "makal"},
                {"makal"     ,"sister",    "makal"},
                {"makal"     ,"ettan",     "makan"},
                {"makal"     ,"aniyan",    "makan"},
                {"makal"     ,"brother",   "makan"},
                {"makal"     ,"illathe muthashan",     "achan"},
                {"makal"     ,"illathe muthashi",      "amma"},
                //
                {"makan"     ,"achan",     "husband"},
                {"makan"     ,"amma",      "wife"},
                {"makan"     ,"oppol",     "makal"},
                {"makan"     ,"aniyathi",  "makal"},
                {"makan"     ,"sister",    "makal"},
                {"makan"     ,"ettan",     "makan"},
                {"makan"     ,"aniyan",    "makan"},
                {"makan"     ,"brother",   "makan"},
                {"makan"     ,"illathe muthashan",     "achan"},
                {"makan"     ,"illathe muthashi",      "amma"},
                //
                {"oppol"     ,"achan",     "achan"},
                {"oppol"     ,"amma",      "amma"},
                {"oppol"     ,"oppol",     "oppol"},
                {"oppol"     ,"aniyathi",  "sister"},
                {"oppol"     ,"sister",    "sister"},
                {"oppol"     ,"ettan",     "brother"},
                {"oppol"     ,"aniyan",    "brother"},
                {"oppol"     ,"brother",   "brother"},
                {"oppol"     ,"ammathe muthashan", "ammathe muthashan"},
                {"oppol"	 ,"ammathe muthashi",  "ammathe muthashi"},
                {"oppol"     ,"illathe muthashan", "illathe muthashan"},
                {"oppol"     ,"illathe muthashi",  "illathe muthashi"},
                {"oppol"     ,"ammathe muthabhan", "ammathe muthabhan"},
                {"oppol"     ,"illathe muthabhan", "illathe muthabhan"},
                {"oppol"     ,"valye achammal",	   "valye achammal"},
                {"oppol"     ,"achammal",          "achammal"},
                {"oppol"     ,"valyachan",         "valyachan"},
                {"oppol"     ,"abhan",             "abhan"},
                {"oppol"     ,"perashi",           "perashi"},
                {"oppol"     ,"chittashi",         "chittashi"},
                {"oppol"     ,"valyammaman",       "valyammaman"},
                {"oppol"     ,"ammaman",           "ammaman"},
                {"oppol"     ,"cheriyamma",        "cheriyamma"},
                //
                {"aniyathi"     ,"achan",     "achan"},
                {"aniyathi"     ,"amma",      "amma"},
                {"aniyathi"     ,"oppol",     "sister"},
                {"aniyathi"     ,"aniyathi",  "anniyathi"},
                {"aniyathi"     ,"sister",    "sister"},
                {"aniyathi"     ,"ettan",     "brother"},
                {"aniyathi"     ,"aniyan",    "brother"},
                {"aniyathi"     ,"brother",   "brother"},
                {"aniyathi"     ,"ammathe muthashan", "ammathe muthashan"},
                {"aniyathi"	    ,"ammathe muthashi",  "ammathe muthashi"},
                {"aniyathi"     ,"illathe muthashan", "illathe muthashan"},
                {"aniyathi"     ,"illathe muthashi",  "illathe muthashi"},
                {"aniyathi"     ,"ammathe muthabhan", "ammathe muthabhan"},
                {"aniyathi"     ,"illathe muthabhan", "illathe muthabhan"},
                {"aniyathi"     ,"valye achammal",	   "valye achammal"},
                {"aniyathi"     ,"achammal",          "achammal"},
                {"aniyathi"     ,"valyachan",         "valyachan"},
                {"aniyathi"     ,"abhan",             "abhan"},
                {"aniyathi"     ,"perashi",           "perashi"},
                {"aniyathi"     ,"chittashi",         "chittashi"},
                {"aniyathi"     ,"valyammaman",       "valyammaman"},
                {"aniyathi"     ,"ammaman",           "ammaman"},
                {"aniyathi"     ,"cheriyamma",        "cheriyamma"},
                //
                {"sister"     ,"achan",     "achan"},
                {"sister"     ,"amma",      "amma"},
                {"sister"     ,"oppol",     "sister"},
                {"sister"     ,"sister",    "sister"},
                {"sister"     ,"aniyathi",  "sister"},
                {"sister"     ,"ettan",     "brother"},
                {"sister"     ,"aniyan",    "brother"},
                {"sister"     ,"brother",   "brother"},
                {"sister"     ,"ammathe muthashan", "ammathe muthashan"},
                {"sister"	  ,"ammathe muthashi",  "ammathe muthashi"},
                {"sister"     ,"illathe muthashan", "illathe muthashan"},
                {"sister"     ,"illathe muthashi",  "illathe muthashi"},
                {"sister"     ,"ammathe muthabhan", "ammathe muthabhan"},
                {"sister"     ,"illathe muthabhan", "illathe muthabhan"},
                {"sister"     ,"valye achammal",	"valye achammal"},
                {"sister"     ,"achammal",          "achammal"},
                {"sister"     ,"valyachan",         "valyachan"},
                {"sister"     ,"abhan",             "abhan"},
                {"sister"     ,"perashi",           "perashi"},
                {"sister"     ,"chittashi",         "chittashi"},
                {"sister"     ,"valyammaman",       "valyammaman"},
                {"sister"     ,"ammaman",           "ammaman"},
                {"sister"     ,"cheriyamma",        "cheriyamma"},
                //
                {"ettan"     ,"achan",     "achan"},
                {"ettan"     ,"amma",      "amma"},
                {"ettan"     ,"oppol",     "oppol"},
                {"ettan"     ,"aniyathi",  "sister"},
                {"ettan"     ,"sister",    "sister"},
                {"ettan"     ,"ettan",     "ettan"},
                {"ettan"     ,"aniyan",    "brother"},
                {"ettan"     ,"brother",   "brother"},
                {"ettan"     ,"ammathe muthashan", "ammathe muthashan"},
                {"ettan"	 ,"ammathe muthashi",  "ammathe muthashi"},
                {"ettan"     ,"illathe muthashan", "illathe muthashan"},
                {"ettan"     ,"illathe muthashi",  "illathe muthashi"},
                {"ettan"     ,"ammathe muthabhan", "ammathe muthabhan"},
                {"ettan"     ,"illathe muthabhan", "illathe muthabhan"},
                {"ettan"     ,"valye achammal",	   "valye achammal"},
                {"ettan"     ,"achammal",          "achammal"},
                {"ettan"     ,"valyachan",         "valyachan"},
                {"ettan"     ,"abhan",             "abhan"},
                {"ettan"     ,"perashi",           "perashi"},
                {"ettan"     ,"chittashi",         "chittashi"},
                {"ettan"     ,"valyammaman",       "valyammaman"},
                {"ettan"     ,"ammaman",           "ammaman"},
                {"ettan"     ,"cheriyamma",        "cheriyamma"},
                //
                {"aniyan"     ,"achan",     "achan"},
                {"aniyan"     ,"amma",      "amma"},
                {"aniyan"     ,"oppol",     "sister"},
                {"aniyan"     ,"aniyathi",  "aniyathi"},
                {"aniyan"     ,"sister",    "sister"},
                {"aniyan"     ,"ettan",     "brother"},
                {"aniyan"     ,"aniyan",    "aniyan"},
                {"aniyan"     ,"brother",   "brother"},
                {"aniyan"     ,"ammathe muthashan", "ammathe muthashan"},
                {"aniyan"     ,"ammathe muthashi",  "ammathe muthashi"},
                {"aniyan"     ,"illathe muthashan", "illathe muthashan"},
                {"aniyan"     ,"illathe muthashi",  "illathe muthashi"},
                {"aniyan"     ,"ammathe muthabhan", "ammathe muthabhan"},
                {"aniyan"     ,"illathe muthabhan", "illathe muthabhan"},
                {"aniyan"     ,"valye achammal",	"valye achammal"},
                {"aniyan"     ,"achammal",          "achammal"},
                {"aniyan"     ,"valyachan",         "valyachan"},
                {"aniyan"     ,"abhan",             "abhan"},
                {"aniyan"     ,"perashi",           "perashi"},
                {"aniyan"     ,"chittashi",         "chittashi"},
                {"aniyan"     ,"valyammaman",       "valyammaman"},
                {"aniyan"     ,"ammaman",           "ammaman"},
                {"aniyan"     ,"cheriyamma",        "cheriyamma"},
                //
                {"brother"     ,"achan",     "achan"},
                {"brother"     ,"amma",      "amma"},
                {"brother"     ,"oppol",     "sister"},
                {"brother"     ,"aniyathi",  "sister"},
                {"brother"     ,"sister",    "sister"},
                {"brother"     ,"ettan",     "brother"},
                {"brother"     ,"aniyan",    "brother"},
                {"brother"     ,"brother",   "brother"},
                {"brother"     ,"ammathe muthashan", "ammathe muthashan"},
                {"brother"     ,"ammathe muthashi",  "ammathe muthashi"},
                {"brother"     ,"illathe muthashan", "illathe muthashan"},
                {"brother"     ,"illathe muthashi",  "illathe muthashi"},
                {"brother"     ,"ammathe muthabhan", "ammathe muthabhan"},
                {"brother"     ,"illathe muthabhan", "illathe muthabhan"},
                {"brother"     ,"valye achammal",	"valye achammal"},
                {"brother"     ,"achammal",          "achammal"},
                {"brother"     ,"valyachan",         "valyachan"},
                {"brother"     ,"abhan",             "abhan"},
                {"brother"     ,"perashi",           "perashi"},
                {"brother"     ,"chittashi",         "chittashi"},
                {"brother"     ,"valyammaman",       "valyammaman"},
                {"brother"     ,"ammaman",           "ammaman"},
                {"brother"     ,"cheriyamma",        "cheriyamma"},
                //
                {"abhan"	   ,"achan",	         "illathe muthashan"},
                {"abhan"	   ,"amma",   	         "illathe muthashi"},
                {"abhan"	   ,"wife",  	         "cheriyamma"},
                {"abhan"	   ,"oppol",  	         "achammal"},
                {"abhan"	   ,"aniyathi",          "achammal"},
                {"abhan"	   ,"sister",  	         "achammal"},
                {"abhan"	   ,"aniyan",  	         "abhan"},
                //
                //
                {"ammathe muthashan"      ,"wife",        "ammathe muthashi"},
                {"ammathe muthashan"      ,"brother",     "ammathe muthabhan"},
                //
                {"illathe muthashan"      ,"wife",        "illathe muthashi"},
                {"illathe muthashan"      ,"brother",     "illathe muthabhan"},
                {"illathe muthashan"      ,"sister",      "achammal"},
                //
                {"ammathe muthashi"      ,"husband",      "ammathe muthashan"},
                //
                {"illathe muthashi"      ,"husband",      "illathe muthashan"},
                //
                {"sister"     ,"brother",   "brother"},
                {"sister"     ,"sister",    "sister"},
                {"brother"    ,"brother",   "brother"},
                {"brother"    ,"sister",    "sister"}
        };

        // populate hash table
        for(int i=0; i<relations.length; i++) {
            // use first two strings of relations as key, third is the result
            // allow breaking relationships (return an array rather than single)
            // allow age comparison
            // compare custom.age and simple.age and use the relationship in calculating
            // the relationship
            ht.put(relations[i][0]+relations[i][1], relations[i][2]);
        }

        String search = custom.relation+simple.relation;
        if(ht.containsKey(search)) {
            rel.relation = ht.get(search);
            return rel;
        } else {
            rel.relation = "none";
            ArrayList<Relation> complexList;
            //complexList.add(a);
            //complexList.add(b);
            //return complexList;
            return rel;
        }
    }


}
