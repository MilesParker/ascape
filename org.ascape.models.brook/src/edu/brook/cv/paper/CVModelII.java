/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

import edu.brook.cv.CVModelInterGroup;

public class CVModelII extends CVModelInterGroup {

    /**
     * 
     */
    private static final long serialVersionUID = 8870616353267658192L;

    public void createScape() {
        super.createScape();
        //setRandomSeed(941562289135L);
        fissionProbability = .05;
        deathAge = 200;
        jailTerm = 15;
        copVision = 1.7;
        personVision = 1.7;
        /*
         * Model II, run 1
         */
        //initialCopDensity = .0;
        //legitimacy = .90;
        /*
         * Model II, run 2, 3
         */
        initialCopDensity = .0;
        //legitimacy = .80;
        /*
         * Model II, run 4a, 4b
         */
        //initialCopDensity = .04;
    }
}
