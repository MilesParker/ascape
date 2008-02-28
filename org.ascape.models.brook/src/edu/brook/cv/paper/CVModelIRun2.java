/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;


public class CVModelIRun2 extends CVModelI {

    /**
     * 
     */
    private static final long serialVersionUID = -429945992065495397L;

    public void createScape() {
        super.createScape();
        //Rnadom seed used for working paper...
        //setRandomSeed(955396277049L);
        copVision = 7.0;
        personVision = 7.0;
        legitimacy = .82;
        jailTerm = 30;
        initialCopDensity = .04;
        targetCopDensity = initialCopDensity;
    }
}
