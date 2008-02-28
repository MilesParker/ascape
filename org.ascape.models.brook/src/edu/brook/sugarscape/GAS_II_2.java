/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class GAS_II_2 extends GAS_II_1 {

    /**
     * 
     */
    private static final long serialVersionUID = 6962031023647527670L;

    public void createScape() {
        super.createScape();
        //Get rid of grow back infinite rule added in superclass
        sugarscape.getRules().setSelected(SugarCell.SUGAR_GROW_BACK_INF_RULE, false);
        sugarscape.addRule(SugarCell.SUGAR_GROW_BACK_1_RULE);
    }
}
