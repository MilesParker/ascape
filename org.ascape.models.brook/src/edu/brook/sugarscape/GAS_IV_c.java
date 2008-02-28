/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class GAS_IV_c extends GAS_IV_1 {

    /**
     * 
     */
    private static final long serialVersionUID = -8855423718427062635L;

    public void createScape() {
        super.createScape();
        agents.addRule(new TradeT3());
    }
}
