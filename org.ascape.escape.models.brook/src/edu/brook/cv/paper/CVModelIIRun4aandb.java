/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

import org.ascape.model.AscapeObject;

public class CVModelIIRun4aandb extends CVModelIIRun2and3 {

    /**
     * 
     */
    private static final long serialVersionUID = 6305846432005601083L;

    public void createScape() {
        super.createScape();
        initialCopDensity = .04;
        setRandomSeed(AscapeObject.ARBITRARY_SEED);
    }
}
