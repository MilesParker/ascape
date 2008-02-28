/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.Scape;

public class GAS_II_5 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = 6725072047329036756L;

    public void createScape() {
        super.createScape();
        agents.addRule(new TrackSocialNetwork());
        agents.setExecutionOrder(Scape.AGENT_ORDER);
    }

    public void createViews() {
        super.createViews();
        sugarView.setDrawNetwork(true);
    }
}
