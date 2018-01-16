package com.namboodiri.illam;

/**
 * Created by root on 1/15/18.
 */

public class Relation {
    String relation;
    Person record;
    String thavazhi;
    int score;

    Relation() {
        relation = "";
        score = 0;
    }

    Relation(String s, int c) {
        relation = s;
        score = c;
    }

}
