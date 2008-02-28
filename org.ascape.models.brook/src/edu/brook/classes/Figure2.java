/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;


public class Figure2 extends BargainingModelOne {

    /**
     * 
     */
    private static final long serialVersionUID = -7046721481059081029L;

    public void createScape() {
        super.createScape();
        setMinimumMemorySize(20);
        setMaximumMemorySize(20);
    }
}
