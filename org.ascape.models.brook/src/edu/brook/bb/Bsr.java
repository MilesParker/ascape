/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.bb;

class Bsr {

    String rule2;
    String rule3;
    String rule4;
    int maxStati;

//---------------------------------------------------------------------------
//Constructor
//
    Bsr(int mS) {
        maxStati = mS;

        rule2 = "1101" + //00 -> 1R1
            "1001" + //01 -> 1L1
            "1000" + //10 -> 1L0
            "1010";   //11 -> 1L2

        rule3 = "1101" + //00 -> 1R1
            "1010" + //01 -> 1L2
            "1000" + //10 -> 1L0
            "1101" + //11 -> 1R1
            "1001" + //20 -> 1L1
            "1111";   //21 -> 1R3

        rule4 = "11001" + //00 -> 1R1
            "10001" + //01 -> 1L1
            "10000" + //10 -> 1L0
            "00010" + //11 -> 0L2
            "11100" + //20 -> 1R4
            "10011" + //21 -> 1L3
            "11011" + //30 -> 1R3
            "01000";   //31 -> 0R0

    }

//------------------------------------------------------------------------
//getRule

    String getCurrentRule() {
        if (maxStati == 2) return rule2;
        if (maxStati == 3) return rule3;
        if (maxStati == 4) return rule4;
        return "error";
    }
//------------------------------------------------------------------------
}
