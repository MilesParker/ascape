/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.rule.Rule;

public class TrackSocialNetwork extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = -434412567804291718L;

    public TrackSocialNetwork() {
        super("Track Social Network");
    }

    public void execute(Agent agent) {
        ((Cell) agent).setNetwork(((CellOccupant) agent).findNeighborsOnHost());
    }
}
