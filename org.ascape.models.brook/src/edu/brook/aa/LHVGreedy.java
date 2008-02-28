/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;


/**
 * This class involves very preliminary exploration.
 */
public class LHVGreedy extends LHV {

    /**
     * 
     */
    private static final long serialVersionUID = 1650824422643975056L;

    public void createScape() {
        super.createScape();
        households.setPrototypeAgent(new HouseholdGreedy());
    }
}
