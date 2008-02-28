/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;


public class Figure8 extends Figure6 {

    /**
     * 
     */
    private static final long serialVersionUID = -217307046772672615L;

    public void createScape() {
        super.createScape();
        setRandomStrategyProbability(0.20f);
        agents.getInitialRules().select("Light Inter Low; Dark Inter High");
    }
}
