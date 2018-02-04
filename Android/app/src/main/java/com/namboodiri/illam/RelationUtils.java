package com.namboodiri.illam;

import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by root on 1/15/18.
 */

public class RelationUtils {

    public ArrayList<Relation> reduceRelation(Relation a, Relation b)
    {
        //Hashtable<String, ComplexRelation> ht = new Hashtable<String, ComplexRelation>();

        String[][] relations = {
                {"achan",   "achan",    "illathe muthashan",  "1"},
                {"achan",   "amma",     "illathe muthashi",   "1"},
                {"achan",   "wife",     "amma",               "1"},
                {"achan",   "makal",    "sister",             "1"},
                {"achan",   "brother",  "abhan",              "1"},
                {"achan",   "oppol",    "valye achammal",     "1"},
                {"achan",   "aniyathi", "achammal",           "1"},
                {"achan",   "sister",   "achammal",           "1"},
                {"achan",   "ettan",    "valyachan",          "1"},
                {"achan",   "aniyan",   "abhan",              "1"},

                {"abhan", "achan", "illathe muthashan", "1"},
                {"amma", "makal", "sister", "1"},
                {"amma", "makan", "brother", "1"},
                {"wife", "makan", "makan", "1"},

                {"illathe muthashan", "sister", "achan achammal", "1"},
        };

        // populate hash table
        /*
        for(int i=0; i<relations.length; i++) {
            // concatenate a and b to form the key
            // value is the complex relation and its score
            ArrayList<Relation> list = new ArrayList<Relation>();
            list.add(new Relation(relations[i][2], Integer.parseInt(relations[i][3])));
            ComplexRelation complex = new ComplexRelation(Integer.parseInt(relations[i][3]), list);
            ht.put(relations[i][0]+relations[i][1], complex);
        }


        String search = a.relation+b.relation;

        if(ht.containsKey(search)) {
            ComplexRelation c = ht.get(search);
            c.complexList.get(0).record = b.record;
            return c.complexList;
        } else {
            ArrayList<Relation> r = new ArrayList<Relation>();
            r.add(a);
            r.add(b);
            return r;
        }*/
        ArrayList<Relation> list = new ArrayList<Relation>();
        ComplexRelation c = new ComplexRelation(1,list);
        Relation rel = new Relation();

        // Achan
        if(a.relation.equals("achan") && b.relation.equals("achan")) {
            rel = new Relation("illathe muthashan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("amma")) {
            rel = new Relation("illathe muthashi", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("wife")) {
            rel = new Relation("amma", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("makal")) {
            rel = new Relation("sister", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("makan")) {
            rel = new Relation("brother", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("aniyan")) {
            rel = new Relation("abhan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("ettan")) {
            rel = new Relation("valyachan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("brother")) {
            if(a.record.year != null && b.record.year != null) {
                if(isAOlderThanB(a.record,b.record)) {
                    rel = new Relation("abhan", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                } else {
                    rel = new Relation("valyachan", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                }
            }
        }

        if(a.relation.equals("achan") && b.relation.equals("aniyathi")) {
            rel = new Relation("achammal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("oppol")) {
            rel = new Relation("valyachammal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("sister")) {
            if(a.record.year != null && b.record.year != null) {
                if(isAOlderThanB(a.record,b.record)) {
                    rel = new Relation("achammal", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                } else {
                    rel = new Relation("valyachammal", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                }
            }
        }

        // Amma
        if(a.relation.equals("amma") && b.relation.equals("achan")) {
            rel = new Relation("ammathe muthashan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("amma")) {
            rel = new Relation("ammathe muthashi", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("husband")) {
            rel = new Relation("achan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("makal")) {
            rel = new Relation("sister", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("makan")) {
            rel = new Relation("brother", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("aniyan")) {
            rel = new Relation("ammaman", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("achan") && b.relation.equals("ettan")) {
            rel = new Relation("valyammaman", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("brother")) {
            Log.e("ILLAM","amma+brother"+a.record.year+","+b.record.year);
            if(a.record.year != null && b.record.year != null) {
                if(isAOlderThanB(a.record,b.record)) {
                    rel = new Relation("ammaman", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                } else {
                    rel = new Relation("valyammaman", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                }
            }
        }

        if(a.relation.equals("amma") && b.relation.equals("aniyathi")) {
            rel = new Relation("chittashi", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("oppol")) {
            rel = new Relation("perashi", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("amma") && b.relation.equals("sister")) {
            if(a.record.year != null && b.record.year != null) {
                if(isAOlderThanB(a.record,b.record)) {
                    rel = new Relation("perashi", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                } else {
                    rel = new Relation("chittashi", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                }
            }
        }

        // reduce the relation - someone's brother to aniyan or ettan
        if(b.relation.equals("brother")) {
            Log.e("ILLAML","Reducing brother");
            if(a.record.year != null && b.record.year != null) {
                if(isAOlderThanB(a.record,b.record)) {
                    rel = new Relation("aniyan", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                } else {
                    rel = new Relation("ettan", 1);
                    rel.record = b.record;
                    c.complexList.add(rel);
                    return c.complexList;
                }
            }
        }

        // Wife
        if(a.relation.equals("wife") && b.relation.equals("makal")) {
            rel = new Relation("makal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("wife") && b.relation.equals("makan")) {
            rel = new Relation("makan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // Husband
        if(a.relation.equals("husband") && b.relation.equals("makal")) {
            rel = new Relation("makal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("husband") && b.relation.equals("makan")) {
            rel = new Relation("makal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // Makal
        if(a.relation.equals("makal") && b.relation.equals("achan")) {
            rel = new Relation("husband", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("makal") && b.relation.equals("amma")) {
            rel = new Relation("wife", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("makal") && (b.relation.equals("oppol") || b.relation.equals("aniyathi") || b.relation.equals("sister"))) {
            rel = new Relation("makal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("makal") && (b.relation.equals("ettan") || b.relation.equals("aniyan") || b.relation.equals("brother"))) {
            rel = new Relation("brother", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // Makan
        if(a.relation.equals("makan") && b.relation.equals("achan")) {
            rel = new Relation("husband", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("makan") && b.relation.equals("amma")) {
            rel = new Relation("wife", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("makan") && (b.relation.equals("oppol") || b.relation.equals("aniyathi") || b.relation.equals("sister"))) {
            rel = new Relation("makal", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("makan") && (b.relation.equals("ettan") || b.relation.equals("aniyan") || b.relation.equals("brother"))) {
            rel = new Relation("brother", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // Abhan
        if(a.relation.equals("abhan") && b.relation.equals("achan")) {
            rel = new Relation("illathe muthashan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("abhan") && b.relation.equals("amma")) {
            rel = new Relation("illathe muthashi", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("abhan") && b.relation.equals("wife")) {
            rel = new Relation("cheriyamma", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("abhan") && (b.relation.equals("oppol") || b.relation.equals("aniyathi") || b.relation.equals("sister"))) {
            rel = new Relation("sister", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        if(a.relation.equals("abhan") && (b.relation.equals("ettan") || b.relation.equals("aniyan") || b.relation.equals("brother"))) {
            rel = new Relation("brother", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // Illathe muthashan
        if(a.relation.equals("illathe muthashan") && (b.relation.equals("ettan") || b.relation.equals("aniyan") || b.relation.equals("brother"))) {
            rel = new Relation("illathe muthabhan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // Ammathe muthashan
        if(a.relation.equals("ammathe muthashan") && (b.relation.equals("ettan") || b.relation.equals("aniyan") || b.relation.equals("brother"))) {
            rel = new Relation("ammathe muthabhan", 1);
            rel.record = b.record;
            c.complexList.add(rel);
            return c.complexList;
        }

        // no match
        ArrayList<Relation> r = new ArrayList<Relation>();
        r.add(a);
        r.add(b);
        return r;
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
                    /*
                    if(sib.year != null && a.year != null) {
                        if(isAOlderThanB(sib,a)) {
                            rel.relation = "oppol";
                            rel.record = b;
                            return rel;
                        } else {
                            rel.relation = "aniyathi";
                            rel.record = b;
                            return rel;
                        }
                    } else {
                    */
                    rel.relation = "sister";
                    rel.record = b;
                    return rel;
                    //}
                }
                if(sib.name.equals(b.name) && b.gender.equals("male")) {
                    /*
                    if(sib.year != null && a.year != null) {
                        if(isAOlderThanB(sib,a)) {
                            rel.relation = "ettan";
                            rel.record = b;
                            return rel;
                        } else {
                            rel.relation = "aniyan";
                            rel.record = b;
                            return rel;
                        }
                    } else {*/
                    rel.relation = "brother";
                    rel.record = b;
                    return rel;
                    //}
                }
            }
        }

        return rel;
    }

    private Boolean isAOlderThanB(Person A, Person B) {
        if(Integer.parseInt(B.year)-Integer.parseInt(A.year) > 0)
            return true;
        else
            return false;
    }

    static String printList(ArrayList<Relation> list) {
        String ret = "";
        if(!list.isEmpty()) {
            for(int i=0; i<list.size(); i++) {
                ret = ret+list.get(i).relation+",yr="+list.get(i).record.year+",name="+list.get(i).record.name;
                //System.out.print();
                //Log.e("",list.get(i).relation+",");
            }
        }
        return ret;
    }

    // derive a complex relationship from a list of simple relations, when possible
    private ArrayList<Relation> getRelationDP(ArrayList<Relation> relations) {
        ComplexRelation [][] cost_matrix = new ComplexRelation[relations.size()][relations.size()];

        int i,j,k;
        double min;
        int s; //subsequences

        // initialize
        for(i=0; i<relations.size(); i++) {
            for(j=0; j<relations.size(); j++) {
                if(i==j) {
                    ArrayList<Relation> list = new ArrayList<Relation>();
                    list.add(relations.get(i));
                    ComplexRelation r = new ComplexRelation(1,list);

                    cost_matrix[i][j] = r;
                }
                else if(i > j) {
                    ArrayList<Relation> list = new ArrayList<Relation>();
                    ComplexRelation r = new ComplexRelation(0, list);
                    cost_matrix[i][j] = r;
                }
                else {
                    ArrayList<Relation> list = new ArrayList<Relation>();
                    ComplexRelation r = new ComplexRelation(Integer.MAX_VALUE, list);
                    cost_matrix[i][j] = r;
                }
            }
        }

        // objective: minimize sum of costs
        for(s=1; s<relations.size(); s++) {
            for(i=0; i<(relations.size()-s); i++) {
                j = i+s;
                min = cost_matrix[i][j].cost;

                for(k=i; k<j; k++) {

                    double cur_cost = 0;
                    ArrayList<Relation> red_list = new ArrayList<Relation>();
                    if(cost_matrix[i][k].complexList.size() == 1 && cost_matrix[k+1][j].complexList.size() == 1) {
                        // potential for reduction
                        red_list = reduceRelation(cost_matrix[i][k].complexList.get(0), cost_matrix[k+1][j].complexList.get(0));
                        cur_cost = (double)1.0/(double)red_list.get(0).score;
                    } else {
                        cur_cost = cost_matrix[i][k].cost+cost_matrix[k+1][j].cost;
                        red_list.addAll(cost_matrix[i][k].complexList);
                        red_list.addAll(cost_matrix[k+1][j].complexList);
                    }
                    if(cur_cost < min) {
                        min = cur_cost;
                        cost_matrix[i][j].cost = min;
                        cost_matrix[i][j].complexList = red_list;
                    }
                }
            }
        }


        String line="";
        for(i=0; i<relations.size(); i++) {
            for(j=0; j<relations.size(); j++) {
                //Log.e("ILLAM:",cost_matrix[i][j].cost+"("+printList(cost_matrix[i][j].complexList)+"),");
                line=line+cost_matrix[i][j].cost+"("+printList(cost_matrix[i][j].complexList)+"),";
                //printList(cost_matrix[i][j].complexList);
                //Log.e("",")");
            }
            Log.e("ILLAM", line);
            line="";
            //System.out.println("");
        }

        //return cost_matrix[0][relations.size()-1].cost;
        return cost_matrix[0][relations.size()-1].complexList;
    }

    public String getRelation(String A, String B, DatabaseHelper myDbH)
    {
        String result = "";
        LinkedList<Relation> customRelationList = new LinkedList<>();
        LinkedList<Relation> relationList = new LinkedList<>();
        Queue<Person> searchQ = new LinkedList<Person>();
        boolean found = false;

        DatabaseHelper myDbHelper = myDbH;

        Hashtable<Person, Person> predecessor = new Hashtable<Person, Person>();
        Hashtable<Person, Integer> visited = new Hashtable<>();

        Log.e("ILLAM","String A is "+A);
        Log.e("ILLAM","String B is "+B);

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

            /*
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
            */

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
                    Person child = persons.get(childIterator.next());
                    if(!visited.containsKey(child)) {
                        searchQ.add(child);
                        predecessor.put(child,n);
                    }
                }
            }

        }

        Relation dummy = new Relation("self",1);
        dummy.record = a;

        if(visited.get(b) == 1) {
            Person n = b;
            //relationList.addLast(dummy);
            while(!n.name.equals(a.name)) {
                Person pred = predecessor.get(n);
                relationList.addFirst(defineSimpleRelation(pred,n,persons));
                n = pred;
            }
            relationList.addFirst(dummy);
        }

        /*
        // shorten sibling relations
        String[] sibRelations = {"brother","sister","ettan","oppol","aniyan","aniyathi"};

        int oldLen;
        int newLen;
        do {
            oldLen = relationList.size();
            for (int i = 0; i < oldLen - 1; i++) {
                if(relationList.get(i) != null && relationList.get(i+1) != null) {
                    if(Arrays.asList(sibRelations).contains(relationList.get(i).relation) &&
                            Arrays.asList(sibRelations).contains(relationList.get(i+1).relation)) {
                        relationList.set(i, relationList.get(i+1));
                        relationList.set(i+1, null);
                    }
                }
            }
            // remove all elements set to null
            relationList.remove(null);
            newLen = relationList.size();
        } while(newLen < oldLen);

        // the relation represents two people A and B and defines the relationship
        // B is A's "relation"
        // defineCustomRelation must return an array of relations
        // allows rewording certain relationships
        // e.g. illathe muthashan sister = achan achammal


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
        */
        Iterator<Relation> my = relationList.iterator();
        while (my.hasNext()) {
            Log.e("ILLAM:","Hello "+my.next().relation);
        }

        ArrayList<Relation> t = new ArrayList<Relation>();
        t.addAll(relationList);
        t = getRelationDP(t);

        Iterator<Relation> it = t.iterator();
        while (it.hasNext()) {
            Relation r = it.next();
            if(!r.relation.equals("self")) {
                if(it.hasNext()) {
                    result = result + r.relation + "'s ";
                } else {
                    result = result + r.relation;
                }
            }
        }

        return result;
    }


}
