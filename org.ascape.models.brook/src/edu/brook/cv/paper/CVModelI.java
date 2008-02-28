/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

public class CVModelI extends edu.brook.cv.CVModel {

    /**
     * 
     */
    private static final long serialVersionUID = -1087160376134801280L;

    public void createScape() {
        super.createScape();
        initialPopulationDensity = 0.70;
        //setRandomSeed(941562289135L);
    }
}
