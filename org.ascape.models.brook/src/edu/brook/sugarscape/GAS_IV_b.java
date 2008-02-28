/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


// Trades like GAS, except agents make one big trade, instead of
// many little ones

public class GAS_IV_b extends GAS_IV_1 {

    /**
     * 
     */
    private static final long serialVersionUID = -3597903690364264605L;

    public void createScape() {
        super.createScape();
        agents.addRule(new TradeT2());
    }
}
