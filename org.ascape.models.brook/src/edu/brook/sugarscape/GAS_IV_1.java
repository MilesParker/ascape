/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class GAS_IV_1 extends GAS_SpiceBase {

    /**
     * 
     */
    private static final long serialVersionUID = 8665556682631210554L;

    public void createScape() {
        super.createScape();
        sugarscape.addRule(SpiceCell.SUGAR_SPICE_GROW_BACK_1_RULE);
        agents.addRule(MOVEMENT_RULE);
        agents.addRule(SugarAgent.HARVEST_RULE);
        agents.addRule(METABOLISM_RULE);
        agents.addRule(SpiceAgent.DEATH_STARVATION_SPICE_RULE);
    }
}
