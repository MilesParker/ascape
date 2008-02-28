/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


// like GAS_IV_c, except with old age & reproduction

public class GAS_IV_h extends GAS_IV_e {

    /**
     * 
     */
    private static final long serialVersionUID = 6031812443086507150L;

    public void createScape() {
        super.createScape();
        agents.addRule(new TradeT3());
    }
}
