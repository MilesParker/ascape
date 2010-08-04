/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;


public class BargainingModelPeyton extends BargainingModelTwo {

    /**
     * 
     */
    private static final long serialVersionUID = -5838881897720774326L;

    public void createScape() {
        super.createScape();
        agents.setPrototypeAgent(new PeytonBargainer());
        setLowPayoff(0.10f);
        setMediumPayoff(0.30f);
        setHighPayoff(0.50f);
    }
}
