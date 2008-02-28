/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;


public class CVModelIIRun1 extends CVModelII {

    /**
     * 
     */
    private static final long serialVersionUID = 5593224423489834019L;

    public void createScape() {
        super.createScape();
        initialCopDensity = .0;
        legitimacy = .90;
        targetCopDensity = initialCopDensity;
    }
}
