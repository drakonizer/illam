package com.namboodiri.illam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.Iterator;

class Relation {
    String relation;
    Person record;
    String thavazhi;
}

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

    private Relation defineSimpleRelation(Person a, Person b, Hashtable<String, Person> persons)
    {
        Relation rel = new Relation();
        if(a.father!=null) {
            if(a.father.equals(b.name)) {
                rel.relation = "achan";
                rel.record = b;
                return rel;
            }
        }

        if(a.mother!=null) {
            if(a.mother.equals(b.name)) {
                rel.relation = "amma";
                rel.record = b;
                return rel;
            }
        }

        if(!a.spouses.isEmpty()) {
            Iterator<String> spouseIterator = a.spouses.iterator();
            while (spouseIterator.hasNext()) {
                Person spouse = persons.get(spouseIterator.next());
                if(spouse.name.equals(b.name) && b.gender.equals("female")) {
                    rel.relation = "wife";
                    rel.record = b;
                    return rel;
                }
                if(spouse.name.equals(b.name) && b.gender.equals("male")) {
                    rel.relation = "husband";
                    rel.record = b;
                    return rel;
                }
            }
        }

        if(!a.children.isEmpty()) {
            Iterator<String> childIterator = a.children.iterator();
            while (childIterator.hasNext()) {
                Person child = persons.get(childIterator.next());
                if(child.name.equals(b.name) && b.gender.equals("female")) {
                    rel.relation = "makal";
                    rel.record = b;
                    return rel;
                }
                if(child.name.equals(b.name) && b.gender.equals("male")) {
                    rel.relation = "makan";
                    rel.record = b;
                    return rel;
                }
            }
        }

        if(!a.siblings.isEmpty()) {
            Iterator<String> sibIterator = a.siblings.iterator();
            while (sibIterator.hasNext()) {
                Person sib = persons.get(sibIterator.next());
                if(sib.name.equals(b.name) && b.gender.equals("female")) {
                    rel.relation = "sister";
                    rel.record = b;
                    return rel;
                }
                if(sib.name.equals(b.name) && b.gender.equals("male")) {
                    rel.relation = "brother";
                    rel.record = b;
                    return rel;
                }
            }
        }

        return rel;
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
                {"achan"     ,"wife",      "amma"},
                {"amma"      ,"husband",   "achan"},
                //
                {"wife"      ,"makan",     "makan"},
                {"wife"      ,"makal",     "makal"},
                //
                {"husband"   ,"makan",     "makan"},
                {"husband"   ,"makal",     "makal"},
                //
                {"amma"      ,"makan",     "brother"},
                {"amma"      ,"makal",     "sister"},
                //
                {"achan"      ,"makan",    "brother"},
                {"achan"      ,"makal",    "sister"},
                //
                {"sister"     ,"amma",     "amma"},
                {"brother"    ,"amma",     "amma"},
                //
                {"sister"     ,"achan",    "achan"},
                {"brother"    ,"achan",    "achan"},
                //
                {"amma"      ,"achan",     "ammathe muthashan"},
                {"amma"      ,"amma",      "ammathe muthashi"},
                //
                {"achan"      ,"achan",     "illathe muthashan"},
                {"achan"      ,"amma",      "illathe muthashi"},
                //
                {"ammathe muthashan"      ,"wife",        "ammathe muthashi"},
                {"ammathe muthashan"      ,"brother",     "ammathe muthabhan"},
                //
                {"illathe muthashan"      ,"wife",        "illathe muthashi"},
                {"illathe muthashan"      ,"brother",     "illathe muthabhan"},
                //
                {"ammathe muthashi"      ,"husband",      "ammathe muthashan"},
                //
                {"illathe muthashi"      ,"husband",      "illathe muthashan"}
        };

        // populate hash table
        for(int i=0; i<relations.length; i++) {
            // use first two strings of relations as key, third is the result
            ht.put(relations[i][0]+relations[i][1], relations[i][2]);
        }

        String search = custom.relation+simple.relation;
        if(ht.containsKey(search)) {
            rel.relation = ht.get(search);
            return rel;
        } else {
            rel.relation = "none";
            return rel;
        }
    }

    private Stack<Relation> reducePass(Stack<Relation> relationStack, Stack<Relation> customRelationStack)
    {
        while(!relationStack.isEmpty()) {
            Relation rel = relationStack.pop();

            if(customRelationStack.isEmpty())
                customRelationStack.push(rel);
            else {
                Relation top = customRelationStack.pop();
                Relation custom = defineCustomRelation(top,rel);
                if(custom.relation.equals("none")) {
                    customRelationStack.push(top);
                    customRelationStack.push(rel);
                } else {
                    customRelationStack.push(custom);
                }
            }
        }
        return customRelationStack;
    }


    public String getRelation(String A, String B)
    {
        String result = "";
        LinkedList<Relation> customRelationList = new LinkedList<>();
        LinkedList<Relation> relationList = new LinkedList<>();
        Queue<Person> searchQ = new LinkedList<Person>();
        boolean found = false;
        DatabaseHelper myDbHelper = new DatabaseHelper(this);


        Hashtable<Person, Person> predecessor = new Hashtable<Person, Person>();
        Hashtable<Person, Integer> visited = new Hashtable<>();

        if(A.equals(B))
            return "Please provide unique name for each person";

        Hashtable<String, Person> persons = myDbHelper.getPersons();
        Person a = myDbHelper.getPerson(persons, A);
        Person b = myDbHelper.getPerson(persons, B);

        searchQ.add(a);
        predecessor.put(a,a);

        // We will use a breadth-first-search algorithm to explore the family tree starting from record B
        // If record A was found during the search process, stop. We will then back-track and print the relation
        // Otherwise, we throw a message that says the relation was not found.
        // a breadth-first-search strategy is used to find the closest relation.
        // try to search in father, mother, all sons, all daughter, spouse's relations, mark visited

        //  as we traverse the graph, we mark at each node the previous node from which we reached this node
        //  we store this predecssor information in the hash-table %predecessor

        while(!searchQ.isEmpty()) {
            Person n = searchQ.remove();

            if(!visited.containsKey(n)) {
                visited.put(n,1);
                if(n.name.equals(b.name)) {
                    found = true;
                    break;
                }
            }

            if(!n.siblings.isEmpty()) {
                Iterator<String> sibIterator = n.siblings.iterator();
                while (sibIterator.hasNext()) {
                    //System.out.println(crunchifyIterator.next());
                    Person sib = persons.get(sibIterator.next());
                    if(!visited.containsKey(sib)) {
                        searchQ.add(sib);
                        predecessor.put(sib,n);
                    }
                }
            }

            if(!n.spouses.isEmpty()) {
                Iterator<String> spouseIterator = n.spouses.iterator();
                while (spouseIterator.hasNext()) {
                    //System.out.println(crunchifyIterator.next());
                    Person spouse = persons.get(spouseIterator.next());
                    if(!visited.containsKey(spouse)) {
                        searchQ.add(spouse);
                        predecessor.put(spouse,n);
                    }
                }
            }

            if(n.father != null) {
                Person father = persons.get(n.father);
                if(!visited.containsKey(father)) {
                    searchQ.add(father);
                    predecessor.put(father,n);
                }
            }

            if(n.mother != null) {
                Person mother = persons.get(n.mother);
                if(!visited.containsKey(mother)) {
                    searchQ.add(mother);
                    predecessor.put(mother,n);
                }
            }

            if(!n.children.isEmpty()) {
                Iterator<String> childIterator = n.children.iterator();
                while (childIterator.hasNext()) {
                    //System.out.println(crunchifyIterator.next());
                    Person child = persons.get(childIterator.next());
                    if(!visited.containsKey(child)) {
                        searchQ.add(child);
                        predecessor.put(child,n);
                    }
                }
            }

        }

        if(visited.get(b) == 1) {
            Person n = b;
            while(!n.name.equals(a.name)) {
                Person pred = predecessor.get(n);
                relationList.addFirst(defineSimpleRelation(pred,n,persons));
                n = pred;
            }
        }



        // first pass
        //customRelationStack = relationStack;
        //customRelationStack = reducePass(relationStack, customRelationStack);
        //relationStack = (Stack<Relation>)customRelationStack.clone();
        //Collections.reverse(relationStack);


        // second pass
        //customRelationStack = reducePass(relationStack, customRelationStack);

        //Collections.reverse(customRelationStack);
        //Iterator<Relation> relIterator = relationList.iterator();
        int oldLen;
        int newLen;
        do {
            oldLen = relationList.size();
            for (int i = 0; i < oldLen - 1; i++) {
                Relation custom = defineCustomRelation(relationList.get(i), relationList.get(i + 1));
                if (!custom.relation.equals("none")) {
                    relationList.set(i, custom);
                    relationList.set(i + 1, null);
                }
            }
            // remove all elements set to null
            relationList.remove(null);
            newLen = relationList.size();
        } while(newLen < oldLen);

        while(!relationList.isEmpty()) {
            result = result+relationList.remove().relation;
        }
        // diya, umadevi
        //wife achan brother achan
        //-->achan brother achan wife

        /*
        while(!relationStack.isEmpty()) {
            result = result+relationStack.pop().relation;
        }
        */

        //makal, wife, makan, husband
        //achan, brother, achan, wife


        return result;
    }

    public void onSearchClick(View v)
    {
        // This is the function that gets executed when the search button is clicked
        String res = getRelation(name1, name2);
        Toast.makeText(this, res,
                Toast.LENGTH_LONG).show();

    }
}
