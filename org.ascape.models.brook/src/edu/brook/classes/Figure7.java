/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;


public class Figure7 extends Figure6 {

    /**
     * 
     */
    private static final long serialVersionUID = 1208719571557483439L;

    public void createScape() {
        super.createScape();
        agents.getInitialRules().select("Intra High/Low");
    }
}
