/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class PollutingSugarAgent extends SugarAgent {

    /**
     * 
     */
    private static final long serialVersionUID = -7071352015160701048L;
    private int beta = 50;

    public void metabolism() {
        ((PollutableSugarCell) getHostCell()).addPollution(sugarMetabolism * beta);
        super.metabolism();
    }
}
