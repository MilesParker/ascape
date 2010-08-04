/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.bb;

import java.util.Random;

class Brr {

    Random rnd;
    char aRule[];
    int ruleLength;

//---------------------------------------------------------------------------
//Constructor
//
    Brr(int rL, long rS) {
        long randomSeed = 123456;
        ruleLength = rL;
        randomSeed = rS;

        rnd = new Random(randomSeed);
        aRule = new char[ruleLength];
    }

//---------------------------------------------------------------------------
//Randomizer
//
    String getCurrentRule() {
        int i;

        for (i = 0; i < ruleLength; i++) {
            if (rnd.nextDouble() < 0.5)
                aRule[i] = '0';
            else
                aRule[i] = '1';
        }
        return String.valueOf(aRule);
    }
//---------------------------------------------------------------------------
}
